/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Renderer;

/** Class to represent a 3D Vector.
 *
 */
public class Vector {
    
    public float x,y,z;
    double length;
    
    public Vector(float x, float y, float z){
        this.x=x;
        this.y=y;
        this.z=z;
        this.length = Math.sqrt(x*x+y*y+z*z);
    }
    
    /**
     * Standard Vector addition.
     * @param a operand.
     */
    public void add(Vector a){
        x = x + a.x;
        y = y + a.y;
        z = z + a.z;
    }
    
    /**
     * Standard Vector subtraction.
     * @param a operand.
     */
    public void subtract(Vector a){
        x = x - a.x;
        y = y - a.y;
        z = z - a.z;
    }
    
    /**
     * Rotate Vector around the Z axis.
     * @param deg degrees of rotation.
     */
    public void rotate(double deg){
        double rad = Math.toRadians(deg);
        x = x * (float)Math.cos(rad) - y * (float)Math.sin(rad);
        y = x * (float)Math.sin(rad) + y * (float)Math.cos(rad);
    }
    
    /**
     * Vector cross product.
     * @param a operand.
     * @return vector perpendicular to this and a.
     */
    public Vector cross(Vector a){
        float x1 = y * a.z - z * a.y;
        float y1 = z * a.x - x * a.z;
        float z1 = x * a.y - y * a.x;
        
        return new Vector(x1,y1,z1);
    }
}
