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
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.eval.Evaluation;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.io.File;

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

// NOTE BEFORE DELIVERING, CHECK COMMENTS FOR PROFESSIONALITY
public class NeuralNetTest {
    
    public static void main(String[] args) throws Exception{
        int numInput = 12; //Setting the number of input neurons
        int numHidden = 50; // SUBJECT TO CHANGE setting the number of hidden layer neurons
        int numOutput = 9; // setting the number of output neurons
        int rngSeed = 123; // setting the RNG seed 
        int batchSize = 150; // SUBJECT TO CHANGE setting the size of the mini-batch        
        int numEpochs = 150; // SUBJECT TO CHANGE setting the number of epochs to run the training for
        int iterations = 10; // SUBJECT TO CHANGE setting the number of iterations
        double learningRate = 0.05; // SUBJECT TO CHANGE the learning rate of the network
        
            
        
        // Reading in the data from a file
        //MIGHT NOT NEED THIS
        /*
        int numLinesToSkip = 0; // SUBJECT TO CHANGE The amount of lines to be skipped (should be zero if we format our data well)
        String delimiter = ","; // what the data is going to be split on
        
        RecordReader recordReader = new CSVRecordReader(numLinesToSkip,delimiter);
        recordReader.initialize(new FileSplit(new ClassPathResource(data).getFile())); // NOTE UPDATE "data.txt" to where the actual file is, and it's name!
        */
        
        int labelIndex  = 12; // SUBJECT TO CHANGE: The index of where the label will be (The label is what the outcome should be)
        String data = "src/Data/test_data_1.csv"; // SUBJECT TO CHANGE the location of our data
        
        DataSet allData = readCSVDataset(data,batchSize,labelIndex,numOutput);
            
        allData.shuffle();
        double ratio = 0.9; // SUBJECT TO CHANGE the percentage of data to be used for training (now set to 80%)
        
        SplitTestAndTrain testAndTrain = allData.splitTestAndTrain(ratio);
        
        DataSet trainingData = testAndTrain.getTrain();
        DataSet testData = testAndTrain.getTest();
        
        //Normalizing our data (giving us mean 0, unit variance):
        DataNormalization normalizer = new NormalizerStandardize();
        normalizer.fit(trainingData); // collect the statistics from the training data. This does not modify the input data
        normalizer.transform(trainingData); // Apply normalization to the training data
        normalizer.transform(testData); // Apply the normalization to the test data, using the statistics from the training set (which is bigger so should be the same or better)
        
        //Building the neural network  NOTE: the format of the network is different across examples, might want to try a bunch of them out or research what vague things do
        System.out.println("Build Model....");
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(rngSeed)
                .iterations(iterations)
                .learningRate(learningRate)
                .updater(Updater.NESTEROVS).momentum(0.9)    // Not exactly sure what this does, might want to leave it out or properly research this
                .regularization(true).l2(1e-4)               // applying L2 regularizations to work against overfitting (that's an "l" not a one)
                .list()
                .layer(0, new DenseLayer.Builder()
                        .nIn(numInput)
                        .nOut(numHidden)
                        .activation("relu")                 // again, not entirely sure what relu is, but this is the activation fucntion (might need to research)
                        .weightInit(WeightInit.XAVIER)
                        .build())
                .layer(1, new OutputLayer.Builder(LossFunction.NEGATIVELOGLIKELIHOOD)
                        .nIn(numHidden)
                        .nOut(numOutput)
                        .activation("softmax")             // again, but vague, but I have some idea what a softmax function is (S function, 1/(1+e^-x))
                        .weightInit(WeightInit.XAVIER)
                        .build())
                .pretrain(false)
                .backprop(true)                           // of course we're using backpropagation!
                .build();
        
        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();
        model.setListeners(new ScoreIterationListener(1)); // Listens to the score every iteration (might want to raise this value if we start training on large datasets
        
         
        
        double[] trainAccuracies = new double[numEpochs];
        double[] testAccuracies = new double[numEpochs];
        Evaluation eval = new Evaluation(numOutput);
         
        
        // HERE BE BUGS!
        System.out.println("Train model....");
        
        model.fit(trainingData);

        
 
        
        for(int i=0; i<numEpochs;i++){ // for the total amount of epochs
            System.out.println("=====================");
            System.out.println("     Epoch "+i);
            System.out.println("=====================");
             model.fit(trainingData);
             
             INDArray outputTraining = model.output(trainingData.getFeatureMatrix());
             INDArray outputTest = model.output(testData.getFeatureMatrix());
             
             //Here we want some of the data from the evaluation, so we can make nice plots regarding the accuracy so we can say something about overfitting
             
             //evaluating on the training data and storing the accuracy in the array
             eval.eval(trainingData.getLabels(),outputTraining);
             trainAccuracies[i]=eval.accuracy();
        
             //evaluating on the test data and storing the accuracy in the array
             eval.eval(testData.getLabels(),outputTest);
             testAccuracies[i]=eval.accuracy();                
        }
        
        
        //creating a list for the two accuracy arrays so we can use them for plotting
        List<NamedDataSet> AccuracyData = new ArrayList<>();
        NamedDataSet trainAccurSet = new NamedDataSet("Training", trainAccuracies);
        NamedDataSet testAccurSet = new NamedDataSet("Test",testAccuracies);
  
        AccuracyData.add(trainAccurSet);
        AccuracyData.add(testAccurSet);        
        
        plotDataSet(AccuracyData);
        
        //Evaluate the model on the test set
    
        System.out.println("Evaluate model....");
        
        INDArray outputTraining = model.output(trainingData.getFeatureMatrix());
        INDArray outputTest = model.output(testData.getFeatureMatrix());
        System.out.println("Scores on training data");
        eval.eval(trainingData.getLabels(), outputTraining);
        System.out.println(eval.stats());
        System.out.println("Scores on test data");
        eval.eval(testData.getLabels(),outputTest);
        System.out.println(eval.stats());
        
        
       
        
        
        //Degbug code
        
        /*
         System.out.println("Test accuracy");
        System.out.print("[");
        for(int i=0; i<testAccuracies.length;i++){
            System.out.print(testAccuracies[i]+" ");
        }
        System.out.print("]");
        System.out.println();
        
         System.out.println("Train accuracy");
        System.out.print("[");
        for(int i=0; i<trainAccuracies.length;i++){
            System.out.print(trainAccuracies[i]+" ");
        }
        System.out.print("]");
        System.out.println();
        
        */
             
        //This is the predicting bit!
        
        /*
        double[][] voorspellingData = new double[][]{{4,4,3,4,1,5,4,4,4,2,4,3},{4,4,4,1,4,5,4,4,4,3,2,3}};
        INDArray voorspeldata = Nd4j.create(voorspellingData);
        
        int[] resultaat = model.predict(voorspeldata);
        
        System.out.println("Testje voor het voorspellen");
        System.out.print("[");
        for(int i=0; i<resultaat.length;i++){
            System.out.print(resultaat[i]+" ");
        }
        System.out.print("]");
        System.out.println();
        */
        
        
        
      
        
        
    } // Ends the whole damn thing
    
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
    private static void plotDataSet(List<NamedDataSet> ArraySetList){
        
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
            
            String title = "Overfitting Data";
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
                f.setTitle("Overfitting data");
                
                f.setVisible(true);      
    }
}


