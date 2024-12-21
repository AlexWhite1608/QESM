package simulation;

import core.NodoFog;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NodoFogFactory {
    private final int minComputationalCapability;
    private final int maxComputationCapability;

    public NodoFogFactory(int min, int max) {
        this.minComputationalCapability = min;
        this.maxComputationCapability = max;
    }

    /**
     * Genera una lista di nodi con capacità casuali entro l'intervallo.
     */
    public List<NodoFog> generateNodi(int numNodi) {
        Random random = new Random();
        List<NodoFog> nodi = new ArrayList<>();
        for (int i = 0; i < numNodi; i++) {
            int capacity = random.nextInt(maxComputationCapability - minComputationalCapability + 1) + minComputationalCapability;
            nodi.add(new NodoFog(capacity));
        }
        return nodi;
    }
}
