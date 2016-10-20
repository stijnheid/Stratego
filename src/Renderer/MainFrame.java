package Renderer;

import javax.media.opengl.awt.GLJPanel;
import Game.GameState;

/**
 *
 */
public final class MainFrame extends javax.swing.JFrame {
    
    // Global state of scene.
    private GameState gs;

    /**
     * Creates new form MainFrame.
     */
    public MainFrame(GameState globalState) {
        this.gs = globalState;
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        glPanel = new GLJPanel();
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Stratego");

        glPanel.setPreferredSize(new java.awt.Dimension(800, 800));
        glPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                glPanelMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout glPanelLayout = new javax.swing.GroupLayout(glPanel);
        glPanel.setLayout(glPanelLayout);
        glPanelLayout.setHorizontalGroup(
            glPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1093, Short.MAX_VALUE)
        );
        glPanelLayout.setVerticalGroup(
            glPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 600, Short.MAX_VALUE)
        );

        getContentPane().add(glPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void glPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_glPanelMouseClicked
        glPanel.requestFocusInWindow();
    }//GEN-LAST:event_glPanelMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    GLJPanel glPanel;
    // End of variables declaration//GEN-END:variables
}

