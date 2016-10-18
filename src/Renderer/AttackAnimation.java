/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Renderer;
import Game.GamePiece;
import Game.BoardPosition;

/**
 *
 */
public class AttackAnimation extends Animation {
    
    public AttackAnimation(Terrain terrain, GamePiece subject, BoardPosition target, AnimationCallback call){
        super(terrain, subject, target, call);
    }
    
    @Override
    public void execute(){
        startloc = skel.offset;
        faceTarget();
        direction = new Vector(0,1,0);
        direction.rotate(skel.getRotation());
        Thread attack = new Thread(() -> {
            //Current camera location.
            Vector center = terrain.camera.center;
            Vector eye = terrain.camera.eye;
            //move Camera to location.
            moveCamera(new Vector(skel.offset.x+5, skel.offset.y, skel.offset.z+5)
                    ,new Vector(skel.offset.x, skel.offset.y, skel.offset.z+1));
            //first let sword appear.
            for(int i=1; i <= (duration/3); i++){
                try {
                    synchronized(terrain.cs.refresh){
                        terrain.cs.refresh.wait();
                        showSword(i);
                        showRank();
                    }
                }   catch (Exception e){}
            }
            //now attack opponent.
            for(int i=1; i <= duration; i++){
                try {
                    synchronized(terrain.cs.refresh){
                        terrain.cs.refresh.wait();
                        attack(i);
                    }
                }   catch (Exception e){}
            }
            skel.showRank = false;
            //return skeleton to default position without sword.
            faceForward();
            skel.swordOpacity = 0;
            //move Camera back to original location.
            moveCamera(eye, center);
            //signal to end animation.
            endAnimation();
        });
        attack.start();
    }
    
    /**
     * Method to fade in sword (from 0 to 1 opacity).
     * @param frame frame of the animation (from 1 to 30).
     */
    public void showSword(float frame){
        skel.swordOpacity = frame / 10;
    }
    
    /**
     * Method to show the rank.
     */
    public void showRank(){
        skel.showRank = true;
    }
    
    /**
     * Method to perform an attack animation.
     * @param frame frame of the animation (in [1,duration]).
     */
    public void attack(double frame){
        if (frame <= (duration/2)){//raise sword in the air.
            skel.shoulderL = new Vector(-0.2,(frame/(duration*5d)),1.6);
            skel.shoulderR = new Vector(0.2,-(frame/(duration*5d)),1.6);
            skel.elbowRRotX = (180d/duration)*frame;
            skel.elbowLRotX = (90d/duration)*frame;
            skel.shoulderRRotX = (180d/duration)*frame;
            skel.shoulderRRotY = (-90d/duration)*frame;
        }   else if (frame <= (5d*duration/6d)){//swing sword at opponent.
            skel.shoulderL = new Vector(-0.2,0.1-((frame-(duration/2d))/(5d*duration/3d)),1.6);
            skel.shoulderR = new Vector(0.2, -0.1+((frame-(duration/2d))/(5*duration/3d)),1.6);
            skel.elbowRRotX = 90d - ((frame-(duration/2d))*(135d/duration));
            skel.elbowLRotX = 45d - ((frame-(duration/2d))*(75d/duration));
            skel.shoulderRRotX = 90d - ((frame-(duration/2d))*(135d/duration));
            skel.shoulderRRotY = -45d + ((frame-(duration/2d))*(180d/duration));
            skel.shoulderLRotX = -(60d/duration)*(frame-(duration/2d));
            skel.swordRotX = -(180d/duration)*(frame-(duration/2d));
        }   else {//return to rest position.
            skel.shoulderL = new Vector(-0.2,-0.1+((frame-(5d*duration/6d))/(5d*duration/3d)),1.6);
            skel.shoulderR = new Vector(0.2,0.1-((frame-(5d*duration/6d))/(5d*duration/3d)),1.6);
            skel.elbowRRotX = 45d - ((frame-(5d*duration/6d))*(270d/duration));
            skel.elbowLRotX = 20d - ((frame-(5d*duration/6d))*(120d/duration));
            skel.shoulderRRotX = 45d - ((frame-(5d*duration/6d))*(270d/duration));
            skel.shoulderRRotY = 45d - ((frame-(5d*duration/6d))*(270d/duration));
            skel.shoulderLRotX = -20d + ((frame-(5d*duration/6d))*(120d/duration));
            skel.swordRotX = -60d + ((frame-(5d*duration/6d))*(360d/duration));
        }
    }
}
