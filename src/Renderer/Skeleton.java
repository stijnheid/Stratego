/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Renderer;
import java.util.List;
import java.util.ArrayList;
import javax.media.opengl.GL2;
import com.jogamp.opengl.util.gl2.GLUT;

/**
 *  Class to holding information for the skeleton of a piece.
 */
public class Skeleton {
    
    /**Vectors for every joint in the skeleton. */
    public Vector head, neck, shoulderL, shoulderR, elbow, wrist,
            spine, hipL, hipR, knee, heel, foot;
    
    /**Offset from (0,0) of skeleton.*/
    public Vector offset;
    
    /**Data Structure holding the joints data.*/
    public List<Vector> joints;
    
    /** Rotation of this skeleton. (0 <= rotation < 360)*/
    public int rotation;
    
    /** Thickness of bones in skeleton. */
    private float boneWidth = 0.025f;
    
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
     * @param offset from (0,0).
     */
    public Skeleton (Vector offset){
        joints = new ArrayList<Vector>();
        this.offset = offset;
        rotation = 0;
        
        head = new Vector(0, 0, 0.9f);
        joints.add(head);
        neck = new Vector(0, 0, 0.8f);
        joints.add(neck);
        shoulderL = new Vector (-0.1f, 0, 0.8f);
        joints.add(shoulderL);
        shoulderR = new Vector (0.1f, 0, 0.8f);
        joints.add(shoulderR);
        elbow = new Vector (0.05f, 0, -0.15f);//RELATIVE TO SHOULDER
        joints.add(elbow);
        wrist = new Vector (0, 0, -0.15f);//RELATIVE TO ELBOW
        joints.add(wrist);
        spine = new Vector (0, 0, 0.5f);
        joints.add(spine);
        hipL = new Vector (-0.075f, 0, 0.4f);
        joints.add(hipL);
        hipR = new Vector (0.075f, 0, 0.4f);
        joints.add(hipR);
        knee = new Vector (0, 0, -0.2f);//RELATIVE TO HIP
        joints.add(knee);
        heel = new Vector (0, 0, -0.2f);//RELATIVE TO KNEE
        joints.add(heel);
        foot = new Vector (0, 0.05f, 0);//RELATIVE TO HEEL
        joints.add(foot);
        
        joints.stream().forEach(v -> v.add(offset));
    }
    
    /**
     * Draws a complete skeleton.
     * @param gl
     * @param glut 
     */
    public void draw(GL2 gl, GLUT glut){
        gl.glPushMatrix();
        gl.glColor3f(1,1,1);
        gl.glRotated(rotation, 0, 0, 1);
        drawTorso(gl, glut);
        drawArm(gl, glut, true);// left arm
        drawArm(gl, glut, false);//right arm
        drawLeg(gl, glut, true);//left leg
        drawLeg(gl, glut, false);//right leg
        gl.glPopMatrix();        
    }
    
