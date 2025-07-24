package com.academic.examapp.reportingsystem;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import java.awt.Color;
import java.util.Vector;
public class ChartUpdater {
    private ChartPanel chartPanel;
    public ChartUpdater(ChartPanel chartPanel) {
        this.chartPanel = chartPanel;
    }
    public void updateChart(Vector<Vector<Object>> dataRows) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Vector<Object> row : dataRows) {
            String topic = row.get(0).toString();
            double score = Double.parseDouble(row.get(1).toString());
            dataset.addValue(score, "Score", topic);
        }
        JFreeChart chart = ChartFactory.createBarChart(
                "Performance Chart", "Topic", "Average Score", dataset,
                PlotOrientation.VERTICAL, false, true, false);


        chart.setBackgroundPaint(Color.white);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.lightGray);

        chartPanel.setChart(chart);
        chartPanel.revalidate();
        chartPanel.repaint();
    }
}
