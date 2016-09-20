/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Renderer;

/**
 *  Interface that describes the desired behaviour of an Animation.
 * @author Maurits Ambags (0771400)
 */
public interface Animation {
    
    
    /**
     * Method to display this animation.
     * Only to be called by Simulator once game itself has been paused.
     */
    public void execute();
}
