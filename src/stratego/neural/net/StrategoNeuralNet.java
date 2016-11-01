/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stratego.neural.net;

import java.io.IOException;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.DataSet;
import java.util.List;
import javax.swing.*;
import java.io.File;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author s146928
 */
public class StrategoNeuralNet {

    static int plotIndex = 1; // storing an index for identifying plots
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        
        /********************************************
        INPUT DATASETS HERE        
        ********************************************/
       
        String data1 = "src/Data/dataPoint_201.csv"; // location of first dataset
        int labelIndex1 = 12; // label index, the place in a single line where the label is (a label is the correct classification that belongs to the datapoint)
        double ratio1 = 0.9; // the ratio of the data to be used as training (remainder is test)
        int batchSize1 = 35; // sets the size of the micro-batch 
        int numClasses1 = 7; // sets the total number of classifications possible.
        
        /***************************************
         * LOADING OF DATASETS HAPPENS HERE
         ***************************************/
        DataSet dataSet1 = readCSVDataset(data1,batchSize1,labelIndex1,numClasses1); // storing the raw CSV data in a DataSet object
       
        /**************************************
         * BUILDING NETWORKS HAPPENS HERE
         **************************************/
        
        // FIRST NETWORK
        int numInput1 = 12;                 // sets the number of input neurons (always set equal to the amount of variables for training in a datapoint
        int numHidden1 = 50;                // set the number of neurons in the hidden layer
        int iterations1 = 10;               // sets the number of iterations to be performed during each epoch
        int scoreListener1 = 1;             // sets after how many iterations the score should be listed on the output terminal
      
        //Note, no need to set numOutput here if we have set numClasses for the dataset, since these are the same
        
        int numEpochs1 = 50;       // sets the amount of epochs to run the training for
        
        String name1 = "One Layer, 201 datapoints, batchsize "+batchSize1+" ratio "+ratio1+" epochs: "+numEpochs1;     // setting the name for identication
        //SECOND NETWORK
        // Right now I'm interested in difference in performance, so I'm just going to copy all the stats from te first and only change the name
        String name2 = "Two Layer, 201 datapoints, batchsize "+batchSize1+" ratio "+ratio1+" epochs: "+numEpochs1;
        
        //THIRD NETWORK
        String name3 = "Three Layer, 201 datapoints, batchsize "+batchSize1+" ratio "+ratio1+" epochs: "+numEpochs1;
/*
        OneLayerNetwork oneLayerNetwork = new OneLayerNetwork(numInput1, numHidden1, numClasses1, iterations1, scoreListener1, name1);
        List<NamedDataSet> plotData1 = oneLayerNetwork.train(dataSet1, ratio1, numEpochs1); // trains the network and returns a List containing overfitting data for the plot.
        plotDataSet(plotData1, oneLayerNetwork.getName());
        */
        TwoLayerNetwork twoLayerNetwork = new TwoLayerNetwork(numInput1, numHidden1, numClasses1, iterations1, scoreListener1, name2);
        List<NamedDataSet> plotData2 = twoLayerNetwork.train(dataSet1, ratio1, numEpochs1); 
        plotDataSet(plotData2, twoLayerNetwork.getName());
        /*
        ThreeLayerNetwork threeLayerNetwork = new ThreeLayerNetwork(numInput1, numHidden1, numClasses1, iterations1, scoreListener1, name3);
        List<NamedDataSet> plotData3 = threeLayerNetwork.train(dataSet1, ratio1, numEpochs1);
        plotDataSet(plotData3, threeLayerNetwork.getName());
        */
        
        /***********************************************************
         * CONSOLE OUTPUT HAPPENS HERE
         ****************************************************/
        //oneLayerNetwork.evaluation();
        twoLayerNetwork.evaluation();
       // threeLayerNetwork.evaluation();

        twoLayerNetwork.storeNetwork("network");
        
        MultiLayerNetwork twoLayerTest = ModelSerializer.restoreMultiLayerNetwork("src/NetworkFiles/network.zip");
        
        System.out.println("Original and restored networks: configs are equal: " + twoLayerNetwork.getNetwork().getLayerWiseConfigurations().equals(twoLayerTest.getLayerWiseConfigurations()));
        System.out.println("Original and restored networks: parameters are equal: " + twoLayerNetwork.getNetwork().params().equals(twoLayerTest.params()));

    }
    
     /**
     * used for testing and training
     *
     * @param csvFileClasspath
     * @param batchSize
     * @param labelIndex
     * @param numClasses
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    private static DataSet readCSVDataset(
            String csvFileClasspath, int batchSize, int labelIndex, int numClasses)
            throws IOException, InterruptedException{

        RecordReader rr = new CSVRecordReader();
        File file = new File(csvFileClasspath);
       // rr.initialize(new FileSplit(new ClassPathResource(csvFileClasspath).getFile()));
        rr.initialize(new FileSplit(file));
        DataSetIterator iterator = new RecordReaderDataSetIterator(rr,batchSize,labelIndex,numClasses);
        return iterator.next();
    }
    
    
    /*
    Generate a scatterplot of the datasets provided
    */
    private static void plotDataSet(List<NamedDataSet> ArraySetList, String network_name){
            
            String plot_title = "Plot "+plotIndex+": Overfitting on network "+network_name;
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
                String xAxisLabel = "Epochs";
                String yAxisLabel = "Accuracy";
                PlotOrientation orientation = PlotOrientation.VERTICAL;
                boolean legend = true; // might wanna set this to true at some point, but research the library
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
            
                plotIndex++; // increase the plotindex
    }

}
