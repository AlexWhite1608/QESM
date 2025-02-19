package utils;

import core.NodoFog;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NodoFogFactory {
    private final int minComputationalCapability;
    private final int maxComputationCapability;
    private final int capacityMean;

    public NodoFogFactory(int min, int max, int capacityMean) {
        this.minComputationalCapability = min;
        this.maxComputationCapability = max;
        this.capacityMean = capacityMean;
    }

    /**
     * Genera una lista di nodi con capacità casuali entro l'intervallo.
     */
    public List<NodoFog> generateNodi(int numNodi) {
        Random random = new Random();
        List<NodoFog> nodi = new ArrayList<>();
        for (int i = 0; i < numNodi; i++) {
            int computationCapability = random.nextInt(maxComputationCapability - minComputationalCapability + 1) + minComputationalCapability;
            int capacity = (int) (random.nextGaussian() * 1 + capacityMean);
            nodi.add(new NodoFog(computationCapability, capacity));
        }
        return nodi;
    }
}
