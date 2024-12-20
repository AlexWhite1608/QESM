package core;

public class Task {
    private Client client;
    private int remainingTime;

    public Task(Client client, int remainingTime) {
        this.client = client;
        this.remainingTime = remainingTime;
    }

    public Client getClient() {
        return client;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void decrementRemainingTime() {
        this.remainingTime--;
    }

    @Override
    public String toString() {
        return "Task{client=" + client.getId() +
                ", remainingTime=" + remainingTime +
                '}';
    }
}

