package core;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Client {
    private int id;
    private Integer arrivalTime; // time slot di arrivo
    private Integer departureTime; // time slot di uscita
    private int meanTaskSize;   // valor medio del numero di task generati
    private int queueTime;  // tempo trascorso in coda
    int x, y;
    private NodoFog assignedNodo;
    private Map<NodoFog, Integer> preferenceList; // Nodo -> Punteggio di preferenza
    private List<Task> taskList = new ArrayList<>(); // Lista dei task

    private static final AtomicInteger idGenerator = new AtomicInteger(1);  // Generatore ID

    public Client(int arrivalTime, Integer departureTime, int meanTaskSize) {
        this.id = idGenerator.getAndIncrement();
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.meanTaskSize = meanTaskSize;
        this.queueTime = 0;
        Random random = new Random();
        this.x = random.nextInt(100);
        this.y = random.nextInt(100);
        this.assignedNodo = null;
        this.preferenceList = new HashMap<>();
    }

    //FIXME: da rivedere (vecchia versione)
//    public void calculatePreferenceList(List<NodoFog> nodi) {
//        for (NodoFog nodo : nodi) {
//            int preferenceScore = nodo.getTotalDelayTime() + (int) calculateDistanceTo(nodo) + nodo.getClientsQueue().size();  // tempo di ritardo totale accumulato + tempo di raggiungimento (distance)
//            preferenceList.put(nodo, preferenceScore);
//        }
//    }

    // Genera una lista di preferenza dei client verso i nodi
    public void calculatePreferenceList(List<NodoFog> nodi) {
        for (NodoFog nodo : nodi) {
            int loadPenalty = nodo.getClientsQueue().size(); // Penalità per coda lunga
            int delayPenalty = nodo.getTotalDelayTime(); // Penalità per ritardo accumulato
            int popularityPenalty = nodo.getTotalServices(); // Penalità per nodi popolari
            int preferenceScore = delayPenalty + (int) calculateDistanceTo(nodo) + loadPenalty + popularityPenalty;
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

    // Genera un numero di task in base alla distribuzione normale con media meanTaskSize
    public void generateTasks() {
        Random random = new Random();
        int numGeneratedTasks = (int) Math.max(1, random.nextGaussian() * 2 + meanTaskSize);

        // Genero i task e li metto nella lista
        for (int i = 0; i < numGeneratedTasks; i++) {
            Task task = new Task(this);
            taskList.add(task);
        }
    }

    //TODO: elimina, usato solo per i test
    public void generateTasksForTest(List<Task> predefinedTasks) {
        this.taskList.clear();
        this.taskList.addAll(predefinedTasks);
    }

    // Ritorna il tempo totale necessario per completare tutti i task
    public int getTotalTaskExecutionTime() {
        return taskList.stream().mapToInt(Task::getRequiredTime).sum();
    }

    // Calcola la distanza euclidea con il nodo specificato
    public double calculateDistanceTo(NodoFog node) {
        return Math.sqrt(Math.pow(this.x - node.getX(), 2) + Math.pow(this.y - node.getY(), 2));
    }

    public void incrementQueueTime(int increment) {
        queueTime += increment;
    }

    public int getId() {
        return id;
    }

    public Integer getArrivalTime() {
        return arrivalTime;
    }

    public Integer getDepartureTime() {
        return departureTime;
    }

    public int getMeanTaskSize() {
        return meanTaskSize;
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

    public void setDepartureTime(Integer departureTime) {
        this.departureTime = departureTime;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    @Override
    public String toString() {
        return "Client{id=" + id +
                ", arrivalTime=" + arrivalTime +
                ", taskListSize=" + taskList.size() +
                ", queueTime=" + queueTime +
                ", assignedNodo=" + (assignedNodo != null ? assignedNodo.getId() : "None") +
                '}';
    }
}

