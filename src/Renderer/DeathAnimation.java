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
        faceTarget();
        startloc = new Vector(skel.offset);
        Thread die = new Thread(() -> {
            /*NO camera movement : already performed in the opponent's attack animation.*/
            //wait for camera movement and opponent to show sword (to sync up animations).
            for(int i=1; i <= (7d/3d)*duration; i++){
                try {
                    synchronized(terrain.cs.refresh){
                        terrain.cs.refresh.wait();
                        if (i >= duration){
                            skel.showRank = true;
                        }
                        if (i > (4d/3d)*duration){//trigger death animation.
                            die(i - (4d/3d)*duration);
                        }
                    }
                }   catch (Exception e){}
            }
            skel.showRank = false;
            //signal to end animation.
            endAnimation();
        });
        die.start();        
    }
    
    public void die(double frame){
        double c;
        if (frame > (duration/2d) && frame <= ((4d*duration)/6d)){//raise arms to protect.
            c = (frame-(duration/2d))*(6/duration);
            System.out.println(c);
            skel.shoulderLRotX = 110d * c;
            skel.shoulderLRotY = -30d * c;
            skel.shoulderRRotX = 110d * c;
            skel.shoulderRRotY = 30d * c;
            skel.elbowLRotX = 90d * c;
            skel.elbowLRotY = -45d * c;
            skel.elbowRRotX = 90d * c;
            skel.elbowRRotY = 45d * c;
        }   else if (frame > ((5d*duration)/6d)){//fall to the ground.
            c = (frame-((5d*duration)/6d))*(6/duration);
            System.out.println(c);
            skel.shoulderLRotX = 110d*(1-c);
            skel.shoulderLRotY = -30d*(1-c);
            skel.shoulderRRotX = 110d*(1-c);
            skel.shoulderRRotY = 30d*(1-c);
            skel.elbowLRotX = 90d*(1-c);
            skel.elbowLRotY = -45d*(1-c);
            skel.elbowRRotX = 90d*(1-c);
            skel.elbowRRotY = 45d*(1-c);
            skel.hipLRotX = 20d*c;
            skel.hipRRotX = 20d*c;
            skel.kneeLRotX = -120d*c;
            skel.kneeRRotX = -120d*c;
            skel.shoulderL = new Vector(-0.2, c/10d, 1.6);
            skel.shoulderR = new Vector(0.2, c/10d, 1.6);
            skel.neck = new Vector(0, c/10d, 1.6);
            skel.head = new Vector(0, c/5d, 1.8);
            skel.offset = new Vector(startloc.x, startloc.y + 0.3*c, startloc.z - 0.3*c);
        }
    }
    
}
