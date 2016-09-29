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
    
    public static int ANIM_WALK = 0;
    public static int ANIM_ATTACK = 1;
    public static int ANIM_DIE = 2;
    
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
        Vector eyeorigin = terrain.camera.eye;
        Vector centerorigin = terrain.camera.center;
        //Vector centerpath = centerorigin.subtract(center);
        //Vector eyepath = eyeorigin.subtract(eye);
        Vector currenteye, currentcenter;
        
        //move towards goal over a period of 2 seconds.
        float start = System.currentTimeMillis();
        float now = start;
        while(now - start < 2000){
            now = System.currentTimeMillis();
            //currenteye = eyeorigin.add(eyepath.scale((now-start)/2000));
            //currentcenter = centerorigin.add(centerpath.scale((now-start/2000)));
            //terrain.adjustCamera(currenteye, currentcenter);
        }
    }
    
    /**
     * Method to execute the Animation (represent it graphically).
     */
    public void execute(){
        
    }
}
