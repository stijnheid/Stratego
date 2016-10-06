/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Renderer;
import Game.BoardPosition;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *  Class holding data regarding openGL viewing (camera, centre, etc).
 */
public class CameraState {
    
    public float tAnim;         // Time since start of animation in seconds.
    private long frameCount;     //Amount of frames having been drawn since init.
    
    public final Object refresh;
    public final Lock varLock;
    
    public Skeleton[][] pieces;
    
    public int w;               // Width of window in pixels.
    public int h;               // Height of window in pixels.
    
    public Vector cnt;          // Center point.
    public float vDist;         // Distance eye point to center point.
    public float vWidth;        // Width of scene to be shown.
    public float theta;         // Azimuth angle in radians.
    public float phi;           // Inclination angle in radians.
    
    /** Constructor with default settings.*/
    public CameraState(){
        vDist = 10f;
        vWidth = 10f;
        cnt = new Vector(0,0,0);
        theta = (float)-Math.PI/2;
        phi = 0.5f;
        tAnim = -1;
        frameCount = 0;
        refresh = new Object();
        varLock = new ReentrantLock();
        
        pieces = new Skeleton[6][6];
        double offset = 2.5;
        for(int i=0; i < 6; i++){
            if(i==0 || i==1){
                for(int j=0; j<6; j++){
                    pieces[i][j] = new Skeleton(new BoardPosition(j,i));
                }
            }   else if(i==4 || i==5){
                for(int j=0;j<6; j++){
                    pieces[i][j] = new Skeleton(new BoardPosition(j,i));
                    pieces[i][j].rotate(180);
                }
            }
        }
    }
    
    public void setCamera(float phi, float theta, float vDist){
        try {
            varLock.lock();
            this.phi = phi;
            this.theta = theta;
            this.vDist = vDist;        
        } finally {
            varLock.unlock();        
        }
    }
    
    public void frameTick(){
        frameCount++;
        synchronized(refresh){
            refresh.notifyAll();
        }
    }
    
    public long frameCount(){
        return frameCount;
    }
    
}
