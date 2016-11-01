/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stratego.neural.net;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.io.FileWriter;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
/**
 * Creates a random dataset to test the training of the network on, which will look like the real data we will have to train the neural network on.
 * This dataset will be written to a CSV file, which can be read in to the neural network. If the file already exists, new data is appended to it. 
 * 
 * NOTE changing the pieces needs to be done MANUALLY in this class!
 * @author s146928
 */
public class DataGenerator {
    
    private static int total_fields= 12; // setting the total amount of fields the pieces can be placed in (try to not make this smaller than the total amount of pieces in the game)
    private static int data_points= 1000; // setting the number of datapoints we want to generate
    private static int possible_outcomes = 6; // setting the number of possible outcomes, or the total amount of defensive setups 
    private static Random random = new Random(); // random number generator
    public static DataSet dataset;
    private static String file_location = "src/Data/"; //C:/Users/s146928/Documents/NetBeansProjects/Stratego Neural Net/src/stratego/neural/net/
    private static String file_name = "proof_of_concept_data.csv";
    
    
    
    //Use this bit if you want to have the class run standalone and make a nice dataset to test stuff with
    
    public static void main(String[] args) throws Exception{
        
        int[][] attackerSetups = attackerSetups();
        int[] results = results(); //don't need at this time
        
        FileWriter fileWriter = new FileWriter(file_location+file_name,true);
        
        // adding all the datapoints to the file
        for(int i=0; i<data_points;i++){
            //first running through the pieces in the attacker setups
            for(int j=0;j<attackerSetups[i].length;j++){
                fileWriter.append(attackerSetups[i][j]+",");
            }
            fileWriter.append(""+results[i]);  // adding "" nothing since otherwise the thing thinks it is not a string 
            fileWriter.append("\n"); // starting a new line
            
        }
        fileWriter.flush();
        fileWriter.close();
        
        /*
        System.out.println("Testing...");
        
        
        System.out.println("Current fields: ");
        
        for(int i=0; i<attackerSetups.length;i++){
        
        System.out.print("[");
        for(int j=0; j < attackerSetups[i].length;j++){
            System.out.print(" "+attackerSetups[i][j]);
        }
        System.out.print("] best defender: "+results[i]);
        System.out.println();
        }
        
        System.out.println("Test Finished");
        */
        
        
    }
    
    
    
    //Turn this bit on if you want to use the constructor
    
    /*
    DataGenerator(int totalFields, int dataPoints, int possibleOutcomes){
        
        total_fields = totalFields;
        data_points = dataPoints;
        possible_outcomes = possibleOutcomes;
        
        
        double[][] attackerSetups = attackerSetups();
        double[] results = results();
           
        INDArray data = Nd4j.create(attackerSetups);
        INDArray labels = Nd4j.create(results);
        
        dataset = new DataSet(data, labels);
        
        /* Testing the output code
        System.out.println("Testing...");
        
        
        System.out.println("Current fields: ");
        
        for(int i=0; i<attackerSetups.length;i++){
        
        System.out.print("[");
        for(int j=0; j < attackerSetups[i].length;j++){
            System.out.print(" "+attackerSetups[i][j]);
        }
        System.out.print("] best defender: "+results[i]);
        System.out.println();
        }
        
        System.out.println("Test Finished");
        
     }
       */
     
    
    //A method which generates a completely random setup 
    private static int[] setup(){
        int ranks = 5; // Setting the amount of ranks in the game
        int[] field = new int[total_fields];
        List<Integer> board = new ArrayList<>(); // creates a new list for the board
        
        for(int i=0; i<total_fields; i++){
            board.add(i); // fills the list with the numbers associated to the places
        }       
        
                 
        GamePiece[] pieces = new GamePiece[ranks];
        
        
        //Generating the pieces array with the correct units and amounts
        pieces[0] = new GamePiece("Marshall",1,1);
        pieces[1] = new GamePiece("General",2,1);
        pieces[2] = new GamePiece("Miner",3,2);
        pieces[3] = new GamePiece("Scout",4,7);
        pieces[4] = new GamePiece("Spy",5,1);
        
        //for all available pieces
        for(int piece_number = 0; piece_number < pieces.length; piece_number++){
            
            //for the amount of this specific piece
            for(int i=0; i<pieces[piece_number].getNumber();i++){
                int index = random.nextInt(board.size()); // creates a random number between 0 and the size of the list
                field[board.get(index)] = pieces[piece_number].getRank(); // adds the rank of the piece to the place on the field retrieved from the list                   
                board.remove(index);
                }
            }       
                
     return field;
    }
    
    private static int[][] attackerSetups(){
        int[][] attacker_setups = new int[data_points][total_fields];
        
        for(int i=0; i<attacker_setups.length;i++){
            attacker_setups[i] = setup();
        }
        
        return attacker_setups;
    }
    
    //A method which randomly generates results for the setups
    private static int[] results(){
        int[] results = new int[data_points];
        
        for(int i = 0; i < results.length; i++){
            results[i] = random.nextInt(possible_outcomes);  // assigning a random outcome to the result, starting at 1 and ending at the maximum.
        }
                
        return results;      
    }
    
    public DataSet getData(){
        return dataset;
    }
    
}
