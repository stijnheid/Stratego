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
    
    /*Duration of the animation (in frames).*/
    protected final static int duration = 30;
    
    /*Position of Skeleton on which this animation acts.*/
    protected final BoardPosition subject;
    
    /*Terrain on which to perform animation (for openGL variables).*/
    protected final Terrain terrain;
    
    /*Goal position of this piece after the animation.*/
    protected final BoardPosition target;
    
    protected Skeleton skel;
    
    /*Direction of animation.*/
    protected Vector direction;
    
    /*Start location of animation.*/
    protected Vector startloc;
    
    protected AnimationCallback call;
    
    /**
     * Constructor for an Animation.
     * @param subject piece executing the animation.
     * @param target where to move (in the case of walking) or what to attack (otherwise).
     * @param terrain the scene on which to operate (required for passing openGL variables).
     */
    public Animation(Terrain terrain, BoardPosition subject, BoardPosition target, AnimationCallback callback){
        this.subject = subject;
        this.terrain = terrain;
        this.target = target;
        this.call = callback;
        try {
            this.skel = terrain.cs.pieces.get(6*subject.getX() + subject.getY());
        }   catch (Exception e){
            //get the piece at cell (4,0).
            this.skel = terrain.cs.pieces.get(24);
        }
        startloc = skel.offset;
        direction = new Vector(0,1,0);
        direction.rotate(skel.getRotation());
    }
    
    /**
     * Method to (smoothly) move the camera into a position to observe the Animation.
     * Does not apply to walking animation.
     * @param eye Vector representing the goal location of the camera.
     * @param center Vector representing the goal focus point of the camera.
     */
    public void moveCamera (Vector eye, Vector center){
                //current Camera variables.
                double phi1 = terrain.cs.phi;
                double theta1 = terrain.cs.theta;
                double dist1 = terrain.cs.vDist;
                Vector cnt1 = terrain.cs.cnt;
                //goal Camera variables.
                double phi2 = (float) getPhi(eye, center);
                double theta2 = (float) getTheta(eye, center);
                double dist2 = (float) getDist(eye, center);
                Vector cnt2 = terrain.cs.cnt;
                //Camera variables during transit.
                double phi3 = phi1, theta3 = theta1, dist3 = dist1;
                Vector cnt3 = new Vector(center);
                cnt3.subtract(cnt1);
                cnt3 = cnt3.scale(1d/(double)duration);
                //loop over a period of frames.
                for (int i=0; i<duration; i++){
                    try {//update on frame refresh.
                        synchronized(terrain.cs.refresh){
                            terrain.cs.refresh.wait();  
                            phi3 += (phi2-phi1)/duration;
                            theta3 += (theta2-theta1)/duration;
                            dist3 += (dist2-dist1)/duration;
                            cnt2.add(cnt3);
                            terrain.cs.setCamera(phi3, theta3, dist3, cnt2); 
                        }
                    }   catch (Exception e){}
                }
                //make sure camera is at the final location.
                terrain.cs.setCamera(phi2, theta2, dist2, cnt2);  
    }
    
    /**
     * Method to execute the Animation (represent it graphically).
     * To be overridden by subclasses.
     */
    public void execute(){
        
    }
    
    public void endAnimation(){
        if (call != null){
            call.animationEnded();        
        }
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
    
    public void faceForward(){
        if (skel.team == Team.RED){
            skel.rotate(0);
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
        theta = Math.atan(eye.y/eye.x);
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
     * Returns distance from eye to centre vectors.
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
