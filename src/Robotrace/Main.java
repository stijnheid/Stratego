package robotrace;

import com.jogamp.opengl.util.texture.Texture;

import java.io.File;

import static javax.media.opengl.GL2.*;
import static java.lang.Math.*;
import java.util.ArrayList;
import static javax.media.opengl.GL.GL_TEXTURE_2D;

/**
 * Handles all of the RobotRace graphics functionality,
 * which should be extended per the assignment.
 * 
 * OpenGL functionality:
 * - Basic commands are called via the gl object;
 * - Utility commands are called via the glu and
 *   glut objects;
 * 
 * GlobalState:
 * The gs object contains the GlobalState as described
 * in the assignment:
 * - The camera viewpoint angles, phi and theta, are
 *   changed interactively by holding the left mouse
 *   button and dragging;
 * - The camera view width, vWidth, is changed
 *   interactively by holding the right mouse button
 *   and dragging upwards or downwards;
 * - The center point can be moved up and down by
 *   pressing the 'q' and 'z' keys, forwards and
 *   backwards with the 'w' and 's' keys, and
 *   left and right with the 'a' and 'd' keys;
 * - Other settings are changed via the menus
 *   at the top of the screen.
 * 
 * Textures:
 * Place your "track.jpg", "brick.jpg", "head.jpg",
 * and "torso.jpg" files in the same folder as this
 * file. These will then be loaded as the texture
 * objects track, bricks, head, and torso respectively.
 * Be aware, these objects are already defined and
 * cannot be used for other purposes. The texture
 * objects can be used as follows:
 * 
 * gl.glColor3f(1f, 1f, 1f);
 * track.bind(gl);
 * gl.glBegin(GL_QUADS);
 * gl.glTexCoord2d(0, 0);
 * gl.glVertex3d(0, 0, 0);
 * gl.glTexCoord2d(1, 0);
 * gl.glVertex3d(1, 0, 0);
 * gl.glTexCoord2d(1, 1);
 * gl.glVertex3d(1, 1, 0);
 * gl.glTexCoord2d(0, 1);
 * gl.glVertex3d(0, 1, 0);
 * gl.glEnd(); 
 * 
 * Note that it is hard or impossible to texture
 * objects drawn with GLUT. Either define the
 * primitives of the object yourself (as seen
 * above) or add additional textured primitives
 * to the GLUT object.
 */

/**
 * Handles all of the Main graphics functionality,
 which should be extended per the assignment.OpenGL functionality:
 - Basic commands are called via the gl object;
 - Utility commands are called via the glu and
   glut objects;
 
 GlobalState:
 The gs object contains the GlobalState as described
 in the assignment:
 - The camera viewpoint angles, phi and theta, are
   changed interactively by holding the left mouse
   button and dragging;
 - The camera view width, vWidth, is changed
   interactively by holding the right mouse button
   and dragging upwards or downwards;
 - The center point can be moved up and down by
   pressing the 'q' and 'z' keys, forwards and
   backwards with the 'w' and 's' keys, and
   left and right with the 'a' and 'd' keys;
 - Other settings are changed via the menus
   at the top of the screen.
 
 Textures:
 Place your "track.jpg", "brick.jpg", "head.jpg",
 and "torso.jpg" files in the same folder as this
 file. These will then be loaded as the texture
 objects track, bricks, head, and torso respectively.
 Be aware, these objects are already defined and
 cannot be used for other purposes. The texture
 objects can be used as follows:
 
 gl.glColor3f(1f, 1f, 1f);
 track.bind(gl);
 gl.glBegin(GL_QUADS);
 gl.glTexCoord2d(0, 0);
 gl.glVertex3d(0, 0, 0);
 gl.glTexCoord2d(1, 0);
 gl.glVertex3d(1, 0, 0);
 gl.glTexCoord2d(1, 1);
 gl.glVertex3d(1, 1, 0);
 gl.glTexCoord2d(0, 1);
 gl.glVertex3d(0, 1, 0);
 gl.glEnd(); 
 
 Note that it is hard or impossible to texture
 objects drawn with GLUT. Either define the
 primitives of the object yourself (as seen
 above) or add additional textured primitives
 to the GLUT object.
 */
public class Main extends Base {
   
   
    /** Array of trees */
    private final Tree[] trees;
    
    /** instance of Axis frame */
    private final AxisFrame axis;
	
   
    /** Instance of the camera. */
    private final Camera camera;
    

    /** Instance of the terrain. */
    private final Terrain terrain;
    
    /** Instances of different textures */
    public static Texture grass, wood,leaves, water;
    
    /**
     * Constructs this robot race by initializing robots,
     * camera, track, and terrain.
     */
    public Main() {
        
    	axis = new AxisFrame(1);

        
        // Initialize the camera
        camera = new Camera();
        
        

        ArrayList<Vector> treePositions = new ArrayList();
        for(int i =0; i<100; i++){
                   Vector outerPos= new Vector(0, 0, 0);
                   Vector outerTan= new Vector(1, 0, 0);
                   Vector treePos = new Vector(outerPos.x + (1+random())*4*outerTan.y, outerPos.y - (1+2*random())*4*outerTan.x, outerPos.z);
                   if (treePos.x>-20 && treePos.x<20 && treePos.y>-20 && treePos.y<20) {
                       treePositions.add(treePos);
                   }
        }
        trees = new Tree[treePositions.size()];
        for (int i =0; i<treePositions.size(); i++){
            trees[i] = new Tree(treePositions.get(i).x, treePositions.get(i).y,1.5-0.4*random(), 4-1.5*random())  ;
            }
        
        // Initialize the terrain
        terrain = new Terrain(40, 40);
        

        
    }
    
