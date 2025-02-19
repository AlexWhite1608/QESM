package simulation;

import core.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import utils.PlotManager;

public class Simulation {
    private final List<Client> clients;
    private final List<NodoFog> nodi;
    private final Random random;
    private final int timeSlotDuration;
    private final Map<Integer, Map<Client, NodoFog>> timeSlotMatchings;   // Tiene traccia dei matching in ogni time slot
    private final Map<Integer, Map<Client, Integer>> maxQueueTimePerSlot;  // mappa per salvare il massimo queueTime ad ogni slot (slot, (client, maxQueueTime))
    private final Map<Integer, Map<NodoFog, Integer>> computationCapacityPerSlot;  // mappa per salvare la capacità computazionale per ogni nodo in ogni slot
    private final Map<Integer, Map<NodoFog, Integer>> delayPerSlot;  // mappa per salvare il ritardo accumulato per ogni nodo in ogni slot
    private final Map<Integer, Map<NodoFog, Integer>> executionTimePerSlot;  // mappa per salvare il tempo di esecuzione totale per ogni nodo in ogni slot
    private final List<Integer> swapsPerTimeSlot;  // Numero di swap per ogni time slot
    private final List<Map<Client, NodoFog>> historicalMatchings;  // Storico degli accoppiamenti
    private final List<Double> stabilityPercentages;  // Percentuali di stabilità per ogni time slot
    private final Map<Integer, List<Client>> clientsPerSlot = new HashMap<>();  // Mappa per salvare i clienti presenti in ogni time slot
    private final Map<Integer, Double> queueTimePerSlot = new HashMap<>();  // Mappa per salvare il tempo di attesa totale per ogni time slot

    private Client departedClient = null;
    private Client arrivedClient = null;

    public Simulation(List<Client> clients, List<NodoFog> nodi, int timeSlotDuration) {
        this.clients = clients;
        this.nodi = nodi;
        this.timeSlotDuration = timeSlotDuration;
        this.timeSlotMatchings = new HashMap<>();
        this.maxQueueTimePerSlot = new HashMap<>();
        this.computationCapacityPerSlot = new HashMap<>();
        this.swapsPerTimeSlot = new ArrayList<>();
        this.historicalMatchings = new ArrayList<>();
        this.stabilityPercentages = new ArrayList<>();
        this.delayPerSlot = new HashMap<>();
        this.executionTimePerSlot = new HashMap<>();
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
        if (currentTimeSlot > 1 && Globals.ALLOW_CLIENT_ARRIVAL_DEPARTURE) {
            // Gestione arrivi
            if (random.nextDouble() < Globals.ARRIVAL_PROBABILITY) {
                handleNewClient(currentTimeSlot);
            }

            // Gestione partenze
            if (random.nextDouble() < Globals.DEPARTURE_PROBABILITY) {
                handleClientExit(currentTimeSlot);
            }
        }

        // Aggiorna la mappa con i clienti presenti alla fine del time slot
        clientsPerSlot.put(currentTimeSlot, new ArrayList<>(clients));

        // Controllo stabilità
        GaleShapleyMatching.checkAndPerformSwaps(clients, nodi);

        // Salva il matching corrente
        Map<Client, NodoFog> currentMatching = new HashMap<>();
        for (Client client : clients) {
            if (client.getAssignedNodo() != null) {
                currentMatching.put(client, client.getAssignedNodo());
            }
        }
        timeSlotMatchings.put(currentTimeSlot, currentMatching);

        // Traccia il numero di swap e la stabilità del matching
        trackStability(currentTimeSlot);

        // Processa i task nei nodi
        for (NodoFog nodo : nodi) {
            nodo.processTasks(timeSlotDuration);
        }

        // **Calcola la somma totale dei tempi di attesa per questo time slot**
        double totalQueueTime = 0.0;
        for (Client client : clients) {
            totalQueueTime += client.getQueueTime(); // Somma tutti i tempi di attesa
        }

        // **Salva il tempo di attesa totale per il time slot corrente**
        queueTimePerSlot.put(currentTimeSlot, totalQueueTime);

        // Calcola il massimo queueTime tra tutti i client per questo time slot
        Map<Client, Integer> maxQueueInfo = getMaxQueueInfo();
        maxQueueTimePerSlot.put(currentTimeSlot, maxQueueInfo);

        // Salva i dati sulla capacità computazionale e il ritardo accumulato
        Map<NodoFog, Integer> computationCapacityMap = new HashMap<>();
        Map<NodoFog, Integer> delayMap = new HashMap<>();
        Map<NodoFog, Integer> executionTimeMap = new HashMap<>();
        for (NodoFog nodo : nodi) {
            computationCapacityMap.put(nodo, nodo.getComputationCapability());
            delayMap.put(nodo, nodo.getTotalDelayTime());
            executionTimeMap.put(nodo, nodo.getTotalExecutionTime());
        }
        computationCapacityPerSlot.put(currentTimeSlot, computationCapacityMap);
        delayPerSlot.put(currentTimeSlot, delayMap);
        executionTimePerSlot.put(currentTimeSlot, executionTimeMap);
    }


