package core;

import java.util.*;

public class GaleShapleyMatching {
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
                System.out.println("Client " + client.getId() + " non assegnato in questo time slot.");
            }
        }

    }
}
