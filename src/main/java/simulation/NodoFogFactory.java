package simulation;

import core.NodoFog;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NodoFogFactory {
    private final int minCapacity;
    private final int maxCapacity;

    public NodoFogFactory(int minCapacity, int maxCapacity) {
        this.minCapacity = minCapacity;
        this.maxCapacity = maxCapacity;
    }

    /**
     * Genera una lista di nodi con capacità casuali entro l'intervallo.
     */
    public List<NodoFog> generateNodi(int numNodi) {
        Random random = new Random();
        List<NodoFog> nodi = new ArrayList<>();
        for (int i = 0; i < numNodi; i++) {
            int capacity = random.nextInt(maxCapacity - minCapacity + 1) + minCapacity;
            nodi.add(new NodoFog(capacity));
        }
        return nodi;
    }
}
