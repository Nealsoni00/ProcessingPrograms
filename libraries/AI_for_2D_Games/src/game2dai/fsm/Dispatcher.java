package game2dai.fsm;

import game2dai.World;
import game2dai.entities.BaseEntity;

import java.util.TreeSet;


/**
 * This class is responsible for storing and dispatching messages between entities
 * @author Peter Lager
 *
 */
public class Dispatcher {

	/**
	 * The set of telegrams still to be processed.
	 */
	private static TreeSet<Telegram> telegrams = new TreeSet<Telegram>();

	/**
	 * Dispatch a telegram to another entity with optional delay and any extra information supplied
	 * 
	 * @param delay_ms the delay (in milliseconds) before the telegram is to be sent
	 * @param sender the ID of the entity sending the message
	 * @param receiver the ID of the entity to receive the telegram
	 * @param msg unique integer identifying the message 
	 * @param eInfo additional information as required - the receiver will know what there is.
	 */
	public static void dispatch(long delay_ms, int sender, int receiver, int msg, Object ... eInfo){
		BaseEntity bgeReceiver = World.allEntities.get(receiver);
		// Only remember telegrams for entities that have a state machine
		if(bgeReceiver != null && bgeReceiver.FSM()!= null){
			Telegram tgram = new Telegram(System.currentTimeMillis() + delay_ms, sender, receiver, msg, eInfo);
			telegrams.add(tgram);
		}
	}

	private static void sendTelegram(int receiver, Telegram tgram) {
		// Confirm that the receiver still exists
		BaseEntity bgeReceiver = World.allEntities.get(receiver);
		if(bgeReceiver != null && bgeReceiver.FSM() != null){
			bgeReceiver.FSM().onMessage(tgram);
		}
	}

	/**
	 * Called by the world update - will send any telegrams ready for processing
	 */
	public static void update(){
		Telegram tgram = null;
		while(!telegrams.isEmpty() && telegrams.first().despatchAt <= System.currentTimeMillis()){
			// Remove the next telegram to be sent
			tgram = telegrams.pollFirst();
			sendTelegram(tgram.receiver, tgram);
		}
	}
	
	/**
	 * Remove all outstanding telegrams - useful if reseting a game or simulation
	 */
	public void deleteAllTelegrams(){
		telegrams.clear();
	}

}
