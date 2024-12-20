import core.Client;
import simulation.ClientFactory;
import core.NodoFog;
import simulation.NodoFogFactory;
import simulation.Simulation;

import java.util.List;

public class Main {
    // Configurazione
    private static final int NUM_NODI = 6;
    private static final int NUM_CLIENTS = 10;
    private static final int NODO_CAPACITY_MIN = 30; // Capacità minima dei nodi
    private static final int NODO_CAPACITY_MAX = 70; // Capacità massima dei nodi
    private static final int CLIENT_TASK_SIZE_MEAN_MIN = 20; // Minimo del valor medio dei task generati
    private static final int CLIENT_TASK_SIZE_MEAN_MAX = 50; // Massimo del valor medio dei task generati

    public static void main(String[] args) {
        NodoFogFactory nodoFogFactory = new NodoFogFactory(NODO_CAPACITY_MIN, NODO_CAPACITY_MAX);
        List<NodoFog> nodi = nodoFogFactory.generateNodi(NUM_NODI);

        ClientFactory clientFactory = new ClientFactory(CLIENT_TASK_SIZE_MEAN_MIN, CLIENT_TASK_SIZE_MEAN_MAX);
        List<Client> clients = clientFactory.generateClients(NUM_CLIENTS);

        // Simulazione
        Simulation simulation = new Simulation(clients, nodi, 20);
        simulation.runSimulation(10);
    }
}
