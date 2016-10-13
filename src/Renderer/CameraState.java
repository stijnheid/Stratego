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
    
    public float tAnim;         // Time since start of animation in seconds.
    private long frameCount;     //Amount of frames having been drawn since init.
    
    public final Object refresh;
    public final Lock varLock;
    
    /*Variable containing the skeletons of the teams.
    Board cells are counted in reading order starting top left = 0.*/
    public Map<Integer,Skeleton> pieces;
    
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
        theta = 0d;// camera starts at (0, vDist/sqrt(2), vDist/sqrt(2)).
        phi = Math.PI/4;// 45 degree angle.
        tAnim = -1;
        frameCount = 0;
        refresh = new Object();
        varLock = new ReentrantLock();
        
        pieces = new HashMap();
        BoardPosition pos;
        int mapindex;
        for(int y=0; y < 6; y++){
            if(y==4 || y==5){
                for(int x=0; x<6; x++){
                    //Attackers
                    mapindex = 6*y + x;
                    pos = new BoardPosition(x,y);
                    pieces.put(mapindex,new Skeleton(pos, Team.RED));
                }
            }   else if(y==0 || y==1){
                for(int x=0;x<6; x++){
                    //Defenders
                    mapindex = 6*y + x;
                    pos = new BoardPosition(x,y);
                    pieces.put(mapindex,new Skeleton(pos, Team.BLUE));
                }
            }
        }
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