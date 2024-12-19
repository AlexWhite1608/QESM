package core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GaleShapleyMatchingTest {

    private List<Client> clients;
    private List<NodoFog> nodi;

    @BeforeEach
    void setUp() {
        NodoFog nodo1 = new NodoFog(10, 5, 3);
        NodoFog nodo2 = new NodoFog(8, 4, 2);
        NodoFog nodo3 = new NodoFog(12, 6, 5);

        Client client1 = new Client(0, 3);
        Client client2 = new Client(1, 2);
        Client client3 = new Client(2, 5);

        clients = Arrays.asList(client1, client2, client3);
        nodi = Arrays.asList(nodo1, nodo2, nodo3);
    }

    @Test
    void testMatchingStability() {
        Map<Client, NodoFog> matching = GaleShapleyMatching.match(clients, nodi);

        assertEquals(clients.size(), matching.size(), "Tutti i client devono essere accoppiati");

        // Verifica che ogni client sia accoppiato con un nodo valido
        for (Client client : clients) {
            assertTrue(matching.containsKey(client), "Il client deve essere nel matching");
            NodoFog nodo = matching.get(client);
            assertNotNull(nodo, "Il nodo assegnato non deve essere null");
            assertTrue(nodi.contains(nodo), "Il nodo assegnato deve essere valido");
        }

        // Verifica stabilità: nessuna coppia non assegnata preferisce formarsi
        for (Map.Entry<Client, NodoFog> entry : matching.entrySet()) {
            Client client = entry.getKey();
            NodoFog assignedNodo = entry.getValue();
            List<NodoFog> clientPrefList = client.getPreferenceList();

            // Controlla che il nodo assegnato sia migliore di ogni nodo successivo nella preference list
            for (NodoFog nodo : clientPrefList) {
                if (nodo.equals(assignedNodo)) break; // Nodo assegnato raggiunto, esci dal loop

                // Nodo migliore non assegnato: verifica che il nodo non preferisca un altro client
                List<Client> nodoPrefList = nodo.getPreferenceList();
                Client currentPartner = matching.entrySet().stream()
                        .filter(e -> e.getValue().equals(nodo))
                        .map(Map.Entry::getKey)
                        .findFirst()
                        .orElse(null);

                assertTrue(
                        nodoPrefList.indexOf(client) >= nodoPrefList.indexOf(currentPartner),
                        "Il nodo non deve preferire un client non assegnato al proprio partner attuale."
                );
            }
        }
    }

    @Test
    void testCorrectAssignment() {
        // Esecuzione dell'algoritmo di Gale-Shapley
        Map<Client, NodoFog> matching = GaleShapleyMatching.match(clients, nodi);

        // Aggiorna i risultati attesi
        NodoFog expectedNodoForClient1 = nodi.get(0);
        NodoFog expectedNodoForClient2 = nodi.get(1);
        NodoFog expectedNodoForClient3 = nodi.get(2);

        assertEquals(expectedNodoForClient1, matching.get(clients.get(0)), "Client 1 dovrebbe essere assegnato a Nodo 1.");
        assertEquals(expectedNodoForClient2, matching.get(clients.get(1)), "Client 2 dovrebbe essere assegnato a Nodo 2.");
        assertEquals(expectedNodoForClient3, matching.get(clients.get(2)), "Client 3 dovrebbe essere assegnato a Nodo 3.");
    }


}

