package simulation;

import core.Client;
import core.NodoFog;
import core.GaleShapleyMatching;
import core.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Simulation {
    private List<Client> clients;
    private List<NodoFog> nodi;
    private Random random;
    private int timeSlotDuration;
    private Map<Integer, Map<Client, NodoFog>> timeSlotMatchings;   // Tiene traccia dei matching in ogni time slot
    private Map<Integer, Map<Client, Integer>> maxQueueTimePerSlot;  // mappa per salvare il massimo queueTime ad ogni slot (slot, (client, maxQueueTime))

    public Simulation(List<Client> clients, List<NodoFog> nodi, int timeSlotDuration) {
        this.clients = clients;
        this.nodi = nodi;
        this.timeSlotDuration = timeSlotDuration;
        this.timeSlotMatchings = new HashMap<>();
        this.maxQueueTimePerSlot = new HashMap<>();
        this.random = new Random();
    }

    /**
     * Simula uno step temporale nella simulazione.
     */
    public void simulateTimeSlot(int currentTimeSlot) {

        // Genera i task dei client
        for (Client client : clients) {
            client.generateTasks();
        }

        // Esegue il matching
        GaleShapleyMatching.match(clients, nodi);

        // Salva il matching corrente
        Map<Client, NodoFog> currentMatching = new HashMap<>();
        for (Client client : clients) {
            if (client.getAssignedNodo() != null) {
                currentMatching.put(client, client.getAssignedNodo());
            }
        }
        timeSlotMatchings.put(currentTimeSlot, currentMatching);

        // Processa i task nei nodi
        for (NodoFog nodo : nodi) {
            nodo.processTasks(timeSlotDuration);
        }

        // Calcola il massimo queueTime tra tutti i client per questo time slot
        Map<Client, Integer> maxQueueInfo = getMaxQueueInfo();
        maxQueueTimePerSlot.put(currentTimeSlot, maxQueueInfo);

    }

    public void runSimulation(int slots) {
        for (int i = 0; i < slots; i++) {
            int currentTimeSlot = i + 1;
            simulateTimeSlot(currentTimeSlot);
            printSystemState(currentTimeSlot);
        }

        SimulationPlot.plotMaxQueueTime(maxQueueTimePerSlot);
    }

    private void printSystemState(int timeSlot) {
        System.out.printf("=== Stato al time slot %d (durata %d) ===", timeSlot, timeSlotDuration);

        System.out.println("\n--- Clienti/Nodi generati ---");
        System.out.println("Numero di Clienti: " + clients.size());
        System.out.println("Numero di Nodi Fog: " + nodi.size());

        // Matching
        System.out.println("\n--- Matching ---");
        Map<Client, NodoFog> currentMatching = timeSlotMatchings.get(timeSlot);
        if (currentMatching != null && !currentMatching.isEmpty()) {
            for (Map.Entry<Client, NodoFog> entry : currentMatching.entrySet()) {
                System.out.println("Client ID: " + entry.getKey().getId() +
                        " -> NodoFog ID: " + entry.getValue().getId());
            }
        } else {
            System.out.println("Nessun matching trovato.");
        }


        // Stato dei Client
        System.out.println("\n--- Stato dei Client ---");
        for (Client client : clients) {
            System.out.println("Client ID: " + client.getId() +
                    ", Tempo Totale richiesto: " + client.getTaskList().stream().mapToInt(Task::getRequiredTime).sum() +
                    ", Tempo totale in attesa: " + client.getQueueTime());
        }

        // Stato dei Nodi
        System.out.println("\n--- Stato dei Nodi Fog ---");
        for (NodoFog nodo : nodi) {
            System.out.println("NodoFog ID: " + nodo.getId() +
                    ", Capacità di Calcolo: " + nodo.getComputationCapability() +
                    ", Tempo Totale di Esecuzione: " + nodo.getTotalExecutionTime() +
                    ", Ritardo accumulato: " + nodo.getTotalDelayTime());
        }

        System.out.println("==========================\n");
    }

    // Restituisce il client con il tempo di attesa massimo e il suo tempo di attesa
    private Map<Client, Integer> getMaxQueueInfo() {
        Client maxWaitClient = null;
        int maxQueueTime = -1;
        for (Client c : clients) {
            int currentQueueTime = c.getQueueTime();
            if (currentQueueTime > maxQueueTime) {
                maxQueueTime = currentQueueTime;
                maxWaitClient = c;
            }
        }

        // Ora maxWaitClient è il client con il tempo di attesa massimo e maxQueueTime è il valore
        Map<Client, Integer> maxQueueInfo = new HashMap<>();
        if (maxWaitClient != null) {
            maxQueueInfo.put(maxWaitClient, maxQueueTime);
        }
        return maxQueueInfo;
    }

}


