package game2dai.entityshapes.ps;

import game2dai.entities.BaseEntity;
import processing.core.PApplet;
import processing.core.PImage;

/**
 * The entity graphic comes from an image file. 
 * 
 * @author Peter Lager
 *
 */
public class BitmapPic extends PicturePS {

	PImage[] img;

	int lastTime = 0, elapsedTime = 0;
	int interval = 0;
	int frameNo = 0;
	
	/**
	 * Single image for all frames
	 * @param papp
	 * @param fname the name of the bitmap image file
	 */
	public BitmapPic(PApplet papp, String fname){
		super(papp);
		img = ImageBank.getImage(papp, fname);
	}
	
	/**
	 * An animated image. <br>
	 * The image frames are stored as tiles within a single image
	 *  
	 * @param papp
	 * @param fname the name of the bitmap image file
	 * @param nCols number of tiles horizontally
	 * @param nRows number of rows vertically
	 * @param interval the time (milliseconds) between image frames
	 */
	public BitmapPic(PApplet papp, String fname, int nCols, int nRows, int interval){
		super(papp);
		img = ImageBank.getImage(papp, fname, nCols, nRows);
		lastTime = app.millis();
		elapsedTime = 0;
		this.interval = (nCols* nRows > 1) ? interval : 0;
	}
	
	
	@Override
	public void draw(BaseEntity owner, float posX, float posY, float velX,
			float velY, float headX, float headY) {
		// Draw and hints that are specified and relevant
		if(hints != 0){
			Hints.hintFlags = hints;
			Hints.draw(app, owner, velX, velY, headX, headY);
		}
		// Determine the angle the entity is facing
		float angle = PApplet.atan2(headY, headX);

		// If this an animated image then update the animation
		if(interval > 0){
			int ctime = app.millis();
			while(ctime > lastTime + interval){
				lastTime += interval;
				frameNo++;
			}
			frameNo %= img.length;
		}

		// Prepare to draw the entity		
		app.pushStyle();
		app.imageMode(PApplet.CENTER);
		app.pushMatrix();
		app.translate(posX, posY);
		app.rotate(angle);

		// Draw the entity		
		app.image(img[frameNo],0,0);

		// Finished drawing
		app.popMatrix();
		app.popStyle();
	}

	/**
	 * Set the number of milliseconds between frames. <br>
	 * Negative values will be treated as zero which will cause the animation to pause.
	 * 
	 * @param interval 
	 */
	public void interval(int interval){
		if(interval <= 0)
			this.interval = 0;
		else
			this.interval = interval;
	}
	
	public int interval(){
		return interval;
	}
	
	/**
	 * Use next frame in the animation
	 * @return this renderer
	 */
	public int nextFrame(){
		frameNo++;
		frameNo %= img.length;
		return frameNo;
	}
	
	/**
	 * Use previous frame in the animation
	 * @return this renderer
	 */
	public int prevFrame(){
		frameNo += img.length - 1;
		frameNo %= img.length;
		return frameNo;	
	}
}
