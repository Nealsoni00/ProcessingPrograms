package game2dai.entityshapes.ps;

import processing.core.PApplet;
import game2dai.entities.BaseEntity;
import game2dai.entityshapes.Picture;

/**
 * This is the base class for all entity renderers to be used in Processing
 * 
 * @author Peter Lager
 *
 */
public class PicturePS extends Picture {

	
	/** Used for drawing the shape */
	protected PApplet app = null;

	/**
	 * Default constructor - use this constructor if the child class is to be a 
	 * local class in the sketch. <br>
	 * This constructor must be called by all child class constructors so that
	 * the renderer has access to the drawing surface. <br>
	 */
	public PicturePS(){
		super();
	}
	
	/**
	 * Use this constructor if the child class is to be a top level class (either 
	 * in its own .java tab or declared as static in a .pde tab) in the sketch. <br>
	 * This constructor must be called by all child class constructors so that
	 * the renderer has access to the drawing surface. <br>
	 * @param papp
	 */
	public PicturePS(PApplet papp){
		super();
		app = papp;
	}
	
	/**
	 * This method allows the user to specify the PApplet object responsible
	 * for drawing this picture. This need only be called if the renderer was 
	 * created with the default (no parameter) constructor and you want to display hints.
	 * @param app
	 */
	public void setApplet(PApplet app){
		if(app != null)
			this.app = app;		
	}
	
	/**
	 * This method must be overriden in all child classes  otherwise nothing is drawn.
	 */
	@Override
	public void draw(BaseEntity owner, float posX, float posY, float velX,
			float velY, float headX, float headY) {
	}

}
