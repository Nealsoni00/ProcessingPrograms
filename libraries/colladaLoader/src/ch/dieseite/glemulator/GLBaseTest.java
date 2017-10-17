package ch.dieseite.glemulator;

import processing.core.*;
import ch.dieseite.glemulator.GLAdapter;


/**
 * <p>This is a OpenGL Test Class only. It tests 3D Shapes, 
 * Lines AmbientLights and Textures. It leaves Processing
 * in 2D and opens a new 3D Window that overrides Processing
 * environment using direct access to JOGL libraries</p>
 * 
 * <p>If this won't work then your Hardware/Driver definitive 
 * cannot support openGL </p>
 * 
 * <p>This class is not required as part of build Path in an IDE<p>
 * 
 * <p>This source is free; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License and by nameing of the originally author</p>
 *
 * @author Markus Zimmermann <a href="http://www.die-seite.ch">http://www.die-seite.ch</a>
 * @version 3.1
 */
public class GLBaseTest extends PApplet
{
	
	GLAdapter adapter;
	PImage img;
	float angleY = -.32000035f;       // rotational angle in degree 
	float angleX = 0.04000026f;
	float angleSpeed = -0.01f;   // rotational speed 
	int width = 500;
	int height = 500;

	
	
	@Override
	public void settings() {
		size(width,height);
	}
	
    @Override
    public void setup()
    {
    	img = loadImage("swissheart2.png");
    	adapter = new GLAdapter(width, height);
    }

    @Override
    public void draw(){

    	drawModel();
		//repaint() IS VERY IMPORTANT INVOKE AT THE END OF THIS METHOD
		//OTHERWISE YOU RISK QUEUE OVERFLOW
    	adapter.repaint();  	
    }


	
	void drawModel() {
		adapter.lights();
		adapter.translate(250, 250, 100);
		adapter.rotateX(angleX);
		adapter.rotateY(angleY);
		adapter.scale(2.5f);     
	  
		adapter.background(200);
	    
		//x y z vectors
		adapter.strokeWeight(2);
		adapter.stroke(125, 0, 0);
		adapter.line(0, 0, 0, width, 0, 0); 
		adapter.stroke(0, 125, 0);
		adapter.line(0, 0, 0, 0, 0, -width);
		adapter.stroke(0, 0, 125);
		adapter.line(0, 0, 0, 0, -height, 0);
	     
		//yellow triangle bottom
		adapter.fill(255,255,0,255);    
		adapter.beginShape(GLAdapter.TRIANGLE);
		adapter.vertex(0, 0, 0);//center
		adapter.vertex(50, 50, 50);//right
		adapter.vertex(0,0,100);//behind
		adapter.endShape();
	     
		//blue triangle right
		adapter.fill(0,255,255,255);
		adapter.beginShape(GLAdapter.TRIANGLE);
		adapter.vertex(25, -50, 50);//top
		adapter.vertex(50, 50, 50);//right
		adapter.vertex(0, 0, 0);//center
		adapter.endShape();

		//blue triangle left
		adapter.fill(0,255,255,255);
		adapter.beginShape(GLAdapter.TRIANGLE);
		adapter.vertex(0, 0, 0);//center
		adapter.vertex(0,0,100);//behind
		adapter.vertex(25, -50, 50);//top
		adapter.endShape();
	   
        //A Golden OpenGL rule says: draw opaque objects first then
		//draw transparent objects from back to front order
		
	    //Swissheart in Front
		adapter.beginShape(GLAdapter.TRIANGLE);
		adapter.texture(img); 
		adapter.vertex(0,0,100,img.width/2f,0);//behind
		adapter.vertex(50, 50, 50,0,img.height);//right
		adapter.vertex(25, -50, 50,img.width,img.height);//top  	    
		adapter.endShape();
	    
		//make animated
        angleY += angleSpeed;
	    angleX +=angleSpeed;
		
	}

    public static void main(String[] args)
    {
    	
    	//run as applett
    	PApplet.main(new String[] {"ch.dieseite.glemulator.GLBaseTest" });
    }
    





    

}



