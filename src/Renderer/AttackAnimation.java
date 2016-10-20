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
     * @param frame frame of the animation (from 1 to 30).
     */
    public void showRank(){
        skel.showRank = true;
    }
    
    /**
     * Method to perform an attack animation.
     * @param frame frame of the animation (from 1 to 30).
     */
    public void attack(double frame){
        if (frame <= 15){//raise sword in the air.
            skel.shoulderL = new Vector(-0.2,(frame/150),1.6);
            skel.shoulderR = new Vector(0.2,-(frame/150),1.6);
            skel.elbowRRotX = 6*frame;
            skel.elbowLRotX = 3*frame;
            skel.shoulderRRotX = 6*frame;
            skel.shoulderRRotY = -3*frame;
        }   else if (frame <= 25){//swing sword at opponent.
            skel.shoulderL = new Vector(-0.2,0.1-((frame-15)/50),1.6);
            skel.shoulderR = new Vector(0.2, -0.1+((frame-15)/50),1.6);
            skel.elbowRRotX = 90 - ((frame-15)*4.5);
            skel.elbowLRotX = 45 - ((frame-15)*2.5);
            skel.shoulderRRotX = 90 - ((frame-15)*4.5);
            skel.shoulderRRotY = -45 + ((frame-15)*9);
            skel.shoulderLRotX = -2*(frame-15);
            skel.swordRotX = -6*(frame-15);
        }   else {//return to rest position.
            skel.shoulderL = new Vector(-0.2,-0.1+((frame-25)/50),1.6);
            skel.shoulderR = new Vector(0.2,0.1-((frame-25)/50),1.6);
            skel.elbowRRotX = 45 - ((frame-25)*9);
            skel.elbowLRotX = 20 - ((frame-25)*4);
            skel.shoulderRRotX = 45 - ((frame-25)*9);
            skel.shoulderRRotY = 45 - ((frame-25)*9);
            skel.shoulderLRotX = -20 + ((frame-25)*4);
            skel.swordRotX = -60 + ((frame-25)*12);
        }
    }
}
