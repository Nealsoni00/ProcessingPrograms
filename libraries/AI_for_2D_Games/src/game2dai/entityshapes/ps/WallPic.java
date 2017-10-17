package game2dai.entityshapes.ps;

import game2dai.entities.BaseEntity;
import game2dai.entities.Wall;
import processing.core.PApplet;

/**
 * A very basic wall picture created using a single line. <br>
 * 
 * 
 * @author Peter Lager
 *
 */
public class WallPic extends PicturePS {
	
	private int strokeCol;
	private float strokeWeight;

	/**
	 * A thin black line.
	 * @param papp
	 */
	public WallPic(PApplet papp){
		super(papp);
		strokeCol = app.color(0);
		strokeWeight = 1f;
	}

	/**
	 * User selected line colour and thickness.
	 * @param papp
	 * @param stroke the line colour
	 * @param weight the line thickness
	 */
	public WallPic(PApplet papp, int stroke, float weight){
		super(papp);
		strokeCol = stroke;
		strokeWeight = weight;
	}

	@Override
	public void draw(BaseEntity owner, float posX, float posY, float velX, float velY,
			float headX, float headY) {
		Wall w = (Wall) owner;

		// Prepare to draw entity
		app.pushStyle();
		app.pushMatrix();

		// Draw the wall
		app.strokeWeight(strokeWeight);
		app.stroke(strokeCol);
		app.line(posX, posY, (float)w.getEndPos().x, (float)w.getEndPos().y);

		// Finished drawing
		app.popMatrix();
		app.popStyle();		
	}

	/**
	 * Set wall colour and thickness (stroke weight)
	 * 
	 * @param col
	 * @param thickness
	 */
	public void wallDetails(int col, float thickness){
		strokeCol = col;
		strokeWeight = thickness;
	}

}
