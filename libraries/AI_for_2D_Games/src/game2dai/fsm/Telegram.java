package game2dai.fsm;

import game2dai.maths.FastMath;


public class Telegram implements Comparable<Telegram>{

	private static final long SAME_TIME_INTERVAL = 250;
	
	//the entity that sent this telegram
	public final int sender;

	//the entity that is to receive this telegram
	public final int receiver;

	//the message itself.
	public final int msg;

	//messages can be dispatched immediately or delayed for a specified amount
	//of time. If a delay is necessary this field is stamped with the time 
	//the message should be dispatched.
	public final long despatchAt;

	//any additional information that may accompany the message
	public Object[] extraInfo;


	protected Telegram(){
		despatchAt = -1;
		sender = -1;
		receiver = -1;
		msg = -1;
	}


	public Telegram(long despatchAt, int sender, int receiver, int msg, Object... info){ 
		this.despatchAt = despatchAt;
		this.sender = sender;
		this.receiver = receiver;
		this.msg = msg;
		if(info != null)
			extraInfo = info;
		else
			extraInfo = new Object[0];
	}

	/**
	 * Telegrams are consider the same if they are to be despatched within 250ms
	 * as defined in SAME_TIME_INTERVAL
	 */
	@Override
	public int compareTo(Telegram tgram) {
		if(sender == tgram.sender 
				&& receiver == tgram.receiver 
				&& msg == tgram.msg
				&& FastMath.abs(despatchAt - tgram.despatchAt) < SAME_TIME_INTERVAL){
			return 0;
		}
		return (despatchAt <= tgram.despatchAt) ? -1 : 1;
	}

}