    private void drawTorso (GL2 gl, GLUT glut){
        Vector middle;
        //draw head manually (make it a bit larger).
        gl.glPushMatrix();
        gl.glTranslatef(head.x,head.y,head.z);
        glut.glutSolidSphere(0.05f, 10, 10);
        gl.glPopMatrix();
        
        //draw torso joint connections.
        gl.glPushMatrix();
        middle = head.between(neck);
        gl.glTranslatef(middle.x, middle.y, middle.z);
        gl.glScalef(boneWidth, boneWidth, head.z-neck.z);
        glut.glutSolidCube(1f);//neck to head.
        gl.glPopMatrix();
        
        gl.glPushMatrix();
        middle = neck.between(shoulderL);
        gl.glTranslatef(middle.x, middle.y, middle.z);
        gl.glScalef(shoulderL.x - neck.x, boneWidth, boneWidth);
        glut.glutSolidCube(1f);//neck to shoulderL.
        gl.glPopMatrix();
        gl.glPushMatrix();
        middle = neck.between(shoulderR);
        gl.glTranslatef(middle.x, middle.y, middle.z);
        gl.glScalef(neck.x - shoulderR.x, boneWidth, boneWidth);
        glut.glutSolidCube(1f);//neck to shoulderR.
        gl.glPopMatrix();
        
        gl.glPushMatrix();
        middle = neck.between(spine);
        gl.glTranslatef(middle.x, middle.y, middle.z);
        gl.glScalef(boneWidth, boneWidth, neck.z - spine.z);
        glut.glutSolidCube(1f);//neck to spine.
        gl.glPopMatrix();
        
        gl.glPushMatrix();
        middle = spine.between(hipL);
        double angle = Math.toDegrees(Math.atan((spine.z-hipL.z)/(spine.x-hipL.x)));
        gl.glTranslatef(middle.x, middle.y, middle.z);
        gl.glRotated(90 - angle, 0, 1, 0);
        gl.glScalef(boneWidth, boneWidth, (float)Math.sqrt(Math.pow(spine.x-hipL.x,2)+Math.pow(spine.z-hipL.z,2)));
        glut.glutSolidCube(1f);//spine to hipL.
        gl.glPopMatrix();
        
        gl.glPushMatrix();
        middle = spine.between(hipR);
        angle = -angle;
        gl.glTranslatef(middle.x, middle.y, middle.z);
        gl.glRotated(90 - angle, 0, 1, 0);
        gl.glScalef(boneWidth, boneWidth, (float)Math.sqrt(Math.pow(spine.x-hipR.x,2)+Math.pow(spine.z-hipR.z,2)));
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
            elbow.subtract(offset);
            gl.glTranslatef(-elbow.x/2, elbow.y/2, elbow.z/2);
            gl.glPushMatrix();
            angle = Math.toDegrees(Math.atan(0.15/0.05));
            gl.glRotated(90 - angle, 0, 1, 0);
            gl.glScalef(boneWidth, boneWidth, (float)Math.sqrt(elbow.z*elbow.z + elbow.x*elbow.x));
            glut.glutSolidCube(1f);//shoulder to elbow.
            gl.glPopMatrix();
            gl.glTranslatef(-elbow.x/2, elbow.y/2, elbow.z/2);
            drawSphere(glut);
            elbow.add(offset);
            gl.glRotatef(elbowLRotX, 1, 0, 0);
            gl.glRotatef(elbowLRotY, 0, 1, 0);
            wrist.subtract(offset);
            gl.glTranslatef(wrist.x/2, wrist.y/2, wrist.z/2);
            gl.glPushMatrix();
            gl.glScalef(boneWidth, boneWidth, wrist.z);
            glut.glutSolidCube(1f);//elbow to wrist.
            gl.glPopMatrix();
            gl.glTranslatef(wrist.x/2, wrist.y/2, wrist.z/2);
            drawSphere(glut);
            wrist.add(offset);
            gl.glPopMatrix();
        }   else {
            gl.glPushMatrix();
            gl.glTranslatef(shoulderR.x, shoulderR.y, shoulderR.z);
            gl.glRotatef(shoulderRRotX, 1, 0, 0);
            gl.glRotatef(shoulderRRotY, 0, 1, 0);
            elbow.subtract(offset);
            gl.glTranslatef(elbow.x/2, elbow.y/2, elbow.z/2);
            gl.glPushMatrix();
            angle = -Math.toDegrees(Math.atan(0.15/0.05));
            gl.glRotated(90 - angle, 0, 1, 0);
            gl.glScalef(boneWidth, boneWidth, (float)Math.sqrt(elbow.z*elbow.z + elbow.x*elbow.x));
            glut.glutSolidCube(1f);//shoulder to elbow.
            gl.glPopMatrix();
            gl.glTranslatef(elbow.x/2, elbow.y/2, elbow.z/2);
            drawSphere(glut);
            elbow.add(offset);
            gl.glRotatef(elbowRRotX, 1, 0, 0);
            gl.glRotatef(elbowRRotY, 0, 1, 0);
            wrist.subtract(offset);
            gl.glTranslatef(wrist.x/2, wrist.y/2, wrist.z/2);
            gl.glPushMatrix();
            gl.glScalef(boneWidth, boneWidth, wrist.z);
            glut.glutSolidCube(1f);//elbow to wrist.
            gl.glPopMatrix();
            gl.glTranslatef(wrist.x/2, wrist.y/2, wrist.z/2);
            drawSphere(glut);
            wrist.add(offset);
            gl.glPopMatrix();            
        }
    }
    
    private void drawLeg (GL2 gl, GLUT glut, boolean leftLeg){
        if (leftLeg){
            gl.glPushMatrix();
            gl.glTranslatef(hipL.x, hipL.y, hipL.z);
            gl.glRotatef(hipLRotX, 1, 0, 0);
            gl.glRotatef(hipLRotY, 0, 1, 0);
            knee.subtract(offset);
            gl.glTranslatef(knee.x/2, knee.y/2, knee.z/2);
            gl.glPushMatrix();
            gl.glScalef(boneWidth, boneWidth, knee.z);
            glut.glutSolidCube(1f);//hip to knee.
            gl.glPopMatrix();
            gl.glTranslatef(knee.x/2, knee.y/2, knee.z/2);
            drawSphere(glut);
            knee.add(offset);
            gl.glRotatef(kneeLRotX, 1, 0, 0);
            gl.glRotatef(kneeLRotY, 0, 1, 0);
            heel.subtract(offset);
            gl.glTranslatef(heel.x/2, heel.y/2, heel.z/2);
            gl.glPushMatrix();
            gl.glScalef(boneWidth, boneWidth, heel.z);
            glut.glutSolidCube(1f);//knee to heel.
            gl.glPopMatrix();
            gl.glTranslatef(heel.x/2, heel.y/2, heel.z/2);
            drawSphere(glut);
            heel.add(offset);
            foot.subtract(offset);
            gl.glTranslatef(foot.x/2, foot.y/2, foot.z/2);
            gl.glPushMatrix();
            gl.glScalef(boneWidth, foot.y, boneWidth);
            glut.glutSolidCube(1f);//heel to foot.
            gl.glPopMatrix();
            gl.glTranslatef(foot.x/2, foot.y/2, foot.z/2);
            drawSphere(glut);
            foot.add(offset);
            gl.glPopMatrix();
        }   else {
            gl.glPushMatrix();
            gl.glTranslatef(hipR.x, hipR.y, hipR.z);
            gl.glRotatef(hipRRotX, 1, 0, 0);
            gl.glRotatef(hipRRotY, 0, 1, 0);
            knee.subtract(offset);
            gl.glTranslatef(knee.x/2, knee.y/2, knee.z/2);
            gl.glPushMatrix();
            gl.glScalef(boneWidth, boneWidth, knee.z);
            glut.glutSolidCube(1f);//hip to knee.
            gl.glPopMatrix();
            gl.glTranslatef(knee.x/2, knee.y/2, knee.z/2);
            drawSphere(glut);
            knee.add(offset);
            gl.glRotatef(kneeRRotX, 1, 0, 0);
            gl.glRotatef(kneeRRotY, 0, 1, 0);
            heel.subtract(offset);
            gl.glTranslatef(heel.x/2, heel.y/2, heel.z/2);
            gl.glPushMatrix();
            gl.glScalef(boneWidth, boneWidth, heel.z);
            glut.glutSolidCube(1f);//knee to heel.
            gl.glPopMatrix();
            gl.glTranslatef(heel.x/2, heel.y/2, heel.z/2);
            drawSphere(glut);
            heel.add(offset);
            foot.subtract(offset);
            gl.glTranslatef(foot.x/2, foot.y/2, foot.z/2);
            gl.glPushMatrix();
            gl.glScalef(boneWidth, foot.y, boneWidth);
            glut.glutSolidCube(1f);//heel to foot.
            gl.glPopMatrix();
            gl.glTranslatef(foot.x/2, foot.y/2, foot.z/2);
            drawSphere(glut);
            foot.add(offset);
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
        glut.glutSolidSphere(0.025f, 10, 10);
    }
    
    public void move(Vector a){
        joints.stream().forEach(e -> e.add(a));
            offset.add(a);
    }
    
    public void rotate(int degrees){
        rotation = degrees;
    }
}
