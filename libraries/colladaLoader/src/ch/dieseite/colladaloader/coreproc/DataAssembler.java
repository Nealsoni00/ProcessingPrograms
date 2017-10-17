package ch.dieseite.colladaloader.coreproc;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;


import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import ch.dieseite.colladaloader.coreproc.ObjectLinker.CTXInfo;
import ch.dieseite.colladaloader.coreproc.ObjectLinker.RecInfo;
import ch.dieseite.colladaloader.coreproc.SaxParser.Record;
import ch.dieseite.colladaloader.coreproc.SimpleDomParser.Element;
import ch.dieseite.colladaloader.wrappers.Line;
import ch.dieseite.colladaloader.wrappers.Triangle;


/**
 * <p>
 * DataAssembler combines the data from ObjectLinker's database into final wrapper 
 * objects they can be used by Processing. To do that it iterates through the database tables
 * and invokes functions. The &lt;assembling&gt; section from linking schema
 * defines iterations and function calls.
 * </p>
 * <p>
 * The DataAssembler works recursively in natural order. Functions can be placed 
 * into iterations and iterations can be nested. However, nested functions is not 
 * allowed.
 * </p>
 * <p>
 * The functions themselves are located in package &quot;asmbeans&quot; and beeing
 * invoked via system class loader. They should do tiny helper jobs 
 * and DataAssembler should manage the whole procedure. Each function must have
 * must have a method header like &quot;public static [Object|void] functionName(ArrayList&lt;Param&gt; paramlist)&quot;
 * </p> 
 * <p>
 * A next important part is the IO stub. The end results from the whole procedure
 * must be returned to user on the other hand there may be some
 * options coming from user what some functions need.
 * </p>
 * <p>
 * The input is realized via user options. (java.util.Properties).
 * The output results goes via LoadingListener as callback. On startup 
 * ColladaLoader creates an empty listener and commit it here.
 * </p>
 * <p>
 * Such Class what handles this I/O relations is located in &quot;asmbeans&quot; 
 * package, is defined in &lt;interface&gt; section of linking schema, 
 * and must have a constructor like 
 * &quot;MyName(Properties colladaMetaData, DataAssembler.LoadingListener ltnr)&quot;
 * </p>
 * 
 * <p>The class has a main test method and depends on ObjectLinker SaxParser SimpleDomParser Log and in linking schema defined assembler classes</p> 
 *
 * <p>This source is free; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License and by nameing of the originally author</p>
 *
 * @author Markus Zimmermann <a href="http://www.die-seite.ch">http://www.die-seite.ch</a>
 * @version 3.1
 */