    public void runSimulation(int slots) {
        for (int i = 0; i < slots; i++) {
            int currentTimeSlot = i + 1;
            simulateTimeSlot(currentTimeSlot);
            printSystemState(currentTimeSlot);
            printSystemStateJSON(currentTimeSlot);
        }

        PlotManager.plotAll(
                maxQueueTimePerSlot,
                swapsPerTimeSlot,
                nodi,
                computationCapacityPerSlot,
                delayPerSlot,
                clientsPerSlot,
                executionTimePerSlot,
                queueTimePerSlot
        );
    }

    // Generazione del nuovo client e assegnazione a un nodo casuale
    private void handleNewClient(int currentTimeSlot) {
        int meanTaskSize = random.nextInt(Globals.CLIENT_TASK_SIZE_MEAN_MAX - Globals.CLIENT_TASK_SIZE_MEAN_MIN + 1) + Globals.CLIENT_TASK_SIZE_MEAN_MIN;
        Client newClient = new Client(currentTimeSlot, null, meanTaskSize);
        clients.add(newClient);
        arrivedClient = newClient;

        // Accoppiamento casuale con un nodo
        Random randomGenerator = new Random();
        int indexNodo = randomGenerator.nextInt(nodi.size());
        NodoFog selectedNodo = nodi.get(indexNodo);

        if (selectedNodo != null) {
            selectedNodo.getClientsQueue().add(newClient);
            newClient.setAssignedNodo(selectedNodo);
            //System.out.println("Nuovo client aggiunto: " + newClient.getId() + " -> NodoFog ID: " + selectedNodo.getId());
        }
    }

    // Rimozione di un client casuale
    private void handleClientExit(int departureTime) {
        if (!clients.isEmpty()) {
            Client exitingClient = clients.remove(random.nextInt(clients.size()));
            exitingClient.setDepartureTime(departureTime);
            NodoFog assignedNodo = exitingClient.getAssignedNodo();
            departedClient = exitingClient;
            if (assignedNodo != null) {
                assignedNodo.getClientsQueue().remove(exitingClient);
                assignedNodo.getTaskQueue().removeAll(exitingClient.getTaskList());
            }
            //assert assignedNodo != null;
            //System.out.println("Client uscente: " + exitingClient.getId() + " dal NodoFog: " + assignedNodo.getId());
        }
    }

    // Traccia il numero di swap e la stabilità del matching
    private void trackStability(int currentTimeSlot) {
        // Registra il numero di swap avvenuti in questo time slot
        int swaps = GaleShapleyMatching.getNumberOfSwaps();
        swapsPerTimeSlot.add(swaps);

        // Confronta gli accoppiamenti attuali con il time slot precedente
        Map<Client, NodoFog> currentMatching = timeSlotMatchings.get(currentTimeSlot);
        if (currentMatching != null) {
            historicalMatchings.add(currentMatching);
        }

        //FIXME: Calcola la percentuale di stabilità (cambiamenti negli accoppiamenti) rispetto al time slot precedente
        if (currentTimeSlot > 1) {
            Map<Client, NodoFog> previousMatching = timeSlotMatchings.get(currentTimeSlot - 1);

            int stablePairs = 0;
            int totalComparableClients = 0;

            for (Map.Entry<Client, NodoFog> entry : currentMatching.entrySet()) {
                Client client = entry.getKey();
                NodoFog currentNodo = entry.getValue();

                // Escludi i nuovi client che non erano presenti nel matching precedente
                if (!previousMatching.containsKey(client)) {
                    continue;
                }

                // Confronta solo i client che esistono sia nel matching attuale che in quello precedente
                totalComparableClients++;
                if (previousMatching.get(client) == currentNodo) {
                    stablePairs++;
                }
            }

            // Calcola la percentuale solo sui client comparabili (escludendo nuovi e rimossi)
            double stabilityPercentage = totalComparableClients > 0
                    ? (double) stablePairs / totalComparableClients * 100
                    : 100.0; // Se non ci sono client comparabili, consideriamo il matching completamente stabile

            stabilityPercentages.add(stabilityPercentage);
        }
    }


    private void printSystemStateJSON(int timeSlot) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Map<String, Object> log = new HashMap<>();
        log.put("timeSlot", timeSlot);
        log.put("timeSlotDuration", timeSlotDuration);
        log.put("totalSwaps:", GaleShapleyMatching.getNumberOfSwaps());

        // Clienti e Nodi generati
        log.put("totalClients", clients.size());
        log.put("totalNodiFog", nodi.size());