    /**
     * Called upon the start of the application.
     * Primarily used to configure OpenGL.
     */
    @Override
    public void initialize() {
		
        // Enable blending.
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                
        // Enable depth testing.
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LESS);
		
	    // Normalize normals.
        gl.glEnable(GL_NORMALIZE);
        
        // Enable textures. 
        gl.glEnable(GL_TEXTURE_2D);
        gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        gl.glBindTexture(GL_TEXTURE_2D, 0);
		
        String pwd = "src" + File.separator + "robotrace" + File.separator;
        grass = loadTexture(pwd + "Grass.jpg");
        wood = loadTexture(pwd + "Wood.jpg");
        leaves = loadTexture(pwd + "Leaves.jpg");
        water = loadTexture(pwd + "water.jpg");
        
        gs.theta = 4.5f;
        gs.phi = 0.5f;

    }
    
    /**
     * Configures the viewing transform.
     */
    @Override
    public void setView() {
        // Select part of window.
        gl.glViewport(0, 0, gs.w, gs.h);

        // Set projection matrix.
        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();

        // Set the perspective.
        // Modify this to meet the requirements in the assignment.
        glu.gluPerspective( 2 * toDegrees(atan((0.5*(((float)gs.vWidth*(float)gs.h/(float)gs.w))) / (float)gs.vDist)),
        					(float) gs.w / (float) gs.h,
        					0.27 + 0.001 * gs.vDist,
        					10 * gs.vDist);
        // Set camera.
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();

        // Update the view according to the camera mode and robot of interest.
        // For camera modes 1 to 4, determine which robot to focus on.
        


        glu.gluLookAt(camera.eye.x, camera.eye.y, camera.eye.z,
                      camera.center.x, camera.center.y, camera.center.z,
                      camera.up.x, camera.up.y, camera.up.z);
        
        //Lighting implementation

        gl.glShadeModel(GL_SMOOTH);
        gl.glEnable(GL_LIGHTING);
        gl.glEnable(GL_LIGHT0);
        gl.glEnable(GL_LIGHT1);
        gl.glDisable(GL_COLOR_MATERIAL);

        // White color definition
        float[] sunColor = {255/255f, 255/255f, 220/255f, 0.7f};
        float[] sunAmbientColor = {255/255f, 255/255f, 220/255f, 0.07f};

        // Light source 10 degrees up and 10 degrees left from the camera
        //float[] lightDir = { (float)(camera.center.x + gs.vDist*cos(gs.phi+toRadians(10))*cos(gs.theta+toRadians(10))), 
        //					 (float)(camera.center.y + gs.vDist*cos(gs.phi+toRadians(10))*sin(gs.theta+toRadians(10))), 
        //					 (float)(camera.center.z + gs.vDist*sin(gs.phi+toRadians(10))) };
        //float[] whiteColor = {255/255f, 255/255f, 255/255f, 1f};

        // Setting the GL_POSITION and GL_DIFFUSE
        //gl.glLightfv(GL_LIGHT0, GL_POSITION, lightDir, 0);
        //gl.glLightfv(GL_LIGHT0, GL_DIFFUSE, whiteColor, 0);
        
        gl.glLightfv(GL_LIGHT1, GL_POSITION, new float[]{1, 1, 0}, 0);
        gl.glLightfv(GL_LIGHT1, GL_DIFFUSE, sunColor, 0);
        gl.glLightfv(GL_LIGHT1, GL_AMBIENT, sunAmbientColor, 0);
        gl.glLightfv(GL_LIGHT1, GL_SPECULAR, sunColor, 0);

    }
    /**
     * Draws the entire scene.
     */
    @Override
    public void drawScene() {
    		
        
    	// Background color.
        gl.glClearColor(135/255f, 206/255f, 235/255f, 0f);
        
        // Clear background.
        gl.glClear(GL_COLOR_BUFFER_BIT);
        
        // Clear depth buffer.
        gl.glClear(GL_DEPTH_BUFFER_BIT);
        
        // Set color to black.
        gl.glColor3f(1f, 1f, 1f);
        
        gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

        if (gs.showAxes) {
            axis.draw(gl, glu, glut);
        }

        // Draw the axis frame.
        // Draw the terrain.
        terrain.draw(gl);

      
        for(Tree tree:trees) {
            tree.draw(gl);
        }
        

    }
   
    public static double[] normal(Vector a, Vector b) {
    	return new double[]{a.y*b.z - b.y*a.z,
    					  	a.z*b.x - b.z*a.x,
    					  	a.x*b.y - b.x*a.y}; 
    }

    /**
     * Main program execution body, delegates to an instance of
 the Main implementation.
     */
    public static void main(String args[]) {
        Main robotRace = new Main();
        robotRace.run();
    } 
}
