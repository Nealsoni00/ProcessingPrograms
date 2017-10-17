package game2dai.entityshapes.ps;

import processing.core.PApplet;
import game2dai.entities.BaseEntity;

/**
 * A simple circular picture with user defined size and colour.
 * 
 * @author Peter Lager
 *
 */
public class CirclePic extends PicturePS {

	private float diam = 10;

	private int fillCol;
	private int strokeCol;
	private float strokeWeight;

	/**
	 * Create a white circle with a thin black border of size 10.
	 * @param papp
	 */
	public CirclePic(PApplet papp){
		super(papp);	
		fillCol = app.color(255);
		strokeCol = app.color(255);
		strokeWeight = 1;
	}
	
	
	/**
	 * Create a circle picture
	 * @param papp
	 * @param size the diameter (2x collision radius)
	 * @param fill the fill colour
	 * @param stroke the border colour
	 * @param weight the border thickness
	 */
	public CirclePic(PApplet papp, float size, int fill, int stroke, float weight){
		super(papp);
		fillCol = fill;
		strokeCol = stroke;
		strokeWeight = weight;
		diam = size;
	}

	/**
	 * Change the drawing factors.
	 * @param fill the fill colour
	 * @param stroke the border colour
	 * @param weight the border thickness
	 */
	public CirclePic appearance(int fill, int stroke, float weight){
		fillCol = fill;
		strokeCol = stroke;
		strokeWeight = weight;
		return this;
	}

	@Override
	public void draw(BaseEntity owner, float posX, float posY, float velX,
			float velY, float headX, float headY) {
		// Draw and hints that are specified and relevant
		if(hints != 0){
			Hints.hintFlags = hints;
			Hints.draw(app, owner, velX, velY, headX, headY);
		}

		// Prepare to draw the entity
		app.pushStyle();
		app.ellipseMode(PApplet.CENTER);
		app.pushMatrix();
		app.translate(posX, posY);
		
		// Draw the entity
		app.strokeWeight(1.3f);
		app.stroke(strokeCol);
		app.strokeWeight(strokeWeight);
		app.fill(fillCol);
		app.ellipse(0,0,diam,diam);

		// Finished drawing
		app.popMatrix();
		app.popStyle();		
	}

}
