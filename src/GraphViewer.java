import javax.swing.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


import java.awt.*;

/**
 * This class represents the Graph Viewer panels in the MainUI. This class inherits from JPanel
 * @author Paul Scoropan, Gouri Sikha, Bizman Sawhney, Owen Tjhie
 */
public class GraphViewer extends JPanel {

    private int viewType; // the index of the view type of this graph
    private String title; // the title of the graph
    private ChartPanel chartPanel; // the ChartPanel representing the graph, if one exists
    private JTextArea reportTextArea; // the text area for the report text, if one exists

    /**
     * The GraphViewer constructor, initializes the GraphViewer as one of the view types based on a switch statement
     * @param viewType the view type of the graph, as an index
     * @param title the title of the graph
     */
    public GraphViewer(int viewType, String title) {
        setBackground(Color.white); // set the background of the panel to white, called from superclass
        this.viewType = viewType; // initialize instance variables
        this.title = title;
        switch (viewType) { // switch for the different view types
            case 0: // REPORT
                reportTextArea = new JTextArea(createReport());
                reportTextArea.setEditable(false); // users can not edit report text
                add(reportTextArea); // add the text area to the panel
                break;
            case 1: // BAR CHART
                createBarChart();
                break;
            case 2: // LINE CHART
                createLineChart();
                break;
            case 3: // SCATTER PLOT
                  createScatterPlot();
                break;
            default:
                MainUI.displayError("GRAPH TYPE ERROR"); // display error if incorrect view type was passed
        }
    }

    /**
     * This method creates a JFreeChart bar chart and adds it to the chartPanel
     */
    private void createBarChart() {

            JFreeChart barChart = ChartFactory.createBarChart(
                    title,
                    "Year",
                    Util.CHART_LABELS[MainUI.getAnalysisType()], // get the chart label based on analysis type
                    createCategoricalDataset(), // create a categorical dataset
                    PlotOrientation.VERTICAL,
                    true, true, false);

           if (MainUI.getAnalysisType() != 0) {
        	   CategoryPlot plot = barChart.getCategoryPlot();
        	   plot.getRenderer().setSeriesVisibleInLegend(true); // add legend if more than one dataset displayed
            } 
            chartPanel = new ChartPanel( barChart ); // initialize chart panel with bar chart
            chartPanel.setPreferredSize(new java.awt.Dimension( 560 , 367 ) );
            add(chartPanel);
    }

    /**
     * This method creates a JFreeChart line chart and adds it to the chartPanel
     */
    private void createLineChart() {
            JFreeChart lineChart = ChartFactory.createXYLineChart(
                    title,
                    "Year",Util.CHART_LABELS[MainUI.getAnalysisType()],
                    createXYDataset(),
                    PlotOrientation.VERTICAL,
                    true,true,false);

            chartPanel = new ChartPanel( lineChart );
            chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
            add(chartPanel);
    }

    /**
     * This method creates a JFreeChart scatter plot and adds it to the chartPanel
     */
    private void createScatterPlot()
    {
    		JFreeChart scatterPlot = ChartFactory.createScatterPlot(title, "Year", Util.CHART_LABELS[MainUI.getAnalysisType()], 
    				createXYDataset(), PlotOrientation.VERTICAL, true, true, false);
    		chartPanel = new ChartPanel( scatterPlot);
    		//chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
    		XYPlot plot = scatterPlot.getXYPlot();
    		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer(); // render XY Line and shape
    		renderer.setSeriesLinesVisible(0,false);
    		renderer.setSeriesShapesVisible(0,true);
    		renderer.setSeriesLinesVisible(1,false);
    		renderer.setSeriesShapesVisible(1,true);
    		plot.setRenderer(renderer);
    		add(chartPanel);
    }


