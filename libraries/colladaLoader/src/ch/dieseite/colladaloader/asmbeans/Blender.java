package ch.dieseite.colladaloader.asmbeans;

import java.util.ArrayList;


import ch.dieseite.colladaloader.coreproc.Log;
import ch.dieseite.colladaloader.asmbeans.Common.Texture;
import ch.dieseite.colladaloader.coreproc.DataAssembler.Param;
import ch.dieseite.colladaloader.coreproc.ObjectLinker.CTXInfo;
import ch.dieseite.colladaloader.coreproc.ObjectLinker.RecInfo;
import ch.dieseite.colladaloader.coreproc.SaxParser.Record;
import ch.dieseite.colladaloader.wrappers.Color;
import ch.dieseite.colladaloader.wrappers.Line;
import ch.dieseite.colladaloader.wrappers.Point3D;
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

public class Blender {
	
	public static void sop(ArrayList<Param> p)
	{
		Record<CTXInfo, RecInfo> eneffect = ASUtils.getParam(p, 0);
		Record<CTXInfo, RecInfo> rr = ASUtils.execArgs(p.get(0).args, eneffect);
		System.out.println((rr!=null)?rr.text:"null");

	}
	
	

	/**
	 * parses raw coord data from xml (polylist tag) to 'java readable' data
	 * @param aPolyList tag (entry)
	 * @param arg1 = path to input tag semantic vertex
	 * @param arg2 = path to input tag semantic texture coords
	 * @param arg3 = path to float_array tag with vertex coords
	 * @param arg4 = path to float_array tag with texture coords
	 * @param arg5 = path to vcount tag 
	 * @param arg6 = path to p tag 
	 * @param arg7 = path to matrix tag 
	 * @return coord raw data
	 */
	public static CoordData parseCoordData(ArrayList<Param> p)
	{
		Record<CTXInfo, RecInfo> aPolyList = ASUtils.getParam(p, 0);
		
		String[] args = p.get(0).args.split(";");
		for (int i = 0; i < args.length; i++) {
			args[i]= args[i].trim(); //if xml args is on one line then you can skip that
		}
		
		Record<CTXInfo, RecInfo> input_vertex = ASUtils.execArgs(args[0], aPolyList);
		Record<CTXInfo, RecInfo> input_texcoord = ASUtils.execArgs(args[1], aPolyList);
		Record<CTXInfo, RecInfo> float_array_vertex = ASUtils.execArgs(args[2], aPolyList);
		Record<CTXInfo, RecInfo> float_array_texcoord = ASUtils.execArgs(args[3], aPolyList);
		Record<CTXInfo, RecInfo> _vcount = ASUtils.execArgs(args[4], aPolyList);
		Record<CTXInfo, RecInfo> _p = ASUtils.execArgs(args[5], aPolyList);
		Record<CTXInfo, RecInfo> _matrix = ASUtils.execArgs(args[6], aPolyList);
		if (_matrix == null)
			_matrix = ASUtils.execArgs(args[7], aPolyList); //in case of bones animations (Armature)
		
		CoordData cd = new CoordData();
		cd.pArray = ASUtils.toInts(_p.text);
		cd.floatArray3D = ASUtils.toFloats(float_array_vertex.text);
		cd.offset_vertex = Integer.parseInt(input_vertex.attr.getProperty("offset"));
		cd.type = (aPolyList.name.equals("polylist"))?CoordData.Type.POLYGON:CoordData.Type.LINE;
		cd.matrix = ASUtils.toFloats(_matrix.text);

		
		if (float_array_texcoord!=null)
		{
			cd.floatArray2D = ASUtils.toFloats(float_array_texcoord.text);
			cd.offset_texcoord = Integer.parseInt(input_texcoord.attr.getProperty("offset"));
		}
		
		//count multiplex
		int allvertex = 0;
		if (cd.type == CoordData.Type.LINE)
		{
			cd.shapeCnt = Integer.parseInt(aPolyList.attr.getProperty("count"));
			int vertexPershape = 2;
			allvertex = cd.shapeCnt*vertexPershape; 
		}
		else
		{
			cd.vcount = ASUtils.toInts(_vcount.text);
			cd.shapeCnt = cd.vcount.length;
			for (int v:cd.vcount)//allVertex = shapecount*vertexPershape = vcount.length*vertexPershape
				allvertex += v;
		}
		cd.muxCnt = Math.round(cd.pArray.length/allvertex);
		
		if (Log.level!=0)
			Log.msg(Blender.class,"Coord Raw Data for "+cd.shapeCnt+" shapes created from <polylist> or <line>"+((float_array_texcoord!=null)?" with texture coordinates":""), 4);

		
		return cd;
		
	}
	

