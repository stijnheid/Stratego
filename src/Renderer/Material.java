package Renderer;

/**
* Materials that can be used for the robots, but have been used
* for all of the drawn objects.
*/
public enum Material {

    /** 
     * Gold material properties.
     * first line: the main color (RGBA)
     * second line: the specular color (RGBA)
     * third line: the amount of shininess 
     * (=how much does the specular reflect?)
     */
    GOLD (
        new float[] {255/255f, 215/255f, 0/255f, 1f},
        new float[] {255/255f, 215/255f, 0/255f, 0.5f},
        0f),

    /**
     * Silver material properties.
     */
    SILVER (
        new float[] {170/255f, 170/255f, 170/255f, 1f},
        new float[] {170/255f, 170/255f, 170/255f, 0.2f},
        0.02f),

    /** 
     * Wood material properties.
     */
    WOOD (
        new float[] {139/255f, 69/255f, 19/255f, 1.0f},
        new float[] {139/255f, 69/255f, 19/255f, 0.9f},
        0.93f),

    /**
     * Orange plastic material properties.
     */
    ORANGE (
        new float[] {255/255f, 69/255f, 0/255f, 1f},
        new float[] {255/255f, 69/255f, 0/255f, 0.9f},
        0.8f),
   /**
    * Red material properties. 
    */
    RED (
        new float[] {255/255f, 0/255f, 0/255f, 1f},
        new float[] {1f, 1f, 1f, 1f},
        0f),
   /**
    * Green material properties. 
    */
    
    GREEN (
        new float[] {0/255f, 255/255f, 0/255f, 1f},
        new float[] {1f, 1f, 1f, 1f},
        0f),
   /**
    * Blue material properties. 
    */
    
    BLUE (
        new float[] {0/255f, 0/255f, 255/255f, 1f},
        new float[] {1f, 1f, 1f, 1f},
        0f),
   /**
    * Yellow material properties. 
    */
    
    YELLOW (
        new float[] {255/255f, 255/255f, 0/255f, 1f},
        new float[] {1f, 1f, 1f, 1f},
        0f),
   /**
    * Black material properties. 
    */
    
    BLANK (
    	new float[] {1f, 1f, 1f, 1f},
    	new float[] {1f, 1f, 1f, 1f},
    	1f),
   /**
    * Track material properties. 
    */
    
    TRACK (
	new float[] {178/255f, 34/255f, 34/255f, 1.0f},
	new float[] {178/255f, 34/255f, 34/255f, 0.8f},
	0.9f),
   /**
    * Line material properties. (=white line on track)
    */

    LINE (
        new float[] {245/255f, 245/255f, 220/255f, 1.0f},
	new float[] {1f, 1f, 1f, 1f},
	0.05f),
   /**
    * concrete material properties. 
    */

    CONCRETE (
	new float[] {245/255f, 245/255f, 220/255f, 1f},
	new float[] {1f, 1f, 1f, 1f},
	0.05f),
   /**
    * Ground material properties. 
    */
	
    GROUND (
	new float[] {150/255f, 75/255f, 0/255f, 1f},
	new float[] {0.3f, 1f, 0.5f, 0.3f},
	1f),
   /**
    * Water material properties. 
    */

    WATER (
	new float[] {244/255f,238/255f,224/255f, 0.30f},
	new float[] {1f, 1f, 1f, 1f},
	0f);
	
    /** The diffuse RGBA reflectance of the material. */
    float[] diffuse;

    /** The specular RGBA reflectance of the material. */
    float[] specular;
    
    /** The specular exponent of the material. */
    float shininess;

    /**
     * Constructs a new material with diffuse and specular properties.
     */
    private Material(float[] diffuse, float[] specular, float shininess) {
        this.diffuse = diffuse;
        this.specular = specular;
        this.shininess = shininess;
    }
}
