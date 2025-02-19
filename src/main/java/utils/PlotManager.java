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
    private static JPanel createClientAveragesPanel(Map<Integer, List<Client>> clientsPerSlot) {
        Map<Integer, double[]> averages = new HashMap<>();

        // Calcolo delle medie e del numero di client
        for (Map.Entry<Integer, List<Client>> entry : clientsPerSlot.entrySet()) {
            int timeSlot = entry.getKey();
            List<Client> clients = entry.getValue();

            double totalQueueTime = 0.0;
            double totalRequiredTime = 0.0;

            for (Client client : clients) {
                totalQueueTime += client.getQueueTime();
                totalRequiredTime += client.getTotalTaskExecutionTime();
            }

            double averageQueueTime = clients.isEmpty() ? 0 : totalQueueTime / clients.size();
            double averageRequiredTime = clients.isEmpty() ? 0 : totalRequiredTime / clients.size();

            averages.put(timeSlot, new double[]{averageQueueTime, averageRequiredTime});
        }

        // Creazione dei dataset con etichette personalizzate
        DefaultCategoryDataset queueTimeDataset = new DefaultCategoryDataset();
        DefaultCategoryDataset requiredTimeDataset = new DefaultCategoryDataset();

        for (Map.Entry<Integer, List<Client>> entry : clientsPerSlot.entrySet()) {
            int timeSlot = entry.getKey();
            List<Client> clients = entry.getValue();

            int clientCount = clients.size(); // Ottieni il numero di client
            String label = timeSlot + " (clients:" + clientCount + ")";

            double[] values = averages.getOrDefault(timeSlot, new double[]{0, 0});

            queueTimeDataset.addValue(values[0], "Tempo di Attesa Medio", label);
            requiredTimeDataset.addValue(values[1], "Tempo Richiesto Medio", label);
        }

        // Creazione dei grafici
        JFreeChart queueTimeChart = ChartFactory.createLineChart(
                "Tempo di Attesa Medio",
                "Time Slot",
                "Tempo",
                queueTimeDataset,
                org.jfree.chart.plot.PlotOrientation.VERTICAL,
                false, true, false
        );

        JFreeChart requiredTimeChart = ChartFactory.createLineChart(
                "Tempo di Computazione Richiesto Medio",
                "Time Slot",
                "Tempo",
                requiredTimeDataset,
                org.jfree.chart.plot.PlotOrientation.VERTICAL,
                false, true, false
        );

        // Personalizzazione dei grafici
        customizeChart(queueTimeChart, new Color(192, 80, 77)); // Rosso per il tempo in attesa
        customizeChart(requiredTimeChart, new Color(155, 187, 89)); // Verde per il tempo richiesto

        // Creazione del pannello con i due grafici
        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.add(new ChartPanel(queueTimeChart));
        panel.add(new ChartPanel(requiredTimeChart));

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

    public static void plotAll(Map<Integer, Map<Client, Integer>> maxQueueTimePerSlot,
                               List<Integer> swapsPerTimeSlot,
                               List<NodoFog> nodi,
                               Map<Integer, Map<NodoFog, Integer>> computationCapacityPerSlot,
                               Map<Integer, Map<NodoFog, Integer>> delayPerSlot,
                               Map<Integer, List<Client>> clientsPerSlot,
                               Map<Integer, Map<NodoFog, Integer>> executionTimesPerSlot
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
        JPanel clientAveragesPanel = createClientAveragesPanel(clientsPerSlot); // Aggiornato

        // Panel stabilità
        JPanel tab1Panel = new JPanel(new GridLayout(1, 2));
        tab1Panel.add(maxQueueTimeChart);
        tab1Panel.add(stabilityChart);

        // Panel nodi
        JPanel tab2Panel = new JPanel(new GridLayout(1, 2));
        tab2Panel.add(nodeStatisticsChart);
        tab2Panel.add(computationDelayChart);

        // Panel clienti
        JPanel tab3Panel = new JPanel(new GridLayout(1, 1));
        tab3Panel.add(clientAveragesPanel);

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
