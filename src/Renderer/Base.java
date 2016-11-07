/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Renderer;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GL;
import static javax.media.opengl.GL.GL_TEXTURE_2D;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLJPanel;
import javax.media.opengl.glu.GLU;
import javax.swing.UIManager;
import Game.GameState;
import Game.Team;
import Logic.Simulation;
import actions.PlyAction;

/**
 * Handles all of the RobotRace graphics functionality,
 * which should be extended per the Assignments.
 */
abstract public class Base {
    // Library version number.
    static public int LIBRARY_VERSION = 5;
    
    // Minimum distance of camera to center point.
    static public float MIN_CAMERA_DISTANCE = 1f;
    
    // Distance multiplier per mouse wheel tick.
    static public float MOUSE_WHEEL_FACTOR = 1.2f;
    
    // Minimum value of phi.
    static public float PHI_MIN = -(float) Math.PI / 2f + 0.01f;
    
    // Maximum value of phi.
    static public float PHI_MAX = (float) Math.PI / 2f - 0.01f;
    
    // Ratio of distance in pixels dragged and radial change of camera.
    static public float DRAG_PIXEL_TO_RADIAN = 0.025f;
    
    // Minimum value of vWidth.
    static public float VWIDTH_MIN = 1f;
    
    // Maximum value of vWidth.
    static public float VWIDTH_MAX = 1000f;
    
    // Ratio of vertical distance dragged and change of vWidth;
    static public float DRAG_PIXEL_TO_VWIDTH = 0.1f;
    
    // Extent of center point change based on key input.
    static public float CENTER_POINT_CHANGE = 1f;
    
    // Desired frames per second.
    static public int FPS = 60;
    
    // holds the selected square on the board.
    static public int selectPiece[] = {0,0};
    
        // holds the selected square on the board.
    static public int selectedPiece[] = {0,0};
    static public int destination[] = {0,0};
    static public int origin[] = {0,0};
    
    // counts the amount of enters
    static private int movecounter = 0;
    // Global state, created at startup.
    protected GameState gs;
    
    //Camera State with FOV variables.
    protected CameraState cs;
    
    // OpenGL reference, continuously updated for correct thread.
    protected GL2 gl;
    
    // OpenGL utility functions.
    protected GLU glu;
    protected GLUT glut;
    
    // Start time of animation.
    private long startTime;
    
    private Simulation simulation;
    
    // Textures.
    public static Texture track, brick, head, torso;
    
    //Animator object.
    protected FPSAnimator animator;
    
    /**
     * Constructs base class.
     */
    public Base() {
        // Global state.
        this.gs = new GameState();
        
        this.cs = new CameraState();
    }
	
	public void run() {
		// Enable fancy GUI theme.
        try {
            UIManager.setLookAndFeel(
                "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch(Exception ex) {
            Logger.getLogger(Base.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // GUI frame.
        MainFrame frame = new MainFrame(gs);
        
        // OpenGL utility functions.
        this.glu = new GLU();
        this.glut = new GLUT();
        
        // Redirect OpenGL listener to the abstract render functions.
        GLJPanel glPanel = frame.glPanel;
        glPanel.addGLEventListener(new GLEventDelegate());
        
        // Attach mouse and keyboard listeners.
        GLListener listener = new GLListener();
        glPanel.addMouseListener(listener);
        glPanel.addMouseMotionListener(listener);
        glPanel.addMouseWheelListener(listener);
        glPanel.addKeyListener(listener);
        glPanel.setFocusable(true);
        glPanel.requestFocusInWindow();
        
        // Attach animator to OpenGL panel and begin refresh
        // at the specified number of frames per second.
        animator =
                new FPSAnimator((GLJPanel) frame.glPanel, FPS, true);
        animator.setIgnoreExceptions(false);
        animator.setPrintExceptions(true);
        animator.setUpdateFPSFrames(5, null);
        animator.start();

        // Stop animator when window is closed.
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                animator.stop();
            }
        });       

        // Show frame.
        frame.setVisible(true);
	}
	
