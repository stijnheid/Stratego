/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Renderer;
import Game.BoardPosition;

import java.util.List;
import java.util.ArrayList;
import javax.media.opengl.GL2;
import com.jogamp.opengl.util.gl2.GLUT;
import static javax.media.opengl.GL.GL_FRONT_AND_BACK;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_AMBIENT_AND_DIFFUSE;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SHININESS;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SPECULAR;

/**
 *  Class to holding information for the skeleton of a piece.
 */
public class Skeleton {
    
    /**Vectors for every joint in the skeleton. */
    public Vector head, neck, shoulderL, shoulderR, elbow, wrist,
            spine, hipL, hipR, knee, heel, foot;
    
    /**Offset from (0,0) of skeleton.*/
    public Vector offset;
    
    /**Position of this skeleton on the board.*/
    public BoardPosition position;
    
    /**Data Structure holding the joints data.*/
    public List<Vector> joints;
    
    /** Rotation of this skeleton. (0 <= rotation < 360)*/
    private int rotation;
    
    /** Thickness of bones in skeleton. */
    private final float boneWidth = 0.05f;
    
    /**Rotations of vital joints in X and Y dimensions.*/
    float shoulderLRotX = 0;
    float shoulderLRotY = 0;
    float shoulderRRotX = 0;
    float shoulderRRotY = 0;
    float elbowLRotX = 0;
    float elbowLRotY = 0;
    float elbowRRotX = 0;
    float elbowRRotY = 0;
    float hipLRotX = 0;
    float hipLRotY = 0;
    float hipRRotX = 0;
    float hipRRotY = 0;
    float kneeLRotX = 0;
    float kneeLRotY = 0;
    float kneeRRotX = 0;
    float kneeRRotY = 0;

    /**
     * Constructor for a skeleton. Does not yet draw a skeleton.
     * @param p position on the board (in cells).
     */
    public Skeleton (BoardPosition p){
        joints = new ArrayList<Vector>();
        this.position = p;
        offset = new Vector(p.getX()-2.5,p.getY()-2.5,0);
        rotation = 0;
        
        head = new Vector(0, 0, 1.8f);
        joints.add(head);
        neck = new Vector(0, 0, 1.6f);
        joints.add(neck);
        shoulderL = new Vector (-0.2f, 0, 1.6f);
        joints.add(shoulderL);
        shoulderR = new Vector (0.2f, 0, 1.6f);
        joints.add(shoulderR);
        elbow = new Vector (0.1f, 0, -0.3f);//RELATIVE TO SHOULDER
        joints.add(elbow);
        wrist = new Vector (0, 0, -0.3f);//RELATIVE TO ELBOW
        joints.add(wrist);
        spine = new Vector (0, 0, 1);
        joints.add(spine);
        hipL = new Vector (-0.15f, 0, 0.8f);
        joints.add(hipL);
        hipR = new Vector (0.15f, 0, 0.8f);
        joints.add(hipR);
        knee = new Vector (0, 0, -0.4f);//RELATIVE TO HIP
        joints.add(knee);
        heel = new Vector (0, 0, -0.4f);//RELATIVE TO KNEE
        joints.add(heel);
        foot = new Vector (0, 0.1f, 0);//RELATIVE TO HEEL
        joints.add(foot);
    }
    
