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

        // Esegui il matching per riassegnare i client
        Map<Client, NodoFog> matching = GaleShapleyMatching.match(clients, nodi);

        // Processa i task nei nodi
        for (NodoFog nodo : nodi) {
            nodo.processTasks();
        }

        // Aggiorna il tempo di coda per i client non serviti
        for (Client client : clients) {
            if (client.getAssignedNodo() == null) {
                client.incrementQueueTime();
            }
        }

        // Aggiungi i task generati dai client assegnati
        for (Map.Entry<Client, NodoFog> entry : matching.entrySet()) {
            Client client = entry.getKey();
            NodoFog nodo = entry.getValue();
            if (nodo.getCurrentLoad() < nodo.getCapacity()) { // Assegna solo se c'è capacità
                int tasks = client.generateTasks();
                nodo.addTasks(client, tasks);
                client.setAssignedNodo(nodo);
            }
        }
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
            System.out.println("  NodoFog " + nodo.getId() + " -> CurrentLoad: " + nodo.getCurrentLoad());
        }
        System.out.println();
    }
}


