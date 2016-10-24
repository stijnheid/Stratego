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
    
    public double x,y,z;
    
    public Vector(float x, float y, float z){
        this.x=x;
        this.y=y;
        this.z=z;
    }
    
    public Vector(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
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
     * Rotate Vector around the Z axis.
     * @param deg degrees of rotation.
     */
    public void rotate(double deg){
        double rad = Math.toRadians(deg);
        double x1,y1;
        x1 = x * Math.cos(rad) - y * (float)Math.sin(rad);
        y1 = x * Math.sin(rad) + y * (float)Math.cos(rad);
        this.x = x1;
        this.y = y1;
    }
    
    /**
     * Vector cross product.
     * @param a operand.
     * @return vector perpendicular to this and a.
     */
    public Vector cross(Vector a){
        double x1 = y * a.z - z * a.y;
        double y1 = z * a.x - x * a.z;
        double z1 = x * a.y - y * a.x;
        
        return new Vector(x1,y1,z1);
    }
    
    /**
     * Return a Vector in the middle of the two given Vectors.
     * @param a
     * @return 
     */
    public Vector between(Vector a){
        double x1 = (x + a.x) / 2;
        double y1 = (y + a.y) / 2;
        double z1 = (z + a.z) / 2;
        
        return new Vector(x1,y1,z1);
    }
    
    /**
     * Return the distance between the two vectors.
     * @param a operand.
     * @return distance between this and a in 3D space.
     */
    public double distance(Vector a){
        return Math.sqrt(Math.pow(x - a.x, 2)+ Math.pow(y - a.y, 2)+ Math.pow(z - a.z, 2));
    }
    
    /**
     * Scale this vector by a factor of t.
     * @param t scaling factor.
     * @return 
     */
    public Vector scale(double t){
        return new Vector(x*t,y*t,z*t);
    }
    
    /**
     * Return the sum of this and another vector.
     * @param a addition operand
     * @return Vector that is this+a.
     */
    public Vector sum(Vector a){
        return new Vector(x+a.x, y+a.y, z+a.z);
    }
}
