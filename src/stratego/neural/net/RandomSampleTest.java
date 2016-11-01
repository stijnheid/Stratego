/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stratego.neural.net;

import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import java.util.Random;
import java.util.ArrayList;


/**
 *
 * @author s146928
 */
public class RandomSampleTest {
    
    
    private static int data_points = 1000;
    private static int repetitions = 1;
    private static Random[] random = new Random[repetitions];
    
   
    
    public static void main(String[] args){
        double[] wins = new double[data_points];
        double[] winrate = new double[data_points];
        List<double[]> data = new ArrayList<>();
        
        for(int i = 0; i<random.length; i++){
            random[i] = new Random();
        }
            
   
        for(int index = 0; index < repetitions; index++){
        
            for(int i=0; i<wins.length;i++){
                wins[i] = (double)random[index].nextInt(2);
            }

            winrate[0] = wins[0];

            for(int i=1; i<winrate.length;i++){
                double total_wins = 0;
                for(int j=0; j<i;j++){
                    total_wins = total_wins + wins[j];
                }
                winrate[i] = (total_wins/i);
            } 

            data.add(winrate);
            }
        

        plotDataSet(data);
    }
    
    private static void plotDataSet(List<double[]> data){
        
            XYSeriesCollection plotData = new XYSeriesCollection();
            
            for(double[] ds: data){
           
                XYSeries series = new XYSeries("Winrate");
                
                for(int i=0; i<ds.length;i++){
                    series.add((double)i,ds[i]);
                }
                
                plotData.addSeries(series);
            }
            
            
            String title = "Simulating Random Samples";
                String xAxisLabel = "Matches";
                String yAxisLabel = "Winrate";
                PlotOrientation orientation = PlotOrientation.VERTICAL;
                boolean legend = false; // might wanna set this to true at some point, but research the library
                boolean tooltips = false;
                boolean urls = false;
                JFreeChart chart = ChartFactory.createScatterPlot(title, xAxisLabel, yAxisLabel, plotData, orientation, legend, tooltips, urls);
              
            JPanel panel = new ChartPanel(chart);
            
                JFrame f = new JFrame();
                f.add(panel);
                f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                f.pack();
                f.setTitle("Random test simulation");
                
                f.setVisible(true);  
    }
}
