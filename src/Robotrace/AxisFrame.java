package robotrace;

import com.jogamp.opengl.util.gl2.GLUT;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import static javax.media.opengl.GL.GL_FRONT_AND_BACK;
import static javax.media.opengl.fixedfunc.GLLightingFunc.*;
import static java.lang.Math.*;

public class AxisFrame {
	
	double length;
	
	AxisFrame(double length) {
		this.length = length;
	}
	
	public void draw(GL2 gl, GLU glu, GLUT glut) {
		

        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, Material.YELLOW.diffuse, 0);
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, Material.YELLOW.diffuse, 0);
        gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, Material.YELLOW.shininess);

        glut.glutSolidSphere(0.05*length, 100, 100);

        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, Material.RED.diffuse, 0);
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, Material.RED.diffuse, 0);
        gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, Material.RED.shininess);

        gl.glPushMatrix();
        gl.glScaled(length, 0.01*length, 0.01*length);
        gl.glTranslatef(0.5f, 0f, 0f);
        glut.glutSolidCube(1f);
        gl.glPopMatrix();
                                        //x axis
        gl.glPushMatrix();
        gl.glTranslated(length, 0, 0);
        gl.glRotatef(90f, 0, 1f, 0);
        glut.glutSolidCone(0.04*length, 0.1*length, 100, 100);
        gl.glPopMatrix();

        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, Material.GREEN.diffuse, 0);
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, Material.GREEN.diffuse, 0);
        gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, Material.GREEN.shininess);

        gl.glPushMatrix();
        gl.glScaled(0.01*length, length, 0.01*length);
        gl.glTranslatef(0f, 0.5f, 0f);
        glut.glutSolidCube(1f);
        gl.glPopMatrix();
                                        //y axis
        gl.glPushMatrix();
        gl.glTranslated(0, length, 0);
        gl.glRotatef(90f, -1f, 0, 0);
        glut.glutSolidCone(0.04*length, 0.1*length, 100, 100);
        gl.glPopMatrix();

        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, Material.BLUE.diffuse, 0);
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, Material.BLUE.diffuse, 0);
        gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, Material.BLUE.shininess);

        gl.glPushMatrix();
        gl.glScaled(0.01*length, 0.01*length, length);
        gl.glTranslatef(0f, 0f, 0.5f);
        glut.glutSolidCube(1f);
        gl.glPopMatrix();

                                        //z axis
        gl.glPushMatrix();
        gl.glTranslated(0, 0, length);
        gl.glRotatef(0, 0, 0, 0);
        glut.glutSolidCone(0.04*length, 0.1*length, 100, 100);
        gl.glPopMatrix();

        }
	}
