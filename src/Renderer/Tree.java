package Renderer;

import static Renderer.Terrain.grass;
import static Renderer.Terrain.leaves;
import static Renderer.Terrain.vakje;
import static Renderer.Terrain.water;
import static Renderer.Terrain.wood;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import java.io.File;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
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
   
   double scalefactor = 0.00001;
   
   Terrain terrain;
   
    List position = new ArrayList(); ;
    List rotation = new ArrayList(); ;
    List pointsSphere = new ArrayList(); ;
    FitSphere sphere;
   //double position[] = new double[4];
   //double rotation[] = new double[4];
   

           
         
         
   
   
   /**
    * Here the main variables are declared to later be used in void draw.    
    * The variables are received from Robotrace and in this way, a random
    * function can be used to determine these doubles. If they were used
    * in the draw method, the trees would vary with every drawn frame
    */
    public Tree(double X, double Y, double Z, GL2 gl, Terrain terrain, int branches, long seed) {

        Vector length =  new Vector(0,0,0);
        this.x = X;             
        this.y = Y; 
        this.z = Z; 
        this.offset = 1;   
        this.height= 5.5; 
        Random randomVar = new Random(seed);
               System.out.println(x);  
        
        
        for (int i =0; i<branches; i++){
            
            rotation.add(randomVar.nextDouble() *360);
            position.add(0.35+ randomVar.nextDouble() *0.25); 
            
            length = new Vector(1f,1f,1f);//(double)position.get(i)*height + height/4*Math.cos(70/360*2*Math.PI)       
            length.rotate((double)rotation.get(i));
            System.out.println(length.x +","+ length.y +","+ length.z);
            pointsSphere.add(length);
            

        }
        pointsSphere.add(new Vector(0, 0, 0.35*height));
        sphere = new FitSphere(pointsSphere, 0.2f);
        
        

        

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

                 
        for (int i =0; i<18; i++){
            Vector mands = (Vector)pointsSphere.get(i);
            double length1 = 0.1;
            double base1 = 0.1;
        
            
            gl.glPushMatrix();
            gl.glTranslated(mands.x, mands.y, mands.z);
            gl.glBegin(GL_TRIANGLE_FAN);
            gl.glTexCoord2d(1, 0);
            gl.glVertex3d(0,0,length1);
            gl.glTexCoord2d(0, 1);
            gl.glVertex3d(-base1,-base1,0);
            gl.glTexCoord2d(0, 0);
            gl.glVertex3d(base1,-base1,0);
            gl.glTexCoord2d(0, 1);
            gl.glVertex3d(base1,base1,0);
            gl.glTexCoord2d(0, 0);            
            gl.glVertex3d(-base1,base1,0);
            gl.glTexCoord2d(0, 1);
            gl.glVertex3d(-base1,-base1,0);
            gl.glEnd();
            gl.glPopMatrix();
        }

        //sphere.draw(gl);
        double rotationfactor = 1;

        
        // Draws the trunk
        // The first vertex defines the height of the trunk which is as high
        // as the tree itself. The bottom vertices are drawn 0.2 lower to make
        // shure the tree touches the ground.
        
        
        // start recursionv
        gl.glPushMatrix();
        gl.glTranslated(x, y, z);
        drawBranch(basewidth, height, gl, tAnim, 0, 0, 0);
        gl.glPopMatrix();        
        
     }
     
     
     
        void drawBranch(double base, double length, GL2 gl, double tAnim, double localposition,double localrotation, int iteration) {
        iteration = iteration + 1;    
        // Defenition of Materials 
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, Material.WOOD.diffuse, 0);   
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, Material.WOOD.specular, 0);
        gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, Material.WOOD.shininess);
        
        double length1 = length +tAnim * scalefactor;
        double base1 = 0.5*base +tAnim * scalefactor;
        
        // Defenition of the texture
        wood = terrain.vakje;
        wood.bind(gl);
        
        

        
            gl.glBegin(GL_TRIANGLE_FAN);
            gl.glTexCoord2d(1, 0);
            gl.glVertex3d(0,0,length1);
            gl.glTexCoord2d(0, 1);
            gl.glVertex3d(-base1,-base1,0);
            gl.glTexCoord2d(0, 0);
            gl.glVertex3d(base1,-base1,0);
            gl.glTexCoord2d(0, 1);
            gl.glVertex3d(base1,base1,0);
            gl.glTexCoord2d(0, 0);            
            gl.glVertex3d(-base1,base1,0);
            gl.glTexCoord2d(0, 1);
            gl.glVertex3d(-base1,-base1,0);
            gl.glEnd();
            
            if (length1 > 1 && iteration < 3){
                for (int i=0; i < position.size()- iteration*2; i++) {
                
                    localposition = (double) position.get(i);
                    localrotation = (double) rotation.get(i);
                    base1 = 0.7*(1-localposition)*base;
                    length1 = length/2+tAnim * scalefactor*localposition;
        
                    gl.glPushMatrix();
                    gl.glTranslated(0, 0, localposition*length);
                    gl.glRotated(localrotation, 0, 0, 1);
                    gl.glRotated(90-60*1/(1+tAnim*scalefactor), 1, 0, 0);
                    drawBranch(base1, length1, gl, tAnim, localposition, localrotation, iteration);
                    
                    if (iteration == 2){
                    
                    }
                                     
                    gl.glPopMatrix(); 
                

                    
                }
              
                
            }

            

            }
        
            
        
      
    }
    
    
    
    
    
    
