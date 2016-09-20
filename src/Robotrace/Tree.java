package robotrace;

import com.jogamp.opengl.util.gl2.GLUT;
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
   // used to calculate the height of the bottom of the upper leaves
   double offset;
   
   // defines position x @param x
   double x;
   
   // defines position y @param y
   double y;
   
   // defines how tall the tree is
   double height;
   
   /**
    * Here the main variables are declared to later be used in void draw.    
    * The variables are received from Robotrace and in this way, a random
    * function can be used to determine these doubles. If they were used
    * in the draw method, the trees would vary with every drawn frame
    */
    public Tree(double x, double y, double offset, double height) {
        this.x = x;             
        this.y = y;             
        this.offset = offset;   
        this.height= height;    
    }
    
    /**
     * Draws the trees.
     * triangle fans are used to draw piramids,
     * which are the basis shapes for the trees
     * .
     * used to make OpenGL work @param gl 
     */
    
     void draw( GL2 gl)   
     {
        // Defenition of Materials 
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, Material.WOOD.diffuse, 0);   
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, Material.WOOD.specular, 0);
        gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, Material.WOOD.shininess);               
        
        // Defenition of the texture
        Main.wood.bind(gl);
        
        // Draws the trunk
        // The first vertex defines the height of the trunk which is as high
        // as the tree itself. The bottom vertices are drawn 0.2 lower to make
        // shure the tree touches the ground.
        gl.glBegin(GL_TRIANGLE_FAN);
            gl.glTexCoord2d(1, 0);                  
            gl.glVertex3d(x,y,Terrain.heightAt(x,y)+height);  
            gl.glTexCoord2d(0, 0);                  
            gl.glVertex3d(-0.1+x,-0.1+y,Terrain.heightAt(x,y)-0.2);           
            gl.glTexCoord2d(0, 1); 
            gl.glVertex3d(0.1+x,-0.1+y,Terrain.heightAt(x,y)-0.2);
            gl.glTexCoord2d(0, 0); 
            gl.glVertex3d(0.1+x,0.1+y,Terrain.heightAt(x,y)-0.2);
            gl.glTexCoord2d(0, 1); 
            gl.glVertex3d(-0.1+x,0.1+y,Terrain.heightAt(x,y)-0.2);
            gl.glTexCoord2d(0, 0);                  
            gl.glVertex3d(-0.1+x,-0.1+y,Terrain.heightAt(x,y)-0.2);
        gl.glEnd();
        
        
        // Defenition of the leaf material
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, Material.GREEN.diffuse, 0);
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, Material.GREEN.specular, 0);
        gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, Material.GREEN.shininess);
        
        // Defenition of the texture
        Main.leaves.bind(gl);
        
        
        // Draws the leaves
        gl.glBegin(GL_TRIANGLE_FAN);
            gl.glTexCoord2d(1, 0);
            gl.glVertex3d(0+x,0+y,Terrain.heightAt(x,y)+height-1);
            gl.glTexCoord2d(0, 1);
            gl.glVertex3d(-0.7+x,-0.7+y,Terrain.heightAt(x,y)+height-2);
            gl.glTexCoord2d(0, 0);
            gl.glVertex3d(0.7+x,-0.7+y,Terrain.heightAt(x,y)+height-2);
            gl.glTexCoord2d(0, 1);
            gl.glVertex3d(0.7+x,0.7+y,Terrain.heightAt(x,y)+height-2);
            gl.glTexCoord2d(0, 0);
            gl.glVertex3d(-0.7+x,0.7+y,Terrain.heightAt(x,y)+height-2);
            gl.glTexCoord2d(0, 1);
            gl.glVertex3d(-0.7+x,-0.7+y,Terrain.heightAt(x,y)+height-2);
        gl.glEnd();
        
        gl.glBegin(GL_TRIANGLE_FAN);
            gl.glTexCoord2d(1, 0);
            gl.glVertex3d(0+x,0+y,Terrain.heightAt(x,y)+height);
            gl.glTexCoord2d(0, 1);
            gl.glVertex3d(-0.5+x,-0.5+y,Terrain.heightAt(x,y)+height-offset);
            gl.glTexCoord2d(0, 0);
            gl.glVertex3d(0.5+x,-0.5+y,Terrain.heightAt(x,y)+height-offset);
            gl.glTexCoord2d(0, 1);
            gl.glVertex3d(0.5+x,0.5+y,Terrain.heightAt(x,y)+height-offset);
            gl.glTexCoord2d(0, 0);            
            gl.glVertex3d(-0.5+x,0.5+y,Terrain.heightAt(x,y)+height-offset);
            gl.glTexCoord2d(0, 1);
            gl.glVertex3d(-0.5+x,-0.5+y,Terrain.heightAt(x,y)+height-offset);
        gl.glEnd();

     }
    
    
    
    
    
    
}
