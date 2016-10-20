/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Renderer;

import java.util.ArrayList;
import java.util.List;
import static javax.media.opengl.GL.GL_FRONT_AND_BACK;
import javax.media.opengl.GL2;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_AMBIENT_AND_DIFFUSE;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SHININESS;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SPECULAR;

/**
 *
 * @author s147724
 */
public class Stone  {
    double X,Y,Z;
    
    FitSphere fitSphere;

    
    public Stone(double x, double y, double z){
    X=x;
    Y=y;
    Z=z-1;
        List<Vector> input = new ArrayList<Vector>();
        input.add(new Vector( X-0.5, Y-1, Z));
        input.add(new Vector( X-0.5, Y, Z));
        input.add(new Vector( X+0.5, Y+1, Z));
        input.add(new Vector( X-0.5, Y-1, Z+1));
        input.add(new Vector( X+0.5, Y-1, Z+1));
        input.add(new Vector( X+0.5, Y, Z+1));
        input.add(new Vector( X-0.5, Y+0.3, Z+1));
        
        fitSphere = new FitSphere(input,0.1f, 10);
    }    
    public void draw(GL2 gl){
        
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, Material.CONCRETE.diffuse, 0);   
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, Material.CONCRETE.specular, 0);
        gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, Material.CONCRETE.shininess);
        
        Terrain.vakje.bind(gl);
        fitSphere.draw(gl, 1f);
    }    
        
}
    
