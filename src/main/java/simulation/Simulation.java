package simulation;

import core.*;

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
    private Map<Integer, Map<NodoFog, Integer>> computationCapacityPerSlot;  // mappa per salvare la capacità computazionale per ogni nodo in ogni slot
    private Map<Integer, Map<NodoFog, Integer>> delayPerSlot;  // mappa per salvare il ritardo accumulato per ogni nodo in ogni slot

    public Simulation(List<Client> clients, List<NodoFog> nodi, int timeSlotDuration) {
        this.clients = clients;
        this.nodi = nodi;
        this.timeSlotDuration = timeSlotDuration;
        this.timeSlotMatchings = new HashMap<>();
        this.maxQueueTimePerSlot = new HashMap<>();
        this.computationCapacityPerSlot = new HashMap<>();
        this.delayPerSlot = new HashMap<>();
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

        // Arrivo/uscita di clienti
        if (currentTimeSlot > 1) {
            if (random.nextDouble() < 0.5) {    //TODO: generalizza tramite parametro la probabilità
                handleNewClient(currentTimeSlot);
            } else {
                handleClientExit(currentTimeSlot);
            }
        }

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

        //FIXME: e se arrivo/uscita del client fosse dopo processing dei task?

        // Calcola il massimo queueTime tra tutti i client per questo time slot
        Map<Client, Integer> maxQueueInfo = getMaxQueueInfo();
        maxQueueTimePerSlot.put(currentTimeSlot, maxQueueInfo);

        // Salva i dati sulla capacità computazionale e il ritardo accumulato
        Map<NodoFog, Integer> computationCapacityMap = new HashMap<>();
        Map<NodoFog, Integer> delayMap = new HashMap<>();
        for (NodoFog nodo : nodi) {
            computationCapacityMap.put(nodo, nodo.getComputationCapability());
            delayMap.put(nodo, nodo.getTotalDelayTime());
        }
        computationCapacityPerSlot.put(currentTimeSlot, computationCapacityMap);
        delayPerSlot.put(currentTimeSlot, delayMap);

    }

    public void runSimulation(int slots) {
        for (int i = 0; i < slots; i++) {
            int currentTimeSlot = i + 1;
            simulateTimeSlot(currentTimeSlot);
            printSystemState(currentTimeSlot);
        }

        SimulationPlot.plotMaxQueueTime(maxQueueTimePerSlot);
        SimulationPlot.plotComputationAndDelay(computationCapacityPerSlot, delayPerSlot);
    }

    // Generazione del nuovo client e assegnazione a un nodo casuale
    private void handleNewClient(int currentTimeSlot) {
        int meanTaskSize = random.nextInt(Globals.CLIENT_TASK_SIZE_MEAN_MAX - Globals.CLIENT_TASK_SIZE_MEAN_MIN + 1) + Globals.CLIENT_TASK_SIZE_MEAN_MIN;
        Client newClient = new Client(currentTimeSlot, null, meanTaskSize);
        clients.add(newClient);

        // Accoppiamento greedy
        Random randomGenerator = new Random();
        int indexNodo = randomGenerator.nextInt(nodi.size());
        NodoFog selectedNodo = nodi.get(indexNodo);

        if (selectedNodo != null) {
            selectedNodo.getClientsQueue().add(newClient);
            newClient.setAssignedNodo(selectedNodo);
            System.out.println("Nuovo client aggiunto: " + newClient.getId() + " -> NodoFog ID: " + selectedNodo.getId());
        }
    }

    // Rimozione di un client casuale
    private void handleClientExit(int departureTime) {
        if (!clients.isEmpty()) {
            Client exitingClient = clients.remove(random.nextInt(clients.size()));
            exitingClient.setDepartureTime(departureTime);
            NodoFog assignedNodo = exitingClient.getAssignedNodo();
            if (assignedNodo != null) {
                assignedNodo.getClientsQueue().remove(exitingClient);
                assignedNodo.getTaskQueue().removeAll(exitingClient.getTaskList());
            }
            assert assignedNodo != null;
            System.out.println("Client uscente: " + exitingClient.getId() + " dal NodoFog: " + assignedNodo.getId());
        }
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
                    ", Numero totale servizi: " + nodo.getTotalServices() +
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


