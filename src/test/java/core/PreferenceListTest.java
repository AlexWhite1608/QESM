package core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PreferenceListTest {
    private NodoFog nodo1;
    private NodoFog nodo2;
    private NodoFog nodo3;
    private Client client1;
    private Client client2;
    private Client client3;
    private List<NodoFog> nodi;
    private List<Client> clients;

    @BeforeEach
    void setUp() {
        nodo1 = new NodoFog(10, 5, 3);
        nodo2 = new NodoFog( 8, 4, 2);
        nodo3 = new NodoFog( 12, 6, 5);

        client1 = new Client(0, 3);
        client2 = new Client( 1, 2);
        client3 = new Client( 2, 5);

        nodi = Arrays.asList(nodo1, nodo2, nodo3);
        clients = Arrays.asList(client1, client2, client3);
    }

    @Test
    void testClientPreferenceList() {
        // Calcolo delle preference list dei client verso i nodi
        for (Client client : clients) {
            client.calculatePreferenceList(nodi);
        }

        List<NodoFog> sortedNodiClient1 = client1.getPreferenceList();
        assertEquals(nodo2, sortedNodiClient1.get(0));
        assertEquals(nodo1, sortedNodiClient1.get(1));
        assertEquals(nodo3, sortedNodiClient1.get(2));
    }

    @Test
    void testNodoPreferenceList() {
        // Calcolo delle preference list dei nodi verso i client
        for (NodoFog nodo : nodi) {
            nodo.calculatePreferenceList(clients);
        }

        List<Client> sortedClientsNodo1 = nodo1.getPreferenceList();

        assertEquals(client2, sortedClientsNodo1.get(0));
        assertEquals(client1, sortedClientsNodo1.get(1));
        assertEquals(client3, sortedClientsNodo1.get(2));
    }
}