public class DataAssembler {
	
	
	/**
	 * Main initial: parses LinkingSchema for jobs and gives
	 * back a valid tree of listed/nested jobs (function calls or iterations)
	 * with valid and linked descriptors for env vars, database
	 * interface or functions
	 * 
	 * @param linkerSchema
	 * @param linkerDatabase (created by ObjectLinker)
	 * @param colladaMetaData startparams, the class defined in &lt;interface&gt; uses it
	 * @param assembingCallback, the class defined in &lt;interface&gt; gives a callback if any processing Objects is ready to draw
	 * @return a valid Job tree
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws ClassNotFoundException
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public Job init(String linkerSchema, 
			HashMap<String, TreeMap<String, Record<CTXInfo,RecInfo>>> linkerDatabase, 
			Properties colladaMetaData,
			LoadingListener assembingCallback) throws ParserConfigurationException, SAXException, IOException, NoSuchMethodException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Element root = parseSchema(linkerSchema);
		Element as = root.getChildrenByName("assembling").get(0);
		Element stdio = root.getChildrenByName("interface").get(0);
		Job assembling = new Job(); //create anempty root
		HashMap<String, Variable> globalOutDescr = new HashMap<String, DataAssembler.Variable>(); //create descriptor env
		if (Log.level !=0) Log.msg(DataAssembler.class,"adding "+stdio.getAttrVal("name")+" descriptor stub" , 3);
		setInterfaceDescriptor(globalOutDescr,stdio, colladaMetaData, assembingCallback);
		if (Log.level !=0) Log.msg(DataAssembler.class,"making job skelleton" , 3);
		createJobTreeSkel(as, assembling);
		if (Log.level !=0) Log.msg(DataAssembler.class,"defining output env vars (tuples + results)" , 3);
		getOutDescriptorsFromJobSkel(assembling, globalOutDescr);
		if (Log.level !=0) Log.msg(DataAssembler.class,"adding database table descriptor stubs" , 3);
		addDescriptorsFromDB(globalOutDescr, linkerDatabase);
		if (Log.level !=0) Log.msg(DataAssembler.class,"hardlinking job I/O stubs to each other" , 3);
		hardLinkIoDescriptors(assembling, globalOutDescr);
		if (Log.level !=0) Log.msg(DataAssembler.class,"Adding Functions.." , 3);
		addFunctionDescriptors(assembling);
		if (Log.level!=0) Log.msg(DataAssembler.class,"init finished, ready for job starting / assembling", 1);
		return assembling;
	}
	
	/**
	 * Starts recursively all defined iterations and
	 * Functions defined inside &lt;assembling&gt; in natural
	 * order. Nested &lt;iteration&gt; tags is allowed but
	 * no nested &lt;function&gt; tags. However they can
	 * be placed into nested &lt;iteration&gt; tags
	 * @param js root tree defined by linker xml file
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public void recursiveJobIteration(Job js) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		
		for (Job j:js.children)
		{
			if (j.flag=='i')
			{
				if (Log.level!=0) Log.msg(DataAssembler.class,"trying iteration from "+j.source, 2);
				Iterable<?> c = null;
				if (j.in.aValue instanceof Record)
					c = new MyIterable((Record<?, ?>)j.in.aValue);
				if (j.in.aValue instanceof Map)
					c = ((Map<?,?>)j.in.aValue).values();
				if (j.in.aValue instanceof Collection)
					c = ((Collection<?>)j.in.aValue);
				
				if (c==null)
					throw new RuntimeException(""+c+" is not iterable !");
				
				int size=0; //just for logs
				for (Object s:c)
				{
					size++;
					if (Log.level!=0) Log.msg(DataAssembler.class,"next tuple..", 3);
					j.out.aValue = s;
					recursiveJobIteration(j);
				}
				
				if (Log.level!=0) Log.msg(DataAssembler.class,"iteration in "+j.source+" ("+size+" tuples) done", 2);
			}
			if (j.flag=='f')
			{
				if (Log.level != 0) 
					Log.msg(DataAssembler.class,"starting function "+j.name+"() ", 2);
				if (Log.level != 0) 
				{
					String _msg = "starting Function "+j.classname+"."+j.name+"() resultPtr="+j.result+" inparams={";
					for (Param e : j.inParams) {
						_msg += e.key+"[args:";
						_msg += e.args+"],";
					}
					_msg +="}";
					Log.msg(DataAssembler.class,_msg, 3);
				}
				//do function
				j.out.aValue = j.function.invoke(null, j.inParams);
				
				if (Log.level >= 2)
					Log.msg(DataAssembler.class,j.name+"() done", Log.level);

			}
		}
	}
	
	
	/**
	 * parses the xml linking schema as DOM Model
	 * should be placed into jar://ch/dieseite/colladaloader/coreproc 
	 * @param filename
	 * @return the &lt;root&gt; root element
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private Element parseSchema(String filename) throws ParserConfigurationException, SAXException, IOException
	{
		//init schema
		if (Log.level!=0) Log.msg(DataAssembler.class,"loading assembling schema "+filename, 1);
		
		SimpleDomParser p = new SimpleDomParser();
		SimpleDomParser.Element xmlDoc = p.loadXMLDataFromJar(filename);
		SimpleDomParser.Element root = xmlDoc.getChildrenByName("root").get(0);
		
		return root;
	}
	
	/**
	 * scans the xml element &lt;assembling&gt; recursively and 
	 * writes/maps its structure into Job &quot;parent&quot;.It sets 
	 * for each subjob a flag (for function or iteration) and String attributes 
	 * defined in xml.
	 * @param assembling
	 * @param parent root
	 */
	private void createJobTreeSkel(Element assembling, Job parent)
	{
		for (Element e:assembling.getChildren())
		{
			Job child = new Job();
			if (e.getName().equals("iteration"))
			{
				child.flag='i';
				child.source = e.getAttrVal("source");
				child.tuple =  e.getAttrVal("tuple");
			}
			if (e.getName().equals("function"))
			{
				child.flag='f';
				child.name = e.getAttrVal("name");
				child.classname = e.getAttrVal("class");
				child.result = e.getAttrVal("result");
				for (Element el:e.getChildrenByName("paramlist"))
				{
					Param p = new Param();
					p.key = el.getAttrVal("source");
					p.args = el.getAttrVal("args");
					child.inParams.add(p);
				}
			}
			
			parent.children.add(child);
			
			createJobTreeSkel(e, child); //go to deeper tree
		}
	}
	
	
	/**
	 * adds into named descriptors the database
	 * from ObjectLinker. Each table gets a new descriptor 
	 * that contains the table. 
	 * The naming is the same as tablename
	 * @param outputs named descriptors 
	 * @param db
	 */
	private void addDescriptorsFromDB(HashMap<String, Variable> outputs, HashMap<String, TreeMap<String, Record<CTXInfo,RecInfo>>> db)
	{
		for (String k:db.keySet())
		{
			TreeMap<String, Record<CTXInfo,RecInfo>> table = db.get(k);
			Variable descriptor = new Variable();
			descriptor.aValue = table;
			outputs.put(k, descriptor);
		}
	}
	
	
	/**
	 * Searches Job tree for &quot;tuple&quot; and &quot;result&quot; definitions
	 * recursively and gives them an output descriptor. Tuple and 
	 * result is the first defined env vars in LinkingSchema
	 * @param root
	 * @param outputs an empty Map, after method call it contains named descriptors
	 */
	private void getOutDescriptorsFromJobSkel(Job root, HashMap<String, Variable> outputs)
	{
		for (Job child:root.children)
		{
			Variable out = new Variable();
			if (child.flag == 'i')
				outputs.put(child.tuple, out);
			
			if (child.flag == 'f')
				outputs.put(child.result, out);

			getOutDescriptorsFromJobSkel(child, outputs); //go to deeper tree
		}
	}
	