	/**
    * Try to load a texture from the given file. The file
    * should be located in the same folder as RobotRace.java.
    */
    public Texture loadTexture(String file) {
        Texture result = null;

        try {
            // Try to load from local folder.
            result = TextureIO.newTexture(new File(file), false);
        } catch(Exception e1) {
        }
            
        if(result != null) {
            //System.out.println("Loaded " + file);
            result.enable(gl);
        }

        return result;
    }
    
    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }
    
    /**
     * Called upon the start of the application.
     * Primarily used to configure OpenGL.
     */
    abstract public void initialize();
    
    /**
     * Configures the viewing transform.
     */
    abstract public void setView();
    
    /**
     * Draws the entire scene.
     */
    abstract public void drawScene();

    /**
     * Pass a vector as a vertex to OpenGL.
     */
    public void glVertex(Vector vector) {
        gl.glVertex3d(vector.x(),
                      vector.y(),
                      vector.z());
    }
    
    /**
     * Delegates OpenGL events to abstract methods.
     */
    private final class GLEventDelegate implements GLEventListener {

        /**
         * Initialization of OpenGL state.
         */
        @Override
        public void init(GLAutoDrawable drawable) {
            gl = drawable.getGL().getGL2();
            
            
            
            // Print library version number.
            //System.out.println("Using RobotRace library version " + LIBRARY_VERSION);
            
            initialize();
        }
    
        /**
         * Render scene.
         */
        @Override
        public void display(GLAutoDrawable drawable) {
            gl = drawable.getGL().getGL2();
            
            // Update wall time, and reset if required.
            if(cs.tAnim < 0) {
                startTime = System.currentTimeMillis();
            }
            cs.tAnim = (float) (System.currentTimeMillis() - startTime) / 1000f;
            
            // Also update view, because global state may have changed.
            setView();
			
			// Bind the null texture to avoid glitches on some machines
			gl.glBindTexture(GL_TEXTURE_2D, 0);
			
            drawScene();
            
            // Report OpenGL errors.
            int errorCode = gl.glGetError();
            while(errorCode != GL.GL_NO_ERROR) {
                System.err.println(errorCode + " " +
                                   glu.gluErrorString(errorCode));
                errorCode = gl.glGetError();
            }
        }

        /**
         * Canvas reshape.
         */
        @Override
        public void reshape(GLAutoDrawable drawable,
                            int x, int y,
                            int width, int height) {
            gl = drawable.getGL().getGL2();
            
            // Update state.
            cs.w = width;
            cs.h = height;
            
            setView();
        }

        @Override
        public void dispose(GLAutoDrawable drawable) {
            
        }

    }
    
    /**
     * Handles mouse events of the GLJPanel to support the interactive
     * change of camera angles and distance in the global state.
     */
    private final class GLListener implements MouseMotionListener,
                                              MouseListener,
                                              MouseWheelListener,
                                              KeyListener {
        // Position of mouse drag source.
        private int dragSourceX, dragSourceY;
        
        // Last mouse button pressed.
        private int mouseButton;

        @Override
        public void mouseDragged(MouseEvent e) {
            double dX = e.getX() - dragSourceX;
            double dY = e.getY() - dragSourceY;
            
            // Change camera angle when left button is pressed.
            try {// Camera settings are synchronised.
                cs.varLock.lock();
                if(mouseButton == MouseEvent.BUTTON1) {
                cs.theta += dX * DRAG_PIXEL_TO_RADIAN;
                cs.phi = Math.max(PHI_MIN,
                                    Math.min(PHI_MAX,
                                             cs.phi + dY * DRAG_PIXEL_TO_RADIAN));
                }
                // Change vWidth when right button is pressed.
                else if(mouseButton == MouseEvent.BUTTON3) {
                    cs.vWidth = Math.max(VWIDTH_MIN,
                                         Math.min(VWIDTH_MAX,
                                                  cs.vWidth + dY * DRAG_PIXEL_TO_VWIDTH));
                }
            }   finally {
                cs.varLock.unlock();
            }

            
            dragSourceX = e.getX();
            dragSourceY = e.getY();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            cs.vDist = (float) Math.max(MIN_CAMERA_DISTANCE,
                                        cs.vDist *
                                        Math.pow(MOUSE_WHEEL_FACTOR,
                                                 e.getWheelRotation()));
        }

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            dragSourceX = e.getX();
            dragSourceY = e.getY();
            mouseButton = e.getButton();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            // Move center point.
            double phiQ = cs.theta + Math.PI / 2.0;
            
            switch(e.getKeyChar()) {
                // Right.
                case 'a':   cs.cnt.subtract(
                                        new Vector(Math.cos(phiQ), Math.sin(phiQ), 0)
                                        .scale(CENTER_POINT_CHANGE));
                            break;
                // Left.
                case 'd':   cs.cnt.add(
                                        new Vector(Math.cos(phiQ), Math.sin(phiQ), 0)
                                        .scale(CENTER_POINT_CHANGE));
                            break;
                // Forwards.
                case 'w':   cs.cnt.subtract(
                                        new Vector(Math.cos(cs.theta), Math.sin(cs.theta), 0)
                                        .scale(CENTER_POINT_CHANGE));
                            break;
                // Backwards.
                case 's':   cs.cnt.add(
                                        new Vector(Math.cos(cs.theta), Math.sin(cs.theta), 0)
                                        .scale(CENTER_POINT_CHANGE));
                            break;
                // Up.
                case 'q':   cs.cnt = new Vector(cs.cnt.x,
                                                cs.cnt.y,
                                                cs.cnt.z + CENTER_POINT_CHANGE);
                            break;
                // Down.
                case 'z':   cs.cnt = new Vector(cs.cnt.x,
                                                cs.cnt.y,
                                                cs.cnt.z - CENTER_POINT_CHANGE);
                            break;
            }
            switch(e.getKeyCode()) {

            case KeyEvent.VK_UP:
                        if (selectPiece[1]<5){
                            selectPiece[1]++; 
                        }
                break;
            case KeyEvent.VK_DOWN:
                        if (selectPiece[1]>0){
                            selectPiece[1]--; 
                        }
                        break;
            case KeyEvent.VK_LEFT:
                        if (selectPiece[0]<5){
                            selectPiece[0]++; 
                        }
                        break;
            case KeyEvent.VK_RIGHT :
                        if (selectPiece[0]>0){
                            selectPiece[0]--; 
                        }
                        break;
            case KeyEvent.VK_ENTER :
                selectedPiece = selectPiece.clone();       
                if (movecounter == 0){
                    origin = selectedPiece;
                    movecounter++;
                }
                if (movecounter == 1 && origin != selectedPiece){
                        destination = selectedPiece;
                        PlyAction action;
                        try {
                            action = new PlyAction(Team.RED, new Game.BoardPosition(origin[0], origin[1]), new Game.BoardPosition(destination[0], destination[1]));                            
                            Base.this.simulation.processAction(action);
                        } catch (Exception err){}

                        movecounter = 0;
                }

                break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
        
    }
    
}
