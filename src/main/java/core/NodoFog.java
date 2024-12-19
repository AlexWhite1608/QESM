package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class NodoFog {
    private int id;
    private int capacity; // Capacità computazionale del nodo
    private int currentLoad; // Carico attuale sul nodo (tempo di coda)
    private int executionTime; // Tempo stimato di esecuzione per un task
    private int distance; // Distanza con i client (simula la latenza)
    private Map<Client, Integer> preferenceList; // Client -> Punteggio di preferenza

    private static final AtomicInteger idGenerator = new AtomicInteger(1);  // Generatore ID

    public NodoFog(int capacity, int executionTime, int distance) {
        this.id = idGenerator.getAndIncrement();
        this.capacity = capacity;
        this.currentLoad = 0;
        this.executionTime = executionTime;
        this.distance = distance;
        this.preferenceList = new HashMap<>();
    }

    // Genera una lista di preferenza dei nodi verso i client
    public void calculatePreferenceList(List<Client> clients) {
        for (Client client : clients) {
            int preferenceScore = client.getQueueTime() + client.getTaskSize() + this.distance; // tempo di coda + esecuzione che dipende da quanti task + distanza
            preferenceList.put(client, preferenceScore);
        }
    }

    // Restituisce la lista di preferenza ordinata
    public List<Client> getPreferenceList() {
        List<Map.Entry<Client, Integer>> sortedEntries = new ArrayList<>(preferenceList.entrySet());
        sortedEntries.sort(Map.Entry.comparingByValue());
        List<Client> sortedClients = new ArrayList<>();
        for (Map.Entry<Client, Integer> entry : sortedEntries) {
            sortedClients.add(entry.getKey());
        }
        return sortedClients;
    }

    public int getId() {
        return id;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getCurrentLoad() {
        return currentLoad;
    }

    public void setCurrentLoad(int currentLoad) {
        this.currentLoad = currentLoad;
    }

    public int getExecutionTime() {
        return executionTime;
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return "NodoFog{id=" + id +
                ", capacity=" + capacity +
                ", currentLoad=" + currentLoad +
                ", executionTime=" + executionTime +
                ", distance=" + distance +
                '}';
    }
}
