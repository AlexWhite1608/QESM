package simulation;


import core.Client;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
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
                    dataset.addValue(maxQueueTime, "Max Queue Time", String.valueOf(timeSlot));
                }
            }
        }

        JFreeChart lineChart = ChartFactory.createLineChart(
                "Max Queue Time Per Time Slot",
                "Time Slot",
                "Max Queue Time",
                dataset,
                org.jfree.chart.plot.PlotOrientation.VERTICAL,
                true, true, false
        );
//        LineAndShapeRenderer renderer = new LineAndShapeRenderer(true, false);
//        lineChart.getCategoryPlot().setRenderer(renderer);
//        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getNumberInstance()));
//        renderer.setBaseItemLabelsVisible(true);

        JFrame frame = new JFrame("Simulation Plot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        ChartPanel chartPanel = new ChartPanel(lineChart);
        frame.add(chartPanel);

        frame.setVisible(true);
    }
}

