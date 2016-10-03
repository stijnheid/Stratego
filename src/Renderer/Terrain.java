/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Renderer;

import robotrace.*;
import Game.GameState;
import Game.GamePiece;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.gl2.GLUT;
import java.io.File;
import static java.lang.Math.*;
import java.util.ArrayList;
import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
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
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_AMBIENT_AND_DIFFUSE;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_COLOR_MATERIAL;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_DIFFUSE;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_LIGHT0;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_LIGHT1;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_LIGHTING;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_NORMALIZE;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_POSITION;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SHININESS;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SMOOTH;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SPECULAR;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION;



/** Class designated to drawing board and surrounding environment.
 *
 *
 */
public class Terrain extends Base {	
   
    /** Instance of the camera. */
    public final Camera camera;
    
    /** Instances of different textures */
    public static Texture grass, vakje ,leaves, water;
    
    private final int boardsize = 8;
    private final int terrainsize = 20;
    
    Skeleton test;
    
    
    public Terrain(){
        
        test = new Skeleton(new Vector(-3.5,-3.5,0));
                
        // Initialize the camera
        camera = new Camera();    
        

    }
    
       /**
     * Configures the viewing transform.
     */
    @Override
    public void setView() {
        // Select part of window.
        gl.glViewport(0, 0, cs.w, cs.h);

        // Set projection matrix.
        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();

        // Set the perspective.
        // Modify this to meet the requirements in the assignment.
        float aspectRatio = (float) cs.w / (float) cs.h;
        double fovy = 60;
        glu.gluPerspective(fovy, aspectRatio, 0.1 * cs.vDist, 10.0 * cs.vDist);
        // Set camera.
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();

        // Update the view according to the camera mode and robot of interest.
        // For camera modes 1 to 4, determine which robot to focus on.
        

        camera.update(cs);
        glu.gluLookAt(camera.eye.x(),camera.eye.y(),camera.eye.z(),cs.cnt.x(),cs.cnt.y(),cs.cnt.z(),0,0,1);
        
        //Lighting implementation

        gl.glShadeModel(GL_SMOOTH);
        gl.glDisable(GL_COLOR_MATERIAL);

        // White color definition

    }
    
