/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.training;

import Game.BoardPosition;
import Game.GameBoard;
import Game.InvalidPositionException;
import Game.Pieces;
import static Game.Pieces.CAPTAIN;
import static Game.Pieces.COLONEL;
import static Game.Pieces.GENERAL;
import static Game.Pieces.LIEUTENANT;
import static Game.Pieces.MAJOR;
import static Game.Pieces.MARSHALL;
import static Game.Pieces.MINER;
import static Game.Pieces.SPY;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author s122041
 */
public class DataPointGenerator {
    private int[] setup;
    private AttackerSetupListener listener;
    
    public DataPointGenerator() throws IOException {
        
    }
    
    public static void main(String[] args) throws IOException {
        new DataPointGenerator().generateData(100);
    }
    
    public void setListner(AttackerSetupListener listener) {
        this.listener = listener;
    }
    
    private static List<Pieces> getArmyComposition() {
        Pieces[] army = new Pieces[] { 
            MARSHALL, SPY, MINER, GENERAL, LIEUTENANT, COLONEL, MAJOR, CAPTAIN, CAPTAIN, LIEUTENANT, MINER, MINER};
        List<Pieces> list = new ArrayList<>();
        for(Pieces type : army) {
            list.add(type);
        }
        
        return list;
        //return Arrays.asList(army); // Return a List that does not support remove()
    }
    
    private void generateData(int rounds) throws IOException {
        for (int i = 0; i < rounds; i++) {
            new DataPointGenerator().createOffensiveSetup(getArmyComposition());
        }
    }
    
    private void createOffensiveSetup(List<Pieces> army) throws IOException {
        setup = new int[0];
        SecureRandom random = new SecureRandom();
        
        String csvFilename = "src/csv/setup.csv";
        FileWriter writer = new FileWriter(csvFilename, true);
        StringBuilder builder = new StringBuilder();
        
        
        while(!army.isEmpty()) {
            int index = random.nextInt(army.size());
            Pieces type = army.get(index);
            String symbol = type.getPieceSymbol();
            army.remove(index);
            
            setup = addInt(setup, Integer.parseInt(symbol));
        }
        
        System.out.println(Arrays.toString(setup));
   
        for (int i = 0; i < setup.length; i++) {
            builder.append(setup[i]);             
            builder.append(",");
        }
        
        writer.append(builder.toString());
        writer.append("\n");
        writer.flush();
        writer.close();
    }
    
    public static int[] addInt(int[] setup, int newPiece){
        //create a new array with extra index
        int[] newSeries = new int[setup.length + 1];

        //copy the integers from series to newSeries    
        for (int i = 0; i < setup.length; i++){
            newSeries[i] = setup[i];
        }
        
        //add the new integer to the last index     
        newSeries[newSeries.length - 1] = newPiece;

        return newSeries;

     }
}
