import core.Client;
import core.NodoFog;
import simulation.Simulation;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Creazione di nodi
        NodoFog nodo1 = new NodoFog(10);
        NodoFog nodo2 = new NodoFog(8);
        NodoFog nodo3 = new NodoFog(12);

        // Creazione di client
        Client client1 = new Client(0, 3);
        Client client2 = new Client(0, 2);
        Client client3 = new Client(0, 5);

        List<Client> clients = Arrays.asList(client1, client2, client3);
        List<NodoFog> nodi = Arrays.asList(nodo1, nodo2, nodo3);

        // Simulazione
        Simulation simulation = new Simulation(clients, nodi);
        simulation.runSimulation(10);
    }

}