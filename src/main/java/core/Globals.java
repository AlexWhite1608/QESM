package core;

public class Globals {
    public static final int TIME_SLOT_DURATION = 10; // Durata di uno slot temporale
    public static final int NUM_TIME_SLOTS = 20; // Numero di slot temporali

    public static final int NUM_NODI = 5;
    public static final int NUM_CLIENTS = 20;
    public static final double TRANSMISSION_SPEED = 10.0; // Velocità di trasmissione in unità di spazio per unità di tempo

    public static final int NODE_CAPACITY_MEAN = 5;   // Media della capacità di un nodo
    public static final int NODO_COMPUTATION_CAPABILITY_MIN = 50; // Capacità computazionale minima dei nodi
    public static final int NODO_COMPUTATION_CAPABILITY_MAX = 80; // Capacità computazionale massima dei nodi

    public static final int CLIENT_TASK_SIZE_MEAN_MIN = 20; // Minimo del valor medio dei task generati
    public static final int CLIENT_TASK_SIZE_MEAN_MAX = 50; // Massimo del valor medio dei task generati

    public static final boolean ALLOW_CLIENT_ARRIVAL_DEPARTURE = true; // Abilita l'arrivo e l'uscita dei client
    public static final double ARRIVAL_PROBABILITY = 0.5; // Probabilità di arrivo di un client
    public static final double DEPARTURE_PROBABILITY = 0.5; // Probabilità di uscita di un client
}
