package game2dai.entityshapes;

import game2dai.entities.BaseEntity;
import game2dai.entityshapes.ps.Hints;

/**
 * Any class to be used to render an entity on the screen should inherit
 * from this class. <br>
 * It provides a simple way to display steering behaviour hints for any
 * entity renderer. This is particularly useful when it comes to testing
 * your steering behaviours. <br>
 * Hints available are <br>
 * HINT_HEADING  the direction the entity is facing<br>
 * HINT_VELOCITY the velocity vector<br>
 * HINT_COLLISION the collision radius <br>
 * HINT_WHISKERS the feelers used in wall avoidance <br>
 * HINT_OBS_AVOID the detection box used in obstacle avoidance <br>
 * HINT_WANDER the wander direction and circle <br>
 * HINT_VIEW the area that can be seen by the entity <br>
 * 
 * When setting the hints to be shown they can be or'ed together e.g.
 * Hints.HINT_HEADING | hints.HINT_VELOCITY

 * 
 * @author Peter Lager
 *
 */
public abstract class Picture {

	/** bit flag defining hints to be displayed */
	protected int hints = 0;

	/**
	 * This constructor is provided when not using Processing.
	 */
	public Picture(){ }
	

	/**
	 * This must be overridden in the child class.
	 * 
	 * @param owner
	 * @param posX
	 * @param posY
	 * @param velX
	 * @param velY
	 * @param headX
	 * @param headY
	 */
	public abstract void draw(BaseEntity owner, float posX, float posY, float velX, float velY,
			float headX, float headY);

	/**
	 * Defines the steering behaviour (SB) hints to be displayed. <br>
	 * Although you can specify any and all hints, only hints for SBs currently in use will be displayed. <br> 
	 * This method replaces any existing SB hints.
	 * @param hints
	 */
	public void showHints(int hints){
		this.hints = hints;
	}

	/**
	 * Add more steering behaviour (SB) hints to those already selected.
	 * @param hints
	 */
	public void addHints(int hints){
		this.hints |= hints;
	}
	
	/**
	 * Remove some or all of the steering behaviour (SB) hints currently selected.
	 * @param hints
	 */
	public void removeHints(int hints){
		this.hints &= (Hints.HINT_ALL ^ hints);
	}
	
	/**
	 * Remove all steering behaviour (SB) hints.
	 */
	public void removeAllHints(){
		this.hints = 0;
	}
	
	/**
	 * Get the hints being currently used.
	 */
	public int getHints(){
		return hints;
	}
}
