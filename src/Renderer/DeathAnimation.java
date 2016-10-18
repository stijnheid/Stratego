/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Renderer;
import Game.GamePiece;
import Game.BoardPosition;

/**
 * Animation to enact the death of a piece.
 */
public class DeathAnimation extends Animation {
    
    public DeathAnimation(Terrain terrain, GamePiece subject, BoardPosition target, AnimationCallback call){
        super(terrain, subject, target, call);
    }
    
    @Override
    public void execute(){
        startloc = skel.offset;
        faceTarget();
        direction = new Vector(0,1,0);
        direction.rotate(skel.getRotation());
        Thread die = new Thread(() -> {
            //Current camera location.
            Vector center = terrain.camera.center;
            Vector eye = terrain.camera.eye;
            //move Camera to location.
            moveCamera(new Vector(skel.offset.x+5, skel.offset.y, skel.offset.z+5)
                    ,new Vector(skel.offset.x, skel.offset.y, skel.offset.z+1));
            //wait for opponent to show sword (to sync up animations).
            for(int i=1; i <= (duration/3); i++){
                try {
                    synchronized(terrain.cs.refresh){
                        terrain.cs.refresh.wait();
                    }
                }   catch (Exception e){}
            }
            //now attack opponent.
            for(int i=1; i <= duration; i++){
                try {
                    synchronized(terrain.cs.refresh){
                        terrain.cs.refresh.wait();
                        die(i);
                    }
                }   catch (Exception e){}
            }
            //move Camera back to original location.
            moveCamera(eye, center);
            //signal to end animation.
            endAnimation();
        });
        die.start();        
    }
    
    public void die(int frame){
        if (frame >= (duration/2d) && frame <= ((5d*duration)/6d)){//raise arms to protect.
            
        }   else {//fall to the ground.
            
        }
    }
    
}