	/**
	 * <p>creates Triangles based on this polylist. The coordinates is converted from 
	 * Blender to Processing as follows: Note: convert is NOT COMPLETE: Vertex results 
	 * must go through a 4x4 Matrix later</p>
	 * 
     * <p>textureProcessing x = textureBlender x * picture.width</p>
     * <p>textureProcessing y = picture.height - textureBlender y * picture.height</p>
     * <p>processing x = Blender x</p>
     * <p>processing y = Blender -z</p>
     * <p>processing z = Blender -y</p>
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
		if (coordsRaw.type == CoordData.Type.POLYGON)
		{
			for (int i = 0, j=0, k=0; i < coordsRaw.shapeCnt; i++) {
				if (coordsRaw.vcount[i]==3) //= is a triangle	
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
				else
				{// or ignore this (part of )polygon if not a triangle
					j +=coordsRaw.vcount[i]; 
					k +=coordsRaw.vcount[i];
				}
			}
		}
		
		if (Log.level!=0)
			Log.msg(Blender.class,triangles.size()+" Triangles created from <polylist>"+((hasTex)?" with texture ("+texture.key+")":""), 4);
		
		return triangles; 
		
	}
	

	
	/**
	 * creates a Color if &lt;color&gt; exist
	 * converts float values from 0-1(Blender) to 0-255(processing)
	 * Note that blender put no material attribute to &lt;lines&gt; (stupid impl) so a dummy color is generated
	 * @param a &lt;PolyList&gt; tag from geometry with colorargs and transparencyArgs
	 * @return a color. if no valid colorargs then a dummy color is generated
	 */
	public static Color createColor(ArrayList<Param> p)
	{
		Record<CTXInfo, RecInfo> aPolyList = ASUtils.getParam(p, 0);
		String[] args = p.get(0).args.split(";");
		Record<CTXInfo, RecInfo> color = ASUtils.execArgs(args[0],aPolyList);
		Record<CTXInfo, RecInfo> trsp = ASUtils.execArgs(args[1],aPolyList);
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
			if (trsp !=null)
			{
				msg +="with transparancy ";
				t= Float.parseFloat(trsp.text)*255;
			}
			c= new Color(r, g, b, t);
		}
		else
		{
			msg +="dummy color ";
			c=new Color(204f, 50f, 0f, 255.0f);
		}
		
		if (Log.level!=0)
			Log.msg(Blender.class,msg+"created", 4);


		return c;	
	}
	
	/**
	*  Transforms all Vertex Coords of Lines and/or Triangles (a stupid idea of blender)
	*	@param 1:	ArrayList<Triangle>
	*	@param 2:  ArrayList<Line>
	*	@param 3:   CoordData
	*/
	public static void transformShapes(ArrayList<Param> p)
	{
		ArrayList<Triangle> triangles = ASUtils.getParam(p, 0);
		ArrayList<Line> lines = ASUtils.getParam(p, 1);
		CoordData cd = ASUtils.getParam(p, 2);

		for (Triangle t : triangles) {
			t.A = transform(t.A, cd.matrix);
			t.B = transform(t.B, cd.matrix);
			t.C = transform(t.C, cd.matrix);
		}
		for (Line l : lines) {
			l.A = transform(l.A,cd.matrix);
			l.B = transform(l.B, cd.matrix);
		}
		
		if (Log.level!=0)
			Log.msg(Blender.class,"Size and Positions of "+triangles.size()+" Triangles and "+lines.size()+" Lines changed", 4);
		
	}
	
	
	
	/**
	 * Transforms a 3D Point with a 4x4 Matrix. It's assumed the Indexes [12-15] of 4x4 matrix is always 0,0,0,1
	 * @param vertex
	 * @param matrix4x4
	 * @return
	 */
	private static Point3D transform(Point3D vertex, float[] matrix4x4)
	{
		Point3D p2 = new Point3D(0, 0, 0);
		p2.x = matrix4x4[0]*vertex.x + matrix4x4[1]*vertex.y + matrix4x4[2]*vertex.z + matrix4x4[3];
		p2.y = matrix4x4[4]*vertex.x + matrix4x4[5]*vertex.y + matrix4x4[6]*vertex.z + matrix4x4[7];
		p2.z = matrix4x4[8]*vertex.x + matrix4x4[9]*vertex.y + matrix4x4[10]*vertex.z + matrix4x4[11];
		//(ignore p2.w = 1)
		return p2;
	}
	
	
	
	/**
	 * wrapper for parsed vertex/texcoord data
	 */
	private static class CoordData extends ch.dieseite.colladaloader.asmbeans.Common.CoordData
	{
		private int[] vcount = null;
		private float[] matrix = null;
	}
	

	

}