	/**
	 * establish recursively hard links to all I/O descriptors 
	 * so each job has now a IN / OUT to the correct neighbours
	 * @param root tree
	 * @param outputs all (particularly empty) IO descriptors
	 * @param db from ObjectLinker
	 */
	private void hardLinkIoDescriptors(Job root, HashMap<String, Variable> outputs)
	{
		for (Job child:root.children)
		{
			
			if (child.flag == 'i')
			{
				child.out = outputs.get(child.tuple); //map a permanent output wrapper
				child.in = outputs.get(child.source); //set input wrapper
				if (Log.level !=0) Log.msg(DataAssembler.class,"Iteration In:"+child.source+" Out:"+child.tuple+" Val:"+((child.in.aValue!=null)?"Database":"null")+" linked" , 4);
			}
			
			if (child.flag == 'f')
			{
				child.out = outputs.get(child.result); //map a permanent wrapper
				String _msg = "In params{"; //just for log
				for (Param p  : child.inParams) {
					p.source = outputs.get(p.key); //set input wrapper
					_msg += p.key+"("+((p.source.aValue==null)?"null":"DB or ENV")+"),";
				}
				if (Log.level !=0) Log.msg(DataAssembler.class,"Function In:"+_msg+"} Out:"+child.result+" linked" , 4);

			}
			hardLinkIoDescriptors(child, outputs); //go to deeper tree
		}
	}
	
	/**
	 * goes through the Job tree recursively. If a 'f' flag
	 * is found then a Method descriptor is set (see java reflection)
	 * @param root
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws ClassNotFoundException
	 */
	private void addFunctionDescriptors(Job root) throws NoSuchMethodException, SecurityException, ClassNotFoundException
	{
		for (Job child:root.children)
		{		
			if (child.flag == 'f')
			{
				Class<?> cl = Class.forName(child.classname);
				child.function = cl.getMethod(child.name,ArrayList.class);
			}
			addFunctionDescriptors(child); //go to deeper tree
		}
	}	
	/**
	 * put a named descriptor whose handle main I/O relations between
	 * assembler classes and upper layer main class. Purpose is
	 * to get startparams (i.e. file paths) and set end 
	 * results back (i.e Triangles and Lines)
	 * @param outputs named descriptors 
	 * @param &lt;interface&gt; tag  from LinkingSchema
	 * @param colladaMetaData startparams
	 * @throws ClassNotFoundException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	private void setInterfaceDescriptor(HashMap<String, Variable> outputs, Element descr, Properties colladaMetaData, LoadingListener callback) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException
	{
		Variable descriptor = new Variable();
		Object interfaceDescriptor = Class.forName(descr.getAttrVal("class")).getConstructor(Properties.class,LoadingListener.class).newInstance(colladaMetaData, callback);
		String handlerName = descr.getAttrVal("name");
		descriptor.aValue = interfaceDescriptor;
		outputs.put(handlerName,descriptor);
	}
	

	//*******************Section wrappers************
	
	/**
	 * contain all infos to do assembling job at runtime
	 * A Job may be an iteration or a function
	 * @author mrcoffee
	 *
	 */
	public static class Job{
		private Job()
		{
			this.children = new ArrayList<DataAssembler.Job>();
			this.inParams = new ArrayList<DataAssembler.Param>();
		}
		/**
		 * main Var: 'i' = do an iteration, 'f' do a function /method 
		 */
		private char flag;
		/**
		 * contains subjobs. If nested &lt;iteration&gt; is defined or if Job
		 * is the root tree then its not emty
		 */
		private ArrayList<Job> children;
		
