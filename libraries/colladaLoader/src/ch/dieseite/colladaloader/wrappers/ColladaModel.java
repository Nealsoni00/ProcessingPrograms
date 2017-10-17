package ch.dieseite.colladaloader.wrappers;

import java.io.Serializable;
import java.util.ArrayList;

import ch.dieseite.colladaloader.coreproc.DataAssembler.LoadingListener;
import processing.core.PApplet;

/**
 * <p>This class holds all 3D data and supports transform functions</p>
 * 
 * <p>This source is free; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License and by nameing of the originally author</p>
 *
 * @author Markus Zimmermann <a href="http://www.die-seite.ch">http://www.die-seite.ch</a>
 * @version 3.1
 */
public class ColladaModel implements LoadingListener, Serializable{
	private ArrayList<Triangle> triangles;
	private ArrayList<Line> lines;
	private PApplet applet;
	
	/**
	 * 
	 * @param current Processing sketch
	 */
	public ColladaModel(PApplet applet)
	{
		triangles = new ArrayList<Triangle>();
		lines = new ArrayList<Line>();
		this.applet = applet;
		Triangle t = new Triangle();
	}

	/**
	 * returns all parsed triangles from dae file
	 * @return
	 */
	public ArrayList<Triangle> getTriangles() {
		return triangles;
	}
	/**
	 * returns all parsed lines or grid structures from dae file
	 * @return
	 */
	public ArrayList<Line> getLines() {
		return lines;
	}

