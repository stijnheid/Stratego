/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Renderer;

import robotrace.*;
import Game.GameState;
import Game.Piece;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.util.texture.Texture;
import java.io.File;
import static java.lang.Math.*;
import java.util.ArrayList;
import javax.media.opengl.*;
import static javax.media.opengl.GL.GL_BLEND;
import static javax.media.opengl.GL.GL_COLOR_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_TEST;
import static javax.media.opengl.GL.GL_FRONT_AND_BACK;
import static javax.media.opengl.GL.GL_LESS;
import static javax.media.opengl.GL.GL_NICEST;
import static javax.media.opengl.GL.GL_ONE_MINUS_SRC_ALPHA;
import static javax.media.opengl.GL.GL_SRC_ALPHA;
import static javax.media.opengl.GL.GL_TEXTURE_2D;
import static javax.media.opengl.GL2.*;
import static javax.media.opengl.GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT;
import static javax.media.opengl.GL2GL3.GL_FILL;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_AMBIENT;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_COLOR_MATERIAL;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_DIFFUSE;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_LIGHT0;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_LIGHT1;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_LIGHTING;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_NORMALIZE;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_POSITION;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SMOOTH;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SPECULAR;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION;



/** Class designated to drawing board and surrounding environment.
 *
 * @author Maurits Ambags (0771400)
 */
public class Terrain extends Base {	
   
    /** Instance of the camera. */
    private final Camera camera;
    
    /** Instances of different textures */
    public static Texture grass, wood,leaves, water;
    
    private final int boardsize = 6;
    private final int terrainsize = 20;
    
    
    public Terrain(){

        
        // Initialize the camera
        camera = new Camera();    

        /*ArrayList<Vector> treePositions = new ArrayList();
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
        */
        // Initialize the terrain
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
        


        glu.gluLookAt(10,10,10,0,0,0,0,0,1);
        
        //Lighting implementation

        gl.glShadeModel(GL_SMOOTH);
        /*gl.glEnable(GL_LIGHTING);
        gl.glEnable(GL_LIGHT0);
        gl.glEnable(GL_LIGHT1);*/
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
		
        String pwd = "src" + File.separator + "Renderer" + File.separator;
        grass = loadTexture(pwd + "Grass.jpg");
        wood = loadTexture(pwd + "Wood.jpg");
        leaves = loadTexture(pwd + "Leaves.jpg");
        water = loadTexture(pwd + "water.jpg");
        
        gs.theta = 4.5f;
        gs.phi = 0.5f;

    }
    
    /**
     * Function to draw the terrain including environment.
     */
    public void drawTerrain(){
        double xmin = -terrainsize/2;
        double xmax = terrainsize/2;
        double ymin = -terrainsize/2;
        double ymax = terrainsize/2;
        double delta = 0.1;
        
        
        this.grass.bind(gl);
        gl.glColor3f(0.4f,0.7f,0.1f); 
        for(double x = xmin; x < xmax; x += delta){
            for(double y = ymin; y < ymax; y += delta){
                if (! ((x > -boardsize/2 && x <boardsize/2) && (y > -boardsize/2 && y <boardsize/2))){
                    gl.glBegin(GL_TRIANGLE_STRIP);
                    gl.glTexCoord2d(0, 0);
                    gl.glVertex3d(x, y, heightAt(x,y));
                    gl.glTexCoord2d(0, 1);
                    gl.glVertex3d(x, y + delta, heightAt(x,y+delta));
                    gl.glTexCoord2d(1, 0);
                    gl.glVertex3d(x + delta, y, heightAt(x+delta,y));
                    gl.glTexCoord2d(1, 1);
                    gl.glVertex3d(x + delta, y + delta, heightAt(x+delta,y+delta));
                    gl.glEnd();                
                }
            }
        }
        
    }
    
    /**
     * Function to draw the pieces on top of the already existing board.
     */
    public void drawBoard(){
        double xmin = -boardsize/2;
        double xmax = boardsize/2;
        double ymin = -boardsize/2;
        double ymax = boardsize/2;
        double delta = 1;
        double z = 0;
        
        boolean[][] cells = new boolean[6][6];
        
        //Square board.
        gl.glColor3f(0f,0f,1f); 
        for(double x = xmin; x < xmax; x += delta){
            for(double y = ymin; y < ymax; y += delta){
                if (! ((x==-1 && y==-2) || (x==-1 && y==-1) || (x==1 && y==-2) || (x==1 && y==-1))){
                    gl.glBegin(GL_QUAD_STRIP);
                    gl.glVertex3d(x, y, z);
                    gl.glVertex3d(x + delta, y, z);
                    gl.glVertex3d(x, y + delta, z);
                    gl.glVertex3d(x + delta, y + delta, z);
                    gl.glEnd();                
                }
            }
        }
    }
    
    /**
     * Method to draw a single piece at the specified position.
     * @param p the Piece object that should be drawn.
     * @param x the x coordinate of this piece (on the board).
     * @param y the y coordinate of this piece (on the board).
     */
    public void drawPiece(Piece p, int x, int y){
        
    }
    
    /**
     * Function that interrupts the game temporarily to showcase an animation.
     * Should only be called by the Simulator whenever it detects that
     * an animation should be played.
     * @param a the Animation that should be played.
     */
    public void playAnimation(Animation a){
        
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
        
        drawTerrain();
    }
    
    public static void main (String[] args){
        Terrain terrain = new Terrain();
        terrain.run();
    }
    
        public double heightAt(double x, double y) {
        double formula1 = 0.6*cos(0.3*x + 0.2*y) + 0.4*cos(x-0.5* y);
        double formula2 = 0;
        double ding = ((abs(x)-boardsize/2)*(abs(y)-boardsize/2))/terrainsize/2;
        return (ding*formula1);
    }
}
