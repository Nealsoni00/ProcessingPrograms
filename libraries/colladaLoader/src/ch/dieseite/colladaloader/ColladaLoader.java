package ch.dieseite.colladaloader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Enumeration;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import ch.dieseite.colladaloader.coreproc.DataAssembler;
import ch.dieseite.colladaloader.coreproc.Log;
import ch.dieseite.colladaloader.coreproc.ObjectLinker;
import ch.dieseite.colladaloader.coreproc.DataAssembler.Job;
import ch.dieseite.colladaloader.wrappers.ColladaModel;
import processing.core.PApplet;
import processing.data.XML;

/**
 * <p>This is the entry class to create resp. load a collada file</p>
 * 
* <p>This source is free; you can redistribute it and/or modify it under
* the terms of the GNU General Public License and by nameing of the originally author</p>
*
* @author Markus Zimmermann <a href="http://www.die-seite.ch">http://www.die-seite.ch</a>
* @version 3.1
*/
public class ColladaLoader {

	

	/**
	 *<p> Loads a file with endings &quot;.kmz&quot; or &quot;.dae&quot; from Processing's data path. Following options is supported:</p>
	 *<li>&quot;option_no_texture&quot; disable PImage loadings. Values is &quot;true&quot; or &quot;false&quot;  (default)</li>
     *<li>&quot;debuglevel&quot; Print out what Loader is doing to console:  Values is &quot;4&quot; = extreme details &quot;3&quot; = details, &quot;2&quot; = medium, &quot;1&quot; = abstract , &quot;0&quot; = none (default)</li>
     <li>&quot;LinkingSchema&quot; dae import profile,  Values is &quot;Sketchup&quot; (default) and &quot;Blender&quot;</li>
	 * @param filename
	 * @param current sketchbook
	 * @param options or null. 
	 * @return a Model with parsed Lines and Triangles
	 */
	public static ColladaModel load(String fileName,PApplet applet, Properties optionals )
	{
		ColladaModel model = null;
		if (optionals==null)
			optionals = new Properties();
		
		
		//start main
		try {
			String daeFile;
            if (fileName.endsWith("kmz"))
            {
            	unzip(fileName, applet);
            	daeFile = readDOCkml(applet);
            }
            else
            	daeFile = fileName;
            
            
			//startdefaults
            Properties defaults = new Properties();
            defaults.setProperty("daePath", applet.dataPath(daeFile));
            defaults.setProperty("debuglevel","0");
            defaults.put("LinkingSchema","Sketchup");
            defaults.setProperty("option_no_texture", "false");
            defaults.putAll(optionals);
    		
            //read start properties
            Log.level = Integer.parseInt(defaults.getProperty("debuglevel"));
			String linkingSchema = "LinkingSchema_"+defaults.getProperty("LinkingSchema")+".xml";

			
            //start parsing .dae
			model = new ColladaModel(applet);
			ObjectLinker linker = new ObjectLinker();
			linker.start(linkingSchema, applet.dataPath(daeFile));
			DataAssembler assembler = new DataAssembler();
	
			Job root = assembler.init(linkingSchema,linker.getDatabase(),defaults,model);
			assembler.recursiveJobIteration(root);      
            
            //do finish
			linker.getDatabase().clear();//flush memory
             //delete tmp zip files (if exist)
           	deleteRecursive(new File(applet.dataPath("tempDir")));

		} catch (Exception e){
				throw new RuntimeException(e);
		}
		return model;
	}
	
	/**
	 * unzips files and directory structure from kmz to data folder
	 * @param kmz filename in data folder
	 * @throws IOException 
	 */
    private static void unzip(String filename, PApplet applet) throws IOException
    {

        ZipFile zipfile = new ZipFile(applet.dataPath(filename));//=fullPath incl filename
        Enumeration<? extends ZipEntry> e = zipfile.entries();
        while(e.hasMoreElements()) {
           ZipEntry entry = (ZipEntry) e.nextElement();
           
           File outfile = new File(applet.dataPath("tempDir/"+entry.getName()));//=fullPath incl filename
           
           //unzip file
           if (!entry.isDirectory())
           {  
        	   outfile.getParentFile().mkdirs(); //enshure an existing dir struct
        	   
        	   //overwrite file
	           int len;
	           byte[] buffer = new byte[16384];
	           BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outfile));
	           BufferedInputStream bis = new BufferedInputStream(zipfile.getInputStream(entry));
	           while ((len = bis.read(buffer)) > 0)
	               bos.write(buffer, 0, len);
	           bos.flush();
	           bos.close();
	           bis.close();
           }

        }
        zipfile.close();

    }

	    
    /**
     * reads where to find the .dae file
     * @return a .dae file location as a relative path to data folder
     */
    private static String readDOCkml(PApplet applet)
    {
        XML root = applet.loadXML(applet.dataPath("tempDir/doc.kml"));
        XML dae = root.getChild("Folder/Placemark/Model/Link/href");
        String file = dae.getContent();
        return "tempDir/"+file;

    }
    /**
     * deletes file and directories recursively (called after unzipping)
     * @param file
     * @throws FileNotFoundException
     */
    private static void deleteRecursive(File file) throws FileNotFoundException
    {
 	   if (file.isDirectory()) {
 		    for (File c : file.listFiles())
 		      deleteRecursive(c);
 		  }
 	   file.delete();
    }
	


	
	
	

    
    
    
    

}
