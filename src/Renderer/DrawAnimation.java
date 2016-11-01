/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Renderer;
import Game.GamePiece;
import Game.BoardPosition;

/**
 *  Class to represent a draw Animation (in which case both pieces die).
 */
public class DrawAnimation extends Animation{
    
    /**Boolean to decide whether to move the Camera (since only one of two will need to call this).*/
    private final boolean moveCam;
    
    public DrawAnimation (Terrain terrain, GamePiece subject, BoardPosition object, AnimationCallback call, boolean moveCam){
        super(terrain, subject, object, call);
        this.moveCam = moveCam;
    }
    
    /**
     * Method to execute this animation.
     */
    @Override
    public void execute(){
        startloc = new Vector(skel.offset);
        startloc.rotate(skel.getRotation());
        faceTarget();
        direction = new Vector(0,1,0);
        direction.rotate(skel.getRotation());
        Thread draw = new Thread(() -> {
            //Current Camera location.
            Vector center = terrain.camera.center;
            Vector eye = terrain.camera.eye;
            if (moveCam) {
                //Calculate camera location.
                Vector cameraloc = direction.cross(new Vector(0,0,1)).scale(-1);
                cameraloc = cameraloc.scale(3d/cameraloc.length());
                cameraloc.add(startloc);
                cameraloc.add(direction.scale(0.5));
                startloc.add(direction.scale(0.5));
                cameraloc.add(new Vector(0,0,4));            
                //Move Camera to location.
                moveCamera(cameraloc
                        ,new Vector(startloc.x, startloc.y, startloc.z+1));
                skel.showRank = true;
                startloc = new Vector(skel.offset);//re-init startloc for attack().
                //Animation iteration loop.
                for(int i=1; i <= (5d/3d)*duration; i++){
                    try {
                        synchronized(terrain.cs.refresh){
                            terrain.cs.refresh.wait();
                            if (i < (duration/3d)){//let sword appear
                                showSword(i);
                            }   else if (i < (4d/3d)*duration){//do attack animation.
                                attack (i - (duration/3d));
                            }   else {
                                //stall.
                            }
                        }
                    }   catch (Exception e){}
                }
                moveCamera(CameraState.defaultloc, CameraState.defaultcnt);
            }   else {//MOVECAM == FALSE.
                startloc = new Vector(skel.offset);//re-init startloc for attack().
                for(int i=1; i <= (8d/3d)*duration; i++){
                    try {
                        synchronized(terrain.cs.refresh){
                            terrain.cs.refresh.wait();
                            if (i >= duration){
                                showSword(i);
                                skel.showRank = true;
                            }
                            if (i > (4d/3d)*duration && i < (7d/3d)*duration){//trigger death animation.
                                attack(i - (4d/3d)*duration);
                            }
                        }
                    }   catch (Exception e){}
                }
            }
            //signal to end animation.
            if (moveCam){
                endAnimation();            
            }
        });
        draw.start();        
    }
    
    public void showSword(float frame){
        skel.swordOpacity = frame / 10;
    }
    
    public void attack(double frame){
        double c;
        if (frame <= (duration/2d)){//raise sword in the air.
            c = frame * (2d/duration);
            skel.shoulderL = new Vector(-0.2,c/10d,1.6);
            skel.shoulderR = new Vector(0.2,-c/10d,1.6);
            skel.elbowRRotX = 90d * c;
            skel.elbowLRotX = 45d * c;
            skel.shoulderRRotX = 90d * c;
            skel.shoulderRRotY = -45d * c;
        }   else if (frame <= (5d*duration/6d)){//swing sword at opponent.
            c = (frame - (duration/2d)) * (3d/duration);
            skel.shoulderL = new Vector(-0.2,0.1-(c/5d),1.6);
            skel.shoulderR = new Vector(0.2, -0.1+(c/5d),1.6);
            skel.elbowRRotX = 90d * (1-c);
            skel.elbowLRotX = 45d - 25d * c;
            skel.shoulderRRotX = 90d - 45d * c;
            skel.shoulderRRotY = -45d + 90d * c;
            skel.shoulderLRotX = -20d * c;
            skel.swordRotX = -80d * c;
        }   else {//die a painful death.
            c = (frame - (5d*duration/6d))*(6d/duration);
            skel.shoulderLRotX = -20d*(1-c);
            skel.shoulderRRotX = 45d*(1-c);
            skel.shoulderRRotY = 45d*(1-c);
            skel.elbowLRotX = 20d*(1-c);
            skel.hipLRotX = 20d*c;
            skel.hipRRotX = 20d*c;
            skel.kneeLRotX = -120d*c;
            skel.kneeRRotX = -120d*c;
            skel.shoulderL = new Vector(-0.2, -0.1+(c/10d), 1.6);
            skel.shoulderR = new Vector(0.2, 0.1-(c/10d), 1.6);
            skel.neck = new Vector(0, c/10d, 1.6);
            skel.head = new Vector(0, c/5d, 1.8);
            skel.offset = new Vector(startloc.x, startloc.y + 0.3*c, startloc.z - 0.3*c);
        }        
    }
}