	/**
	 * callback fired by any Class in asmbeans package
	 */
	@Override
	public void trianglesParsed(ArrayList<Triangle> triangles) {
		if (triangles!=null)
			this.triangles = triangles;
	}
	/**
	 * callback fired by any Class in asmbeans package
	 */
	@Override
	public void linesParsed(ArrayList<Line> lines) {
		if (lines != null)
			this.lines = lines;
	}
	
	
	/**
	 * <p>Draws the model using Processing P3D renderer. </p>
	 * <p>The source code of this method can be used as drawing example 
	 * if user want to customize</p>.
	 * <p>This is a untested part because software developers platform crashed on actual P3D/P2D version
	 * so he was forced to use the GLAdapter instead</p>
	 */
//	public void draw(GLAdapter applet, GLAdapter PApplet) //needed on developers environment only
	public void draw()
	{
	  	//reset shapes
	  	applet.noStroke(); //reset if last draw was lines
	  	applet.fill(127); //define a standart color
	  	
	      for (Triangle tri : triangles)
	      {

	          if (!tri.containsTexture)
	          {
	              applet.fill(tri.colour.red, tri.colour.green, tri.colour.blue, tri.colour.transparency);
	              applet.beginShape(PApplet.TRIANGLE);

	              applet.vertex(tri.A.x, tri.A.y, tri.A.z);
	              applet.vertex(tri.B.x, tri.B.y, tri.B.z);
	              applet.vertex(tri.C.x, tri.C.y, tri.C.z);

	              applet.endShape();

	          } else
	          {
	              applet.beginShape(PApplet.TRIANGLE);
	              applet.texture(tri.imageReference);
	              
	              applet.vertex(tri.A.x, tri.A.y, tri.A.z, tri.texA.x, tri.texA.y);
	              applet.vertex(tri.B.x, tri.B.y, tri.B.z, tri.texB.x, tri.texB.y);
	              applet.vertex(tri.C.x, tri.C.y, tri.C.z, tri.texC.x, tri.texC.y);

	              applet.endShape();

	          }

	      }

	      for (Line lin : lines)
	      {
	      	  applet.strokeWeight(0.5f);//define thickness of drawn lines
	          applet.stroke(lin.colour.red, lin.colour.green, lin.colour.blue);
	          applet.line(lin.A.x, lin.A.y, lin.A.z, lin.B.x, lin.B.y, lin.B.z);

	      }
	  }
	
/**
 * Draws collada using Processing default 2D renderer
 */
	public void draw2D()
	{
	  	//reset shapes
	  	applet.noStroke(); //reset if last draw was lines
	  	applet.fill(127); //define a standart color
 
	  	
	      for (Triangle tri : triangles)
	      {

	    	  if (!tri.containsTexture)
	    		  applet.fill(tri.colour.red, tri.colour.green, tri.colour.blue, tri.colour.transparency);
	    	  else
	    		  applet.fill(100,100,150,255);
	    	  applet.beginShape(PApplet.TRIANGLES);

	    	  applet.vertex(tri.A.x, tri.A.y);
	    	  applet.vertex(tri.B.x, tri.B.y);
	    	  applet.vertex(tri.C.x, tri.C.y);

	    	  applet.endShape();


	      }

	      for (Line lin : lines)
	      {
	    	  applet.strokeWeight(0.5f);//define thickness of drawn lines
	    	  applet.stroke(lin.colour.red, lin.colour.green, lin.colour.blue);
	    	  applet.line(lin.A.x, lin.A.y, lin.B.x, lin.B.y);

	      }
	  }

	 
 /**
  * makes the model (not the view) smaller or bigger
  * @param factor value &lt; 1.0f = smaller, value &gt; 1.0f = bigger
  */
     public void scale(float factor)
     {
         for (Triangle tri : triangles)
         {      	 
             tri.A.x *= factor; tri.A.y *= factor; tri.A.z *= factor;
             tri.B.x *= factor; tri.B.y *= factor; tri.B.z *= factor;
             tri.C.x *= factor; tri.C.y *= factor; tri.C.z *= factor;
         }

         for (Line lin : lines)
         {
             lin.A.x *= factor; lin.A.y *= factor; lin.A.z *= factor;
             lin.B.x *= factor; lin.B.y *= factor; lin.B.z *= factor;
         }

     }
     /**
      * shifts the model (not the view) along the axis x,y or z
      * @param len (in pixels)
      * @param axis valid characters for the axis is: 'x','y' or 'z'
      */
     public void shift(float len, char axis)
     {
         for (Triangle tri : triangles)
         {
             if (axis == 'x') tri.A.x += len; if (axis == 'y') tri.A.y += len; if (axis == 'z') tri.A.z += len;
             if (axis == 'x') tri.B.x += len; if (axis == 'y') tri.B.y += len; if (axis == 'z') tri.B.z += len;
             if (axis == 'x') tri.C.x += len; if (axis == 'y') tri.C.y += len; if (axis == 'z') tri.C.z += len;
         }

         for (Line lin : lines)
         {
             if (axis == 'x') lin.A.x += len; if (axis == 'y') lin.A.y += len; if (axis == 'z') lin.A.z += len;
             if (axis == 'x') lin.B.x += len; if (axis == 'y') lin.B.y += len; if (axis == 'z') lin.B.z += len;

         }
     }
     /**
      * Rotates the model (not the view) in X,Y,or Z Axis
      * @param radiant from 0 to 2*PI
      * @param axis valid characters for the axis is: 'x','y' or 'z'
      */
     public void rotate(float radiant, char axis)
     {
         for (Triangle tri : triangles)
         {
             //Buerglers Matrices
             if (axis == 'z')
             {
                 float Ax = (float)(Math.cos(radiant)*tri.A.x - Math.sin(radiant)*tri.A.y);
                 float Ay = (float)(Math.sin(radiant)*tri.A.x + Math.cos(radiant)*tri.A.y);
                 float Bx = (float)(Math.cos(radiant)*tri.B.x - Math.sin(radiant)*tri.B.y);
                 float By = (float)(Math.sin(radiant)*tri.B.x + Math.cos(radiant)*tri.B.y);
                 float Cx = (float)(Math.cos(radiant)*tri.C.x - Math.sin(radiant)*tri.C.y);
                 float Cy = (float)(Math.sin(radiant)*tri.C.x + Math.cos(radiant)*tri.C.y);
                 tri.A.x = Ax; tri.A.y = Ay; tri.B.x = Bx; tri.B.y = By; tri.C.x = Cx; tri.C.y = Cy;
             }

             if (axis == 'y')
             {
                 float Ax = (float)(Math.cos(radiant)*tri.A.x + Math.sin(radiant)*tri.A.z); if (axis == 'z')tri.A.x = (float)(Math.cos(radiant)*tri.A.x - Math.sin(radiant)*tri.A.y);
                 float Az = (float)(Math.cos(radiant)*tri.A.z - Math.sin(radiant)*tri.A.x); if (axis == 'z')tri.A.y = (float)(Math.sin(radiant)*tri.A.x + Math.cos(radiant)*tri.A.y);
                 float Bx = (float)(Math.cos(radiant)*tri.B.x + Math.sin(radiant)*tri.B.z); if (axis == 'z')tri.B.x = (float)(Math.cos(radiant)*tri.B.x - Math.sin(radiant)*tri.B.y);
                 float Bz = (float)(Math.cos(radiant)*tri.B.z - Math.sin(radiant)*tri.B.x); if (axis == 'z')tri.B.y = (float)(Math.sin(radiant)*tri.B.x + Math.cos(radiant)*tri.B.y);
                 float Cx = (float)(Math.cos(radiant)*tri.C.x + Math.sin(radiant)*tri.C.z); if (axis == 'z')tri.C.x = (float)(Math.cos(radiant)*tri.C.x - Math.sin(radiant)*tri.C.y);
                 float Cz = (float)(Math.cos(radiant)*tri.C.z - Math.sin(radiant)*tri.C.x); if (axis == 'z')tri.C.y = (float)(Math.sin(radiant)*tri.C.x + Math.cos(radiant)*tri.C.y);
                 tri.A.x = Ax; tri.A.z = Az; tri.B.x = Bx; tri.B.z = Bz; tri.C.x = Cx; tri.C.z = Cz;
             }

             if (axis == 'x')
             {
                 float Ay = (float)(Math.cos(radiant)*tri.A.y-Math.sin(radiant)*tri.A.z);
                 float Az = (float)(Math.sin(radiant)*tri.A.y+Math.cos(radiant)*tri.A.z);
                 float By = (float)(Math.cos(radiant)*tri.B.y-Math.sin(radiant)*tri.B.z);
                 float Bz = (float)(Math.sin(radiant)*tri.B.y+Math.cos(radiant)*tri.B.z);
                 float Cy = (float)(Math.cos(radiant)*tri.C.y-Math.sin(radiant)*tri.C.z);
                 float Cz = (float)(Math.sin(radiant)*tri.C.y+Math.cos(radiant)*tri.C.z);
                 tri.A.y = Ay; tri.A.z = Az; tri.B.y = By; tri.B.z = Bz; tri.C.y = Cy; tri.C.z = Cz;
             }


         }

         for (Line lin : lines)
         {
             if (axis == 'z')
             {
                 float Ax = (float)(Math.cos(radiant)*lin.A.x - Math.sin(radiant)*lin.A.y);
                 float Ay = (float)(Math.sin(radiant)*lin.A.x + Math.cos(radiant)*lin.A.y);
                 float Bx = (float)(Math.cos(radiant)*lin.B.x - Math.sin(radiant)*lin.B.y);
                 float By = (float)(Math.sin(radiant)*lin.B.x + Math.cos(radiant)*lin.B.y);
                 lin.A.x = Ax; lin.A.y = Ay; lin.B.x = Bx; lin.B.y = By;
             }

             if (axis == 'y')
             {
                 float Ax = (float)(Math.cos(radiant)*lin.A.x + Math.sin(radiant)*lin.A.z); if (axis == 'z')lin.A.x = (float)(Math.cos(radiant)*lin.A.x - Math.sin(radiant)*lin.A.y);
                 float Az = (float)(Math.cos(radiant)*lin.A.z - Math.sin(radiant)*lin.A.x); if (axis == 'z')lin.A.y = (float)(Math.sin(radiant)*lin.A.x + Math.cos(radiant)*lin.A.y);
                 float Bx = (float)(Math.cos(radiant)*lin.B.x + Math.sin(radiant)*lin.B.z); if (axis == 'z')lin.B.x = (float)(Math.cos(radiant)*lin.B.x - Math.sin(radiant)*lin.B.y);
                 float Bz = (float)(Math.cos(radiant)*lin.B.z - Math.sin(radiant)*lin.B.x); if (axis == 'z')lin.B.y = (float)(Math.sin(radiant)*lin.B.x + Math.cos(radiant)*lin.B.y);
                 lin.A.x = Ax; lin.A.z = Az; lin.B.x = Bx; lin.B.z = Bz;
             }

             if (axis == 'x')
             {
                 float Ay = (float)(Math.cos(radiant)*lin.A.y-Math.sin(radiant)*lin.A.z);
                 float Az = (float)(Math.sin(radiant)*lin.A.y+Math.cos(radiant)*lin.A.z);
                 float By = (float)(Math.cos(radiant)*lin.B.y-Math.sin(radiant)*lin.B.z);
                 float Bz = (float)(Math.sin(radiant)*lin.B.y+Math.cos(radiant)*lin.B.z);
                 lin.A.y = Ay; lin.A.z = Az; lin.B.y = By; lin.B.z = Bz;
             }
         }


     }

}
