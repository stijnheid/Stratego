/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stratego.neural.net;

//Importing INDArrays because that makes it easier to output on terminal and I'm lazy
import java.util.List;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

/**
 *
 * @author s146928
 */
public class SimulatedAnnealingTest {
    
   // static double[] actualWeights = new double[]{0.5,-0.3,0.4,-0.7,0.2};
   // static INDArray trueweights = Nd4j.create(actualWeights);
    
   public static void main(String[] args) throws Exception{
       
      // List<NamedDataSet> plotdata = new ArrayList<>();
      // List<NamedDataSet> polyplot = new ArrayList<>();
       
       
       
       System.out.println("Testing stuff for simulated annealing");
       SimulatedAnnealing sim = new SimulatedAnnealing(6, 15);
       double[] initialWeights = sim.getWeights();
       INDArray weight = Nd4j.create(initialWeights);
       System.out.println(weight);
       sim.run();
       System.out.println("Run finished");
       System.out.println("*****************************");
       System.out.println("initialized result");
       System.out.println(weight);
       System.out.println("final result: ");
       
       INDArray endresult = Nd4j.create(sim.getWeights());
       
       System.out.println(endresult);
       
       /*
       System.out.println("actual weights are: ");
       System.out.println(trueweights);
       */
       System.out.println("*****************************");
       
       SimulatedAnnealing sim2 = new SimulatedAnnealing(6, 50);
       sim2.run();
       
       SimulatedAnnealing sim3 = new SimulatedAnnealing(6, 200);
       sim3.run();
       
       SimulatedAnnealing sim4 = new SimulatedAnnealing(6, 1000);
       sim4.run();
       
       
      // plotdata.add(new NamedDataSet("Energy",sim.getEnergies()));
       
      // plotDataSet(plotdata, "Energies", "iterations","Energy",false);
       
      /*
       
       double[] testpolydata = createDataset(sim.getWeights(),100);
       double[] actualpolydata = createDataset(actualWeights, 100);
       double[] initpolydata = createDataset(initialWeights, 100);
       
       */
       /*
       INDArray init = Nd4j.create(initpolydata);
       System.out.println("init data: "+init);
       */
       /*
       NamedDataSet testpoly = new NamedDataSet("Simulation polynomial", testpolydata);
       NamedDataSet controlpoly = new NamedDataSet("Actual polynomial", actualpolydata);
       NamedDataSet initialpoly = new NamedDataSet("Initial polynomial",initpolydata);
       
       polyplot.add(testpoly);
      // polyplot.add(controlpoly);
       polyplot.add(initialpoly);
       
      // plotDataSet(polyplot, "Polynomials","x","y",true);
       //Making some datasets for plotting 
       
       /*
       
       double a = (-(0.08-0.06)/(75.0/100.0));
       double test = Math.exp(-(0.08-0.06)/((double)75/(double)100));
       double fiets = Math.exp(a);
       System.out.println("Testje voor euler functie, uitkomst: "+test);
       System.out.println("Testje voor exponent, uitkomst: "+a);
       System.out.println("Testje voor los exponent berekenen: "+fiets);
       */
       
 
   } 
   
   /*
   private static void plotDataSet(List<NamedDataSet> ArraySetList, String name, String xAxis, String yAxis, boolean legend){
            
                  
            String plot_title = name;
            XYSeriesCollection plotData = new XYSeriesCollection();
            
            for(NamedDataSet ns: ArraySetList)
            {
                XYSeries series = new XYSeries(ns.getName());
                double[] data = ns.getArray();
                for(int i=0; i<data.length;i++){
                    series.add((double)i,data[i]);
                }
                
                plotData.addSeries(series);
            }
            
            String title = plot_title;
                String xAxisLabel = xAxis;
                String yAxisLabel = yAxis;
                PlotOrientation orientation = PlotOrientation.VERTICAL;
                //boolean legend = true; // might wanna set this to true at some point, but research the library
                boolean tooltips = false;
                boolean urls = false;
                JFreeChart chart = ChartFactory.createScatterPlot(title, xAxisLabel, yAxisLabel, plotData, orientation, legend, tooltips, urls);
              
            JPanel panel = new ChartPanel(chart);
            
                JFrame f = new JFrame();
                f.add(panel);
                f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                f.pack();
                f.setTitle(plot_title);
                
                f.setVisible(true);  
    }
   
   */
}
