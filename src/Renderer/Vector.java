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
    
    public Vector(float x, float y, float z){
        this.x=x;
        this.y=y;
        this.z=z;
    }
    
    public Vector(double x, double y, double z){
        this.x = (float) x;
        this.y = (float) y;
        this.z = (float) z;
    }
    
    public Vector(Vector a){
        this.x = a.x;
        this.y = a.y;
        this.z = a.z;
    }
    
    public double x(){
        return x;
    }
    
    public double y(){
        return y;
    }
    
    public double z(){
        return z;
    }
    
    public double length(){
        return Math.sqrt(x*x+y*y+z*z);
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
     * Rotate Vector around the Z axis (horizontal).
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
    
    /**
     * Return a Vector in the middle of the two given Vectors.
     * @param a
     * @return 
     */
    public Vector between(Vector a){
        float x1 = (x + a.x) / 2;
        float y1 = (y + a.y) / 2;
        float z1 = (z + a.z) / 2;
        
        return new Vector(x1,y1,z1);
    }
    
    /**
     * Return the distance between the two vectors.
     * @param a operand.
     * @return distance between this and a in 3D space.
     */
    public float distance(Vector a){
        return (float)Math.sqrt(Math.pow(x - a.x, 2)+ Math.pow(y - a.y, 2)+ Math.pow(z - a.z, 2));
    }
    
    /**
     * Scale this vector by a factor of t.
     * @param t scaling factor (t \in [0,1]).
     * @return 
     */
    public Vector scale(float t){
        return new Vector(x*t,y*t,z*t);
    }
}
