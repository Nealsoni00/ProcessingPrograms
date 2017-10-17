package ch.dieseite.colladaloader.asmbeans;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import ch.dieseite.colladaloader.coreproc.Log;
import ch.dieseite.colladaloader.coreproc.DataAssembler.Param;
import ch.dieseite.colladaloader.coreproc.ObjectLinker.CTXInfo;
import ch.dieseite.colladaloader.coreproc.ObjectLinker.RecInfo;
import ch.dieseite.colladaloader.coreproc.SaxParser.Record;
import ch.dieseite.colladaloader.wrappers.Triangle;
import processing.core.PApplet;
import processing.core.PImage;

/**
 * <p>This class contains utility tools for all common or proprietary 
 * assembling classes.</p>
 *
 * <p>This source is free; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License and by nameing of the originally author</p>
 *
 * @author Markus Zimmermann <a href="http://www.die-seite.ch">http://www.die-seite.ch</a>
 * @version 3.1
 */
public class ASUtils {
	
	/**
	 * jump (n = depth) times to next record (NOT into Children)
	 * It uses the &quot;Record.info.next&quot; field for it. If this is
	 * null then jumping is aborted with a null return
	 * @param current record
	 * @param depth 
	 * @return null if depth too high
	 */
	public static Record<CTXInfo, RecInfo> next(Record<CTXInfo, RecInfo> record, int depth)
	{
	
		Record<CTXInfo, RecInfo> _e = record;
		for (int i=0; i<depth; i++)
			if (_e == null)
				return null;
			else
				_e=_e.info.next;
		return _e;			
	}
	
	/**
	 * returns the first match of the children from current Record
	 * that matches to search pattern. A pattern syntax can be a collada tag name
	 * or tag name plus attribute criteria
	 * @param current record
	 * @param searchPtrn either &quot;tagname&quot; or &quot;tagname#attribute=value&quot;
	 * @return null if nothing found
	 */
	public static Record<CTXInfo, RecInfo> childBySearch(Record<CTXInfo, RecInfo> parent, String searchPtrn)
	{
		if (parent!=null && searchPtrn !=null && parent.filteredChildren !=null)
		{
			String[] fine = searchPtrn.split("(#|=)");
			for (Record<CTXInfo, RecInfo> en:parent.filteredChildren)
			{
				if (fine[0].equals(en.name) && 
						(fine.length==1||(""+en.attr.get(fine[1])).equals(fine[2])))
				return en;
	
			}
		}
		return null;
	}
	
	/**
	 * <p>runs childBySearch() back() and next() in args order.
	 * The syntax format for each argument is &quot;[[j][b] depth][c tagname]&quot;
	 * separated by one space char</p>
	 * <p>commands j = next, c = childBySearch, b = back</p>
	 * <p>an Example can be: &quot;j 2 c instance_effect j 1&quot;</p>
	 * @param args
	 * @param current record
	 * @return
	 */
	public static Record<CTXInfo,RecInfo> execArgs(String args, Record<CTXInfo,RecInfo> r)
	{
		String[] a = args.split(" ");
		int i=0;
		while (i<a.length)
		{
			String cmd = a[i];
			i++;
			String val = a[i];
			i++;
			
			if (cmd.equals("j"))
				r = ASUtils.next(r, Integer.parseInt(val));
			if (cmd.equals("c"))
				r = ASUtils.childBySearch(r, val);
			if (cmd.equals("b"))
				r = ASUtils.back(r, Integer.parseInt(val));
		}
		
		return r;
	}
	
	/**
	 * jump (n = depth) times to previous record
	 * It uses the &quot;Record.info.back&quot; field for it. If this is
	 * null then jumping is aborted with a null returns
	 * @param current record
	 * @param depth 
	 * @return null if depth too high
	 */
	public static Record<CTXInfo, RecInfo> back(Record<CTXInfo, RecInfo> record, int depth)
	{
	
		Record<CTXInfo, RecInfo> _e = record;
		for (int i=0; i<depth; i++)
			if (_e == null)
				return null;
			else
				_e=_e.info.back;
		return _e;			
	}
	
	/**
	 * converts float values from string separated by
	 * &quot; &quot; (space) to an Array of floats
	 * @param floatArray
	 * @return
	 */
	public static float[] toFloats(String floatArray)
	{
		String[] s = floatArray.split(" ");
		float[] f = new float[s.length];
		for (int i = 0; i < f.length; i++) {
			f[i] = Float.parseFloat(s[i]);
		}
		return f;
	}
	
	/**
	 * converts int values from string separated by
	 * &quot; &quot; (space) to an Array of integer
	 * @param intArray
	 * @return
	 */
	public static int[] toInts(String intArray)
	{
		String[] s = intArray.split(" ");
		int[] ia = new int[s.length];
		for (int i = 0; i < ia.length; i++) {
			ia[i] = Integer.parseInt(s[i]);
		}
		return ia;
	}
	

