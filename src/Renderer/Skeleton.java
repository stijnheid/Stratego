/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Renderer;
import Game.BoardPosition;
import Game.Pieces;
import static Game.Pieces.BOMB;
import static Game.Pieces.FLAG;
import Game.Team;
import static Renderer.Material.BLANK;
import Renderer.Terrain;

import java.util.List;
import java.util.ArrayList;
import javax.media.opengl.GL2;
import com.jogamp.opengl.util.gl2.GLUT;
import static javax.media.opengl.GL.GL_FRONT_AND_BACK;
import static javax.media.opengl.GL.GL_TRIANGLE_STRIP;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_AMBIENT_AND_DIFFUSE;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SHININESS;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SPECULAR;

/**
 *  Class to holding information for the skeleton of a piece.
 */
public class Skeleton {
    
    /**Vectors for every joint in the skeleton. */
    public Vector head, neck, shoulderL, shoulderR, elbow, wrist,
            spine, hipL, hipR, knee, heel, foot, sword;
    
    /**Offset from (0,0) of skeleton.*/
    public Vector offset;
    
    /**Position of this skeleton on the board.*/
    public BoardPosition position;
    
    /**Map index of this skeleton on the board (relates to BoardPosition).*/
    public int mapindex;
    
    /**Team on which this skeleton resides.
     (Red Attacks, Blue Defends)*/
    public Team team;
    
    /**Holds the rank of the to be drawn piece*/
    public Pieces rank; 
    
    
    /**Data Structure holding the joints data.*/
    public List<Vector> joints;
    
    /** Rotation of this skeleton. (0 <= rotation < 360)*/
    private int rotation;
    
    /** Thickness of bones in skeleton. */
    private final double boneWidth = 0.05f;
    
    /**Material properties of skeleton (depending on team).*/
    private final float[] skelDiffuse;
    private final float[] skelSpecular;
    private final float skelShine;
    
    /** Material properties of sword.*/
    private final float[] swordDiffuse;
    private final float[] swordSpecular;
    private final float swordShine;
    
    /**Rotations of vital joints in X and Y dimensions.*/
    double shoulderLRotX = 0;
    double shoulderLRotY = 0;
    double shoulderRRotX = 0;
    double shoulderRRotY = 0;
    double elbowLRotX = 0;
    double elbowLRotY = 0;
    double elbowRRotX = 0;
    double elbowRRotY = 0;
    double hipLRotX = 0;
    double hipLRotY = 0;
    double hipRRotX = 0;
    double hipRRotY = 0;
    double kneeLRotX = 0;
    double kneeLRotY = 0;
    double kneeRRotX = 0;
    double kneeRRotY = 0;
    double swordRotX = 0;
    float swordOpacity = 0;
    
    //should the ranks be shown
    boolean showRank = false;

