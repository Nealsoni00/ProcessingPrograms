package ch.dieseite.colladaloader.asmbeans;

import java.util.ArrayList;
import java.util.HashMap;

import ch.dieseite.colladaloader.coreproc.Log;
import ch.dieseite.colladaloader.coreproc.DataAssembler.Param;
import ch.dieseite.colladaloader.coreproc.ObjectLinker.CTXInfo;
import ch.dieseite.colladaloader.coreproc.ObjectLinker.RecInfo;
import ch.dieseite.colladaloader.coreproc.SaxParser.Record;
import ch.dieseite.colladaloader.wrappers.Color;
import ch.dieseite.colladaloader.wrappers.Line;
import ch.dieseite.colladaloader.wrappers.Triangle;
import processing.core.PImage;

/**
* <p>The handler for proprietary issues. It contains static methods:</p>
* <li>they work for Blender and Sketchup beans only</li>
* <li>doing small defined jobs they work for both beans</li>
* <li>a way to prevent code duplications</li>
* <li>can also be invoked by DataAssembler via LinkingSchema.xml</li>
* <li>do not change behavior to other methods but invoke order may be important</li>
* <p>This source is free; you can redistribute it and/or modify it under
* the terms of the GNU General Public License and by nameing of the originally author</p>
*
* @author Markus Zimmermann <a href="http://www.die-seite.ch">http://www.die-seite.ch</a>
* @version 3.1
*/
public class Common {
	
	/**
	 * extracts PImage from triangles tag
	 * @param 1. a &lt;Triangles&gt; or a &lt;PolyList&gt; (parent) with args: path to &lt;init_from&gt; tag (&lt;library_images&gt;)
	 * @param 2. PImage Pool (HashMap&lt;String,Texture&gt;)
	 * @return a Processing Image with meta data
	 */
	public static Texture getTexImage(ArrayList<Param> p)
	{
		Record<CTXInfo, RecInfo> aShape = ASUtils.getParam(p, 0);
		HashMap<String,Texture> images = ASUtils.getParam(p, 1);
		DescriptorCommon dsk = ASUtils.getParam(p, 2);
		
		Record<CTXInfo, RecInfo> img = ASUtils.execArgs(p.get(0).args, aShape);
		if (Log.level!=0)
			Log.msg(Common.class,((img == null)?"No Texture":img.text)+" found for aShape (from geometry ID:"+aShape.info.back.info.uniqueID+")", 4);

		if (img !=null && !dsk.ignoreTextures())
			return images.get(img.text);
		else
			return null;

	}
	
	/**
	 * Adds a new PImage Texture (with meta data) from a image file 
	 * (given by &lt;init_from&gt; tag) into a new created or 
	 * existing texture pool. 
	 * 
	 * @param 1. a certain subtag of &lt;effect&gt; with args to &lt;init_from&gt;, containing a single image file name
	 * @param 2. an existing texture pool (HashMap&lt;String,Texture&gt;) or null
	 * @param 3. interface descriptor (implementing DataAssembler.LoadingListener)
	 * @return a new image is added to pool
	 */
	public static HashMap<String,Texture> importTextures(ArrayList<Param> p)
	{
		Record<CTXInfo, RecInfo> aRefImageName = ASUtils.getParam(p, 0);
		Record<CTXInfo, RecInfo> init_from = ASUtils.execArgs(p.get(0).args, aRefImageName);
		HashMap<String,Texture> texPool = ASUtils.getParam(p, 1);
		DescriptorCommon dsk = ASUtils.getParam(p, 2);
		
    	if (texPool == null)
    		texPool = new HashMap<String,Texture>();
    	
    	String texName = "no";
		if (init_from !=null && !dsk.ignoreTextures())
		{
			texName = init_from.text;
			String daePath = dsk.getDaePath();
			PImage img = ASUtils.loadTexture(texName, daePath);
			texPool.put(texName, new Common.Texture(texName, img));
		}
		if (Log.level!=0)
			Log.msg(Common.class,texName+" Texture image from file added to pool", 4);
		
		return texPool;
	
	}
	
