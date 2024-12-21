package core;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class NodoFog {
    private int id;
    private int computationCapability;  // Potenza di calcolo del nodo
    private int totalExecutionTime; // Tempo totale di esecuzione accumulato eseguendo i vari task
    private int totalDelayTime; // Tempo totale di ritardo accumulato (quando l'esecuzione dei task ha sforato il time slot
    private int maxQueueSize; // Dimensione massima della coda
    int x, y;
    private Map<Client, Integer> preferenceList; // Client -> Punteggio di preferenza
    private Queue<Task> taskQueue; // Coda dei task
    private Queue<Client> clientsQueue; // Coda dei client

    private static final AtomicInteger idGenerator = new AtomicInteger(1);  // Generatore ID

    public NodoFog(int computationCapability, int maxQueueSize) {
        this.id = idGenerator.getAndIncrement();
        this.computationCapability = computationCapability;
        this.maxQueueSize = maxQueueSize;
        this.totalExecutionTime = 0;
        this.totalDelayTime = 0;
        Random random = new Random();
        this.taskQueue = new LinkedList<>();
        this.clientsQueue = new LinkedList<>();
        this.x = random.nextInt(100);
        this.y = random.nextInt(100);
        this.preferenceList = new HashMap<>();
    }

    // Genera una lista di preferenza dei nodi verso i client
    public void calculatePreferenceList(List<Client> clients) {
        for (Client client : clients) {
            int preferenceScore = -client.getQueueTime() + client.getTotalTaskExecutionTime() + (int) calculateDistanceTo(client);  //FIXME preferisco i client che hanno aspettato di più e con il tempo di esecuzione più basso
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

    // Processa i task nella coda
    public void processTasks(int timeSlotDuration) {
        int timeLeft = timeSlotDuration;

        while (!clientsQueue.isEmpty() && timeLeft > 0) {
            Client currentClient = clientsQueue.poll();
            List<Task> clientTasks = currentClient.getTaskList();

            // Calcola il tempo totale richiesto per eseguire tutti i task del client
            int clientTotalTime = clientTasks.stream().mapToInt(Task::getRequiredTime).sum();

            // Riduci il tempo richiesto in base alla capacità di calcolo del nodo
            int adjustedExecutionTime = (int) Math.ceil(clientTotalTime / (double) computationCapability);

            if (adjustedExecutionTime <= timeLeft) {
                // Il task set del client è completato entro il time slot
                totalExecutionTime += adjustedExecutionTime;
                timeLeft -= adjustedExecutionTime;

                taskQueue.removeAll(clientTasks);
            } else {
                // Il task set del client richiede più tempo del time slot
                totalExecutionTime += adjustedExecutionTime;
                totalDelayTime += adjustedExecutionTime - timeLeft; // Aggiungi il ritardo
                timeLeft = 0;

                taskQueue.removeAll(clientTasks);

                break; // Il time slot è esaurito, termina l'elaborazione
            }

            // Aggiorna il tempo di coda per i client rimanenti nella coda
            for (Client remainingClient : clientsQueue) {
                remainingClient.incrementQueueTime(adjustedExecutionTime);
            }
        }
    }


    public boolean isQueueFull() {
        return clientsQueue.size() >= maxQueueSize;
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getComputationCapability() {
        return computationCapability;
    }

    public int getTotalExecutionTime() {
        return totalExecutionTime;
    }

    public int getTotalDelayTime() {
        return totalDelayTime;
    }

    public Queue<Task> getTaskQueue() {
        return taskQueue;
    }

    public Queue<Client> getClientsQueue() {
        return clientsQueue;
    }

    @Override
    public String toString() {
        return "NodoFog{id=" + id +
                ", computationCapability=" + computationCapability +
                ", totalExecutionTime=" + totalExecutionTime +
                ", totalDelayTime=" + totalDelayTime +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
