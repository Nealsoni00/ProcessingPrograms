package ch.dieseite.colladaloader.asmbeans;

import java.util.ArrayList;

import java.util.TreeMap;

import ch.dieseite.colladaloader.coreproc.Log;
import ch.dieseite.colladaloader.asmbeans.Common.CoordData;
import ch.dieseite.colladaloader.asmbeans.Common.Texture;
import ch.dieseite.colladaloader.coreproc.DataAssembler.Param;
import ch.dieseite.colladaloader.coreproc.ObjectLinker.CTXInfo;
import ch.dieseite.colladaloader.coreproc.ObjectLinker.RecInfo;
import ch.dieseite.colladaloader.coreproc.SaxParser.Record;
import ch.dieseite.colladaloader.wrappers.Color;
import ch.dieseite.colladaloader.wrappers.Triangle;



/**
* <p>The handler for proprietary issues. It contains static methods:</p>
* <li>they work for Blender only</li>
* <li>is defined in LinkingSchema.xml</li>
* <li>is invoked by DataAssembler</li>
* <li>doing small defined jobs</li>
* <li>do not change behavior to other methods but invoke order may be important</li>
* <p>This source is free; you can redistribute it and/or modify it under
* the terms of the GNU General Public License and by nameing of the originally author</p>
*
* @author Markus Zimmermann <a href="http://www.die-seite.ch">http://www.die-seite.ch</a>
* @version 3.1
*/

public class Sketchup {
	
	public static void sop(ArrayList<Param> p)
	{
		Record<CTXInfo, RecInfo> eneffect = ASUtils.getParam(p, 0);
		Record<CTXInfo, RecInfo> rr = ASUtils.execArgs(p.get(0).args, eneffect);
		System.out.println((rr!=null)?rr.text:"null");

	}
	
	/**
	 * iterates all material/symbol binds (defined in &lt;visual_scene&gt;) they belong 
	 * to geometry id of current shape (triangle or line). Because symbol id's are not unique
	 * this method picks up a set of symbol id's from binds which is mapped to
	 * a Geometry unique id from current shape. Then follows a equal check
	 * (i.e. triangle#material <-> instance_geometry#symbol) and link
	 * @param 1. a current Shape from &lt;geometry&gt;
	 * @param 2. geometry_symbol_Binds from &lt;visual_scene&gt;
	 * @param 3. parent tag of shape (&lt;geometry&gt;)
	 */
	public static void iterate_geometry_symbol_Binds_and_Link(ArrayList<Param> p)
	{
		//import params
		Record<CTXInfo,RecInfo> aShape  = ASUtils.getParam(p, 0);
		TreeMap<String, Record<CTXInfo,RecInfo>> geometry_symbol_Binds  = ASUtils.getParam(p, 1);
		Record<CTXInfo,RecInfo> aGeometry  = ASUtils.getParam(p, 2);

		
		String symbolName = aShape.attr.getProperty("material");
		Record<CTXInfo,RecInfo> aBindSet = geometry_symbol_Binds.get(aGeometry.info.uniqueID);
		
		boolean success = false;
		if (aBindSet !=null)
		{
			for (Record<CTXInfo,RecInfo> aBind:aBindSet.filteredChildren)
			{
				String symbolBindName = aBind.attr.getProperty("symbol");
				if (symbolName.equals(symbolBindName))
				{
					aShape.info.next = aBind;
					aBind.info.back = aShape;
					success = true;
					break;
				}
			}
		}
		
		if (Log.level != 0)
			Log.msg(Sketchup.class,"A shape with symbol ID "+symbolName+" from Geometry (id:"+aGeometry.info.uniqueID+") "+((success)?"linked":" has NO LINK"), 4);
		
	}
	
	

	
	/**
	 * parses raw coord data from xml (triangles/lines tag) to 'java readable' data
	 * @param a triangles/lines tag (entry)
	 * @param arg1 = path to input tag semantic vertex
	 * @param arg2 = path to input tag semantic texture coords
	 * @param arg3 = path to float_array tag with vertex coords
	 * @param arg4 = path to float_array tag with texture coords
	 * @param arg5 = path to p tag 
	 * @return coord raw data
	 */
	public static CoordData parseCoordData(ArrayList<Param> p)
	{
		Record<CTXInfo, RecInfo> aShape = ASUtils.getParam(p, 0);
		
		String[] args = p.get(0).args.split(";");
		for (int i = 0; i < args.length; i++) {
			args[i]= args[i].trim(); //if xml args is on one line then you can skip that
		}
		
		Record<CTXInfo, RecInfo> input_vertex = ASUtils.execArgs(args[0], aShape);
		Record<CTXInfo, RecInfo> input_texcoord = ASUtils.execArgs(args[1], aShape);
		Record<CTXInfo, RecInfo> float_array_vertex = ASUtils.execArgs(args[2], aShape);
		Record<CTXInfo, RecInfo> float_array_texcoord = ASUtils.execArgs(args[3], aShape);
		Record<CTXInfo, RecInfo> _p = ASUtils.execArgs(args[4], aShape);

		
		CoordData cd = new CoordData();
		cd.pArray = ASUtils.toInts(_p.text);
		cd.floatArray3D = ASUtils.toFloats(float_array_vertex.text);
		cd.offset_vertex = Integer.parseInt(input_vertex.attr.getProperty("offset"));
		cd.type = (aShape.name.equals("triangles"))?CoordData.Type.TRIANGLE:CoordData.Type.LINE;

		if (float_array_texcoord!=null)
		{
			cd.floatArray2D = ASUtils.toFloats(float_array_texcoord.text);
			cd.offset_texcoord = Integer.parseInt(input_texcoord.attr.getProperty("offset"));
		}
		
		//count multiplex
		int vertexPershape = (cd.type==CoordData.Type.TRIANGLE)?3:2;
		cd.shapeCnt = Integer.parseInt(aShape.attr.getProperty("count"));
		int allvertex = cd.shapeCnt*vertexPershape; 
		cd.muxCnt = Math.round(cd.pArray.length/allvertex);
		
		if (Log.level!=0)
			Log.msg(Sketchup.class,"Coord Raw Data for "+cd.shapeCnt+" <"+cd.type.name()+"> created "+((float_array_texcoord!=null)?" with texture coordinates":""), 4);

		
		return cd;
		
	}
	

