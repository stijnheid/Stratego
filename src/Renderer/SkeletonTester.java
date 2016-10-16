/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Renderer;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JFrame;

/**
 * Class to test skeleton joint edits.
 */
public class SkeletonTester implements ActionListener {
    
    private final Skeleton skeleton;
    
    private JTextField box;
    
    private JFrame frame;
    
    String text;
    
    Thread stuff;
    
    public SkeletonTester(Skeleton s){
        this.skeleton = s;
        stuff = new Thread(()->{
            box = new JTextField(20);
            box.addActionListener(this);
            frame = new JFrame("SkeletonTester");
            frame.add(box);
            frame.pack();
            frame.setVisible(true);  
        });
        stuff.start();
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        text = box.getText();
        box.setText("");
        try {
            parse(text);        
        }   catch (Exception err){
                System.out.println("Could not parse message: "+text);          
        }
    }
    
    private void parse(String message){
        String[] contents = message.split(" ");
        switch (contents[0]){
            case "shoulderL": skeleton.shoulderL = new Vector(
                    Double.parseDouble(contents[1]),
                    Double.parseDouble(contents[2]),
                    Double.parseDouble(contents[3]));
                break;
            case "shoulderR": skeleton.shoulderR = new Vector(
                    Double.parseDouble(contents[1]),
                    Double.parseDouble(contents[2]),
                    Double.parseDouble(contents[3]));
                break;
            case "shoulderLRotX": skeleton.shoulderLRotX = Integer.parseInt(contents[1]);
                break;
            case "shoulderLRotY": skeleton.shoulderLRotY = Integer.parseInt(contents[1]);
                break;
            case "shoulderRRotX": skeleton.shoulderRRotX = Integer.parseInt(contents[1]);
                break;
            case "shoulderRRotY": skeleton.shoulderRRotY = Integer.parseInt(contents[1]);
                break;
            case "elbowLRotX": skeleton.elbowLRotX = Integer.parseInt(contents[1]);
                break;
            case "elbowLRotY": skeleton.elbowLRotY = Integer.parseInt(contents[1]);
                break;
            case "elbowRRotX": skeleton.elbowRRotX = Integer.parseInt(contents[1]);
                break;
            case "elbowRRotY": skeleton.elbowRRotY = Integer.parseInt(contents[1]);
                break;
            case "swordRotX": skeleton.swordRotX = Integer.parseInt(contents[1]);
                break;
            case "swordOpacity": skeleton.swordOpacity = (float)Double.parseDouble(contents[1]);
                break;
            case "exit": stuff.interrupt();
                frame.dispose();
                break;
            default:
                throw new IllegalArgumentException();
        }
    }    
}
