package simulation;

import core.Client;
import core.NodoFog;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

public class SimulationPlot {

    // Metodo per plottare il tempo di attesa massimo dei client per ogni time slot
    public static void plotMaxQueueTime(Map<Integer, Map<Client, Integer>> maxQueueTimePerSlot) {
        // Creazione del dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map.Entry<Integer, Map<Client, Integer>> entry : maxQueueTimePerSlot.entrySet()) {
            int timeSlot = entry.getKey();
            Map<Client, Integer> clientMaxQueue = entry.getValue();

            if (!clientMaxQueue.isEmpty()) {
                for (Map.Entry<Client, Integer> clientEntry : clientMaxQueue.entrySet()) {
                    int maxQueueTime = clientEntry.getValue();
                    dataset.addValue(maxQueueTime, "Max QueueTime", String.valueOf(timeSlot));
                }
            }
        }

        JFreeChart lineChart = ChartFactory.createLineChart(
                "Max Client QueueTime",
                "Time Slot",
                "QueueTime",
                dataset,
                org.jfree.chart.plot.PlotOrientation.VERTICAL,
                false, true, false
        );

        // Personalizzazione del grafico
        CategoryPlot plot = lineChart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        //lineChart.getLegend().setPosition(RectangleEdge.BOTTOM);

        JFrame frame = new JFrame("Simulation Plot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        ChartPanel chartPanel = new ChartPanel(lineChart);
        frame.add(chartPanel);

        frame.setVisible(true);
    }

    // Metodo per plottare capacità computazionale e ritardo accumulato per ogni nodo
    public static void plotComputationAndDelay(Map<Integer, Map<NodoFog, Integer>> computationCapacityPerSlot,
                                               Map<Integer, Map<NodoFog, Integer>> delayPerSlot) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map.Entry<Integer, Map<NodoFog, Integer>> entry : delayPerSlot.entrySet()) {
            int timeSlot = entry.getKey();
            Map<NodoFog, Integer> delayMap = entry.getValue();
            Map<NodoFog, Integer> computationMap = computationCapacityPerSlot.get(timeSlot);

            for (Map.Entry<NodoFog, Integer> delayEntry : delayMap.entrySet()) {
                NodoFog nodo = delayEntry.getKey();
                int delay = delayEntry.getValue();
                int computation = computationMap.getOrDefault(nodo, 0);

                dataset.addValue(delay, "NodoFog " + nodo.getId() + " (Pot: " + computation + ", " + "Cap: " + nodo.getMaxQueueSize() + ")", String.valueOf(timeSlot));
            }
        }

        JFreeChart lineChart = ChartFactory.createLineChart(
                "NodoFog Delay",
                "Time Slot",
                "Delay",
                dataset,
                org.jfree.chart.plot.PlotOrientation.VERTICAL,
                true, true, false
        );

        // Personalizzazione del grafico
        CategoryPlot plot = lineChart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        //lineChart.getLegend().setPosition(RectangleEdge.BOTTOM);

        LineAndShapeRenderer renderer = new LineAndShapeRenderer(true, true);
        lineChart.getCategoryPlot().setRenderer(renderer);
