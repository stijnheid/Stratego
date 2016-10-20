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
    
    private final int[] attackersetup;
    private final String[][] defendersetup = new String[][]{     //All the defender setups have been hand crafted. The first six values are the front row, the second six are the back row.
        {"B","6","4","7","4","B","F","B","9","5","5","4"}, // First defender setup
        {"4","B","B","5","5","4","B","F","4","9","7","6"}, // Second defender setup
        {"6","9","7","B","4","4","B","F","5","4","5","B"}, // Third defender setup
        {"4","7","6","9","B","B","B","F","5","5","4","4"}, // Fourth defender setup
        {"4","B","5","6","4","B","5","4","9","7","B","F"}, // Fifth defender setup
        {"B","4","6","9","4","F","B","B","4","7","5","5"}, // Sixth defender setup
    }; 
    private MultiLayerNetwork network;
    private Main main;
    private final int width = 6;
    private final int height = 6;
    
    public NeuralNetwork(Main main, int[] setup) throws IOException{
        this.main = main;
        this.attackersetup = setup;
        MultiLayerNetwork network = ModelSerializer.restoreMultiLayerNetwork("network.zip");        //Loads in the neural network trained before hand from a file
        predict();
    }
    /*
    Uses the neural network to predict the defender setup
    Takes an integer array containing the attacker setup as a parameter
    */
    public void predict(){
        INDArray attacksetup = Nd4j.create(attackersetup); // generates an INDArray object from the integer array setup, to be used in the neural network
        int[] result = network.predict(attacksetup); // predicting the best defender setup with the neural network. This generates an array of classifications for each input INDArray
                                                     // Which in this case is just on value but there will only be one value in it
        String board = boardGenerator(attackersetup, defendersetup[result[0]]); // Generates a board string from the attacker setup and the predicted best defender setup
        GameBoard gameboard = GameBoard.loadBoard(board, width, height); // creates a gameboard object based on the string representation
        main.setGameBoard(gameboard); // sets the gameboard in the Main class
    }
    /*
    A method that takes arrays of the attacker (integer array) and the defender (string array)
    and generates a board setup string, to be fed into the loadBoard method of the GameBoard class.
    The attacker is at the top end of the board, so the back row is above the front row.         
    The defender is at the bottom of the board, so the back row is behind the front row. 
    
        E.G: attacker: | 1 | 2 | 3 |
                        --- --- ---
                       | 4 | 5 | 6 |
                        --- --- ---
                       |   |   |   |
                        --- --- ---
                       |   |   |   |
                        --- --- ---
             defender: | 1 | 2 | 3 |
                        --- --- ---
                       | 4 | 5 | 6 |         
    */
    private String boardGenerator(int[] attack, String[] defend){
        String separator = "--- --- --- --- --- ---\n";
        String emptyrow =  "   |   |   |   |   |   \n";
        int[] backRow = new int[width]; // creating an array for the back row
        int[] frontRow = new int[width];// creating an array for the front row
        
        String[] defBackRow = new String[width]; //creating an array for the defender back row
        String[] defFrontRow = new String[width]; // creating an array for the defender front row
        
        //Looping over the attacker and defender arrays, picking the first half (equal to the width of the board) for the back row of the attacker, and the front row of the defender
        for(int i=0; i<width; i++){   
            backRow[i] = attack[i];
            defFrontRow[i] = defend[i];
        }
        
        //Looping over the attacker and defender array, picking the second half for the front row for the attacker, and the second half for the back row for the defender; 
        for(int i=width; i< attack.length;i++){
            frontRow[i-width] = attack[i];
            defBackRow[i-width] = defend[i];
        }
        
        String attackerBackRow = rowGenerator(backRow);
        String attackerFrontRow = rowGenerator(frontRow);
        
        String defenderBackRow = rowGenerator(defBackRow);
        String defenderFrontRow = rowGenerator(defFrontRow);
        
        
        String board = attackerBackRow+     // First row of the board, back row of the attacker
                       separator+
                       attackerFrontRow+    // Second row of the board, front row of the attacker
                       separator+ 
                       emptyrow+            // Third row of the board, empty
                       separator+
                       emptyrow+            // Fourth row of the board, empty
                       separator+
                       defenderFrontRow+    // Fifth row of the board, front row of the defender
                       separator+
                       defenderBackRow;     // Sixth row of the board, back row of the defender
        return board; // returns the board
    }
    /*
    Takes a row of the setup (integer array) and generates a row for the attacker setup 
    This is only ever called for the attacker, which is the red team
    */
    private String rowGenerator(int[] setuprow){
        String row = "";
        for(int i=0; i<width;i++){
            row = row+"r:"+setuprow[i]; // adds r: plus the number from the array to the string
            if(i<width-1){
                row = row+"|"; // adds the | separator, except for the last cell
            }
        }
        row = row+"\n"; // creates a new line
        
        return row; 
    }
    /*
    Takes a row of the setup (string array) and generates a row for the defender setup.
    This is only ever called for the defende, which is the blue team.
    */
    private String rowGenerator(String[] setuprow){
        String row = "";
        for(int i=0; i<width;i++){
            row = row+"b:"+setuprow[i]; // adds b: plus the number from the array to the string
            if(i<width-1){
                row = row+"|"; // adds the | separator, except for the last cell
            }
        }
        row = row+"\n"; // creates a new line
        
        return row; 
    }
    
    public String[][] getDefesiveSetups(){
        return defendersetup;
    }    
}
