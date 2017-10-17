package ch.dieseite.glemulator;
import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.HashMap;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import javax.swing.JFrame;
import javax.swing.JPanel;

import processing.core.PImage;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.TextureData;

/**
 * <p>This class emulates some 3D Processing methods using
 * direct access to the JOGL library.(see 
 * <a href="http://www.jogamp.org/">
 * http://www.jogamp.org/</a>
 * This Emulator is usefull if OPENGL mode on Processing 
 * is not working</p>  
 * 
 * <p>This source underlies the GNU General Public License; 
 * You can redistribute it or modify by nameing the originally author</p>
 *
 * @author Markus Zimmermann <a href="http://www.die-seite.ch">http://www.die-seite.ch</a>
 * @version 3.1
 */
abstract class PEmulator extends JPanel implements GLEventListener
{
	protected static final int TRIANGLE = GL.GL_TRIANGLES;
	private int w; private int h;
	private int currentShapeMode = 0;
	private GL2 currentGL2;
	private Texture currentTexture;
	private PImage currentImage;
	private HashMap<PImage, Texture> textureCache;
	private GLCanvas canvas;
	
	
	protected PEmulator(int width, int height)
	{
		w = width;
		h = height;

		textureCache = new HashMap<PImage, Texture>();
	    JFrame frame = new JFrame("OpenGL_Compatibility_Adapter");
	    frame.addWindowListener(new WindowAdapter() {
	         @Override 
	         public void windowClosing(WindowEvent e) {
		        	 System.exit(0);
	         }
	      });

	    frame.setContentPane(this);
	    frame.setSize(w, h);
	    
	    canvas = new GLCanvas();
	    canvas.addGLEventListener(this);
	    setLayout(new BorderLayout());
	    add(canvas, BorderLayout.CENTER);
		
		frame.setVisible(true);
	}
	
	/**
	 * Does a printout of the current GL version and driver
	 */
	public void GLConfig()
	{
		com.jogamp.newt.opengl.GLWindow.main(null);
	}
	
	/**
	 * draws the polygons and lines. Must be overridden by GLAdapter
	 */
	abstract void execQueue();
	
	/**
	 * a helper to create a GL Texture based on processing spec's
	 */
	private Texture createTexture(int width, int height, int[] pixels)
	{
		byte[] image = new byte[pixels.length*4];
		for (int i = 0; i < pixels.length; i++) {
			int color = pixels[i];
			//extract from processingformat:ARGB
			byte b = (byte)(color & 0xff);
			byte g = (byte)((color >> 8) & 0xff);
			byte r = (byte)((color >> 16) & 0xff);
			byte a = (byte)((color >> 24) & 0xff);
			//compress to GL-Format: BGRA
			int j = i*4;
			image[j]= b;
			image[j+1]= g;
			image[j+2]= r;
			image[j+3]= a;
		}
	    Buffer buffer = ByteBuffer.wrap(image);

	   
	    								//glprofile, internalFormat, width, height, border, pixelFormat, pixelType, mipmap, dataIsCompressed, mustFlipVertically, buffer, flusher
	    TextureData td = new TextureData(currentGL2.getGLProfile(),GL.GL_RGBA,width,height,0,GL2.GL_BGRA,GL.GL_UNSIGNED_BYTE,true,false,false,buffer,null);
	    //INFO: td = TextureIO.newTextureData(gl2.getGLProfile(), new File("./bin/images/red.png"), false, ".png");
	    Texture tx = new Texture(currentGL2, td);
		//INFO: tx = TextureIO.newTexture(
		//    getClass().getClassLoader().getResource("images/crate.png"), // relative to project root 
		//    false, ".png");
	    return tx;
	    
	}
	

