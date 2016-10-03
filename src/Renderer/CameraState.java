/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Renderer;

/**
 *  Class holding data regarding opengl viewing (camera, center, etc).
 */
public class CameraState {
    
    public float tAnim;         // Time since start of animation in seconds.
    
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
        theta = 0f;
        phi = 0f;
        tAnim = -1;        
    }
    
}
