package core;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Client {
    private int id;
    private int arrivalTime;
    private int taskSize;
    private int queueTime;
    int x, y;
    private NodoFog assignedNodo;
    private Map<NodoFog, Integer> preferenceList; // Nodo -> Punteggio di preferenza

    private static final AtomicInteger idGenerator = new AtomicInteger(1);  // Generatore ID

    public Client(int arrivalTime, int taskSize) {
        this.id = idGenerator.getAndIncrement();
        this.arrivalTime = arrivalTime;
        this.taskSize = taskSize;
        this.queueTime = 0;
        Random random = new Random();
        this.x = random.nextInt(100);
        this.y = random.nextInt(100);
        this.assignedNodo = null;
        this.preferenceList = new HashMap<>();
    }

    // Genera una lista di preferenza dei client verso i nodi
    public void calculatePreferenceList(List<NodoFog> nodi) {
        for (NodoFog nodo : nodi) {
            int preferenceScore = nodo.getCurrentLoad() + (int) calculateDistanceTo(nodo);   // tempo di coda (load) + tempo di raggiungimento (distance)
            preferenceList.put(nodo, preferenceScore);
        }
    }

    // Restituisce la lista di preferenza ordinata
    public List<NodoFog> getPreferenceList() {
        List<Map.Entry<NodoFog, Integer>> sortedEntries = new ArrayList<>(preferenceList.entrySet());
        sortedEntries.sort(Map.Entry.comparingByValue());
        List<NodoFog> sortedNodi = new ArrayList<>();
        for (Map.Entry<NodoFog, Integer> entry : sortedEntries) {
            sortedNodi.add(entry.getKey());
        }
        return sortedNodi;
    }

    // Calcola la distanza euclidea con il nodo specificato
    public double calculateDistanceTo(NodoFog node) {
        return Math.sqrt(Math.pow(this.x - node.getX(), 2) + Math.pow(this.y - node.getY(), 2));
    }

    public int getId() {
        return id;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getTaskSize() {
        return taskSize;
    }

    public int getQueueTime() {
        return queueTime;
    }

    public void setQueueTime(int queueTime) {
        this.queueTime = queueTime;
    }

    public NodoFog getAssignedNodo() {
        return assignedNodo;
    }

    public void setAssignedNodo(NodoFog nodo) {
        this.assignedNodo = nodo;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Client{id=" + id +
                ", arrivalTime=" + arrivalTime +
                ", taskSize=" + taskSize +
                ", queueTime=" + queueTime +
                ", assignedNodo=" + (assignedNodo != null ? assignedNodo.getId() : "None") +
                '}';
    }
}

