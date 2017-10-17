package game2dai.fsm;

import game2dai.World;
import game2dai.entities.BaseEntity;

/**
 * This is the base class for all 'finite states'. <br<
 * 
 * It is recommended that child classes have no instance variables
 * in which case they can be implemented as singleton classes. Since
 * entities are changing state frequently this will enable entities to
 * share a state object and reduce object creation and garbage 
 * collection. <br>
 * 
 * 
 * @author Peter Lager
 *
 */
public abstract class State {


	public String name = "";
	
	//this will execute when the state is entered
	public abstract void enter(BaseEntity user);

	//this is the state's normal update function
	public abstract void execute(BaseEntity user, double deltaTime, World world);

	//this will execute when the state is exited.
	public abstract void exit(BaseEntity user);

	//this executes if the agent receives a message from the message dispatcher
	public abstract boolean onMessage(BaseEntity user, Telegram tgram);

}
