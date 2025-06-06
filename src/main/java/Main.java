import core.Client;
import core.Globals;
import utils.ClientFactory;
import core.NodoFog;
import utils.NodoFogFactory;
import simulation.Simulation;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        NodoFogFactory nodoFogFactory = new NodoFogFactory(Globals.NODO_COMPUTATION_CAPABILITY_MIN, Globals.NODO_COMPUTATION_CAPABILITY_MAX, Globals.NODE_CAPACITY_MEAN);
        List<NodoFog> nodi = nodoFogFactory.generateNodi(Globals.NUM_NODI);

        ClientFactory clientFactory = new ClientFactory(Globals.CLIENT_TASK_SIZE_MEAN_MIN, Globals.CLIENT_TASK_SIZE_MEAN_MAX);
        List<Client> clients = clientFactory.generateClients(Globals.NUM_CLIENTS);

        // Simulazione
        Simulation simulation = new Simulation(clients, nodi, Globals.TIME_SLOT_DURATION);
        simulation.runSimulation(Globals.NUM_TIME_SLOTS);
    }
}