	/**
	 * loads a PImage from file using Processing API
	 * @param image file name, <u>relative to collada</u> file path (i.e untitled/texture0.jpg)
	 * @param collada file path, <u>relative to &quot;data&quot;</u> path (i.e models/untitled.dae)
	 * @return a new image
	 */
    public static PImage loadTexture(String texName, String daeFile)
    {
    	//init internas
    	PApplet apl = new PApplet();
    	apl.sketchPath(); //set default path (required call !!)
    	
        String daeRoot = new File(daeFile).getParent(); //relative Path to data folder
        
    	File picfile = new File(daeRoot, texName);
    	PImage tex = apl.loadImage(apl.dataPath(picfile.getPath())); 
    	if (tex == null) throw new RuntimeException("texture File "+apl.dataPath(picfile.getPath())+" doesn't exist or is not supported by Processing");
     	
		if (Log.level!=0)
			Log.msg(ASUtils.class,"PImage "+apl.dataPath(picfile.getPath())+" loadImage success", 4);
		
		return tex;

    }
    
	/** 
	* <p>converts a collada &lt;float_array&gt; into 3D Points. 
	* Its also doing a coordinate convert from Collada
	* to processing. Creates cloneable Points</p>
    * 
    * <p>per definition is:</p>
    * <p>processing x = Collada x</p>
    * <p>processing y = Collada -z</p>
    * <p>processing z = Collada -y</p>
	* @param floatarray
	* @return a converted drawable 3DPoint Array
	*/
	public static ASUtils.Point3D[] to3DPoints(float[] floatarray)
	{
		int count = Math.round(floatarray.length/3); //xyz
		ASUtils.Point3D[] pa = new ASUtils.Point3D[count];
		for (int i = 0; i < pa.length; i++) {
			float x = floatarray[i*3];
			float y = -floatarray[i*3+2];
			float z = -floatarray[i*3+1];
			pa[i] = new ASUtils.Point3D(x, y, z);
		}
		return pa;
	}
	
	/** 
	* <p>converts a collada &lt;float_array&gt; into 2D Texture Points. 
	* Its also doing a coordinate convert from collada
	* to processing.Creates cloneable Points</p>
    * 
    * <p>per definition is:</p>
    * <p>textureProcessing x = textureSketchup x * picture.width</p>
    * <p>textureProcessing y = picture.height - textureSketchup y * picture.height</p>
    * 
	* @param floatarray
	* @param imageWidth
	* @param imageHeight
	* @return a converted drawable 2DPoint Array
	*/
	public static ASUtils.Point2D[] to2DPoints(float[] floatarray, float width, float height)
	{
		int count = Math.round(floatarray.length/2); //xy
		ASUtils.Point2D[] pa = new ASUtils.Point2D[count];
		for (int i = 0; i < pa.length; i++) {
			float x = floatarray[i*2]*width;
			float y = height-(floatarray[i*2+1]*height);
			pa[i] = new ASUtils.Point2D(x, y);
		}
		return pa;
	}
	
	/**
	 * extract from a multiplexed &lt;p&gt; array
	 * the required indices for a Point2D or Point3D array
	 * @param pArray (=all indices values from &lt;p&gt; tag)
	 * @param offset (= attribute value from collada &lt;input&gt; tag)
	 * @param muxCnt (=how many float arrays is packed/multiplexed in &lt;p&gt; tag resp. in pArray)
	 * @return indices for access a raw Point[] array correctly
	 */
	public static int[] demuxPArray(int[] pArray, int offset, int muxCnt)
	{
//		int muxCnt = Math.floorDiv(pArray.length, (shapeCount*vertexPerShape)); //= num of offsets that multiplexed in p tag
		int[] pa = new int[Math.round(pArray.length/muxCnt)];
		
		for (int n=1; n <= pa.length;n++)
		{				   //inspired on math formula f(x)even = 2*n-2 n={1,2...}, f(x)odd = 2*n-1 n={1,2...} for num of offsets = 2
			int pt_index =  pArray[(muxCnt*n)-muxCnt+offset];
			pa[n-1]=pt_index;
		}
		return pa;
	}
	
	
	
	/**
	 * cloneable Wrapper
	 * @author mrcoffee
	 *
	 */
	static class Point3D extends ch.dieseite.colladaloader.wrappers.Point3D
	{
		Point3D(float xx, float yy, float zz) {
			super(xx, yy, zz);
		}
		public ch.dieseite.colladaloader.wrappers.Point3D copy()
		{
			return new ch.dieseite.colladaloader.wrappers.Point3D(x,y,z);
		}
		
	}
	/**
	 * cloneable Wrapper
	 * @author mrcoffee
	 *
	 */
	static class Point2D extends ch.dieseite.colladaloader.wrappers.Point2D
	{
		Point2D(float xx, float yy) {
			super(xx, yy);
		}
		public ch.dieseite.colladaloader.wrappers.Point2D copy()
		{
			return new ch.dieseite.colladaloader.wrappers.Point2D(x,y);
		}
		
	}
	
	/**
	 * returns and casts the nth param value from param list
	 * @param p
	 * @param index
	 * @return
	 */
	public static<T> T getParam(ArrayList<Param> p, int index)
	{
		T param = (T)p.get(index).source.aValue;
		return param;
	}
	

}
