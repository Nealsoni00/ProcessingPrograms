package ch.dieseite.colladaloader.asmbeans;

import java.util.ArrayList;
import java.util.Properties;

import ch.dieseite.colladaloader.coreproc.DataAssembler;
import ch.dieseite.colladaloader.coreproc.DataAssembler.LoadingListener;
import ch.dieseite.colladaloader.coreproc.DataAssembler.Param;
import ch.dieseite.colladaloader.wrappers.ColladaModel;
import ch.dieseite.colladaloader.wrappers.Line;
import ch.dieseite.colladaloader.wrappers.Triangle;


/**
 * <p>this class is the named descriptor, defined in
 * LinkingSchema (&lt;interface&gt;) whose handle main
 * I/O relations between assembler classes and upper 
 * layer main class.</p>
 * 
 * <p>Purpose of this interface is setting startparams 
 * (i.e. file paths) and getting end results back 
 * when DataAssembler is done (i.e Triangles and Lines)
 * Any such defined interface must have a constructor
 * like &quot;AnyName(Properties colladaMetaData, DataAssembler.LoadingListener ltnr)&quot;</p>
 * 
 * <p>This source is free; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License and by nameing of the originally author</p>
 *
 * @author Markus Zimmermann <a href="http://www.die-seite.ch">http://www.die-seite.ch</a>
 * @version 3.1
 */
public class DescriptorCommon  {
	
	private LoadingListener l;
	private boolean ignoreTex;
	private String daePath;
	private ArrayList<Triangle> tris;
	private ArrayList<Line> lins;
	
	
	public DescriptorCommon(Properties colladaMetaData, DataAssembler.LoadingListener lstnr)
	{
		ignoreTex = false;
		daePath = colladaMetaData.getProperty("daePath");
    	if (colladaMetaData.containsKey("option_no_texture"))
    		ignoreTex = colladaMetaData.getProperty("option_no_texture").equals("true");
		l = lstnr;
		tris = new ArrayList<Triangle>();
		lins = new ArrayList<Line>();
	}
	
	/**
	 * Add Triangles to master cache
	 * @param subList
	 */
	public void addTriangles(ArrayList<Triangle> subList)
	{
		tris.addAll(subList);
	}
	/**
	 * Add Lines to master cache
	 * @param subList
	 */
	public void addLines(ArrayList<Line> subList)
	{
		lins.addAll(subList);
	}
	

	/**
	 * Fire LoadingListener what ColladaModel implements
	 */
	public void submitTriangles()
	{
		l.trianglesParsed(tris);
	}
	/**
	 * Fire LoadingListener what ColladaModel implements
	 */	
	public void submitLines()
	{
		l.linesParsed(lins);
	}
	


    
    /**
     * Says if the option "no_textures" is active
     * @return
     */
    public boolean ignoreTextures()
    {
    	return ignoreTex;
    }
    /**
     * returns current Location of collada xml file
     * @return
     */
    public String getDaePath()
    {
    	return daePath;
    }


}

