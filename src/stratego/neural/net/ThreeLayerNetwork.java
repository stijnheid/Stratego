/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stratego.neural.net;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.deeplearning4j.eval.Evaluation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;

/**
 * Allows creation of a three layer neural network, where the user can specify
 * the number of input neurons, the number of hidden neurons, the number of 
 * output neurons, the number of iterations (learning steps of the network),
 *  how often we want to have the score iteration posted in the terminal,
 *  and a name for identification.
 * 
 * NOTE: There are two constructors to be called, one when the amount of neurons
 * in the hidden layers is to be the same, and one when they are to be different
 * Careful when initialising this!
 *  This class also contains helper methods to retrieve the network, 
 *  retrieve the name of the network, and to store the network in a file.
 * This class does not handle training the network.
 * 
 * @author Luc Heuff (0913435)
 */
public class ThreeLayerNetwork {
    
   static int numInput;
   static int numHiddenFirst;
   static int numHiddenSecond;
   static int numHiddenThird;
   static int numOutput;
   static int rngSeed = 12345;
   static int iterations;
   static int scoreListener;
   static double learningRate = 0.05;
   static MultiLayerNetwork model;
   static String name;
   static String trainingEvaluation;
   static String testEvaluation; 
    
    ThreeLayerNetwork(int num_input, int num_hidden, int num_output, int iteration, int score_listener, String network_name){
        
        this.numInput = num_input;
        this.numHiddenFirst = num_hidden;
        this.numHiddenSecond = num_hidden;
        this.numHiddenThird = num_hidden;
        this.numOutput = num_output;
        this.iterations = iteration;
        this.scoreListener = score_listener;
        this.name = network_name;
        constructor();
               
    }
    
    ThreeLayerNetwork(int num_input, int num_hidden_first, int num_hidden_second, int num_hidden_third, int num_output, int iteration, int score_listener, String network_name){
        
        numInput = num_input;
        numHiddenFirst = num_hidden_first;
        numHiddenSecond = num_hidden_second;
        numHiddenThird = num_hidden_third;
        numOutput = num_output;
        iterations = iteration;
        scoreListener = score_listener;
        name = network_name;
        constructor();
               
    }
    
    
    /* 
    * This method handles the initial configuration of the network
    */
    private static void constructor(){
        
        System.out.println("Build Model....");
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(rngSeed)
                .iterations(iterations)
                .learningRate(learningRate)
                .updater(Updater.NESTEROVS).momentum(0.9)    
                .regularization(true).l2(1e-4)               // applying L2 regularizations to work against overfitting (that's an "l" not a one)
                .list()
                .layer(0, new DenseLayer.Builder()
                        .nIn(numInput)
                        .nOut(numHiddenFirst)
                        .activation("relu")                 // again, not entirely sure what relu is, but this is the activation fucntion (might need to research)
                        .weightInit(WeightInit.XAVIER)
                        .build())
                .layer(1, new DenseLayer.Builder()
                        .nIn(numHiddenFirst)
                        .nOut(numHiddenSecond)
                        .activation("relu")
                        .weightInit(WeightInit.XAVIER)
                        .build())
                .layer(2, new DenseLayer.Builder()
                        .nIn(numHiddenSecond)
                        .nOut(numHiddenThird)
                        .activation("relu")
                        .weightInit(WeightInit.XAVIER)
                        .build())
                .layer(3, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .nIn(numHiddenThird)
                        .nOut(numOutput)
                        .activation("softmax")             // again, but vague, but I have some idea what a softmax function is (S function, 1/(1+e^-x))
                        .weightInit(WeightInit.XAVIER)
                        .build())
                .pretrain(false)
                .backprop(true)                           // of course we're using backpropagation!
                .build();
        
        model = new MultiLayerNetwork(conf);
        model.init();
        model.setListeners(new ScoreIterationListener(1));
        System.out.println("Build completed");
    }
    /*
    * This method returns the network for training purposes
    */
    static public MultiLayerNetwork getNetwork(){
        return model;
    }
    
    public String getName(){
        return name;
    }
    
