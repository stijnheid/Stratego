/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.training;

import Game.Team;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
//only importing this for testing purposes
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import tools.deeplearning.BattleTranscript;

/**
 * Performs simulated annealing. 
 * @author s146928
 */
public class SimulatedAnnealing {
    
    int temperature;
    int start_temperature;
    double[] weights;
    Random random;
    double energy; // creating an instance variable for the energy, since this requires simulating games. We don't want to simulate the same game multiple times.
    double energies[];
    
    private WeightSetListener listener;
    private List<NamedDataSet> plot = new ArrayList();    
    
    //Constructor to create a SimulatedAnnealing class for the given amount of weights
    SimulatedAnnealing(int num_weights, int temp){
        temperature = temp;
        start_temperature = temp;
        weights = new double[num_weights];
        random = new Random();
        
        // Initialize weights.
        for(int i=0; i<weights.length;i++){
            weights[i] = (double)(random.nextInt(20) - 10) / 10;
        }
        
        energy = getEnergy(weights); // calculates the energy of the first solution
        System.out.println("Starting energy: "+energy);
        energies = new double[temp];
    }
    
    public void setListener(WeightSetListener listener) {
        this.listener = listener;
    }
    
    //A method that runs the simulated annealing algorithm
    public void start() throws IOException{
        
        int index = 0; // index for the array with mean differences
        while(temperature != 0) {
            
            double[] weightsCopy = weights.clone();         // creating a copy to store adjustments in  
            int randIndex = random.nextInt(weights.length); // generates a random value between 0 and the length of the weights array;
            weightsCopy[randIndex] = updateWeight(randIndex, weightsCopy); // adjust the weight in the copy array
            INDArray weightsprint = Nd4j.create(weightsCopy);
            INDArray oldweightsprint = Nd4j.create(weights);
            System.out.println("Current weights: "+oldweightsprint);
            System.out.println("Proposed weights: "+weightsprint);
            /*
            //USE THIS FOR TESTING *************
            INDArray weight = Nd4j.create(weightsCopy);
            System.out.println(weight);
            // *********************************
            */
            
            // Notify the listener.
            // Blocking method.
            double winRate = listener.generated(weights);
            // Process the result.
            
            //if the adjustment is accepted, copy it to the original weights array
            double e = 1 - winRate; //getEnergy(weightsCopy);
            System.out.println("Proposed energy: "+e);
            if(checkAccepted(weightsCopy, e)){
                weights = weightsCopy; // updates the weights
                energy = e; // updates the energy of the (new) current solution
                System.out.println("Change Accepted");                
            }
            
            energies[index] = energy; // since we are using - winrate in the simulation, but we want the actual winrate in the graph 
            System.out.println("Current energy: "+ energy);
            index++;
            temperature--; //reduces the temperature by one.
            System.out.println("Current temperature: "+temperature);
            
        }
    }
    
    public void savePlot(boolean show, String filename) throws IOException {
        this.plot.add(new NamedDataSet("Energy", getEnergies()));
        plotDataSet(this.plot, "Energies", "Iterations", "Energy", false, 
                show, filename);        
    }
    
    public double[] getEnergies(){
        return energies;
    }
    
    //A method that returns the current set of weights
    public double[] getWeights(){
        return weights;
    }
    
    //A method that adjusts a weight according to the predefined rules:
    // if weight 1, subtract 0.1
    // if weight -1, add 0.1
    // else, randomly add 0.1 or -0.1
    //Uses parameter index for the picked position in the array
    public double updateWeight(int index, double[] weights){
        double[] changes = new double[]{-0.1,0.1};// writing the choices in an array
        System.out.println("Weight to be changed "+(index+1));
        System.out.print("Choice: ");
        if(weights[index] > 0.9){
            System.out.println("add -0.1");
            return weights[index] - 0.1; // subtracting 0.1 if 1
        } else if(weights[index] < -0.9){
            System.out.println(" add 0.1");
            return weights[index] + 0.1; // adding 0.1 if -1
        } else {
            double change = changes[random.nextInt(2)]; // pick one of the adjustments at random, since the RNG returns 0 or 1
            System.out.println("add "+ change);
            return weights[index]+change; // adding 0.1 or -0.1
        }
        
    }
    

    //A method which checks if an adjustment is accepted.
    public boolean checkAccepted(double[] weights, double e){
        double test = random.nextDouble();
        test = Math.log(test);
        if (e < energy){ // if the energy is smaller than the current energy
            System.out.println("Energy smaller, at:  "+e+", automatically accepted");
            return true;
        //If the calculated acceptance probability is larger than a random value between 0.0 and 1.0, the change is accepted
        } else if(calculateProbability(weights,e)>test){ // don't need to flip the sign since log scaled probabilities are convenient
            System.out.println("Compare probability smaller at : "+test);
            return true;
        }
        
        return false;
    }
    
    //A method which calculates the acceptance probability of the adjustment
    public double calculateProbability(double[] weights, double e){
        
        double prob = Math.exp(-((e - energy)/((double)temperature/(double)start_temperature))); // dividing the temperature by the start temperature to normalize 
        prob = Math.log(prob);
        System.out.println("Acceptance probability: "+prob);
        return prob;
    }
    
    
    /*A method that calculates the energy depending on a set of weights
    *NOTE THIS IS WHERE THE SIMULATED GAMES WILL COME IN.
    *
    * In the case of the simulations, this will just be the win rate. 
    * But to prove that our algorithm actually gets somewhere, we need something a bit more fancy
    * So we'll pick a few random points, feed them in a polynomial with the weights generated by the algorithm
    * and compare these against the weights I've set from the beginning. The energy will be the mean difference from the 
    * result of the polynomial
    */
    public double getEnergy(double[] weights){
       //   return 1-random.nextDouble(); // Note, we want to take -winrate, since the algorithm strives to the LOWEST energy, making lower energy better.
                                     // this is the same as writing e - e' in the algorithm (just easier and cleaner to do it here)
                                     
       //let's try going back to something more interesting
       double e = 0;
       for(double weight : weights){
           e = e + weight;
       }
       e = e * e; // for the hell of it working with e^2, since that is a little more unstable than e itself (changes are exaggerated)
       return e;
    }
    
    private static void plotDataSet(List<NamedDataSet> ArraySetList, 
            String name, String xAxis, String yAxis, boolean legend, 
            boolean show, String filename) throws IOException{
        String plot_title = name;
        XYSeriesCollection plotData = new XYSeriesCollection();

        for(NamedDataSet ns: ArraySetList) {
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
        JFreeChart chart = ChartFactory.createScatterPlot(title, xAxisLabel, 
                yAxisLabel, plotData, orientation, legend, tooltips, urls);

        if(show) {
            JPanel panel = new ChartPanel(chart);
            JFrame f = new JFrame();
            f.add(panel);
            f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            f.pack();
            f.setTitle(plot_title);
            f.setVisible(true);
        }
        
        ChartUtilities.saveChartAsJPEG(new File(filename), 
                chart, 1280, 1024);
    }
}