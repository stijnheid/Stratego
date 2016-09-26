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
    
    public Vector head, neck, shoulderL, shoulderR, elbowL, elbowR, wristL, wristR,
            spine, hipL, hipR, kneeL, kneeR, heelL, heelR, footL, footR;
    
    public Vector offset;
    
    public List<Vector> joints;
    
    /**
     * Constructor for a skeleton. Does not yet draw a skeleton.
     * @param offset 
     */
    public Skeleton (Vector offset){
        joints = new ArrayList<Vector>();
        this.offset = offset;
        
        head = new Vector(0, 0, 0.95f);
        joints.add(head);
        neck = new Vector(0, 0, 0.8f);
        joints.add(neck);
        shoulderL = new Vector (-0.1f, 0, 0.8f);
        joints.add(shoulderL);
        shoulderR = new Vector (0.1f, 0, 0.8f);
        joints.add(shoulderR);
        elbowL = new Vector (-0.15f, 0, 0.65f);
        joints.add(elbowL);
        elbowR = new Vector (0.15f, 0, 0.65f);
        joints.add(elbowR);
        wristL = new Vector (-0.15f, 0, 0.5f);
        joints.add(wristL);
        wristR = new Vector (0.15f, 0, 0.5f);
        joints.add(wristR);
        spine = new Vector (0, 0, 0.6f);
        joints.add(spine);
        hipL = new Vector (-0.075f, 0, 0.4f);
        joints.add(hipL);
        hipR = new Vector (0.075f, 0, 0.4f);
        joints.add(hipR);
        kneeL = new Vector (-0.075f, 0, 0.2f);
        joints.add(kneeL);
        kneeR = new Vector (0.075f, 0, 0.2f);
        joints.add(kneeR);
        heelL = new Vector (-0.075f, 0, 0);
        joints.add(heelL);
        heelR = new Vector (0.075f, 0, 0);
        joints.add(heelR);
        footL = new Vector (-0.075f, 0.1f, 0);
        joints.add(footL);
        footR = new Vector (0.075f, 0.1f, 0);
        joints.add(footR);
        
        joints.stream().forEach(v -> v.add(offset));
    }
    
    public void draw(GL2 gl, GLUT glut){
        joints.stream().forEach((v) -> {
           gl.glTranslatef(v.x, v.y, v.z);
           glut.glutSolidSphere(0.025f, 10, 10);
           gl.glTranslatef(-v.x, -v.y, -v.z);});
    }
    
    public void move(Vector a){
        joints.stream().forEach(e -> e.add(a));
            offset.add(a);
    }
    
    public void rotate(double degrees){
        joints.stream().forEach((v) -> {
            v.subtract(offset);
            v.rotate(degrees);
            v.add(offset);});
    }
}
