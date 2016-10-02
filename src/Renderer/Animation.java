/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Renderer;
import Game.GamePiece;
import Game.BoardPosition;

/**
 *  Class to represent an animation.
 */
public class Animation {
    
    public static int WALK = 0;
    public static int ATTACK = 1;
    public static int DIE = 2;
    
    GamePiece subject;
    
    int AnimType;
    
    Terrain terrain;
    
    BoardPosition target;
    
    /**
     * Constructor for an Animation.
     * @param subject piece executing the animation.
     * @param AnimType which animation to execute.
     * @param target where to move (in the case of walking) or what to attack (otherwise).
     * @param terrain the scene on which to operate (required for passing openGL variables).
     */
    public Animation(GamePiece subject, int AnimType, BoardPosition target, Terrain terrain){
        this.subject = subject;
        this.AnimType = AnimType;
        this.terrain = terrain;
        this.target = target;
    }
    
    /**
     * Method to (smoothly) move the camera into a position to observe the Animation.
     * Does not apply to walking animation.
     * @param eye Vector representing the goal location of the camera.
     * @param center Vector representing the goal focus point of the camera.
     */
    private void moveCamera (Vector eye, Vector center){
        /*
        Vector currenteye = new Vector(terrain.camera.eye);
        Vector currentorigin = new Vector(terrain.camera.center);
        Vector eyepath = new Vector(currenteye);
        eyepath.subtract(eye);
        Vector centerpath = new Vector(currentorigin);
        centerpath.subtract(center);
        float index;
        
        //move towards goal over a period of 2 seconds.
        float start = System.currentTimeMillis();
        float now = start;
        while(now - start < 2000){
            now = System.currentTimeMillis();
            index = (now-start)/2000;
            currenteye.x = eye.x - (1-index) * eyepath.x;
            currenteye.y = eye.y - (1-index) * eyepath.y;
            currenteye.z = eye.z - (1-index) * eyepath.z;
            currentorigin.x = center.x - (1-index) * centerpath.x;
            currentorigin.y = center.y - (1-index) * centerpath.y;
            currentorigin.z = center.z - (1-index) * centerpath.z;
            terrain.adjustCamera(currenteye,currentorigin);
        }
        //just to make sure it's in place correctly.
        terrain.adjustCamera(eye, center);*/
    }
    
    /**
     * Method to execute the Animation (represent it graphically).
     * To be overridden by subclasses.
     */
    public void execute(){
        
    }
    
    /**
     * Method to make the GamePiece face its target (before starting to walk/attack).
     */
    public void faceTarget(){
        int x = subject.getPosition().getX();
        int y = subject.getPosition().getY();
        if (target.getX() > x){
            //rotate to face forward.
        }   else if(target.getX() == x){
                if(target.getY() > y){
                    //rotate to face right.
                }   else {
                    //rotate to face left.
                }
        }   else {
            //rotate to face backwards.
        }
    }
}