//        renderer.setBaseItemLabelGenerator((dataset1, row, column) -> {
//            String label = dataset1.getRowKey(row).toString();
//            return label.substring(label.indexOf("Cap: ") + 5, label.length() - 1); // Mostra solo la capacità
//        });
        renderer.setBaseItemLabelsVisible(true);
        renderer.setBaseShapesVisible(true);
        renderer.setDrawOutlines(true);

        JFrame frame = new JFrame("NodoFog Computation and Delay Plot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        ChartPanel chartPanel = new ChartPanel(lineChart);
        frame.add(chartPanel);

        frame.setVisible(true);
    }

    // Metodo per plottare il numero di swap per ogni time slot
    public static void plotStability(List<Integer> swapsPerTimeSlot) {
        // Dataset per gli swap per time slot
        DefaultCategoryDataset barDataset = new DefaultCategoryDataset();
        // Dataset per il totale cumulativo degli swap
        DefaultCategoryDataset lineDataset = new DefaultCategoryDataset();

        int cumulativeSwaps = 0;

        for (int i = 0; i < swapsPerTimeSlot.size(); i++) {
            int timeSlot = i + 1;
            int swaps = swapsPerTimeSlot.get(i);
            cumulativeSwaps += swaps;

            barDataset.addValue(swaps, "Swap per Time Slot", String.valueOf(timeSlot));
            lineDataset.addValue(cumulativeSwaps, "Totale Swap", String.valueOf(timeSlot));
        }

        // Creazione del grafico combinato
        JFreeChart combinedChart = ChartFactory.createBarChart(
                "Swap per Time Slot e Swap Totali",
                "Time Slot",
                "Numero di Swap",
                barDataset,
                org.jfree.chart.plot.PlotOrientation.VERTICAL,
                true, true, false
        );

        // Ottieni il plot del grafico
        CategoryPlot plot = combinedChart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);

        // Configura il renderer per il grafico a barre
        BarRenderer barRenderer = new BarRenderer();
        barRenderer.setSeriesPaint(0, new Color(79, 129, 189)); // Colore per le barre
        barRenderer.setShadowVisible(false); // Disabilita le ombre
        barRenderer.setMaximumBarWidth(0.03); // Riduci la larghezza delle barre

        plot.setRenderer(0, barRenderer);

        // Aggiungi il dataset per la linea cumulativa
        LineAndShapeRenderer lineRenderer = new LineAndShapeRenderer();
        lineRenderer.setSeriesPaint(0, new Color(192, 80, 77)); // Colore per la linea
        lineRenderer.setBaseShapesVisible(true); // Mostra i punti sulla linea
        plot.setDataset(1, lineDataset);
        plot.mapDatasetToRangeAxis(1, 0);
        plot.setRenderer(1, lineRenderer);

        // Creazione della finestra per visualizzare il grafico
        JFrame frame = new JFrame("Stabilità del Sistema");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);

        ChartPanel chartPanel = new ChartPanel(combinedChart);
        frame.add(chartPanel);

        frame.setVisible(true);
    }

    // Metodo per plottare le statistiche dei nodi fog
    public static void plotNodeStatistics(List<NodoFog> nodi) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Aggiunta dei dati al dataset
        for (NodoFog nodo : nodi) {
            dataset.addValue(nodo.getTotalExecutionTime(), "Tempo Totale di Esecuzione", "Nodo " + nodo.getId() + " (Ser: " + nodo.getTotalServices() + ")");
            dataset.addValue(nodo.getTotalDelayTime(), "Ritardo Accumulato", "Nodo " + nodo.getId() + " (Ser: " + nodo.getTotalServices() + ")");
        }

        // Creazione del grafico a barre
        JFreeChart barChart = ChartFactory.createBarChart(
                "NodoFog Esecuzione/Ritardo",
                "Nodi",
                "Valori",
                dataset,
                org.jfree.chart.plot.PlotOrientation.VERTICAL,
                true, true, false
        );

        CategoryPlot plot = barChart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);

        BarRenderer renderer = new BarRenderer();
        renderer.setSeriesPaint(0, new Color(155, 187, 89)); // Colore per il tempo totale di esecuzione
        renderer.setSeriesPaint(1, new Color(192, 80, 77));  // Colore per il ritardo accumulato

        // Rimuovi l'effetto riflesso
        renderer.setBarPainter(new BarRenderer().getBarPainter());
        renderer.setShadowVisible(false); // Disabilita le ombre
        renderer.setItemMargin(0.05); // Riduci la distanza tra le barre (default è 0.2)
        renderer.setMaximumBarWidth(0.03); // Riduci la larghezza delle barre

        plot.setRenderer(renderer);

        // Creazione della finestra per il grafico
        JFrame frame = new JFrame("Statistiche dei Nodi Fog");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        ChartPanel chartPanel = new ChartPanel(barChart);
        frame.add(chartPanel);

        frame.setVisible(true);
    }



    //FIXME: Metodo per plottare la stabilità degli accoppiamenti nel tempo
    public static void plotStabilityPercentage(List<Double> stabilityPercentages) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (int i = 0; i < stabilityPercentages.size(); i++) {
            dataset.addValue(stabilityPercentages.get(i), "Stabilità", String.valueOf(i + 2));
        }

        JFreeChart lineChart = ChartFactory.createLineChart(
                "Stabilità degli Accoppiamenti nel Tempo",
                "Time Slot",
                "Percentuale di Stabilità (%)",
                dataset,
                org.jfree.chart.plot.PlotOrientation.VERTICAL,
                false, true, false
        );

        CategoryPlot plot = lineChart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);

        JFrame frame = new JFrame("Grafico Stabilità Matching");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        ChartPanel chartPanel = new ChartPanel(lineChart);
        frame.add(chartPanel);

        frame.setVisible(true);
    }


}
