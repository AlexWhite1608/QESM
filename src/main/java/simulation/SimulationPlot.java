package simulation;

import core.Client;
import core.NodoFog;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
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
}
