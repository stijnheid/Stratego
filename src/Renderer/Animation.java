/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Renderer;
import Game.GamePiece;
import Game.BoardPosition;
import Game.Team;

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
    public void moveCamera (Vector eye, Vector center){
        Thread pan = new Thread(new Runnable() {
            public void run() {
                //current Camera variables.
                float phi1 = terrain.cs.phi;
                float theta1 = terrain.cs.theta;
                float dist1 = terrain.cs.vDist;
                //goal Camera variables.
                float phi2 = (float) getPhi(eye, center);
                float theta2 = (float) getTheta(eye, center);
                float dist2 = (float) getDist(eye, center);
                //Camera variables during transit.
                float phi3 = phi1, theta3 = theta1, dist3 = dist1;
                //loop over a period of 60 frames.
                for (int i=0; i<60; i++){
                    try {//update on frame refresh.
                        synchronized(terrain.cs.refresh){
                            terrain.cs.refresh.wait();  
                            phi3 += (phi2-phi1)/60;
                            theta3 += (theta2-theta1)/60;
                            dist3 += (dist2-dist1)/60;
                            terrain.cs.setCamera(phi3, theta3, dist3); 
                        }
                    }   catch (Exception e){}
                }
                //make sure camera is at the final location.
                terrain.cs.setCamera(phi2, theta2, dist2);  
                try {
                                    Thread.sleep(500);
                }catch (Exception e){
                    
                }
                terrain.cs.setCamera(phi1, theta1, dist1);
            }
        });
        pan.start();
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
    
    /**
     * Returns the theta for the vector eye.
     * @param eye camera eye.
     * @param center camera centre;
     * @return 
     */
    private double getTheta(Vector eye, Vector center){
        double theta;
        eye.subtract(center);
        theta = Math.atan(eye.x/eye.y);
        eye.add(center);
        return theta;
    }
    
    /**
     * Returns the phi for the vector eye.
     * @param eye camera eye.
     * @param center camera centre;
     * @return 
     */
    private double getPhi(Vector eye, Vector center){
        double phi;
        eye.subtract(center);
        phi = Math.asin(eye.z()/eye.length());
        eye.add(center);
        return phi;
    }
    
    /**
     * Returns distance from eye to center vectors.
     * @param eye camera eye.
     * @param center camera centre.
     * @return 
     */
    private double getDist(Vector eye, Vector center){
        eye.subtract(center);
        Vector diff = new Vector(eye);
        eye.add(center);
        return diff.length();
    }
}
