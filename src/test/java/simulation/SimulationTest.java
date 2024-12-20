package simulation;

import core.Client;
import core.NodoFog;
import core.Task;
import org.junit.jupiter.api.Test;
import simulation.Simulation;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SimulationTest {

    @Test
    public void testMatchingAndDelays() {
        // Step 1: Creazione di client e nodi usando le factory
        ClientFactory clientFactory = new ClientFactory(2, 5); // Client con task medi tra 2 e 5
        NodoFogFactory nodoFogFactory = new NodoFogFactory(2, 4); // Nodi con capacità tra 2 e 4

        List<Client> clients = clientFactory.generateClients(10); // Genera 10 client
        List<NodoFog> nodi = nodoFogFactory.generateNodi(5); // Genera 5 nodi

        // Step 2: Calcolo delle preference list
        for (Client client : clients) {
            client.calculatePreferenceList(nodi); // Genera la lista delle preferenze del client
        }
        for (NodoFog nodo : nodi) {
            nodo.calculatePreferenceList(clients); // Genera la lista delle preferenze del nodo
        }

        int timeSlotDuration = 10; // Durata di ogni time slot
        Simulation simulation = new Simulation(clients, nodi, timeSlotDuration);

        // Step 3: Simulazione su più time slot
        int numTimeSlots = 3; // Esegui la simulazione su 3 time slot
        simulation.runSimulation(numTimeSlots);

        // Step 4: Verifica del matching
        for (Client client : clients) {
            NodoFog assignedNodo = client.getAssignedNodo();
            assertNotNull(assignedNodo, "Il client deve essere assegnato a un nodo");

            // Verifica che il nodo assegnato sia nella lista di preferenza del client
            List<NodoFog> clientPreferences = client.getPreferenceList();
            assertTrue(clientPreferences.contains(assignedNodo), "Il nodo assegnato deve essere nella lista di preferenza del client");

            // Verifica che il client sia nella lista di preferenza del nodo
            List<Client> nodoPreferences = assignedNodo.getPreferenceList();
            assertTrue(nodoPreferences.contains(client), "Il client deve essere nella lista di preferenza del nodo assegnato");
        }

        // Step 5: Verifica del calcolo dei ritardi e dell'esecuzione cumulativa
        for (NodoFog nodo : nodi) {
            int totalExecutionTime = nodo.getTotalExecutionTime();
            int totalDelayTime = nodo.getTotalDelayTime();

            // Calcolo teorico del ritardo per il time slot corrente
            int currentExpectedDelay = Math.max(0, totalExecutionTime - timeSlotDuration);

            // Verifica del ritardo accumulato
            assertEquals(currentExpectedDelay, totalDelayTime,
                    String.format("Il ritardo accumulato del nodo %d al time slot attuale deve essere calcolato correttamente", nodo.getId()));
        }

    }



}