	/**
	 * <p>creates Triangles based on this shape. The coordinates is converted from 
	 * Sketchup to Processing as follows:</p>
	 * 
     * <p>textureProcessing x = textureSketchup x * picture.width</p>
     * <p>textureProcessing y = picture.height - textureSketchup y * picture.height</p>
     * <p>processing x = Sketchup x </p>
     * <p>processing y = Sketchup -z </p>
     * <p>processing z = Sketchup -y </p>
	 * 
	 * @param 1. Raw coords (vertex+texture)
	 * @param 2. a Texture image
	 * @param 3. a Color (converted to processing)
	 * @param 4. the Collada interface
	 * @return a sublist for this polylist
	 */
	public static ArrayList<Triangle> createTriangles(ArrayList<Param> p)
	{
		//head init
		CoordData coordsRaw = ASUtils.getParam(p, 0);
		Texture texture = ASUtils.getParam(p, 1);
		Color c = ASUtils.getParam(p, 2);
		DescriptorCommon dsk = ASUtils.getParam(p, 3);
		ArrayList<Triangle> triangles = new ArrayList<Triangle>();
		
		//init vertex
		int[] dpv = ASUtils.demuxPArray(coordsRaw.pArray, coordsRaw.offset_vertex, coordsRaw.muxCnt);
		ASUtils.Point3D[] vertices = ASUtils.to3DPoints(coordsRaw.floatArray3D);
		
		//init texture
		boolean hasTex = coordsRaw.offset_texcoord >=0 && !dsk.ignoreTextures();
		int[] dpt = null;
		ASUtils.Point2D[] texcoords = null;
		if (hasTex)
		{
			dpt= ASUtils.demuxPArray(coordsRaw.pArray, coordsRaw.offset_texcoord, coordsRaw.muxCnt);
			texcoords = ASUtils.to2DPoints(coordsRaw.floatArray2D,texture.value.width,texture.value.height);
		}
		
		//begin creating triangles
		for (int i = 0, j=0, k=0; i < coordsRaw.shapeCnt; i++) {
			if (coordsRaw.type == CoordData.Type.TRIANGLE) 	
			{
				Triangle tri = new Triangle();
				//vertices part
				tri.A=vertices[dpv[j++]].copy();//copy is required procedure to make ColladaModel methods work (scale translate..etc)
				tri.B=vertices[dpv[j++]].copy();
				tri.C=vertices[dpv[j++]].copy();
				
				//texture part
				tri.containsTexture = hasTex;
				if (!hasTex)
					tri.colour = new Color(c.red, c.green, c.blue, c.transparency);
				else
				{
					tri.imageReference = texture.value;
					tri.imageFileName = texture.key;
					tri.texA=texcoords[dpt[k++]].copy();//copy is required procedure to make ColladaModel methods work (scale translate..etc)
					tri.texB=texcoords[dpt[k++]].copy();
					tri.texC=texcoords[dpt[k++]].copy();
				}
				
				//add to pool	
				triangles.add(tri);
			}
		}
		
		if (Log.level!=0)
			Log.msg(Sketchup.class,triangles.size()+" Triangles created from <triangles>"+((hasTex)?" with texture ("+texture.key+")":""), 4);
		
		return triangles; 
		
	}
	

	

	
	/**
	 * creates a Color if &lt;color&gt; exist
	 * converts float values from 0-1(Sketchup) to 0-255(processing)
	 * @param a &lt;lines/triangles&gt; tag from geometry with colorargs
	 * @return a color. if no valid colorargs then a dummy color is generated
	 */
	public static Color createColor(ArrayList<Param> p)
	{
		Record<CTXInfo, RecInfo> aShape = ASUtils.getParam(p, 0);
		String args = p.get(0).args;
		Record<CTXInfo, RecInfo> color = ASUtils.execArgs(args,aShape);
		Color c = null;
		String msg = "";
		
		if (color !=null)
		{
			msg +="color ";
			String[] rgb = color.text.split(" ");
			float r = Float.parseFloat(rgb[0])*255;
			float g = Float.parseFloat(rgb[1])*255;
			float b = Float.parseFloat(rgb[2])*255;
			float t = Float.parseFloat(rgb[3])*255; //mostly a dummy value 255

			c= new Color(r, g, b, t);
		}
		else
		{
			msg +="dummy color ";
			c=new Color(127.0f, 140.0f, 150.0f, 255.0f);
		}
		
		if (Log.level!=0)
			Log.msg(Sketchup.class,msg+"created", 4);


		return c;	
	}
	
	

}
