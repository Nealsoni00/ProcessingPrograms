package game2dai.entityshapes.ps;

import game2dai.entities.BaseEntity;
import processing.core.PApplet;

/**
 * Creates a circular picture that looks like an umbrella from above.
 * 
 * @author Peter Lager
 *
 */
public class Umbrella extends PicturePS {

	private int nbrParts = 0;
	private float[] x = null;
	private float[] y = null;
	float radius = 0;

	private int[] colors = null;

	private float rot = 0;

	/**
	 * Create a simple 8 piece black and white umbrella
	 * @param papp
	 * @param size (should equal the collision radius)
	 */
	public Umbrella(PApplet papp, double size){
		this(papp, size, null);
	}

	/**
	 * Create an umbrella with at least 8 sections using the colour list.
	 * @param papp
	 * @param size the radius of the umbrella (should equal the collision radius)
	 * @param colList an array of colour values
	 */
	public Umbrella(PApplet papp, double size, int[] colList){
		super(papp);
		radius = (float) size;
		setFill(colList);
		rot = app.random(0, PApplet.TWO_PI);
	}

	/**
	 * Set the colours to be used by the brolly.
	 */
	public void setFill(int[] colList){
		// Make sure we have a color list with at least 2 colors
		if(colList == null || colList.length< 2)
			colors = new int[] { app.color(0), app.color(255) };
		else
			colors = colList;
		nbrParts = colors.length;
		while(nbrParts < 8)
			nbrParts += colors.length; 
		x = new float[nbrParts];
		y = new float[nbrParts];
		float da = PApplet.TWO_PI / nbrParts, a;
		for(int i = 0; i < nbrParts; i++){
			a = da * i;
			x[i] = radius * PApplet.cos(a);
			y[i] = radius * PApplet.sin(a);
		}
	}


	@Override
	public void draw(BaseEntity owner, float posX, float posY, float velX, float velY,
			float headX, float headY) {

		if(hints != 0){
			Hints.hintFlags = hints;
			Hints.draw(app, owner, velX, velY, headX, headY);
		}
		
		app.pushStyle();
		app.pushMatrix();
		app.translate(posX, posY);
		app.rotate(rot);
		int colNo = 0;
		for(int i = 1; i < x.length; i++){
			fillTriangle(i-1, i, colors[colNo]);
			colNo = (colNo + 1) % colors.length;
		}
		fillTriangle(x.length-1, 0, colors[colNo]);

		app.popMatrix();
		app.popStyle();		
	}
	
	/**
	 * Used to fill a triangle shape.
	 * 
	 * @param p1
	 * @param p2
	 * @param col
	 */
	private void fillTriangle(int p1, int p2, int col){
		app.noStroke();
		app.fill(col);
		app.beginShape(PApplet.TRIANGLES);
		app.vertex(0,0);
		app.vertex(x[p1], y[p1]);
		app.vertex(x[p2], y[p2]);
		app.endShape(PApplet.CLOSE);
	}

}
