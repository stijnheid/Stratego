package robotrace;

import com.jogamp.opengl.util.gl2.GLUT;
import java.awt.Color;
import javax.media.opengl.GL2;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import java.util.ArrayList;
import java.util.List;        
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import static java.lang.Math.*;
import static javax.media.opengl.GL.GL_LINEAR;
import static javax.media.opengl.GL.GL_RGBA;
import static javax.media.opengl.GL.GL_RGBA8;
import static javax.media.opengl.GL.GL_TEXTURE_2D;
import static javax.media.opengl.GL.GL_TEXTURE_MAG_FILTER;
import static javax.media.opengl.GL.GL_TEXTURE_MIN_FILTER;
import static javax.media.opengl.GL.GL_TRIANGLE_STRIP;
import static javax.media.opengl.GL.GL_RGB;
import static javax.media.opengl.GL.GL_TRIANGLE_FAN;
import static javax.media.opengl.GL.GL_UNSIGNED_BYTE;
import static javax.media.opengl.GL.GL_FRONT_AND_BACK;
import static javax.media.opengl.GL2.GL_QUAD_STRIP;
import static javax.media.opengl.GL2GL3.GL_TEXTURE_1D;
import static javax.media.opengl.fixedfunc.GLLightingFunc.*;
/**
 * Implementation of the terrain.
 */
public class Terrain {
	
	/** Variables holding the width and length of this terrain */
	double width;
	double length;

	/*
	 * The constructor of the terrain which 
	 * stores the given length and width
	 * in an according variable
	 */
    public Terrain(double width, double length) {
    	this.width = width;
    	this.length = length;
    }

    /**
     * Draws the terrain.
     */
    public void draw(GL2 gl) {
        
    // computes a 2D Vector Array containing the coördinates of each point in 
    // the terrain mesh.
    int xCount = (int) width+40;
    int yCount = (int) length+40;

    Vector[][] points = new Vector[xCount][yCount];
        
    for (int i = 0; i < points.length; i++) {
        for (int n = 0; n < points[i].length; n++) {
        	double x = -0.5*width + i*width/xCount;
        	double y = -0.5*length + n*length/yCount;
        	double z = heightAt(x ,y); 
        	points[i][n] = new Vector(x, y ,z);
        }
    }
    
    
    //Sets up the Material for the to be rendered terrain
    gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, Material.GROUND.diffuse, 0);
    gl.glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, Material.GROUND.specular, 0);
    gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, Material.GROUND.shininess);
    
    // sets up the texture
    Main.grass.bind(gl);
        
    // draws every point of the terrain mesh in a quad strip, allong with mapping
    // the texture and setting up the normals
    for (int xi = 0; xi < points.length - 1; xi++) {
        for (int yi = 0; yi < points[xi].length - 1; yi++) {

        gl.glBegin(GL_QUAD_STRIP);
        	gl.glNormal3dv(Main.normal(points[xi][yi], points[xi+1][yi+1]), 0);
        	gl.glNormal3dv(Main.normal(points[xi][yi], points[xi+1][yi]), 0);
        	gl.glNormal3dv(Main.normal(points[xi][yi], points[xi][yi+1]), 0);
        	gl.glNormal3dv(Main.normal(points[xi+1][yi], points[xi+1][yi+1]), 0);
        	gl.glNormal3dv(Main.normal(points[xi+1][yi+1], points[xi+1][yi+1]), 0);
        	gl.glTexCoord2d(0, 0);
        	gl.glVertex3d(points[xi][yi].x, points[xi][yi].y, points[xi][yi].z);
        	gl.glTexCoord2d(1, 0);
        	gl.glVertex3d(points[xi+1][yi].x, points[xi+1][yi].y, points[xi+1][yi].z);
        	gl.glTexCoord2d(0, 1);
        	gl.glVertex3d(points[xi][yi+1].x, points[xi][yi+1].y, points[xi][yi+1].z);
        	gl.glTexCoord2d(1, 1);
        	gl.glVertex3d(points[xi+1][yi+1].x, points[xi+1][yi+1].y, points[xi+1][yi+1].z);
        gl.glEnd();
        }
    }
    
    // sets up the Material and texture
    gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, Material.WATER.diffuse, 0);
    gl.glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, Material.WATER.specular, 0);
    gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, Material.WATER.shininess);
    Main.water.bind(gl);

    // Because the water is actually just one plane, there is only one normal
    // Vector, so it can be taken out of the loop.
    gl.glNormal3d(0, 0, 1);
    
    // Draws the Water, with te same size quads as the terrain
    for (int xi = 0; xi < points.length - 1; xi++) {
        for (int yi = 0; yi < points[xi].length - 1; yi++) {
            gl.glBegin(GL_QUAD_STRIP); 
        	gl.glTexCoord2d(0, 0);
        	gl.glVertex3d(points[xi][yi].x, points[xi][yi].y, 0);
        	gl.glTexCoord2d(1, 0);
        	gl.glVertex3d(points[xi+1][yi].x, points[xi+1][yi].y, 0);
        	gl.glTexCoord2d(0, 1);
        	gl.glVertex3d(points[xi][yi+1].x, points[xi][yi+1].y, 0);
        	gl.glTexCoord2d(1, 1);
        	gl.glVertex3d(points[xi+1][yi+1].x, points[xi+1][yi+1].y,0);
            gl.glEnd(); 
        }
    }
}        

    /**
     * Computes the elevation of the terrain at (x, y).
     */
    static double heightAt(double x, double y) {
        double height = 0;
        if (abs(x) < 1)
            height = x*x-0.9;
        else height = 0.1;
        return (height);
    }
}
