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
        startloc = new Vector(skel.offset);
        startloc.rotate(skel.getRotation());
        faceTarget();
        direction = new Vector(0,1,0);
        direction.rotate(skel.getRotation());
        Thread attack = new Thread(() -> {
            //Current camera location.
            Vector center = terrain.camera.center;
            Vector eye = terrain.camera.eye;
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
            startloc = new Vector(skel.offset);//re-init for attack().
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
     * Method to perform an attack animation.
     * @param frame frame of the animation (in [1,duration]).
     */
    public void attack(double frame){
        double c;
        if (frame <= (duration/2d)){//raise sword in the air.
            c = frame * (2d/duration);
            skel.shoulderL = new Vector(-0.2,c/10,1.6);
            skel.shoulderR = new Vector(0.2,-c/10,1.6);
            skel.elbowRRotX = 90 * c;
            skel.elbowLRotX = 45 * c;
            skel.shoulderRRotX = 90 * c;
            skel.shoulderRRotY = -45 * c;
            skel.hipLRotX = 30d * c;
            skel.hipRRotX = -30d * c;
            skel.kneeLRotX = -30d * c;
            skel.offset = new Vector(startloc.x, startloc.y + 0.3*c, startloc.z);
        }   else if (frame <= (5d*duration/6d)){//swing sword at opponent.
            c = (frame - (duration/2d)) * (3d/duration);
            skel.shoulderL = new Vector(-0.2,0.1-(c/5d),1.6);
            skel.shoulderR = new Vector(0.2, -0.1+(c/5d),1.6);
            skel.elbowRRotX = 90d - 45d * c;
            skel.elbowLRotX = 45d - 25d * c;
            skel.shoulderRRotX = 90d - 45d * c;
            skel.shoulderRRotY = -45d + 90d * c;
            skel.shoulderLRotX = -20d * c;
            skel.swordRotX = -80d * c;
        }   else {//return to rest position.
            c = (frame - (5d/6d)*duration) * (6d/duration);
            skel.shoulderL = new Vector(-0.2,-0.1+(c/10d),1.6);
            skel.shoulderR = new Vector(0.2,0.1-(c/10d),1.6);
            skel.elbowRRotX = 45d * (1-c);
            skel.elbowLRotX = 20d * (1-c);
            skel.shoulderRRotX = 45d * (1-c);
            skel.shoulderRRotY = 45d * (1-c);
            skel.shoulderLRotX = -20d * (1-c);
            skel.swordRotX = -80d * (1-c);
            skel.offset = new Vector(startloc.x, startloc.y + 0.3*(1-c), startloc.z);
            skel.hipLRotX = 30d * (1-c);
            skel.hipRRotX = -30d * (1-c);
            skel.kneeLRotX = -30d * (1-c);
        }
    }
}