    /**
     * Draws a complete skeleton.
     * @param gl
     * @param glut 
     */
    public void draw(GL2 gl, GLUT glut){
        gl.glPushMatrix();
        gl.glDisable(GL2.GL_TEXTURE_2D);
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, Material.GOLD.diffuse, 0);
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, Material.GOLD.specular, 0);
        gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, Material.GOLD.shininess);
        gl.glRotated(rotation, 0, 0, 1);
        gl.glTranslatef(offset.x, offset.y, offset.z);
        drawTorso(gl, glut);
        drawArm(gl, glut, true);// left arm
        drawArm(gl, glut, false);//right arm
        drawLeg(gl, glut, true);//left leg
        drawLeg(gl, glut, false);//right leg
        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glPopMatrix();
    }
    
    private void drawTorso (GL2 gl, GLUT glut){
        Vector middle;
        //draw head manually (make it a bit larger).
        gl.glPushMatrix();
        gl.glTranslatef(head.x,head.y,head.z);
        glut.glutSolidSphere(2*boneWidth, 10, 10);
        gl.glPopMatrix();
        
        //draw torso joint connections.
        gl.glPushMatrix();
        middle = head.between(neck);
        gl.glTranslatef(middle.x, middle.y, middle.z);
        gl.glRotated(-Math.toDegrees(Math.atan((head.y-neck.y)/(head.z-neck.z))),1,0,0);
        gl.glScalef(boneWidth, boneWidth, head.z-neck.z);
        glut.glutSolidCube(1f);//neck to head.
        gl.glPopMatrix();
        
        gl.glPushMatrix();
        middle = neck.between(shoulderL);
        gl.glTranslatef(middle.x, middle.y, middle.z);
        gl.glRotated(Math.toDegrees(Math.atan((neck.y-shoulderL.y)/(neck.x-shoulderL.x))),0,0,1);
        gl.glScalef(neck.distance(shoulderL), boneWidth, boneWidth);
        glut.glutSolidCube(1f);//neck to shoulderL.
        gl.glPopMatrix();
        gl.glPushMatrix();
        middle = neck.between(shoulderR);
        gl.glTranslatef(middle.x, middle.y, middle.z);
        gl.glRotated(Math.toDegrees(Math.atan((neck.y-shoulderR.y)/(neck.x-shoulderR.x))),0,0,1);
        gl.glScalef(neck.distance(shoulderR), boneWidth, boneWidth);
        glut.glutSolidCube(1f);//neck to shoulderR.
        gl.glPopMatrix();
        
        gl.glPushMatrix();
        middle = neck.between(spine);
        gl.glTranslatef(middle.x, middle.y, middle.z);
        gl.glRotated(-Math.toDegrees(Math.atan((neck.y-spine.y)/(neck.z-spine.z))),1,0,0);
        gl.glScalef(boneWidth, boneWidth, neck.distance(spine));
        glut.glutSolidCube(1f);//neck to spine.
        gl.glPopMatrix();
        
        gl.glPushMatrix();
        middle = spine.between(hipL);
        double angle = Math.toDegrees(Math.atan((spine.z-hipL.z)/(spine.x-hipL.x)));
        gl.glTranslatef(middle.x, middle.y, middle.z);
        gl.glRotated(90 - angle, 0, 1, 0);
        gl.glRotated(-90 + Math.toDegrees(Math.atan((spine.z-hipL.z)/(spine.y-hipL.y))),1,0,0);
        gl.glScalef(boneWidth, boneWidth, spine.distance(hipL));
        glut.glutSolidCube(1f);//spine to hipL.
        gl.glPopMatrix();
        
        gl.glPushMatrix();
        middle = spine.between(hipR);
        angle = -angle;
        gl.glTranslatef(middle.x, middle.y, middle.z);
        gl.glRotated(90 - angle, 0, 1, 0);
        gl.glRotated(90 - Math.toDegrees(Math.atan((spine.z-hipR.z)/(spine.y-hipR.y))),1,0,0);
        gl.glScalef(boneWidth, boneWidth, spine.distance(hipR));
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
            gl.glTranslatef(shoulderL.x, shoulderL.y, shoulderL.z);
            gl.glRotatef(shoulderLRotX, 1, 0, 0);
            gl.glRotatef(shoulderLRotY, 0, 1, 0);
            gl.glTranslatef(-elbow.x/2, elbow.y/2, elbow.z/2);
            gl.glPushMatrix();
            angle = Math.toDegrees(Math.atan(0.15/0.05));
            gl.glRotated(90 - angle, 0, 1, 0);
            gl.glScalef(boneWidth, boneWidth, (float)Math.sqrt(elbow.z*elbow.z + elbow.x*elbow.x));
            glut.glutSolidCube(1f);//shoulder to elbow.
            gl.glPopMatrix();
            gl.glTranslatef(-elbow.x/2, elbow.y/2, elbow.z/2);
            drawSphere(glut);
            gl.glRotatef(elbowLRotX, 1, 0, 0);
            gl.glRotatef(elbowLRotY, 0, 1, 0);
            gl.glTranslatef(wrist.x/2, wrist.y/2, wrist.z/2);
            gl.glPushMatrix();
            gl.glScalef(boneWidth, boneWidth, wrist.z);
            glut.glutSolidCube(1f);//elbow to wrist.
            gl.glPopMatrix();
            gl.glTranslatef(wrist.x/2, wrist.y/2, wrist.z/2);
            drawSphere(glut);
            gl.glPopMatrix();
        }   else {
            gl.glPushMatrix();
            gl.glTranslatef(shoulderR.x, shoulderR.y, shoulderR.z);
            gl.glRotatef(shoulderRRotX, 1, 0, 0);
            gl.glRotatef(shoulderRRotY, 0, 1, 0);
            gl.glTranslatef(elbow.x/2, elbow.y/2, elbow.z/2);
            gl.glPushMatrix();
            angle = -Math.toDegrees(Math.atan(0.15/0.05));
            gl.glRotated(90 - angle, 0, 1, 0);
            gl.glScalef(boneWidth, boneWidth, (float)Math.sqrt(elbow.z*elbow.z + elbow.x*elbow.x));
            glut.glutSolidCube(1f);//shoulder to elbow.
            gl.glPopMatrix();
            gl.glTranslatef(elbow.x/2, elbow.y/2, elbow.z/2);
            drawSphere(glut);
            gl.glRotatef(elbowRRotX, 1, 0, 0);
            gl.glRotatef(elbowRRotY, 0, 1, 0);
            gl.glTranslatef(wrist.x/2, wrist.y/2, wrist.z/2);
            gl.glPushMatrix();
            gl.glScalef(boneWidth, boneWidth, wrist.z);
            glut.glutSolidCube(1f);//elbow to wrist.
            gl.glPopMatrix();
            gl.glTranslatef(wrist.x/2, wrist.y/2, wrist.z/2);
            drawSphere(glut);
            gl.glPopMatrix();            
        }
    }
    
    private void drawLeg (GL2 gl, GLUT glut, boolean leftLeg){
        if (leftLeg){
            gl.glPushMatrix();
            gl.glTranslatef(hipL.x, hipL.y, hipL.z);
            gl.glRotatef(hipLRotX, 1, 0, 0);
            gl.glRotatef(hipLRotY, 0, 1, 0);
            gl.glTranslatef(knee.x/2, knee.y/2, knee.z/2);
            gl.glPushMatrix();
            gl.glScalef(boneWidth, boneWidth, knee.z);
            glut.glutSolidCube(1f);//hip to knee.
            gl.glPopMatrix();
            gl.glTranslatef(knee.x/2, knee.y/2, knee.z/2);
            drawSphere(glut);
            gl.glRotatef(kneeLRotX, 1, 0, 0);
            gl.glRotatef(kneeLRotY, 0, 1, 0);
            gl.glTranslatef(heel.x/2, heel.y/2, heel.z/2);
            gl.glPushMatrix();
            gl.glScalef(boneWidth, boneWidth, heel.z);
            glut.glutSolidCube(1f);//knee to heel.
            gl.glPopMatrix();
            gl.glTranslatef(heel.x/2, heel.y/2, heel.z/2);
            drawSphere(glut);
            gl.glTranslatef(foot.x/2, foot.y/2, foot.z/2);
            gl.glPushMatrix();
            gl.glScalef(boneWidth, foot.y, boneWidth);
            glut.glutSolidCube(1f);//heel to foot.
            gl.glPopMatrix();
            gl.glTranslatef(foot.x/2, foot.y/2, foot.z/2);
            drawSphere(glut);
            gl.glPopMatrix();
        }   else {
            gl.glPushMatrix();
            gl.glTranslatef(hipR.x, hipR.y, hipR.z);
            gl.glRotatef(hipRRotX, 1, 0, 0);
            gl.glRotatef(hipRRotY, 0, 1, 0);
            gl.glTranslatef(knee.x/2, knee.y/2, knee.z/2);
            gl.glPushMatrix();
            gl.glScalef(boneWidth, boneWidth, knee.z);
            glut.glutSolidCube(1f);//hip to knee.
            gl.glPopMatrix();
            gl.glTranslatef(knee.x/2, knee.y/2, knee.z/2);
            drawSphere(glut);
            gl.glRotatef(kneeRRotX, 1, 0, 0);
            gl.glRotatef(kneeRRotY, 0, 1, 0);
            gl.glTranslatef(heel.x/2, heel.y/2, heel.z/2);
            gl.glPushMatrix();
            gl.glScalef(boneWidth, boneWidth, heel.z);
            glut.glutSolidCube(1f);//knee to heel.
            gl.glPopMatrix();
            gl.glTranslatef(heel.x/2, heel.y/2, heel.z/2);
            drawSphere(glut);
            gl.glTranslatef(foot.x/2, foot.y/2, foot.z/2);
            gl.glPushMatrix();
            gl.glScalef(boneWidth, foot.y, boneWidth);
            glut.glutSolidCube(1f);//heel to foot.
            gl.glPopMatrix();
            gl.glTranslatef(foot.x/2, foot.y/2, foot.z/2);
            drawSphere(glut);
            gl.glPopMatrix();
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
        gl.glTranslatef(target.x, target.y, target.z);
        drawSphere(glut);
        gl.glPopMatrix();
    }
    
    private void drawSphere(GLUT glut){
        glut.glutSolidSphere(boneWidth, 10, 10);
    }
    
    public void move(Vector a){
        joints.stream().forEach(e -> e.add(a));
        offset.add(a);
    }
    
    public void rotate(int degrees){
        rotation = degrees; 
        offset.rotate(-degrees);
    }
}
