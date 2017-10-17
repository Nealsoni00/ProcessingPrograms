package game2dai.utils;

public class StopWatch {

	private long currTime;
	private long lastTime;
	private long lapTime;
	private long startTime;
		
	/**
	 * Create a stop watch and initialise it to the current time.
	 */
	public StopWatch(){
		reset();
	}
	
	/**
	 * Initialise the stop-watch to the current time.
	 */
	public void reset(){
		currTime = lastTime = startTime = System.nanoTime();
		lapTime = 0;
	}
	
	/**
	 * Get the time since the stop watch was created or last reset.
	 * @return run time in seconds
	 */
	public double getRunTime(){
		double rt = 1.0E-9 * (System.nanoTime() - startTime);
		return rt;
	}
	
	/**
	 * Get the elapsed time since this method was called. <br>
	 * This should be called just ONCE at the beginning of the draw cycle
	 * @return elapsed time in seconds.
	 */
	public double getElapsedTime(){
		currTime = System.nanoTime();
		lapTime = currTime - lastTime;
		lastTime = currTime;
		return 1.0E-9 * lapTime;
	}
	
}
