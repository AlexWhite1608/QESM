package core;

import java.util.*;

public class GaleShapleyMatching {
    /**
     * Implementa l'algoritmo di Gale-Shapley per trovare un matching stabile tra client e nodi fog.
     * L'algoritmo calcola internamente le preference list di ciascun client e nodo.
     *
     * @param clients Lista dei client che richiedono l'esecuzione di task.
     * @param nodi    Lista dei nodi fog che offrono capacità computazionale.
     * @return Una mappa {@code Map<Client, NodoFog>} che associa ciascun client al nodo fog selezionato
     *         nel matching stabile.
     */
    public static Map<Client, NodoFog> match(List<Client> clients, List<NodoFog> nodi) {
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

                // Se il nodo è libero, accetta la proposta
                if (!engagedPairs.containsValue(nodo)) {
                    engagedPairs.put(client, nodo);
                    break; // Il client è ora accoppiato
                } else {
                    // Nodo già accoppiato, controlla se preferisce il nuovo client
                    Client currentClient = getClientEngagedWith(engagedPairs, nodo);
                    List<Client> nodoPrefList = nodo.getPreferenceList();

                    if (nodoPrefList.indexOf(client) < nodoPrefList.indexOf(currentClient)) {
                        // Nodo preferisce il nuovo client: rimuovi il precedente
                        engagedPairs.remove(currentClient);
                        freeClients.add(currentClient); // Rendi il precedente client libero
                        engagedPairs.put(client, nodo);
                        break; // Il nuovo client è ora accoppiato
                    }
                }
            }

            // Se il client non è riuscito a trovare un nodo, resta nella coda
            if (!engagedPairs.containsKey(client)) {
                freeClients.add(client);
            }
        }

        return engagedPairs;
    }

    private static Client getClientEngagedWith(Map<Client, NodoFog> engagedPairs, NodoFog nodo) {
        return engagedPairs.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(nodo))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }
}