		/**
		 * iteration param written by xml parser
		 */
		private String tuple;
		/**
		 * iteration param written by xml parser
		 */
		private String source;
		
		/**
		 * function param written by xml parser
		 */
		private String name;
		/**
		 * function param written by xml parser
		 */
		private String classname;
		/**
		 * function param written by xml parser
		 */
		private String result;
		
		/**
		 * allocation written by hardlinker, used as method params by assembler classes
		 */
		private ArrayList<Param> inParams;
		/**
		 * In Descriptor written by hardlinker, used as 'list' by iterations
		 */
		private Variable in;
		/**
		 * In Descriptor written by hardlinker, my be a return 
		 * Value from assembler classes or an Iteration tuple 
		 */
		private Variable out;
		/**
		 * if Job is a function this descriptor var is set
		 */
		private Method function;
	}
	
	

	/**
	 * A wrapper for the inparams to static methods from
	 * in package &quot;asmbeans&quot;
	 * @author mrcoffee
	 *
	 */
	public static class Param
	{
		/**
		 * set by xml parser, focus &lt;source&gt; tag
		 */
		public String key; //set by xml
		/**
		 * set by xml parser, focus &lt;args&gt; tag, used by assembling classes
		 */
		public String args;
		/**
		 * descriptor, set by hard linker
		 */
		public Variable source;
	}

	
	/**
	 * a Descriptor to establish fixed links from Job to Job
	 * @author mrcoffee
	 *
	 */
	public static class Variable
	{
		public Object aValue;
	}
	
	/**
	 * wrapps children array from a Record into a Iterable object
	 * @author mrcoffee
	 */
	private static class MyIterable implements Iterable<Record<?,?>>, Iterator<Record<?,?>>
	{
		private Record<?,?>[] children; 
		private int maxInd;
		private int index;

		private MyIterable(Record<?,?> r)
		{
			this.children = r.filteredChildren;
			maxInd = (children == null)?-1:children.length-1;
		}

		@Override
		public Iterator<Record<?,?>> iterator() {index = -1; return this;}

		@Override
		public boolean hasNext() {return index < maxInd;}

		@Override
		public Record<?,?> next() {
			index++;
			return children[index];
		}
		public void remove()
		{
			throw new UnsupportedOperationException("MyIterable.remove() not implemented");
		}

	}
	
	/**
	 * Is invoked by assembling classes if any processing
	 * objects is parsed /ready to draw
	 * @author mrcoffee
	 *
	 */
	public interface LoadingListener{
		void trianglesParsed(ArrayList<Triangle> triangles);
		void linesParsed(ArrayList<Line> lines);
	}
	
	
//	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, NoSuchMethodException, SecurityException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
//		
//		Log.level=4;
//		ObjectLinker linker = new ObjectLinker();
//		linker.start("LinkingSchema_Sketchup.xml", "data/simpleshapes.dae");//from equal named .kmz
//		DataAssembler assembler = new DataAssembler();
//		Properties p = new Properties();
//		p.setProperty("daePathFromKML", "models/myDae.dae");
//		LoadingListener lsnr = new LoadingListener() {			
//			@Override
//			public void trianglesParsed(ArrayList<Triangle> triangles) {
//			}
//			@Override
//			public void linesParsed(ArrayList<Line> lines) {
//			}
//		};		
//		Job root = assembler.init("LinkingSchema_Sketchup.xml",linker.getDatabase(),p,lsnr);
//		assembler.recursiveJobIteration(root);
//	}

}
