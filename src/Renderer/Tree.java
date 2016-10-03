package Renderer;

import static Renderer.Terrain.grass;
import static Renderer.Terrain.leaves;
import static Renderer.Terrain.vakje;
import static Renderer.Terrain.water;
import static Renderer.Terrain.wood;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import java.io.File;
import static javax.media.opengl.GL.GL_FRONT_AND_BACK;
import static javax.media.opengl.GL.GL_TRIANGLE_FAN;
import javax.media.opengl.GL2;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_AMBIENT_AND_DIFFUSE;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SHININESS;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SPECULAR;

/**
 * This is the class that draws the trees.
 */
public class Tree {
    
    private static Texture leaves, wood;
   // used to calculate the height of the bottom of the upper leaves
   double offset;
   
   // defines position x @param x
   double x;
   
   // defines position y @param y
   double y;
   
      // defines position y @param y
   double z;
   
   // defines how tall the tree is
   double height;
   
   double basewidth = 0.2;
   
   double scalefactor = 0.0001;
   
   Terrain terrain;
   
   /**
    * Here the main variables are declared to later be used in void draw.    
    * The variables are received from Robotrace and in this way, a random
    * function can be used to determine these doubles. If they were used
    * in the draw method, the trees would vary with every drawn frame
    */
    public Tree(double x, double y, double z, GL2 gl, Terrain terrain) {
        this.x = x;             
        this.y = y; 
        this.z = z; 
        this.offset = 1;   
        this.height= 4; 
        

        

    }
    
    /**
     * Draws the trees.
     * triangle fans are used to draw piramids,
     * which are the basis shapes for the trees
     * .
     * used to make OpenGL work @param gl 
     */
    
     void draw( GL2 gl, float tAnim)   
     {

        height += tAnim * scalefactor;
        basewidth += tAnim * scalefactor*0.1;
        double rotationfactor = 1;
        
        // Draws the trunk
        // The first vertex defines the height of the trunk which is as high
        // as the tree itself. The bottom vertices are drawn 0.2 lower to make
        // shure the tree touches the ground.
        
        
        // base
        drawBranch(basewidth, height, gl, tAnim);
        
        
        //


        double position = 0.5;
        double rotation = 40;
        double base = (1-position)*basewidth;
        double length = 1+tAnim * scalefactor*position;
        
        
        gl.glPushMatrix();
        gl.glTranslated(0, 0, position*height);
        gl.glRotated(rotation, 0, 0, 1);
    	gl.glRotated(70-60*1/(1+tAnim*rotationfactor), 1, 0, 0);
        drawBranch(base, length, gl, tAnim);
    	gl.glPopMatrix();
        
        rotation = 90; position = 0.45;base = (1-position)*basewidth; length = 1+tAnim * scalefactor*position;
        gl.glPushMatrix();
        gl.glTranslated(0, 0, position*height);
        gl.glRotated(rotation, 0, 0, 1);
    	gl.glRotated(70-60*1/(1+tAnim*rotationfactor), 1, 0, 0);
        drawBranch(base, length, gl, tAnim);
    	gl.glPopMatrix();
        
        rotation = 210; position = 0.55; base = (1-position)*basewidth; length = 1+tAnim * scalefactor*position;
        gl.glPushMatrix();
        gl.glTranslated(0, 0, position*height);
        gl.glRotated(rotation, 0, 0, 1);
    	gl.glRotated(70-60*1/(1+tAnim*rotationfactor), 1, 0, 0);
        drawBranch(base, length, gl, tAnim);
    	gl.glPopMatrix();
        
        rotation = 160; position = 0.5;base = (1-position)*basewidth; length = 1+tAnim * scalefactor*position;
        gl.glPushMatrix();
        gl.glTranslated(0, 0, position*height);
        gl.glRotated(rotation, 0, 0, 1);
    	gl.glRotated(70-60*1/(1+tAnim*rotationfactor), 1, 0, 0);
        drawBranch(base, length, gl, tAnim);
    	gl.glPopMatrix();
        
        rotation = 10; position = 0.35;base = (1-position)*basewidth; length = 1+tAnim * scalefactor*position;
        gl.glPushMatrix();
        gl.glTranslated(0, 0, position*height);
        gl.glRotated(rotation, 0, 0, 1);
    	gl.glRotated(70-60*1/(1+tAnim*rotationfactor), 1, 0, 0);
        drawBranch(base, length, gl, tAnim);
    	gl.glPopMatrix();
        


        
        
        


        






        




/*
        // Draws the leaves
        gl.glBegin(GL_TRIANGLE_FAN);
            gl.glTexCoord2d(1, 0);
            gl.glVertex3d(0+x,0+y,z+height-1);
            gl.glTexCoord2d(0, 1);
            gl.glVertex3d(-0.7+x,-0.7+y,z+height-2);
            gl.glTexCoord2d(0, 0);
            gl.glVertex3d(0.7+x,-0.7+y,z+height-2);
            gl.glTexCoord2d(0, 1);
            gl.glVertex3d(0.7+x,0.7+y,z+height-2);
            gl.glTexCoord2d(0, 0);
            gl.glVertex3d(-0.7+x,0.7+y,z+height-2);
            gl.glTexCoord2d(0, 1);
            gl.glVertex3d(-0.7+x,-0.7+y,z+height-2);
        gl.glEnd();
        
        gl.glBegin(GL_TRIANGLE_FAN);
            gl.glTexCoord2d(1, 0);
            gl.glVertex3d(0+x,0+y,z+height);
            gl.glTexCoord2d(0, 1);
            gl.glVertex3d(-0.5+x,-0.5+y,z+height-offset);
            gl.glTexCoord2d(0, 0);
            gl.glVertex3d(0.5+x,-0.5+y,z+height-offset);
            gl.glTexCoord2d(0, 1);
            gl.glVertex3d(0.5+x,0.5+y,z+height-offset);
            gl.glTexCoord2d(0, 0);            
            gl.glVertex3d(-0.5+x,0.5+y,z+height-offset);
            gl.glTexCoord2d(0, 1);
            gl.glVertex3d(-0.5+x,-0.5+y,z+height-offset);
        gl.glEnd();
        */

     }
     
     
     