    /**
     * This method creates an XY dataset
     * @return a XYDataset object from the JFreeChart library
     */
    private XYDataset createXYDataset() {
    	XYSeriesCollection dataset = new XYSeriesCollection();
    	XYSeries series1 = new XYSeries("Forest Area");//make multiple series if needed
        for (int i = MainUI.getStartYear(); i <= MainUI.getEndYear(); i++) {
            try {
            	series1.add(i, Double.parseDouble(MainUI.getMapForest().get(i).toString()));  // add data to series
            } catch (Exception e) {
                MainUI.displayError("Unhandled error occured while rendering dataset");
                MainUI.displayError(e.getStackTrace().toString());
            }
        }
    	dataset.addSeries(series1); // add series to dataset
    	if (MainUI.getAnalysisType()==0)
    	{
    		return dataset; // return dataset if the first analysis type
    	}
    	//if more variables are needed then -
    	XYSeries series2 = new XYSeries(Util.CHART_LABELS[MainUI.getAnalysisType()]); //second series
        for (int i = MainUI.getStartYear(); i <= MainUI.getEndYear(); i++) {
            try {
            	series2.add(i, Double.parseDouble(MainUI.getMapVar().get(i).toString())); // add data to second dataset
            } catch (Exception e) {
                MainUI.displayError("Unhandled error occured while rendering dataset");
                MainUI.displayError(e.getStackTrace().toString());
            }
        }
        dataset.addSeries(series2); // add second dataset
    	return dataset;
    }

    /**
     * This method creates a categorical dataset
     * @return a CategoryDataset object from the JFreeChart library
     */
    private CategoryDataset createCategoricalDataset() {
        if (MainUI.getAnalysisType() == 0) {
            final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            for (int i = MainUI.getStartYear(); i <= MainUI.getEndYear(); i++) {
                try {
                    dataset.addValue(Double.parseDouble(MainUI.getMapForest().get(i).toString()), Util.COUNTRIES[MainUI.getCountry()], Integer.toString(i)); // add value at year
                } catch (Exception e) {
                    MainUI.displayError("Unhandled error occured while rendering dataset");
                }
            }

            return dataset;
        } else { // if two datasets
        	int size = (MainUI.getEndYear()+1) - MainUI.getStartYear();
            double data[][] = new double[2][size];
            for (int i = 0; i < size ; i++) {
                try {
                    data[0][i]=Double.parseDouble(MainUI.getMapForest().get(MainUI.getStartYear() + i).toString()); // add first dataset
                } catch (Exception e) {
                    MainUI.displayError("Unhandled error occured while rendering dataset");
                }
            }
            for (int i = 0; i < size ; i++) {
                try {
                    data[1][i]=Double.parseDouble(MainUI.getMapVar().get(MainUI.getStartYear() + i).toString()); // add second dataset
                } catch (Exception e) {
                    MainUI.displayError("Unhandled error occured while rendering dataset");
                }
            }
            return DatasetUtilities.createCategoryDataset(Util.getAnalysisList(), Util.getYearsInRange(MainUI.getStartYear(), MainUI.getEndYear()), data);
        }
    }

    /**
     * This method updates the contents of the GraphViewer based on the view type
     */
    public void display() {
        if (viewType == 1) { // if the view type requires a categorical dataset
            chartPanel.getChart().getCategoryPlot().setDataset(createCategoricalDataset()); // create a new dataset and set the chart to it
        } else if (viewType == 2 || viewType == 3) { // if the view type requires an XY dataset
            chartPanel.getChart().getXYPlot().setDataset(createXYDataset()); // create a new dataset and set the chart to it
        } else if (viewType == 0) { // if the view type is report
            reportTextArea.setText(createReport()); // create a new report and set the text to it
        }

    }

    /**
     * This method gets the view type of this GraphViewer
     * @return the view type as an index
     */
    public int getViewType() {
        return viewType;
    }

    /**
     * This method creates a report text of the data
     * @return the report text as a string
     */
    public String createReport() {
        String report = Util.ANALYSIS_TYPES[MainUI.getAnalysisType()] + "\n======================================\n"; // report title is analysis type

        for (int i = MainUI.getStartYear(); i <= MainUI.getEndYear(); i++) { // loop through selected years
            report += "Year " + i + ":\n"; // append the year name
            if (MainUI.getAnalysisType() == 0) { // if analysis type is 0 there is only one set of data and its handled separately
                report += "\t\t" + Util.CHART_LABELS[0] + " => " + MainUI.getMapForest().get(i) + "\n"; // append data from forest map
            } else {
                report += "\t\t" + Util.CHART_LABELS[0] + " => " + MainUI.getMapForest().get(i) + "\n"; // append data from forest map
                report += "\t\t" + Util.CHART_LABELS[MainUI.getAnalysisType()] + " => " + MainUI.getMapVar().get(i) + "\n"; // append data from second variable map
            }
        }
        return report; // return the concatenated report text

    }

}


