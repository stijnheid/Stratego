/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import Game.GameBoard;
import Game.GameState;
import Logic.Simulation;
import NeuralNetwork.NeuralNetwork;
import Renderer.SetupGUI;
import Renderer.Terrain;
import java.io.IOException;

/**
 * Main class which handles creating all the objects and running the game
 * @author s146928
 */
public  class Main {
    
    int[] attackersetup;
    GameBoard board;
    
    public Main() throws IOException{
        
        SetupGUI setupGUI = new SetupGUI(this); // creates a SetupGUI object, creating an attacker setup and storing this in the instance variable
        
        synchronized(this){
        try{
            wait();
        } catch (Exception e){
            
        }
    }
        //The neural net requires the CORRECT attacker setup, so in between here there needs to be a callback to make sure we don't run this code before the user is
        // done creating a setup 
        NeuralNetwork neuralnetwork = new NeuralNetwork(this, attackersetup); // creates a NeuralNetwork object, creating a defensive setup and a complete GameBoard, and storing this in the instance variable
        GameState gamestate = new GameState();
        gamestate.setGameBoard(board);
        
        Simulation simulation = new Simulation();
        
        //This also needs to happen sequentially, we first need all the crap above before we create a new Terrain
        
        Terrain terrain = new Terrain(gamestate,board,simulation);
        // Here we have the GameBoard object, use this to create the game itself
        // Something needs to call the animations too!
        
    }
    
    public static void main(String[] args){
         try{    
            Main main = new Main();
         } catch (Exception e){
             
         }
   
    }
    
    public void setAttackerSetup(int[] setup){
        this.attackersetup = setup;
    }
    
    public void setGameBoard(GameBoard board){
        this.board = board;
    }
    
    
}