        void drawBranch(double base, double length, GL2 gl, double tAnim) {
            
        // Defenition of Materials 
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, Material.WOOD.diffuse, 0);   
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, Material.WOOD.specular, 0);
        gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, Material.WOOD.shininess);               
        
        // Defenition of the texture
        wood = terrain.vakje;
        wood.bind(gl);
        
        int maxVertex = 7;
        
        gl.glBegin(GL_TRIANGLE_FAN);
        gl.glTexCoord2d(1, 0);
        gl.glVertex3d(0,0,length);
        for (double i = 0; i <= 2*Math.PI; i= i+(1/maxVertex)*2*Math.PI) {
        gl.glTexCoord2d(0, i/maxVertex);
        gl.glVertex3d(0.5*base*Math.sin(i),0.5*base*Math.cos(i),0);
    	}
        gl.glEnd();
            
        
        
        if (tAnim > 1)
        {
            
        // Defenition of the leaf material
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, Material.GREEN.diffuse, 0);
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, Material.GREEN.specular, 0);
        gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, Material.GREEN.shininess);
        
        
        // Defenition of the texture
        // Defenition of the texture
        leaves = terrain.leaves;
        leaves.bind(gl);
        
        
        
            double time = tAnim - 1;
            base = base*3*(1-1/(1+time));
            
            gl.glBegin(GL_TRIANGLE_FAN);
            gl.glTexCoord2d(1, 0);
            gl.glVertex3d(0,0,length);
            gl.glTexCoord2d(0, 1);
            gl.glVertex3d(-base,-base,length*0.6);
            gl.glTexCoord2d(0, 0);
            gl.glVertex3d(base,-base,length*0.6);
            gl.glTexCoord2d(0, 1);
            gl.glVertex3d(base,base,length*0.6);
            gl.glTexCoord2d(0, 0);            
            gl.glVertex3d(-base,base,length*0.6);
            gl.glTexCoord2d(0, 1);
            gl.glVertex3d(-base,-base,length*0.6);
            gl.glEnd();
            
            
        }
    }
    
    
    
    
    
    
}