    /**
     * Constructor for a skeleton. Does not yet draw a skeleton.
     * @param p position on the board (in cells).
     * @param t team on which this skeleton resides.
     */
    public Skeleton (BoardPosition p, Team t, Pieces r){
        rank = r;
        
        joints = new ArrayList<Vector>();
        this.position = p;
        this.team = t;
        offset = new Vector(-2.5+p.getX(),2.5-p.getY(),0);
        mapindex =  p.getX() + 6 * p.getY();
        if (team == Team.BLUE){
            rotation = 0;
            skelDiffuse = new float[]{0.05f,0.05f,0.9f,1};
            skelSpecular = new float[]{0.05f,0.05f,0.9f,1};
            skelShine = 0.9f;
        }   else {
            rotate(180);
            skelDiffuse = new float[]{0.9f,0.05f,0.05f,1};
            skelSpecular = new float[]{0.9f,0.05f,0.05f,1};
            skelShine = 0.9f;
        }       
        head = new Vector(0, 0, 1.8);
        joints.add(head);
        neck = new Vector(0, 0, 1.6);
        joints.add(neck);
        shoulderL = new Vector (-0.2, 0, 1.6);
        joints.add(shoulderL);
        shoulderR = new Vector (0.2, 0, 1.6);
        joints.add(shoulderR);
        elbow = new Vector (0.1, 0, -0.3);//RELATIVE TO SHOULDER
        joints.add(elbow);
        wrist = new Vector (0, 0, -0.3);//RELATIVE TO ELBOW
        joints.add(wrist);
        spine = new Vector (0, 0, 1);
        joints.add(spine);
        hipL = new Vector (-0.15, 0, 0.8);
        joints.add(hipL);
        hipR = new Vector (0.15, 0, 0.8);
        joints.add(hipR);
        knee = new Vector (0, 0, -0.4);//RELATIVE TO HIP
        joints.add(knee);
        heel = new Vector (0, 0, -0.4);//RELATIVE TO KNEE
        joints.add(heel);
        foot = new Vector (0, 0.1, 0);//RELATIVE TO HEEL
        joints.add(foot);
        sword = new Vector(0, 0.8, 0);///RELATIVE TO WRIST
        joints.add(sword);
        
        swordDiffuse = new float[] {139/255f, 69/255f, 19/255f, 0};
        swordSpecular = new float[] {139/255f, 69/255f, 19/255f, 0};
        swordShine = 0.93f;
        
    }
    
