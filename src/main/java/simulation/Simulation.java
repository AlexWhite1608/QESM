package simulation;

import core.Client;
import core.NodoFog;
import core.GaleShapleyMatching;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class Simulation {
    private List<Client> clients;
    private List<NodoFog> nodi;
    private Random random;

    public Simulation(List<Client> clients, List<NodoFog> nodi) {
        this.clients = clients;
        this.nodi = nodi;
        this.random = new Random();
    }

    /**
     * Simula uno step temporale nella simulazione.
     */
    public void simulateTimeStep() {
        // Aggiorna lo stato dei nodi (riduce il tempo di esecuzione e rimuove task completati)
        for (NodoFog nodo : nodi) {
            updateNodeLoad(nodo);
        }

        // Aggiorna il tempo di coda per i client non serviti
        for (Client client : clients) {
            if (client.getAssignedNodo() == null) {
                client.incrementQueueTime();
            }
        }

        // Esegui il matching per riassegnare i client non assegnati
        Map<Client, NodoFog> matching = GaleShapleyMatching.match(clients, nodi);

        // Applica il matching e aggiorna i carichi
        for (Map.Entry<Client, NodoFog> entry : matching.entrySet()) {
            Client client = entry.getKey();
            NodoFog nodo = entry.getValue();
            if (nodo.getCurrentLoad() < nodo.getCapacity()) { // Assegna solo se il nodo ha capacità disponibile
                assignClientToNode(client, nodo);
            }
        }
    }

    /**
     * Aggiorna il carico del nodo riducendo il tempo di esecuzione e rimuovendo i task completati.
     */
    private void updateNodeLoad(NodoFog nodo) {
        if (nodo.getExecutionTime() > 0) {
            nodo.setExecutionTime(nodo.getExecutionTime() - 1); // Riduce il tempo di esecuzione
        }
        if (nodo.getExecutionTime() <= 0 && nodo.getCurrentLoad() > 0) {
            nodo.setCurrentLoad(nodo.getCurrentLoad() - 1); // Rimuove un task completato
        }
    }

    /**
     * Calcola il tempo di esecuzione per un task di un client.
     */
    public int calculateExecutionTime(Client client) {
        int baseTime = client.getTaskSize();
        int variability = (int) (baseTime * 0.2);
        return baseTime + random.nextInt(2 * variability + 1) - variability;
    }

    /**
     * Assegna un client a un nodo, aggiornando il carico e il tempo di esecuzione.
     */
    public void assignClientToNode(Client client, NodoFog nodo) {
        client.setAssignedNodo(nodo);
        int executionTime = calculateExecutionTime(client);
        nodo.setCurrentLoad(nodo.getCurrentLoad() + 1);
        nodo.setExecutionTime(nodo.getExecutionTime() + executionTime);
    }

    /**
     * Simula l'intero sistema per un numero di step temporali.
     */
    public void runSimulation(int steps) {
        for (int i = 0; i < steps; i++) {
            System.out.println("Step " + (i + 1) + ":");
            simulateTimeStep();
            printSystemState();
        }
    }

    /**
     * Stampa lo stato attuale del sistema.
     */
    private void printSystemState() {
        System.out.println("Client State:");
        for (Client client : clients) {
            System.out.println("  Client " + client.getId() + " -> QueueTime: " + client.getQueueTime() +
                    ", AssignedNodo: " + (client.getAssignedNodo() != null ? client.getAssignedNodo().getId() : "None"));
        }
        System.out.println("NodoFog State:");
        for (NodoFog nodo : nodi) {
            System.out.println("  NodoFog " + nodo.getId() + " -> CurrentLoad: " + nodo.getCurrentLoad() +
                    ", ExecutionTime: " + nodo.getExecutionTime());
        }
        System.out.println();
    }
}

