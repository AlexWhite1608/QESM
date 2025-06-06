package utils;

import core.Client;
import core.NodoFog;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlotManager {

    // Metodo per plottare il tempo di attesa massimo dei client per ogni time slot
    private static ChartPanel createMaxQueueTimeChart(Map<Integer, Map<Client, Integer>> maxQueueTimePerSlot) {
        // Creazione del dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map.Entry<Integer, Map<Client, Integer>> entry : maxQueueTimePerSlot.entrySet()) {
            int timeSlot = entry.getKey();
            Map<Client, Integer> clientMaxQueue = entry.getValue();

            if (!clientMaxQueue.isEmpty()) {
                for (Map.Entry<Client, Integer> clientEntry : clientMaxQueue.entrySet()) {
                    int maxQueueTime = clientEntry.getValue();
                    dataset.addValue(maxQueueTime, "Tempo di attesa massimo", String.valueOf(timeSlot));
                }
            }
        }

        JFreeChart lineChart = ChartFactory.createLineChart(
                "Tempo di attesa massimo per i client",
                "Time Slot",
                "Tempo di attesa",
                dataset,
                org.jfree.chart.plot.PlotOrientation.VERTICAL,
                false, true, false
        );

        // Personalizzazione del grafico
        CategoryPlot plot = lineChart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);

        return new ChartPanel(lineChart);
    }

    // Metodo per plottare capacità computazionale e ritardo accumulato per ogni nodo
    private static ChartPanel createComputationDelayChart(Map<Integer, Map<NodoFog, Integer>> computationCapacityPerSlot,
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

                dataset.addValue(delay, "Nodo " + nodo.getId() + " (Pot: " + computation + ", " + "Cap: " + nodo.getMaxQueueSize() + ")", String.valueOf(timeSlot));
            }
        }

        JFreeChart lineChart = ChartFactory.createLineChart(
                "Ritardo accumulato dai nodi",
                "Time Slot",
                "Ritardo",
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
        renderer.setBaseItemLabelsVisible(true);
        renderer.setDrawOutlines(true);

        return new ChartPanel(lineChart);

    }

    // Metodo per plottare il numero di swap per ogni time slot
    private static ChartPanel createStabilityChart(List<Integer> swapsPerTimeSlot) {
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
        plot.setDataset(1, lineDataset);
        plot.mapDatasetToRangeAxis(1, 0);
        plot.setRenderer(1, lineRenderer);

        return new ChartPanel(combinedChart);
    }

    // Metodo per plottare le statistiche dei nodi fog
    private static ChartPanel createNodeExecutionChart(Map<Integer, Map<NodoFog, Integer>> executionTimesPerSlot) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Aggiunta dei dati al dataset
        for (Map.Entry<Integer, Map<NodoFog, Integer>> entry : executionTimesPerSlot.entrySet()) {
            int timeSlot = entry.getKey();
            Map<NodoFog, Integer> executionTimes = entry.getValue();

            for (Map.Entry<NodoFog, Integer> nodoEntry : executionTimes.entrySet()) {
                NodoFog nodo = nodoEntry.getKey();
                int executionTime = nodoEntry.getValue();

                // Ogni nodo ha una propria serie
                dataset.addValue(executionTime, "Nodo " + nodo.getId(), String.valueOf(timeSlot));
            }
        }

        // Creazione del grafico a linee
        JFreeChart lineChart = ChartFactory.createLineChart(
                "Tempo di Esecuzione per Nodo",
                "Time Slot",
                "Tempo di Esecuzione",
                dataset,
                org.jfree.chart.plot.PlotOrientation.VERTICAL,
                true, true, false
        );

        // Personalizzazione del grafico
        CategoryPlot plot = lineChart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);

        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setDrawOutlines(true); // Disegna i bordi attorno ai punti
        plot.setRenderer(renderer);

        return new ChartPanel(lineChart);
    }

    // Metodo per plottare i tempi medi di attesa e di esecuzione dei client
    private static JPanel createClientAveragesPanel(Map<Integer, Double> queueTimePerSlot, Map<Integer, List<Client>> clientsPerSlot) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map.Entry<Integer, Double> entry : queueTimePerSlot.entrySet()) {
            int timeSlot = entry.getKey();
            double totalQueueTime = entry.getValue();

            // Ottieni il numero di client attivi in questo time slot
            int clientCount = clientsPerSlot.getOrDefault(timeSlot, new ArrayList<>()).size();

            // Evita divisioni per zero
            double averageQueueTime = clientCount > 0 ? totalQueueTime / clientCount : 0;

            // Label con numero di client
            String label = timeSlot + " (clients: " + clientCount + ")";

            dataset.addValue(averageQueueTime, "Tempo Medio di Attesa", label);
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Tempo Medio di Attesa Totale Client",
                "Time Slot",
                "Tempo Medio di Attesa",
                dataset,
                org.jfree.chart.plot.PlotOrientation.VERTICAL,
                false, true, false
        );

        customizeChart(chart, new Color(192, 80, 77));

        JPanel panel = new JPanel(new GridLayout(1, 1));
        panel.add(new ChartPanel(chart));

        return panel;
    }


    private static void customizeChart(JFreeChart chart, Color color) {
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);

        // Personalizzazione del renderer
        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setSeriesPaint(0, color); // Colore specifico
        renderer.setDrawOutlines(true);

        plot.setRenderer(renderer);

        // Personalizzazione delle etichette sull'asse X
        CategoryAxis axis = plot.getDomainAxis();
        axis.setTickLabelFont(new Font("Arial", Font.PLAIN, 10));
        axis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
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

    private static ChartPanel createAvgTaskCompletionTimeChart(Map<Integer, Double> avgTaskCompletionTimePerSlot) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map.Entry<Integer, Double> entry : avgTaskCompletionTimePerSlot.entrySet()) {
            int timeSlot = entry.getKey();
            double avgCompletionTime = entry.getValue();
            dataset.addValue(avgCompletionTime, "Tempo Medio di Completamento", String.valueOf(timeSlot));
        }

        JFreeChart lineChart = ChartFactory.createLineChart(
                "Tempo Medio di Completamento per Task",
                "Time Slot",
                "Tempo Medio (unità)",
                dataset,
                org.jfree.chart.plot.PlotOrientation.VERTICAL,
                false, true, false
        );

        CategoryPlot plot = lineChart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);

        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setSeriesPaint(0, new Color(56, 142, 60)); // Colore verde
        renderer.setDrawOutlines(true);
        plot.setRenderer(renderer);

        return new ChartPanel(lineChart);
    }

    public static void plotAll(Map<Integer, Map<Client, Integer>> maxQueueTimePerSlot,
                               List<Integer> swapsPerTimeSlot,
                               List<NodoFog> nodi,
                               Map<Integer, Map<NodoFog, Integer>> computationCapacityPerSlot,
                               Map<Integer, Map<NodoFog, Integer>> delayPerSlot,
                               Map<Integer, List<Client>> clientsPerSlot,
                               Map<Integer, Map<NodoFog, Integer>> executionTimesPerSlot,
                               Map<Integer, Double> queueTimePerSlot,
                               Map<Integer, Double> avgTaskCompletionTimePerSlot
    ) {

        // Crea il JFrame principale
        JFrame frame = new JFrame("Grafici");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);

        // Creazione dei pannelli di grafico
        ChartPanel maxQueueTimeChart = createMaxQueueTimeChart(maxQueueTimePerSlot);
        ChartPanel stabilityChart = createStabilityChart(swapsPerTimeSlot);
        ChartPanel nodeStatisticsChart = createNodeExecutionChart(executionTimesPerSlot);
        ChartPanel computationDelayChart = createComputationDelayChart(computationCapacityPerSlot, delayPerSlot);
        JPanel clientAveragesPanel = createClientAveragesPanel(queueTimePerSlot, clientsPerSlot); // Aggiornato

        // Panel stabilità
        JPanel tab1Panel = new JPanel(new GridLayout(1, 2));
        tab1Panel.add(maxQueueTimeChart);
        tab1Panel.add(stabilityChart);

        // Panel nodi
        JPanel tab2Panel = new JPanel(new GridLayout(1, 2));
        tab2Panel.add(nodeStatisticsChart);
        tab2Panel.add(computationDelayChart);

        JPanel tab3Panel = new JPanel(new GridLayout(1, 2));
        tab3Panel.add(clientAveragesPanel);
        tab3Panel.add(createAvgTaskCompletionTimeChart(avgTaskCompletionTimePerSlot)); // Aggiungi qui

        // Creazione delle schede (tabs)
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Stabilità", tab1Panel);
        tabbedPane.addTab("Nodi", tab2Panel);
        tabbedPane.addTab("Client", tab3Panel);

        // Aggiunta delle schede al frame
        frame.add(tabbedPane);

        // Visualizzazione
        frame.setVisible(true);
    }
}
