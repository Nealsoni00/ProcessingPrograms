package ch.dieseite.glemulator;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import processing.core.PImage;

/**
 * <p>This class is an OpenGL Adapter in case the OpenGL mode
 * (P2D/P3D) doesn't work in Processing. It overrides Processing
 * environment using direct access to JOGL libraries
 * and emulates a new Processing like 3D API </p>
 * 
 * <p>As a subclass of PEmulator it merges its callbacks 
 * and callbacks from PApplet into a proper 
 * runtime order.</p>
 * 
 * <p>An usage example is the following:</p>
 * 
 * <p><u>Sketch header</u></p>
 * <p><pre>GLAdapter adapter;</pre></p>
 * 
 * <p><u>settings() section</u></p>	
 * <p><pre>size(sizeX, sizeY); //leave Processing in 2D</pre></p>
	
 * <p><u>setup() section</u></p>
 * <p><pre>adapter = new GLAdapter(sizeX, sizeY);</pre></p>

 * <p><u>draw() section</u></p>
 * <p><pre>
 * adapter.line(srcX, srcY, srcZ, dstX, dstX, dstZ); //an example
 * 
 * //repaint() IS A VERY IMPORTANT INVOKE AT 
 * //THE END OF DRAW SECTION
 * //OTHERWISE YOU RISK QUEUE OVERFLOW
 * adapter.repaint();  //flush jobs	
 * </pre></p>
 * 
 * <p>This source underlies the GNU General Public License; 
 * You can redistribute it or modify by nameing the originally author</p>
 *
 * @author Markus Zimmermann <a href="http://www.die-seite.ch">http://www.die-seite.ch</a>
 * @version 3.1
 */
public class GLAdapter extends PEmulator{
	public static final int TRIANGLE = PEmulator.TRIANGLE;
	
	private BlockingQueue<Runnable> jobqueue;
	private boolean drawing = false;

	
	
	public GLAdapter(int width, int height) {
		super(width, height);
		jobqueue = new LinkedBlockingQueue<Runnable>();

	}
	
	/**
	 * callback, invoked by superclass on GLEventListener.display() event
	 */
	@Override
	void execQueue()
	{
		while (!jobqueue.isEmpty()) {
			try {
				jobqueue.take().run();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		drawing = false; //set command cache ready
	}
	/**
	 * caches all processing draw commands until repaint is invoked
	 * @param r
	 */
	private void putQueue(Runnable r)
	{
		if (!drawing)
		{
			try {
				jobqueue.put(r);
			} catch (InterruptedException e) { throw new RuntimeException(e);}
		}
		
	}
	
	
	/**
	 * must be the last invoke inside the draw() block from PApplet sketch
	 */
	@Override
	public void repaint()
	{
		if (!drawing)
		{
			drawing=true;
			super.repaint();
		}
	}
	
	
	// --- processing API's to queue --------
	// these methods cache Processing API commands into a queue
	// the queue is flushed when repaint() is invoked
	
	/**
	 * see Processing API
	 */
	public void lights()
	{
		putQueue(new Runnable() {
			public void run() {	_lights();}
		});
	}
	/**
	 * see Processing API
	 */
	public void strokeWeight(final float w)
	{
		putQueue(new Runnable() {
			public void run() {	_strokeWeight(w);}
		});
	}
	/**
	 * see Processing API
	 */	
	public void stroke(final float r,final float g,final float b)
	{
		putQueue(new Runnable() {		
			public void run() {	_stroke(r, g, b);}
		});
	}
	/**
	 * see Processing API
	 */	
	public void line(final float xa,final float ya,final float za, final float xb,final float yb,final float zb)
	{
		putQueue(new Runnable() {		
			public void run() {	_line(xa, ya, za, xb, yb, zb);}
		});
	}
	/**
	 * see Processing API
	 */	
	public void translate(final float x, final float y, final float z)
	{
		putQueue(new Runnable() {		
			public void run() {	_translate(x, y, z);}
		});
	}
	/**
	 * see Processing API
	 */	
	public void rotateY(final float angle)
	{
		putQueue(new Runnable() {		
			public void run() {	_rotateY(angle);}
		});
	}
	/**
	 * see Processing API
	 */	
	public void rotateX(final float angle)
	{
		putQueue(new Runnable() {		
			public void run() {	_rotateX(angle);}
		});
	}
	/**
	 * see Processing API
	 */	
	public void rotateZ(final float angle)
	{
		putQueue(new Runnable() {		
			public void run() {	_rotateZ(angle);}
		});
	}
	/**
	 * see Processing API
	 */	
	public void scale(final float v)
	{
		putQueue(new Runnable() {		
			public void run() {	_scale(v);}
		});
	}
	/**
	 * see Processing API
	 */	
	public void beginShape(final int mode)
	{
		putQueue(new Runnable() {		
			public void run() {	_beginShape(mode);}
		});
	}
	/**
	 * see Processing API
	 */	
	public void endShape()
	{
		putQueue(new Runnable() {		
			public void run() {	_endShape();}
		});
	}
	/**
	 * see Processing API
	 */
	public void fill(final float r,final float g,final float b, final float t)
	{
		putQueue(new Runnable() {		
			public void run() {	_fill(r, g, b, t);}
		});
	}
	/**
	 * see Processing API
	 */	
	public void fill(final float c)
	{
		putQueue(new Runnable() {		
			public void run() {	_fill(c);}
		});
	}
	/**
	 * see Processing API
	 */
	public void vertex(final float x,final float y, final float z)
	{
		putQueue(new Runnable() {		
			public void run() {	_vertex(x, y, z);}
		});
	}
	/**
	 * see Processing API
	 */	
	public void vertex(final float x,final float y, final float z, final float tx, final float ty)
	{
		putQueue(new Runnable() {		
			public void run() {	_vertex(x, y, z, tx, ty);}
		});
	}
	/**
	 * see Processing API
	 */
	public void texture(final PImage img)
	{
		putQueue(new Runnable() {		
			public void run() {	_texture(img);}
		});
	}
	/**
	 * see Processing API
	 */	
	public void background(final float c)
	{
		putQueue(new Runnable() {		
			public void run() {_background(c);}
		});
	}
	
	//--------nothing to queue here ------------
	/**
	 * see Processing API
	 */		
	public int color(int r, int g, int b, int a)
	{
		return _color(r, g, b, a);
	}
	
	



}