    /**
     * Draws a complete skeleton.
     * @param gl
     * @param glut 
     */
    public void draw(GL2 gl, GLUT glut){
        gl.glPushMatrix();
        gl.glDisable(GL2.GL_TEXTURE_2D);
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, skelDiffuse, 0);
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, skelSpecular, 0);
        gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, skelShine);
        gl.glRotated(rotation, 0, 0, 1);
        gl.glTranslated(offset.x, offset.y, offset.z);
        gl.glScaled(0.8d, 0.8d, 0.8d);
        drawTorso(gl, glut);
        drawLeg(gl, glut, true);//left leg
        drawLeg(gl, glut, false);//right leg
        drawArm(gl, glut, true);// left arm
        drawArm(gl, glut, false);//right arm
        
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, BLANK.diffuse, 0);
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, BLANK.specular, 0);
        gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, BLANK.shininess);
        drawRank(gl);
        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glPopMatrix();
    }
    
    private void drawTorso (GL2 gl, GLUT glut){
        Vector middle;
        //draw head manually (make it a bit larger).
        gl.glPushMatrix();
        gl.glTranslated(head.x,head.y,head.z);
        glut.glutSolidSphere(2*boneWidth, 10, 10);
        gl.glPopMatrix();
        
        //draw torso joint connections.
        gl.glPushMatrix();
        middle = head.between(neck);
        gl.glTranslated(middle.x, middle.y, middle.z);
        gl.glRotated(-Math.toDegrees(Math.atan((head.y-neck.y)/(head.z-neck.z))),1,0,0);
        gl.glScaled(boneWidth, boneWidth, head.z-neck.z);
        glut.glutSolidCube(1f);//neck to head.
        gl.glPopMatrix();
        
        gl.glPushMatrix();
        middle = neck.between(shoulderL);
        gl.glTranslated(middle.x, middle.y, middle.z);
        gl.glRotated(Math.toDegrees(Math.atan((neck.y-shoulderL.y)/(neck.x-shoulderL.x))),0,0,1);
        gl.glScaled(neck.distance(shoulderL), boneWidth, boneWidth);
        glut.glutSolidCube(1f);//neck to shoulderL.
        gl.glPopMatrix();
        gl.glPushMatrix();
        middle = neck.between(shoulderR);
        gl.glTranslated(middle.x, middle.y, middle.z);
        gl.glRotated(Math.toDegrees(Math.atan((neck.y-shoulderR.y)/(neck.x-shoulderR.x))),0,0,1);
        gl.glScaled(neck.distance(shoulderR), boneWidth, boneWidth);
        glut.glutSolidCube(1f);//neck to shoulderR.
        gl.glPopMatrix();
        
        gl.glPushMatrix();
        middle = neck.between(spine);
        gl.glTranslated(middle.x, middle.y, middle.z);
        gl.glRotated(-Math.toDegrees(Math.atan((neck.y-spine.y)/(neck.z-spine.z))),1,0,0);
        gl.glScaled(boneWidth, boneWidth, neck.distance(spine));
        glut.glutSolidCube(1f);//neck to spine.
        gl.glPopMatrix();
        
        gl.glPushMatrix();
        middle = spine.between(hipL);
        double angle = Math.toDegrees(Math.atan((spine.z-hipL.z)/(spine.x-hipL.x)));
        gl.glTranslated(middle.x, middle.y, middle.z);
        gl.glRotated(90 - angle, 0, 1, 0);
        gl.glRotated(-90 + Math.toDegrees(Math.atan((spine.z-hipL.z)/(spine.y-hipL.y))),1,0,0);
        gl.glScaled(boneWidth, boneWidth, spine.distance(hipL));
        glut.glutSolidCube(1f);//spine to hipL.
        gl.glPopMatrix();
        
        gl.glPushMatrix();
        middle = spine.between(hipR);
        angle = -angle;
        gl.glTranslated(middle.x, middle.y, middle.z);
        gl.glRotated(90 - angle, 0, 1, 0);
        gl.glRotated(90 - Math.toDegrees(Math.atan((spine.z-hipR.z)/(spine.y-hipR.y))),1,0,0);
        gl.glScaled(boneWidth, boneWidth, spine.distance(hipR));
        glut.glutSolidCube(1f);//spine to hipR.
        gl.glPopMatrix();
        
        drawAt(gl, glut, neck);
        drawAt(gl, glut, shoulderL);
        drawAt(gl, glut, shoulderR);
        drawAt(gl, glut, spine);
        drawAt(gl, glut, hipL);
        drawAt(gl, glut, hipR);
    }
    
    private void drawArm (GL2 gl, GLUT glut, boolean leftArm){
        double angle;
        
        if (leftArm){
            gl.glPushMatrix();
            gl.glTranslated(shoulderL.x, shoulderL.y, shoulderL.z);
            gl.glRotated(shoulderLRotX, 1, 0, 0);
            gl.glRotated(shoulderLRotY, 0, 1, 0);
            gl.glTranslated(-elbow.x/2, elbow.y/2, elbow.z/2);
            gl.glPushMatrix();
            angle = Math.toDegrees(Math.atan(0.15/0.05));
            gl.glRotated(90 - angle, 0, 1, 0);
            gl.glScaled(boneWidth, boneWidth, (float)Math.sqrt(elbow.z*elbow.z + elbow.x*elbow.x));
            glut.glutSolidCube(1f);//shoulder to elbow.
            gl.glPopMatrix();
            gl.glTranslated(-elbow.x/2, elbow.y/2, elbow.z/2);
            drawSphere(glut);
            gl.glRotated(elbowLRotX, 1, 0, 0);
            gl.glRotated(elbowLRotY, 0, 1, 0);
            gl.glTranslated(wrist.x/2, wrist.y/2, wrist.z/2);
            gl.glPushMatrix();
            gl.glScaled(boneWidth, boneWidth, wrist.z);
            glut.glutSolidCube(1f);//elbow to wrist.
            gl.glPopMatrix();
            gl.glTranslated(wrist.x/2, wrist.y/2, wrist.z/2);
            drawSphere(glut);
            gl.glPopMatrix();
        }   else {
            gl.glPushMatrix();
            gl.glTranslated(shoulderR.x, shoulderR.y, shoulderR.z);
            gl.glRotated(shoulderRRotX, 1, 0, 0);
            gl.glRotated(shoulderRRotY, 0, 1, 0);
            gl.glTranslated(elbow.x/2, elbow.y/2, elbow.z/2);
            gl.glPushMatrix();
            angle = -Math.toDegrees(Math.atan(0.15/0.05));
            gl.glRotated(90 - angle, 0, 1, 0);
            gl.glScaled(boneWidth, boneWidth, (float)Math.sqrt(elbow.z*elbow.z + elbow.x*elbow.x));
            glut.glutSolidCube(1f);//shoulder to elbow.
            gl.glPopMatrix();
            gl.glTranslated(elbow.x/2, elbow.y/2, elbow.z/2);
            drawSphere(glut);
            gl.glRotated(elbowRRotX, 1, 0, 0);
            gl.glRotated(elbowRRotY, 0, 1, 0);
            gl.glTranslated(wrist.x/2, wrist.y/2, wrist.z/2);
            gl.glPushMatrix();
            gl.glScaled(boneWidth, boneWidth, wrist.z);
            glut.glutSolidCube(1f);//elbow to wrist.
            gl.glPopMatrix();
            gl.glTranslated(wrist.x/2, wrist.y/2, wrist.z/2);
            drawSphere(glut);
            //Draw sword in right hand.
            if (swordOpacity > 0){
                gl.glRotated(swordRotX, 1, 0, 0);
                gl.glTranslated(sword.x/2, sword.y/2, sword.z/2);
                gl.glScaled(boneWidth/2, sword.y, boneWidth/2);

                swordDiffuse[3] = swordOpacity;
                swordSpecular[3] = swordOpacity;

                gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, swordDiffuse, 0);
                gl.glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, swordSpecular, 0);
                gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, swordShine);

                glut.glutSolidCube(1f);               
            }
            gl.glPopMatrix(); 
        }
    }
    
    private void drawLeg (GL2 gl, GLUT glut, boolean leftLeg){
        if (leftLeg){
            gl.glPushMatrix();
            gl.glTranslated(hipL.x, hipL.y, hipL.z);
            gl.glRotated(hipLRotX, 1, 0, 0);
            gl.glRotated(hipLRotY, 0, 1, 0);
            gl.glTranslated(knee.x/2, knee.y/2, knee.z/2);
            gl.glPushMatrix();
            gl.glScaled(boneWidth, boneWidth, knee.z);
            glut.glutSolidCube(1f);//hip to knee.
            gl.glPopMatrix();
            gl.glTranslated(knee.x/2, knee.y/2, knee.z/2);
            drawSphere(glut);
            gl.glRotated(kneeLRotX, 1, 0, 0);
            gl.glRotated(kneeLRotY, 0, 1, 0);
            gl.glTranslated(heel.x/2, heel.y/2, heel.z/2);
            gl.glPushMatrix();
            gl.glScaled(boneWidth, boneWidth, heel.z);
            glut.glutSolidCube(1f);//knee to heel.
            gl.glPopMatrix();
            gl.glTranslated(heel.x/2, heel.y/2, heel.z/2);
            drawSphere(glut);
            gl.glTranslated(foot.x/2, foot.y/2, foot.z/2);
            gl.glPushMatrix();
            gl.glScaled(boneWidth, foot.y, boneWidth);
            glut.glutSolidCube(1f);//heel to foot.
            gl.glPopMatrix();
            gl.glTranslated(foot.x/2, foot.y/2, foot.z/2);
            drawSphere(glut);
            gl.glPopMatrix();
        }   else {
            gl.glPushMatrix();
            gl.glTranslated(hipR.x, hipR.y, hipR.z);
            gl.glRotated(hipRRotX, 1, 0, 0);
            gl.glRotated(hipRRotY, 0, 1, 0);
            gl.glTranslated(knee.x/2, knee.y/2, knee.z/2);
            gl.glPushMatrix();
            gl.glScaled(boneWidth, boneWidth, knee.z);
            glut.glutSolidCube(1f);//hip to knee.
            gl.glPopMatrix();
            gl.glTranslated(knee.x/2, knee.y/2, knee.z/2);
            drawSphere(glut);
            gl.glRotated(kneeRRotX, 1, 0, 0);
            gl.glRotated(kneeRRotY, 0, 1, 0);
            gl.glTranslated(heel.x/2, heel.y/2, heel.z/2);
            gl.glPushMatrix();
            gl.glScaled(boneWidth, boneWidth, heel.z);
            glut.glutSolidCube(1f);//knee to heel.
            gl.glPopMatrix();
            gl.glTranslated(heel.x/2, heel.y/2, heel.z/2);
            drawSphere(glut);
            gl.glTranslated(foot.x/2, foot.y/2, foot.z/2);
            gl.glPushMatrix();
            gl.glScaled(boneWidth, foot.y, boneWidth);
            glut.glutSolidCube(1f);//heel to foot.
            gl.glPopMatrix();
            gl.glTranslated(foot.x/2, foot.y/2, foot.z/2);
            drawSphere(glut);
            gl.glPopMatrix();
        }
    }
        
     private void drawRank (GL2 gl){
         
 
        if (showRank){
            
             gl.glEnable(GL2.GL_TEXTURE_2D);
                    switch(rank) {
            case BOMB:
                Terrain.Bomb.bind(gl);
                break;
            case FLAG:
                Terrain.Flag.bind(gl);
                break;
            case SPY:
                Terrain.Spy.bind(gl);
                break;
            case SCOUT:
                Terrain.Scout.bind(gl);
                break;
            case MINER:
                Terrain.Miner.bind(gl);
                break;
            case SERGEANT:
                Terrain.Sergeant.bind(gl);
                break;
            case LIEUTENANT:
                Terrain.Lieutenant.bind(gl);
                break;
            case CAPTAIN:
                Terrain.Captain.bind(gl);
                break;
            case MAJOR:
                Terrain.Major.bind(gl);
                break;
            case COLONEL:
                Terrain.Colonel.bind(gl);
                break;
            case MARSHALL:
                Terrain.Marshall.bind(gl);
                break;
            case GENERAL:
                Terrain.General.bind(gl);
                break;
            default:
                
        }
           gl.glBegin(GL_TRIANGLE_STRIP);
                
                gl.glTexCoord2d(0, 0);
                gl.glVertex3d(0, -0.3, 2.5);
                  
                gl.glTexCoord2d(1, 0);
                gl.glVertex3d(0, 0.3, 2.5);
                    
                gl.glTexCoord2d(0, 1);
                gl.glVertex3d(0,-0.3,3.5);

                gl.glTexCoord2d(1, 1);
                gl.glVertex3d(0,0.3,3.5);
            gl.glEnd(); 
            
            gl.glDisable(GL2.GL_TEXTURE_2D);
         }
         
     }
    /**
     * Draw a sphere at the specified target (relative to current location).
     * @param gl
     * @param glut
     * @param target offset from current location to draw at.
     */
    private void drawAt (GL2 gl, GLUT glut, Vector target){
        gl.glPushMatrix();
        gl.glTranslated(target.x, target.y, target.z);
        drawSphere(glut);
        gl.glPopMatrix();
    }
    
    private void drawSphere(GLUT glut){
        glut.glutSolidSphere(boneWidth, 10, 10);
    }
    
    public void move(Vector a){
        offset.add(a);
    }
    
    /**
     * Method to rotate skeleton to the given angle (regardless of current angle).
     * @param degrees angle.
     */
    public void rotate(int degrees){
        int change = rotation - degrees;
        rotation = degrees; 
        offset.rotate(change);
    }
    
    public int getRotation(){
        return rotation;
    }
    
    public BoardPosition getPosition(){
        return position;
    }
}
