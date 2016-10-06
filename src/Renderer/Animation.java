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
    
    /*Duration of the animation (in frames).*/
    protected final static int duration = 30;
    
    /*GamePiece on which this animation acts.*/
    protected final GamePiece subject;
    
    /*Which animation to perform (WALK/ATTACK/DIE).*/
    protected final int AnimType;//one of [0,1,2].
    
    /*Terrain on which to perform animation (for openGL variables).*/
    protected final Terrain terrain;
    
    /*Goal position of this piece after the animation.*/
    protected final BoardPosition target;
    
    protected Skeleton skel;
    
    /**
     * Constructor for an Animation.
     * @param subject piece executing the animation.
     * @param AnimType which animation to execute.
     * @param target where to move (in the case of walking) or what to attack (otherwise).
     * @param terrain the scene on which to operate (required for passing openGL variables).
     */
    public Animation(Terrain terrain, GamePiece subject, int AnimType, BoardPosition target){
        this.subject = subject;
        this.AnimType = AnimType;
        this.terrain = terrain;
        this.target = target;
        try {
            this.skel = terrain.cs.pieces[subject.getPosition().getX()][subject.getPosition().getY()];    
        }   catch (Exception e){
            this.skel = new Skeleton(new BoardPosition(1,0));
        }
    }
    
    /**
     * Method to (smoothly) move the camera into a position to observe the Animation.
     * Does not apply to walking animation.
     * @param eye Vector representing the goal location of the camera.
     * @param center Vector representing the goal focus point of the camera.
     */
    public void moveCamera (Vector eye, Vector center){
        Thread pan = new Thread(() -> {
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
                //loop over a period of frames.
                for (int i=0; i<duration; i++){
                    try {//update on frame refresh.
                        synchronized(terrain.cs.refresh){
                            terrain.cs.refresh.wait();  
                            phi3 += (phi2-phi1)/duration;
                            theta3 += (theta2-theta1)/duration;
                            dist3 += (dist2-dist1)/duration;
                            terrain.cs.setCamera(phi3, theta3, dist3); 
                        }
                    }   catch (Exception e){}
                }
                //make sure camera is at the final location.
                terrain.cs.setCamera(phi2, theta2, dist2);  
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
        BoardPosition pos = skel.getPosition();
        int x = pos.getX();
        int y = pos.getY();
        if (target.getX() > x){
            skel.rotate(0);
        }   else if(target.getX() == x){
                if(target.getY() > y){
                    skel.rotate(-90);
                }   else {
                    skel.rotate(90);
                }
        }   else {
            skel.rotate(180);
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
