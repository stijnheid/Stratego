/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NeuralNetwork;

import Game.GameBoard;
import java.io.IOException;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import Main.Main;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

/**
 * This class handles retrieving the neural network from a file, predicting the best defensive setup based on the attacker setup,
 * creating a string that accommodates the loadBoard function in GameBoard, and finally creating a GameBoard object that is passed 
 * to the Main object.
 * 
 * This class takes as input a Main class and an integer array containing the attacker setup.
 * 
 * @author s146928
 */
public class NeuralNetwork {
    
    private final String[] attackersetup;

    private MultiLayerNetwork network;
    private Main main;
 
    
    public NeuralNetwork(Main main, String[] setup) throws IOException{
        this.main = main;
        this.attackersetup = setup;
        MultiLayerNetwork network = ModelSerializer.restoreMultiLayerNetwork("src/NeuralNetwork/network.zip");        //Loads in the neural network trained before hand from a file
    }
    /*
    Uses the neural network to predict the defender setup
    Takes an integer array containing the attacker setup as a parameter
    */
    public int predict(){
        
        int[] numberedattacksetup = generateAttackerSetup(attackersetup);
        
        INDArray attacksetup = Nd4j.create(numberedattacksetup); // generates an INDArray object from the integer array setup, to be used in the neural network
        int[] result = network.predict(attacksetup); // predicting the best defender setup with the neural network. This generates an array of classifications for each input INDArray
                                                     // Which in this case is just on value but there will only be one value in it
       // String board = boardGenerator(numberedattackersetup, defendersetup[result[0]]); // Generates a board string from the attacker setup and the predicted best defender setup
       // GameBoard gameboard = GameBoard.loadBoard(board, width, height); // creates a gameboard object based on the string representation
       return result[0];
    }
    
    /*
    *A method that takes a setup in string format and interprets this as numbers, since the neural network does not like strings, only numbers.
    */
    public int[] generateAttackerSetup(String[] setup){
        int attackersetup[] = new int[setup.length];
        
        //Parses the string array, and changes the letters in the string to the correct number
        for(int i=0; i<setup.length;i++){
            if(setup[i].equals("S")){
                attackersetup[i] = 0; // sets the value to 0 if the piece is a spy
            } else {
                attackersetup[i] = Integer.parseInt(setup[i]); // parses the number in the string to an integer
            }
   
        }
        
        return attackersetup;        
    }
}
    
    
    
    
   