    /*
    * This method allows storing the network to a file with a given name
    */
    public void storeNetwork(String name) throws IOException{
       
        String pathname = "src/NetworkFiles/OneLayerNetwork"+name+".zip";
        ModelSerializer.writeModel(model,new File(pathname),true);        
    }
    
     public List<NamedDataSet> train(DataSet data, double ratio, int numEpochs){
        
               
        data.shuffle(); // shuffles the data, reducing the chance of bias when splitting the dataset
        SplitTestAndTrain testAndTrain = data.splitTestAndTrain(ratio); // generates an object for splitting the data
        
        DataSet trainingData = testAndTrain.getTrain(); // stores the training data
        DataSet testData = testAndTrain.getTest(); // stores the test data
        
        //For the network to perform optimally, the data needs to be normalized
        DataNormalization normalizer = new NormalizerStandardize();
        normalizer.fit(trainingData);  // collecting statistics from the input data, not modifying the input data
        normalizer.transform(trainingData); // applying normalization to the training data
        normalizer.transform(testData); // applying the training data normalization to the test data (since the training data is bigger, better statistics, better normalization
        
        //Creating arrays to store the accuracy during training in
        double[] trainAccuracy = new double[numEpochs];
        double[] testAccuracy = new double[numEpochs]; 
        
        Evaluation eval = new Evaluation(); // creating the Evaluation object, allowing easy access to evaluation statistics
        
        System.out.println("Training network "+name+".....");
        
        //running the training for the given amount of epochs
        for(int i=0; i<numEpochs;i++){
            System.out.println("======================");
            System.out.println("      Epoch "+i);
            System.out.println("======================");
            
            model.fit(trainingData); // training with the training set
            
            //Note, a DataSet contains the input variables as well as the classification values. getFeatureMatrix only the input variables in an INDArray object
            INDArray outputTraining = model.output(trainingData.getFeatureMatrix()); //gets the output (classification) of the network based on the training data input
            INDArray outputTest = model.output(testData.getFeatureMatrix()); // gets the output (classification) of the network based on the test data input
            
            // getLabels retrieves the classification values from a DataSet
            eval.eval(trainingData.getLabels(),outputTraining); // evaluates the results classified by the network compared to the actual results, on the training data
            trainAccuracy[i] = eval.accuracy(); // stores the accuracy statistic in the training array
            
            //If this was the final epoch, write some extra statistics
            if(i == numEpochs-1){
                /*
                System.out.println("Evaluate network "+name+".....");
                System.out.println("Scores on training data: ");
                System.out.println(eval.stats()); // shows more elaborately the performance of the network
                */
                trainingEvaluation = eval.stats();
            }
            
            eval.eval(testData.getLabels(),outputTest); // evaluates the results classified by the network compared to the actual results, on the test data
            testAccuracy[i] = eval.accuracy(); // stores the accuracy statistic in the test array
            
            //if this was the final epoc, finish writing the extra statistics
            if(i == numEpochs-1){
                /*
                System.out.println("Socres on test data: ");
                System.out.println(eval.stats()); // shows more elaborately the performance of the network
                */
                testEvaluation = eval.stats();
            }
            
        }
        
        List<NamedDataSet> accuracyDataList = new ArrayList<>(); // creaing a list of NameDataSet objects, required for the overfitting plot
        NamedDataSet trainAccur = new NamedDataSet("Training",trainAccuracy); // creating a NamedDataSet for the training accuracies
        NamedDataSet testAccur = new NamedDataSet("Test", testAccuracy); // creaing a NamedDataSet for the test accuracies
        
        //adding the NamedDataSets to the list
        accuracyDataList.add(trainAccur);
        accuracyDataList.add(testAccur);
        
        return accuracyDataList;
    }
    
     public void evaluation(){
        
        System.out.println("=====================================");
        System.out.println("Evaluate network "+name+".....");
        System.out.println("Scores on training data: ");
        System.out.println(trainingEvaluation);
        System.out.println();
        System.out.println("Scores on test data: ");
        System.out.println(testEvaluation);
        System.out.println("=====================================");
    }
}
