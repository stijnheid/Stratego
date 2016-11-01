/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stratego.neural.net;

/**
 *
 * @author s146928
 */
public class NamedDataSet {
    
    private String name;
    private double[] data;
    
    NamedDataSet(String name_data, double[] data_array){
        name = name_data;
        data = data_array;
    }
    
    public double[] getArray(){
        return data;
    }
    
    public String getName(){
        return name;
    }
}
