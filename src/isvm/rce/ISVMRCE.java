/*
I3S-CNRS\IFI 
 */
package isvm.rce;

import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * @author Sikadie
 */
public class ISVMRCE extends ApplicationFrame {

    /**
     * @param args the command line arguments
     */
    public static int NBRESEQUENCE = 30;

    public static void main(String[] args) {
        int traceSize = 0;
        List<Trace[]> tabTrace, tabItteration;
        tabTrace = new ArrayList<>();
        tabItteration = new ArrayList<>();

        for (int i = 0; i < NBRESEQUENCE; i++) {
            ISvmRce isvmRce = new ISvmRce();
            isvmRce.init();
            isvmRce.start();
            List<Trace> traces = isvmRce.getTraceExecution();
            traceSize = traces.size();
            Trace[] traceCurrent = new Trace[traceSize];
            for (int j = 0; j < traceSize; j++) {
                traceCurrent[j] = traces.get(j);
            }
            tabTrace.add(i, traceCurrent);
        }
        //at this level, we have an list on NBRESEQUENCE itterations

        //now it is time to transform this list of array to tabitteration to easy compute the mean, std
        for (int i = 0; i < traceSize; i++) {
            Trace[] tabItt = new Trace[NBRESEQUENCE];
            for (int j = 0; j < NBRESEQUENCE; j++) {
                tabItt[j] = tabTrace.get(j)[i];
            }
            tabItteration.add(tabItt);
        }
        //Now we compute the mean, std of precision
        Trace[] result = TraceResult.experiment(tabItteration);
        ISVMRCE chart = new ISVMRCE("Experiment results ISVM-RCE ",
                "Mean Precision on " + NBRESEQUENCE + " itterations", result);
        chart.pack();
        RefineryUtilities.centerFrameOnScreen(chart);
        chart.setVisible(true);
        System.out.println("");
    }

    public ISVMRCE(String applicationTitle, String chartTitle, Trace[] result) {
        super(applicationTitle);
        JFreeChart xylineChart = ChartFactory.createLineChart(
                chartTitle,
                "AVG number of cluster",
                "Accuracy",
                createDataset(result),
                PlotOrientation.VERTICAL,
                true, true, false);
        //  xylineChart.

// Create an NumberAxis
        ChartPanel chartPanel = new ChartPanel(xylineChart);
        //chartPanel.setPreferredSize(new java.awt.Dimension(1000, 700));
        chartPanel.setAutoscrolls(true);

        /*   CategoryPlot plot = (CategoryPlot) xylineChart.getPlot();
    LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
    renderer.setShapesVisible(true);
    DecimalFormat decimalformat1 = new DecimalFormat("##");
    renderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", decimalformat1));
    renderer.setItemLabelsVisible(true)*/
        this.setContentPane(chartPanel);
    }

    private DefaultCategoryDataset createDataset(Trace[] result) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        int size = result.length;
        for (int i = 0; i < size; i++) {
            dataset.addValue(result[i].getPrecision(), "precision", result[i].getNumberCluster() + "");
        }

        return dataset;

    }

}