        // Matching
        Map<String, String> matchingLog = new HashMap<>();
        Map<Client, NodoFog> currentMatching = timeSlotMatchings.get(timeSlot);
        if (currentMatching != null) {
            for (Map.Entry<Client, NodoFog> entry : currentMatching.entrySet()) {
                matchingLog.put("Client " + entry.getKey().getId(), "NodoFog " + entry.getValue().getId());
            }
        }
        log.put("matching", matchingLog.isEmpty() ? "Nessun matching trovato" : matchingLog);

        // Clienti arrivati / partiti
        Map<String, String> clientChanges = new HashMap<>();
        if (arrivedClient != null) {
            clientChanges.put("arrivedClient", "Client " + arrivedClient.getId() + " -> NodoFog " + arrivedClient.getAssignedNodo().getId());
        }
        if (departedClient != null) {
            clientChanges.put("departedClient", "Client " + departedClient.getId() + " uscito");
        }
        log.put("clientChanges", clientChanges.isEmpty() ? "Nessun cliente arrivato o partito" : clientChanges);

        // Stato dei Client
        List<Map<String, Object>> clientStates = new ArrayList<>();
        for (Client client : clients) {
            Map<String, Object> clientLog = new HashMap<>();
            clientLog.put("id", client.getId());
            clientLog.put("arrivalTime", client.getArrivalTime());
            clientLog.put("totalRequiredTime", client.getTaskList().stream().mapToInt(Task::getRequiredTime).sum());
            clientLog.put("queueTime", client.getQueueTime());
            clientStates.add(clientLog);
        }
        log.put("clients", clientStates);

        // Stato dei Nodi
        List<Map<String, Object>> nodoStates = new ArrayList<>();
        for (NodoFog nodo : nodi) {
            Map<String, Object> nodoLog = new HashMap<>();
            nodoLog.put("id", nodo.getId());
            nodoLog.put("computationCapability", nodo.getComputationCapability());
            nodoLog.put("maxCapacity", nodo.getMaxQueueSize());
            nodoLog.put("totalExecutionTime", nodo.getTotalExecutionTime());
            nodoLog.put("totalServices", nodo.getTotalServices());
            nodoLog.put("totalDelay", nodo.getTotalDelayTime());
            nodoStates.add(nodoLog);
        }
        log.put("nodi", nodoStates);

        // Salvataggio su file
        try (FileWriter file = new FileWriter("output/timeSlot_" + timeSlot + ".json")) {
            gson.toJson(log, file);
        } catch (IOException e) {
            System.err.println("Errore nel salvataggio del file: " + e.getMessage());
        }
    }

    private void printSystemState(int timeSlot) {
        System.out.printf("\n=== Stato al time slot %d (durata %d) ===", timeSlot, timeSlotDuration);

        System.out.println("\n--- Clienti/Nodi generati ---");
        System.out.println("Numero di Clienti: " + clients.size());
        System.out.println("Numero di Nodi Fog: " + nodi.size());

        // Matching
        System.out.println("\n--- Matching ---");
        System.out.println("Swap avvenuti: " + GaleShapleyMatching.getNumberOfSwaps());
        Map<Client, NodoFog> currentMatching = timeSlotMatchings.get(timeSlot);
        if (currentMatching != null && !currentMatching.isEmpty()) {
            for (Map.Entry<Client, NodoFog> entry : currentMatching.entrySet()) {
                System.out.println("Client " + entry.getKey().getId() +
                        " -> NodoFog " + entry.getValue().getId());
            }
        } else {
            System.out.println("Nessun matching trovato.");
        }

        // Clienti arrivati / partiti
        System.out.println("\n--- Clienti Arrivati/Partiti ---");
        if (arrivedClient != null) {
            System.out.println("Client " + arrivedClient.getId() + " assegnato al NodoFog " + arrivedClient.getAssignedNodo().getId());
        }
        if (departedClient != null) {
            System.out.println("Client " + departedClient.getId() + " assegnato al NodoFog " + departedClient.getAssignedNodo().getId() + " uscito");
        }

        // Stato dei Client
        System.out.println("\n--- Stato dei Client ---");
        for (Client client : clients) {
            System.out.println("Client " + client.getId()
                    + " (a: " + client.getArrivalTime() + ")" +
                    ", Tempo Totale richiesto: " + client.getTaskList().stream().mapToInt(Task::getRequiredTime).sum() +
                    ", Tempo totale in attesa: " + client.getQueueTime());
        }

        // Stato dei Nodi
        System.out.println("\n--- Stato dei Nodi Fog ---");
        for (NodoFog nodo : nodi) {
            System.out.println("NodoFog ID: " + nodo.getId() +
                    ", Potenza di Calcolo: " + nodo.getComputationCapability() +
                    ", Capacità: " + nodo.getMaxQueueSize() +
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