	/**
	 * add Parsed triangles or lines to master pool
	 * @param 1. Interface descriptor
	 * @param 2. Triangle Sublist (ArrayList&lt;Triangle&gt;)
	 * @param 3. Line Sublist (ArrayList&lt;Line&gt;)
	 */
	public static void addShapes(ArrayList<Param> p)
	{
		DescriptorCommon dsk = ASUtils.getParam(p, 0);
		ArrayList<Triangle> t = ASUtils.getParam(p, 1);
		ArrayList<Line> l = ASUtils.getParam(p, 2);
		dsk.addTriangles(t);
		dsk.addLines(l);
		if (Log.level!=0)
			Log.msg(Common.class,t.size()+" Triangles and "+l.size()+" Lines added to Main Pool in Interface", 4);
	}
	
	/**
	 * Send Triangle and Line Masterpool to Model.
	 * = fires DataAssembler.LoadingListener methods
	 * @param p
	 */
	public static void submitShapes(ArrayList<Param> p)
	{
		DescriptorCommon dsk = ASUtils.getParam(p, 0);
		dsk.submitTriangles();
		dsk.submitLines();
		if (Log.level!=0)
			Log.msg(Common.class,"Model Listener (=ColladaModel) fired, Data sent", 4);

	}
	
	/**
	 * <p>creates Lines based on this shape. The coordinates is converted from 
	 * Collada to Processing as follows:</p>
	 * 
     * <p>processing x = Collada x </p>
     * <p>processing y = Collada -z </p>
     * <p>processing z = Collada -y </p>
	 * 
	 * @param 1. Raw coords (vertex) Type:Common.CoordData
	 * @param 2. a Color (converted to processing) Type:wrappers.Color
	 * @return a sublist for this lines Type:ArrayList&lt;Line&gt;
	 */
	public static ArrayList<Line> createLines(ArrayList<Param> p)
	{
		//head init
		CoordData coordsRaw = ASUtils.getParam(p, 0);
		Color c = ASUtils.getParam(p, 1);

		ArrayList<Line> lines = new ArrayList<Line>();
		
		//init vertex
		int[] dpv = ASUtils.demuxPArray(coordsRaw.pArray, coordsRaw.offset_vertex, coordsRaw.muxCnt);
		ASUtils.Point3D[] vertices = ASUtils.to3DPoints(coordsRaw.floatArray3D);
		
		
		//begin creating lines
		for (int i = 0, j=0; i < coordsRaw.shapeCnt; i++) {
			if (coordsRaw.type == CoordData.Type.LINE) 	
			{
				Line lin = new Line();
				//vertices part
				lin.A=vertices[dpv[j++]].copy();//copy is required procedure to make ColladaModel methods work (scale translate..etc)
				lin.B=vertices[dpv[j++]].copy();
				lin.colour = new Color(c.red, c.green, c.blue, c.transparency);
				//add to pool	
				lines.add(lin);
			}
		}
		
		if (Log.level!=0)
			Log.msg(Common.class,lines.size()+" Lines created from <lines>", 4);
		
		return lines; 
		
	}
	
	/**
	 * Wrapper for Texture containing meta data
	 * @author mrcoffee
	 *
	 */
	public static class Texture
	{
		public String key;
		public PImage value;
		public Texture(String key, PImage value) {
			super();
			this.key = key;
			this.value = value;
		}
		
	}
	
	/**
	 * wrapper for parsed vertex/texcoord data
	 */
	public static class CoordData
	{
		public enum Type{TRIANGLE,LINE,POLYGON};
		public Type type;
		public int shapeCnt;
		public int muxCnt = -1;
		public int offset_vertex = -1;
		public int offset_texcoord = -1;
		public int[] pArray = null;
		public float[] floatArray3D = null;
		public float[] floatArray2D = null;

	}

}