        /**
     * Called upon the start of the application.
     * Primarily used to configure OpenGL.
     */
    @Override
    public void initialize() {
        

        gl.glEnable(GL_LIGHTING);
        gl.glEnable(GL_LIGHT0);
        
                // White color definition
        float[] sunColor = {255/255f, 255/255f, 220/255f, 0.7f};
        float[] sunAmbientColor = {255/255f, 255/255f, 220/255f, 0.7f};


        
        gl.glLightfv(GL_LIGHT0, GL_POSITION, new float[]{0, 0, 20}, 0);
        gl.glLightfv(GL_LIGHT0, GL_DIFFUSE, sunColor, 0);
        gl.glLightfv(GL_LIGHT0, GL_AMBIENT, sunAmbientColor, 0);
        gl.glLightfv(GL_LIGHT0, GL_SPECULAR, sunColor, 0);
        
        gl.glClearColor(135/255f, 206/255f, 235/255f, 0f);
        
        // Clear background.
        gl.glClear(GL_COLOR_BUFFER_BIT);
        
        // Clear depth buffer.
        gl.glClear(GL_DEPTH_BUFFER_BIT);
        
        // Set color to black.
        gl.glColor3f(1f, 1f, 1f);
        
        gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

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
		
        String pwd = "src/Textures/";
        grass = loadTexture(pwd + "battlefieldos.jpg");
        vakje = loadTexture(pwd + "Vakje.jpg");
        leaves = loadTexture(pwd + "Leaves.jpg");
        water = loadTexture(pwd + "water.jpg");
        
        cs.theta = 4.5f;
        cs.phi = 0.5f;

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
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, Material.GROUND.diffuse, 0);
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, Material.GROUND.specular, 0);
        gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, Material.GROUND.shininess);
        

        
        this.grass.bind(gl);
        gl.glColor3f(0.4f,0.7f,0.1f); 
        for(double x = xmin; x <= xmax; x += delta){

            for(double y = ymin; y <= ymax; y += delta){
                            
                if (! ((x > -boardsize/2 && x <boardsize/2) && (y > -boardsize/2 && y <boardsize/2))){
                    gl.glBegin(GL_TRIANGLE_STRIP);


                    //gl.glTexCoord2d(0, 0);
                    Vector vector1 = new Vector(0,-delta, heightAt(x, y-delta)-heightAt(x, y));
                    Vector vector2 = new Vector(-delta,0, heightAt(x-delta, y)-heightAt(x, y));
                    Vector vector3 = vector2.cross(vector1);

                    Vector vector4 = new Vector(0,-delta, heightAt(x-delta, y-delta)-heightAt(x, y-delta));
                    Vector vector5 = new Vector(-delta,0, heightAt(x-delta, y-delta)-heightAt(x-delta, y));
                    Vector vector6 = vector5.cross(vector4);
                    
                    gl.glTexCoord2d(texPos(x,y)[0], texPos(x,y)[1]);
                    gl.glNormal3d(vector3.x(),vector3.y(),vector3.z());
                    gl.glVertex3d(x, y, heightAt(x,y));
                    
                    gl.glTexCoord2d(texPos(x, y - delta)[0], texPos(x,y - delta)[1]);
                    gl.glVertex3d(x, y - delta, heightAt(x,y-delta));
                    
                    gl.glTexCoord2d(texPos(x-delta,y)[0], texPos(x-delta,y)[1]);
                    gl.glVertex3d(x - delta, y, heightAt(x-delta,y));
                    
                    gl.glTexCoord2d(texPos(x - delta,y - delta)[0], texPos(x - delta,y - delta)[1]);
                    //gl.glNormal3d(vector6.x(),vector6.y(),vector6.z());
                    gl.glVertex3d(x - delta, y - delta, heightAt(x - delta,y - delta));
                    gl.glEnd();                
                }
            }
        }
        
    }
    
    /**
     * Function to draw the board containing the game pieces.
     */
    public void drawBoard(){      

        int xmin = -boardsize/2;
        int xmax = boardsize/2;
        int ymin = -boardsize/2;
        int ymax = boardsize/2;
        int delta = 1;
        double z = 0;
        this.vakje.bind(gl);
        boolean[][] cells = new boolean[8][8];
        cells[2][3]=true;
        cells[2][4]=true;
        cells[5][3]=true;
        cells[5][4]=true;
        
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, Material.CONCRETE.diffuse, 0);
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, Material.CONCRETE.specular, 0);
        gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, Material.CONCRETE.shininess);
        
        //Square board.
        gl.glColor3f(0, 0, 1);
        gl.glNormal3d(0, 0, 1);
        for(double x = xmin; x < xmax; x += delta){
            for(double y = ymin; y < ymax; y += delta){
                if (!cells[(int)x+4][(int)y+4]){
                    gl.glBegin(GL_QUAD_STRIP);
                    gl.glTexCoord2d(0, 0);
                    gl.glVertex3d(x, y, z);
                    gl.glTexCoord2d(1, 0);
                    gl.glVertex3d(x + delta, y, z);
                    gl.glTexCoord2d(0, 1);
                    gl.glVertex3d(x, y + delta, z);
                    gl.glTexCoord2d(1, 1);
                    gl.glVertex3d(x + delta, y + delta, z);
                    gl.glEnd();                
                }
            }
        }
    }
    
    /**
     * Method to draw a single piece at the specified position.
     * @param x the x coordinate of this piece (on the board).
     * @param y the y coordinate of this piece (on the board).
     */
    public void drawPiece(int x, int y){
        
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
        drawBoard();
        test.draw(gl, glut);
    }
    
    public static void main (String[] args){
        Terrain terrain = new Terrain();
        terrain.run();
    }
    
        public double heightAt(double x, double y) {
        double formula1 = 0.6*cos(0.3*x + 0.2*y) + 0.4*cos(x-0.5* y);
        double ding = 2*((abs(x)-boardsize/2)*(abs(y)-boardsize/2))/terrainsize/2;
        return (ding*formula1);
    }
        
        public double[] texPos(double x, double y) {
        double xPos = (terrainsize/2+x)/terrainsize;
        double yPos = (terrainsize/2+y)/terrainsize;
        double position[] = new double[]{xPos,yPos};
        return position;
    }

}
