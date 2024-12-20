package core;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class NodoFog {
    private int id;
    private int capacity; // Capacità computazionale del nodo
    private int currentLoad; // Carico attuale sul nodo (tempo di coda)
    private int executionTime; // Tempo stimato di esecuzione per un task
    int x, y;
    private Map<Client, Integer> preferenceList; // Client -> Punteggio di preferenza

    private static final AtomicInteger idGenerator = new AtomicInteger(1);  // Generatore ID

    public NodoFog(int capacity) {
        this.id = idGenerator.getAndIncrement();
        this.capacity = capacity;
        this.currentLoad = 0;
        this.executionTime = 0;
        Random random = new Random();
        this.x = random.nextInt(100);
        this.y = random.nextInt(100);
        this.preferenceList = new HashMap<>();
    }

    // Genera una lista di preferenza dei nodi verso i client
    public void calculatePreferenceList(List<Client> clients) {
        for (Client client : clients) {
            int preferenceScore = client.getQueueTime() + this.executionTime + (int) calculateDistanceTo(client); // tempo di coda + esecuzione che dipende da quanti task + distanza
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

    // Calcola la distanza euclidea con il client specificato
    public double calculateDistanceTo(Client client) {
        return Math.sqrt(Math.pow(this.x - client.getX(), 2) + Math.pow(this.y - client.getY(), 2));
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

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setExecutionTime(int executionTime) {
        this.executionTime = executionTime;
    }

    @Override
    public String toString() {
        return "NodoFog{id=" + id +
                ", capacity=" + capacity +
                ", currentLoad=" + currentLoad +
                ", executionTime=" + executionTime +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
