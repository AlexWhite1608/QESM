package core;

import java.util.*;

public class GaleShapleyMatching {

    private static int numberOfSwaps = 0;

    /**
     * Implementa l'algoritmo di Gale-Shapley per trovare un matching stabile tra client e nodi fog.
     * L'algoritmo calcola internamente le preference list di ciascun client e nodo.
     *
     * @param clients Lista dei client che richiedono l'esecuzione di task.
     * @param nodi    Lista dei nodi fog che offrono capacità computazionale.
     */
    public static void match(List<Client> clients, List<NodoFog> nodi) {
        for (Client client : clients) {
            client.calculatePreferenceList(nodi);
        }

        for (NodoFog nodo : nodi) {
            nodo.calculatePreferenceList(clients);
        }

        // Proposte dei client ai nodi
        Map<Client, List<NodoFog>> clientProposals = new HashMap<>();
        for (Client client : clients) {
            clientProposals.put(client, new ArrayList<>());
        }

        // Lista di client liberi (che non hanno ancora una corrispondenza stabile)
        Queue<Client> freeClients = new LinkedList<>(clients);

        // Coppie del matching finale
        Map<Client, NodoFog> engagedPairs = new HashMap<>();

        // Algoritmo di matching
        while (!freeClients.isEmpty()) {
            Client client = freeClients.poll(); // Prendi un client libero
            List<NodoFog> clientPrefList = client.getPreferenceList();

            for (NodoFog nodo : clientPrefList) {
                if (nodo.isQueueFull()) continue; //FIXME: Salta se il nodo è pieno

                // Salta se il client ha già proposto a questo nodo
                if (clientProposals.get(client).contains(nodo)) continue;

                // Registra la proposta
                clientProposals.get(client).add(nodo);

                // Aggiungi il client alla coda del nodo
                nodo.getClientsQueue().add(client);
                client.setAssignedNodo(nodo);

                // Ordina la coda in base alla lista di preferenza del nodo
                List<Client> sortedClients = new ArrayList<>(nodo.getClientsQueue());
                sortedClients.sort((c1, c2) -> {
                    List<Client> nodoPrefList = nodo.getPreferenceList();
                    return Integer.compare(nodoPrefList.indexOf(c1), nodoPrefList.indexOf(c2));
                });

                // Aggiorna la coda del nodo con l'ordine corretto
                nodo.getClientsQueue().clear();
                nodo.getClientsQueue().addAll(sortedClients);

                // Accetta la proposta e registra il matching
                engagedPairs.put(client, nodo);
                break; // Il client è ora accoppiato (anche se in coda)
            }

            // Se il client non è riuscito a trovare un nodo, resta non assegnato
            if (!engagedPairs.containsKey(client)) {
                //TODO: se client non è assegnato ad un nodo, aumenta il suo tempo di attesa!!
                client.incrementQueueTime(Globals.TIME_SLOT_DURATION);
                System.out.println("Client " + client.getId() + " non assegnato in questo time slot.");
            }
        }

    }

    // Verifica se conviene fare scambi per migliorare la stabilità del matching
    public static void checkAndPerformSwaps(List<Client> clients, List<NodoFog> nodi) {
        for (Client client1 : clients) {
            for (Client client2 : clients) {
                if (client1 != client2 && client1.getAssignedNodo() != null && client2.getAssignedNodo() != null) {
                    NodoFog nodo1 = client1.getAssignedNodo();
                    NodoFog nodo2 = client2.getAssignedNodo();

                    // Ottieni le preferenze attuali
                    List<NodoFog> client1Preferences = client1.getPreferenceList();
                    List<NodoFog> client2Preferences = client2.getPreferenceList();

                    // Ottieni le posizioni attuali e quelle dopo l'eventuale scambio
                    int client1CurrentPreference = client1Preferences.indexOf(nodo1);
                    int client2CurrentPreference = client2Preferences.indexOf(nodo2);

                    int client1NewPreference = client1Preferences.indexOf(nodo2);
                    int client2NewPreference = client2Preferences.indexOf(nodo1);

                    // Condizioni per il miglioramento
                    boolean client1Improves = client1NewPreference < client1CurrentPreference;
                    boolean client2Improves = client2NewPreference < client2CurrentPreference;

                    // Verifica se lo scambio è vantaggioso
                    if (client1Improves || client2Improves) {

                        // Effettua lo scambio
                        nodo1.getClientsQueue().remove(client1);
                        nodo2.getClientsQueue().remove(client2);

                        nodo1.getClientsQueue().add(client2);
                        nodo2.getClientsQueue().add(client1);

                        client1.setAssignedNodo(nodo2);
                        client2.setAssignedNodo(nodo1);

                        numberOfSwaps++;
                    }
                }
            }
        }
    }

    public static int getNumberOfSwaps() {
        return numberOfSwaps;
    }
}
