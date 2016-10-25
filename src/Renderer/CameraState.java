/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Renderer;
import Game.BoardPosition;
import Game.Team;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Map;
import java.util.HashMap;

/**
 *  Class holding data regarding openGL viewing (camera, centre, etc).
 */
public class CameraState {
    
    public static Vector defaultloc = new Vector(0, 7, 7);
    public static Vector defaultcnt = new Vector(0, 0, 0);
    
    public float tAnim;         // Time since start of animation in seconds.
    private long frameCount;     //Amount of frames having been drawn since init.
    
    public final Object refresh;
    public final Lock varLock;
    
    public int w;               // Width of window in pixels.
    public int h;               // Height of window in pixels.
    
    public Vector cnt;          // Center point.
    public double vDist;         // Distance eye point to center point.
    public double vWidth;        // Width of scene to be shown.
    public double theta;         // Azimuth angle in radians.
    public double phi;           // Inclination angle in radians.
    
    /** Constructor with default settings.*/
    public CameraState(){
        vDist = 10d;
        vWidth = 10d;
        cnt = new Vector(0,0,0);
        theta = Math.PI/2;// camera starts at (0, vDist/sqrt(2), vDist/sqrt(2)).
        phi = Math.PI/5;// 45 degree angle.
        tAnim = -1;
        frameCount = 0;
        refresh = new Object();
        varLock = new ReentrantLock();
    }
    
    public void setCamera(double phi, double theta, double vDist, Vector cnt){
        try {
            varLock.lock();
            this.phi = phi;
            this.theta = theta;
            this.vDist = vDist;  
            this.cnt = cnt;
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