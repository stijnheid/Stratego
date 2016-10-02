package robotrace;
import static java.lang.Math.*;
import Renderer.Vector;
import Game.GameState;

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
    public void update(GameState gs) {
        setDefaultMode(gs);
    }

    /**
     * Computes eye, center, and up, based on the camera's default mode.
     * The camera is never rolled, so the view is always alligned with the 
     * horizon, thus the upvector is equal to the Z vector.
     */
    private void setDefaultMode(GameState gs) {
        /*eye = new Vector(gs.cnt.x + gs.vDist*cos(gs.phi)*cos(gs.theta),
        		 gs.cnt.y + gs.vDist*cos(gs.phi)*sin(gs.theta),
        		 gs.cnt.z + gs.vDist*sin(gs.phi) );

        center = 	 gs.cnt; 
        
        up = Vector.Z;*/
        eye = new Vector(Math.cos(gs.theta) * gs.vDist * Math.cos(gs.phi) + gs.cnt.x(),
                    Math.sin(gs.theta) * gs.vDist * Math.cos(gs.phi) + gs.cnt.y(),
                    gs.vDist * Math.sin(gs.phi) + gs.cnt.z());//converting given variables to cartesian coordinates.
            center = new Vector(gs.cnt.x(), gs.cnt.y(), gs.cnt.z());
            up = Z;
    }



}
