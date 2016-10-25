package Renderer;
import static java.lang.Math.*;
import Renderer.Vector;
import Renderer.CameraState;

/**
 * Implementation of a camera with a position and orientation. 
 */
public class Camera {
    /** The position of the camera. */
    public Vector eye = new Vector(3f, 6f, 5f);

    /** The point to which the camera is looking. */
    public Vector center = new Vector(0,0,0);

    /** The up vector. */
    public Vector up = new Vector(0,0,1);
    
    public final static Vector Z = new Vector(0,0,1);
  
    /**
     * Updates the camera viewpoint and direction based on the
     * selected camera mode.
     */
    public void update(CameraState gs) {
        setDefaultMode(gs);
    }

    /**
     * Computes eye, centre, and up, based on the camera's default mode.
     * The camera is never rolled, so the view is always aligned with the 
     * horizon, thus the up vector is equal to the Z vector.
     */
    private void setDefaultMode(CameraState gs) {
        double theta = gs.theta;
        double phi = gs.phi;
        double vDist = gs.vDist;
        eye = new Vector(Math.cos(theta) * vDist * Math.cos(phi) + gs.cnt.x(),
                    Math.sin(theta) * vDist * Math.cos(phi) + gs.cnt.y(),
                    vDist * Math.sin(phi) + gs.cnt.z());//converting given variables to cartesian coordinates.
            center = new Vector(gs.cnt.x(), gs.cnt.y(), gs.cnt.z());
            up = Z;
    }



}
