package core;

public class Globals {
    public static final int TIME_SLOT_DURATION = 10; // Durata di uno slot temporale
    public static final int NUM_TIME_SLOTS = 20; // Numero di slot temporali
    public static final int NUM_NODI = 5;
    public static final int NUM_CLIENTS = 10;
    public static final int NODO_COMPUTATION_CAPABILITY_MIN = 30; // Capacità computazionale minima dei nodi
    public static final int NODO_COMPUTATION_CAPABILITY_MAX = 70; // Capacità computazionale massima dei nodi
    public static final int CLIENT_TASK_SIZE_MEAN_MIN = 20; // Minimo del valor medio dei task generati
    public static final int CLIENT_TASK_SIZE_MEAN_MAX = 50; // Massimo del valor medio dei task generati
    public static final double ARRIVAL_DEPARTURE_PROBABILITY = 0.5; // Probabilità di arrivo/partenza di un client
    public static final int NODE_CAPACITY_MEAN = 4;   // Media della capacità di un nodo
}
