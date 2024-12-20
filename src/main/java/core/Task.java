package core;

import java.util.Random;

public class Task {
    private Client client;
    private int requiredTime;   // tempo richiesto per completare il task
    Random random = new Random();

    public Task(Client client) {
        this.client = client;
        this.requiredTime = random.nextInt(5) + 1; // tempo di esecuzione variabile per ogni task (1-5)
    }

    public Client getClient() {
        return client;
    }

    public int getRequiredTime() {
        return requiredTime;
    }
}

