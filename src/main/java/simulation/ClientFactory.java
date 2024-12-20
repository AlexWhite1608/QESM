package simulation;

import core.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ClientFactory {
    private final int minTaskSizeMean;
    private final int maxTaskSizeMean;

    public ClientFactory(int minTaskSizeMean, int maxTaskSizeMean) {
        this.minTaskSizeMean = minTaskSizeMean;
        this.maxTaskSizeMean = maxTaskSizeMean;
    }

    /**
     * Genera una lista di client con valori medi di task casuali entro l'intervallo.
     */
    public List<Client> generateClients(int numClients) {
        Random random = new Random();
        List<Client> clients = new ArrayList<>();
        for (int i = 0; i < numClients; i++) {
            int meanTaskSize = random.nextInt(maxTaskSizeMean - minTaskSizeMean + 1) + minTaskSizeMean;
            clients.add(new Client(0, meanTaskSize)); // Tutti i client iniziali arrivano al tempo 0
        }
        return clients;
    }
}
