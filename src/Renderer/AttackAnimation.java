/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Renderer;
import Game.BoardPosition;

/**
 *
 */
public class AttackAnimation extends Animation {
    
    public AttackAnimation(Terrain terrain, BoardPosition subject, BoardPosition target, AnimationCallback call){
        super(terrain, subject, target, call);
    }
    
    @Override
    public void execute(){
        startloc = skel.offset;
        faceTarget();
        direction = new Vector(0,1,0);
        direction.rotate(skel.getRotation());
        Thread walk = new Thread(() -> {
            //Current camera location.
            Vector center = terrain.camera.center;
            Vector eye = terrain.camera.eye;
            //move Camera to location.
            moveCamera(new Vector(skel.offset.x+5, skel.offset.y, skel.offset.z+5)
                    ,new Vector(skel.offset.x, skel.offset.y, skel.offset.z+1));
            //first let sword appear.
            for(int i=1; i <= duration; i++){
                try {
                    synchronized(terrain.cs.refresh){
                        terrain.cs.refresh.wait();
                        showSword(i);
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
            //return skeleton to default position without sword.
            faceForward();
            skel.swordOpacity = 0;
            //move Camera back to original location.
            moveCamera(eye, center);
        });
        walk.start();
        //update CameraState variable to account for moved piece.
        synchronized (terrain.cs.pieces){
            terrain.cs.pieces.remove(skel.mapindex);
            int index =  skel.position.getX() + 6 * skel.position.getY();
            terrain.cs.pieces.put(index,skel);
            skel.mapindex = index;        
        }
    }
    
    /**
     * Method to fade in sword (from 0 to 1 opacity).
     * @param frame frame of the animation (from 1 to 30).
     */
    public void showSword(int frame){
        skel.swordOpacity += (double)frame / 30;
    }
    
    /**
     * Method to perform an attack animation.
     * @param frame frame of the animation (from 1 to 30).
     */
    public void attack(int frame){
        
    }
}
