/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Renderer;


import java.util.List;
import static java.lang.Math.*;
import java.util.ArrayList;
import static javax.media.opengl.GL.GL_FRONT_AND_BACK;
import javax.media.opengl.GL2;
import static javax.media.opengl.GL.GL_TRIANGLE_STRIP;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_AMBIENT_AND_DIFFUSE;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_LIGHTING;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SHININESS;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SPECULAR;

/**
 *
 * @author s147724
 */
public class FitSphere {
    
    List<Vector> input;
    List<Vector> PointsSphere;
    List<Vector> WeightPoints;
    List<Vector> Difference;
    double NodesOnRing;
    

   
    public FitSphere(List<Vector> Input, float adaption, double nodes){
        input = Input;
           
           
        PointsSphere = new ArrayList();
        WeightPoints = new ArrayList();
        Difference = new ArrayList();
        
        NodesOnRing = nodes;
        
        Vector summation = new Vector(0,0,0);
        Vector SpherePoint = new Vector(0,0,0);
        float radius = 0;
        
        for (Vector v : input){
            summation.add(v);  
        }
        summation = summation.scale(1/(float)input.size());
        



       
        for (Vector v : input){
            radius += summation.distance(v);
        }
        radius = radius/input.size();




        for (double i =0; i<=NodesOnRing; i++){
            for (double j=0; j <=NodesOnRing; j++){

                SpherePoint = new Vector(sin(i/NodesOnRing*2*PI)*sin(j/NodesOnRing*2*PI), sin(i/NodesOnRing*2*PI)*cos(j/NodesOnRing*2*PI), cos(i/NodesOnRing*2*PI)).scale(radius);          
                SpherePoint.add(summation);
                PointsSphere.add(SpherePoint);
            }
        }
        
        int limit = 3;
        if (limit > input.size()){
            limit = input.size();
        }
        

        int p = 0;
        for(Vector v: PointsSphere){
            WeightPoints.clear();
            Difference.clear();
            for (Vector w : input){
                boolean NotAdded = true;
                
                if (WeightPoints.isEmpty()){
                    WeightPoints.add(w);
                }
                else{
                for (int i = 0; i < WeightPoints.size(); i++){
                    
                    if (( w.distance(v)< WeightPoints.get(i).distance(v) && NotAdded )){
                        WeightPoints.add(i, w);
                        NotAdded = false; 
                    }
                }  
                
                if (WeightPoints.size()<limit && NotAdded){
                    WeightPoints.add(w);
                }
                if (WeightPoints.size() >limit){
                    WeightPoints.remove(limit);  
                }
                }
            }
            /*
            System.out.print("spherepoint =" );
            System.out.println(p);
            System.out.println(v.x +","+ v.y +","+ v.z);
            System.out.println("Weightthing "+WeightPoints.size());  
            */
            
            for(int i =0; i< WeightPoints.size(); i++){
                //System.out.println(WeightPoints.get(i).x +","+ WeightPoints.get(i).y +","+ WeightPoints.get(i).z);
                Vector u = new Vector(WeightPoints.get(i));  
                u.subtract(v);
                Difference.add(u);
            }
            for (Vector u : Difference){
                //System.out.println(u.x +","+ u.y +","+ u.z);
                v.add(u.scale(adaption));
            }
                

            p++;
        }

    }
    
    void draw(GL2 gl, double partdrawn){
        

        

        gl.glBegin(GL_TRIANGLE_STRIP);
        for (int i = 0; i <partdrawn*PointsSphere.size()-6*NodesOnRing ; i=i-1){
                    
            
            Vector vector1 = new Vector(PointsSphere.get(i+(int)NodesOnRing));
            Vector vector2 = new Vector(PointsSphere.get(i +1));
            
            vector1.subtract(PointsSphere.get(i));
            vector2.subtract(PointsSphere.get(i));
            Vector vector3 = vector1.cross(vector2);
            gl.glNormal3d(vector3.x, vector3.y, vector3.z);
            
            
            gl.glTexCoord2d(0, 0);
            gl.glVertex3d(PointsSphere.get(i).x, PointsSphere.get(i).y, PointsSphere.get(i).z);
            i+=NodesOnRing+1;
            
            
            gl.glTexCoord2d(0, 1);
            gl.glVertex3d(PointsSphere.get(i).x, PointsSphere.get(i).y, PointsSphere.get(i).z);
            i-=(NodesOnRing);
            
            
            gl.glNormal3d(vector3.x, vector3.y, vector3.z);
            gl.glTexCoord2d(1, 1);
            gl.glVertex3d(PointsSphere.get(i).x, PointsSphere.get(i).y, PointsSphere.get(i).z);
            i+=NodesOnRing+1;
            
            
            gl.glTexCoord2d(1, 0);
            gl.glVertex3d(PointsSphere.get(i).x, PointsSphere.get(i).y, PointsSphere.get(i).z);
            i-=NodesOnRing-1;
            

        }
        gl.glEnd();




        
    }
    
}