	//-----------GL Listener Overrides---------
	
	
	@Override
	public synchronized void display(GLAutoDrawable drawable) {
		currentGL2 = drawable.getGL().getGL2();
	    // Clear the color and the depth buffers
		currentGL2.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);	    
		// reset the current model-view matrix
		currentGL2.glLoadIdentity();  
		//shift camera pos to processing coords
		currentGL2.glTranslatef(-(((float)w)/2.0f), (((float)h)/2.0f), -(float)(w|h));
		
		execQueue();//draw PApplet sketch
	}

	@Override
	public synchronized void init(GLAutoDrawable drawable) {
		currentGL2 = drawable.getGL().getGL2();
		currentGL2.glEnable(GL2.GL_DEPTH_TEST);  // avoid overlapping through newer created shapes
		currentGL2.glEnable(GL2.GL_BLEND); //enable alphachannels
		currentGL2.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA); //enable alphachannels
		currentGL2.glDepthFunc(GL2.GL_LEQUAL); //make  lines visible if a triangle has same position
	}

	@Override
	public synchronized void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		// Get the OpenGL graphics context
	    currentGL2 = drawable.getGL().getGL2();
	 
	    // Set up the projection matrix - choose perspective view
	    currentGL2.glMatrixMode(GL2.GL_PROJECTION);  
	    currentGL2.glLoadIdentity(); // reset
	      
	    // =Frustum: fovy, aspect, zNear, zFar (required!)
	    //choose znear/zfar wise, otherwise you get aliasing (google z Fighting, 16 bit resolution)
	    float aspect = (float)width / height;
	    GLU glu = new GLU();
	    glu.gluPerspective(53.0f, aspect, 40f, 20000.0f); 
	      
	    // Enable the model-view transform
	    currentGL2.glMatrixMode(GL2.GL_MODELVIEW);
	    currentGL2.glLoadIdentity(); // reset
		
	}
	@Override
	public void dispose(GLAutoDrawable arg0) {}
	
	//----JPanel override ------
	
	@Override
	public void repaint() 
	{
		super.repaint();
		if (canvas !=null)canvas.repaint();
	};
	
	
	//------- Processing emulated methods -----------
	/**
	 * see Processing API
	 */	
	protected void _lights()
	{
	    float[] colorDiff  = {.8f,.8f,.8f};
	    float[] colorAmb  = {.2f,.2f,.2f};
	    float lightPos0[] = {500.0f, 500.0f, 500.0f, 1.0f}; //Positioned at (4, 0, 8)
		currentGL2.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPos0,0);
		currentGL2.glLightfv( GL2.GL_LIGHT0, GL2.GL_DIFFUSE, colorDiff, 0 );
		currentGL2.glLightfv( GL2.GL_LIGHT0, GL2.GL_AMBIENT, colorAmb, 0 );
		currentGL2.glEnable(GL2.GL_LIGHTING); //light in general
		currentGL2.glEnable(GL2.GL_LIGHT0);
		currentGL2.glEnable(GL2.GL_NORMALIZE); //direction of Light reflection automatically
		currentGL2.glEnable(GL2.GL_COLOR_MATERIAL); //otherwise you got to use glMaterial instead glcolor
		currentGL2.glShadeModel(GL2.GL_FLAT);// make sharp reflections
	}
	/**
	 * see Processing API
	 */
	protected void _strokeWeight(float w)
	{
		currentGL2.glLineWidth(w);
	}
	/**
	 * see Processing API
	 */
	protected void _stroke(float r,float g,float b)
	{
		r = r/255;
		g = g/255;
		b = b/255;
		float t = 255f;

//	    float color[] = {r, g,b};
//	    gl2.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, color,0);
		currentGL2.glColor4f(r, g, b, t);
	}
	/**
	 * see Processing API
	 */
	protected void _line(float xa,float ya,float za, float xb,float yb,float zb)
	{
		currentGL2.glBegin(GL.GL_LINES);
		currentGL2.glVertex3f(xa, -ya, za);
		currentGL2.glVertex3f(xb, -yb, zb);
		currentGL2.glEnd();
	}
	/**
	 * see Processing API
	 */
	protected void _translate(float x, float y, float z)
	{
		currentGL2.glTranslatef(x, -y, z);
	}
	/**
	 * see Processing API
	 */
	protected void _rotateY(float angle)
	{
		double grad = angle * 360.0 / (Math.PI*2);
		currentGL2.glRotatef((float)grad, 0.0f, 1.0f, 0.0f);
	}
	/**
	 * see Processing API
	 */
	protected void _rotateX(float angle)
	{
		double grad = angle * 360.0 / (Math.PI*2);
		currentGL2.glRotatef((float)grad, 1.0f, 0.0f, 0.0f);
	}
	/**
	 * see Processing API
	 */
	protected void _rotateZ(float angle)
	{
		double grad = angle * 360.0 / (Math.PI*2);
		currentGL2.glRotatef((float)grad, 0.0f, 0.0f, 1.0f);
	}
	/**
	 * see Processing API
	 */
	protected void _scale(float v)
	{
		currentGL2.glScalef(v, v, v); 
	}
	/**
	 * see Processing API
	 */
	protected void _beginShape(int mode)
	{
		currentShapeMode = mode;
		currentGL2.glBegin(mode);
	}
	/**
	 * see Processing API
	 */
	protected void _endShape()
	{
		currentGL2.glEnd();
		//needs to be deactivated: colors in next shapes won't appear else
		if (currentTexture!=null)currentTexture.disable(currentGL2);
		currentShapeMode = 0;

	}
	/**
	 * see Processing API
	 */
	protected void _fill(float r,float g,float b, float t)
	{
		r = r/255f;
		g = g/255f;
		b = b/255f;
		t = t/255f;
//		float color[] = {r, g,b,t};
//		gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, color,0);
		currentGL2.glColor4f(r, g, b,t);
	}
	/**
	 * see Processing API
	 */	
	protected void _fill(float c)
	{
		c = c/255f;
		currentGL2.glColor4f(c,c,c,255f);
	}
	/**
	 * see Processing API
	 */
	protected void _vertex(float x,float y, float z)
	{
		currentGL2.glVertex3f(x,-y,z);
	}
	/**
	 * see Processing API
	 */	
	protected void _vertex(float x,float y, float z, float tx, float ty)
	{
		float _tx = tx/((float)currentImage.width); 
		float _ty = ty/((float)currentImage.height);
		currentGL2.glTexCoord2f(_tx, _ty);
		currentGL2.glVertex3f(x,-y,z);
	}
	/**
	 * returns a processing pixel (see Processing API)
	 */
	protected int _color(int r, int g, int b, int a)
	{
		int _r = (r << 16)& 0xFF0000;
		int _g = (g << 8)& 0xFF00;
		int _b =  b & 0xFF;
		int _a = (a << 24) & 0xFF000000;
		int c = _a | _r | _g | _b;
		return c;
	}

	/**
	 * refer to processing API
	 * @param img
	 */
	protected void _texture(PImage img)
	{
		currentGL2.glEnd();; //texture must be created first before beginShape() calling
		currentTexture = textureCache.get(img);
		currentImage = img;
		if (currentTexture == null)//texture should be loaded once only
		{
			currentTexture = createTexture(img.width, img.height, img.pixels);
			textureCache.put(img, currentTexture);
		}
	    currentTexture.bind(currentGL2);
	    currentTexture.enable(currentGL2);
	    currentGL2.glBegin(currentShapeMode);
	    
	    currentGL2.glColor4f(1f, 1f, 1f,1f); //texture need to have a white background
	    
	}
	/**
	 * refer to processing API
	 * @param img
	 */	
	protected void _background(float c)
	{
		float _c = c/255f;
	    // Set background color in RGBA
		currentGL2.glClearColor(_c, _c, _c, 1.0f);
	}


}
