package game2dai.entityshapes.ps;

import game2dai.entities.BaseEntity;
import processing.core.PApplet;

/**
 * A simple circle for a round obstacle. <br>
 * 
 * @author Peter Lager
 *
 */
public class ObstaclePic extends PicturePS {

	private int fillCol;
	private int strokeCol;
	private float strokeWeight;

	/**
	 * A simple white circle with thin black border.	
	 * @param papp
	 */
	public ObstaclePic(PApplet papp){
		super(papp);
		fillCol = app.color(255);
		strokeCol = app.color(255);
		strokeWeight = 1;
	}

	/**
	 * Picture with user defined colours
	 * @param papp
	 * @param fill the fill colour
	 * @param stroke the border colour
	 * @param weight the border thickness
	 */
	public ObstaclePic(PApplet papp, int fill, int stroke, float weight){
		super(papp);
		fillCol = fill;
		strokeCol = stroke;
		strokeWeight = weight;
	}


	@Override
	public void draw(BaseEntity owner, float posX, float posY, float velX, float velY,
			float headX, float headY) {
		app.pushStyle();
		app.pushMatrix();
		app.translate(posX, posY);

		app.stroke(strokeCol);
		app.fill(fillCol);	
		app.strokeWeight(strokeWeight);
		app.ellipseMode(PApplet.CENTER);

		double cr = owner.colRadius();
		app.ellipse(0, 0, 2*(float)cr, 2*(float)cr);

		app.popMatrix();
		app.popStyle();		
	}

}
