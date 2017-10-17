package ch.dieseite.colladaloader.coreproc;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import ch.dieseite.colladaloader.coreproc.SaxParser.Context;
import ch.dieseite.colladaloader.coreproc.SaxParser.Record;

/**
 * <p>
 * ObjectLinker creates and holds an object oriented database. 
 * So we get an abstraction to collada XML structure. ObjectLinker get 
 * its instructions via &lt;loading&gt; section in linking schema.
 * </p>
 * <p>
 * In natural order and for each &lt;entities&gt; tag a new scan via SaxParser 
 * starts again committing search patterns. Each returned 
 * &quot;SaxParser.Record&quot; objects is added into DB tables and 
 * linked/backlinked to other Records 
 *  (see also linking schema specifications &lt;target&gt;).
 * </p>
 * <p>
 * Following fields into &quot;SaxParser.Record&quot; is added by ObjectLinker:
 * </p>
 * <p>
 * <li>a unique ID (auto generated or collada defined)</li>
 * <li>a next link to a foreign Record (optional, collada defined)</li>
 * <li>a back link to a foreign or parent Record (optional, collada defined)</li>
 * <li>a set of fetched child Records (optional, search pattern dependent)</li>
 * </p>
 * <p>The database has the format:</p>
 * 
 * <p>HashMap&lt;poolname,TreeMap&lt;uniqueID,recordObject&gt;&gt;<br />
 * resp.<br />
 * HashMap&lt;String,TreeMap&lt;String,SaxParser.Record&gt;&gt;<p>
 * 
 * <p>Note: If the natural order of Records matters let ObjectLinker
 * generate an uniqueID and limit to 2'147'483'647 entities
 * per pool name</p>
 * 
 * <p>this class has a main() method to test a sketchup Version 8 dae file
 * with colors and texture. It depends on xmlfiles SaxParser SimpleDomParser and Log</p>
 *
 * <p>This source is free; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License and by nameing of the originally author</p>
 *
 * @author Markus Zimmermann <a href="http://www.die-seite.ch">http://www.die-seite.ch</a>
 * @version 3.1
 */
public class ObjectLinker {

	
	private HashMap<String,TreeMap<String,Record<CTXInfo, RecInfo>>> database;
	private int IdCnt;

	public ObjectLinker()
	{
		database = new HashMap<String, TreeMap<String,Record<CTXInfo, RecInfo>>>();
		IdCnt =0;
	}
	
	/**
	 * parses linking schema. It returns all &lt;entities&gt; inside &lt;loading&gt;
	 * tags in natural order. it also creates a database skeleton
	 * based by Linking Schema
	 * @param linking schema
	 * @return all jobs to be done
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	private List<SimpleDomParser.Element> initLoadingJobs(String filename) throws ParserConfigurationException, SAXException, IOException
	{
	
		SimpleDomParser p = new SimpleDomParser();
		SimpleDomParser.Element xmlDoc = p.loadXMLDataFromJar(filename);
		SimpleDomParser.Element root = xmlDoc.getChildren().get(0); //move to 'root' tag
		
		SimpleDomParser.Element loading = root.getChildrenByName("loading").get(0); //move to 'loading' tag
		
		List<SimpleDomParser.Element> entities = loading.getChildrenByName("entities"); //all children names 'entities'
		
		//create db Skelletton
		for (SimpleDomParser.Element el: entities)
			database.put(el.getAttrVal("poolname"), new TreeMap<String, Record<CTXInfo, RecInfo>>());
		
		return entities;

	}
	
	/**
	 * creates a sax parser that scans a later defined 
	 * collada file. It embeds also a callback handler 
	 * that edit the internal database by incoming 
	 * callbacks from SaxParser
	 * @return
	 */
	private SaxParser<CTXInfo,RecInfo> initSaxParser()
	{
		
		SaxParser.RecordHandler<CTXInfo,RecInfo> rh = new SaxParser.RecordHandler<CTXInfo,RecInfo>() {			

			/**
			 * puts all found records into db
			 */
			@Override
			public void foundParent(SaxParser.Record<CTXInfo,RecInfo> r) {
		
				linkRecord(r, null, r.ctx);
				TreeMap<String, Record<CTXInfo, RecInfo>> pool = database.get(r.ctx.info.thisPool);
				Object prev = pool.put(r.info.uniqueID, r);

				//log it
				if (Log.level!=0)
				{
					  String s1 = "collada parent tag '"+r.name+"' to pool '"+r.ctx.info.thisPool+"' ";
					  String s2 = "containing "+((r.filteredChildren!=null)?"children /subchildren":"no children");
					  String s3 = " was "+((prev!=null)?"replaced. Error: ID was NOT unique!":"added");
					  Log.msg(ObjectLinker.class,s1+s2+s3, 3);
				}
								
			}
		};

		return new SaxParser<CTXInfo,RecInfo>(rh);
	}
	
	/**
	 * Iterates all jobs in &lt;entities&gt; and rescans
	 * the whole .dae document each time
	 * @param entities
	 * @param daeFile
	 * @param sax
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private void execJobs(List<SimpleDomParser.Element> entities, String daeFile, SaxParser<CTXInfo,RecInfo> sax) throws ParserConfigurationException, SAXException, IOException
	{
		
		for (SimpleDomParser.Element en: entities)
		{
			if (Log.level!=0) Log.msg(ObjectLinker.class,"init new entity job", 2);
			String _cpn = en.getAttrVal("poolname");
			Context<CTXInfo,RecInfo>[] srchChain = (Context<CTXInfo,RecInfo>[]) initSearchPatterns(en, null,_cpn).toArray(new Context[1]);
			
			//log before
			int _zb =0,_za =0;
			
			if (Log.level!=0) 
			{
				String _prm ="";
				 for (Context<CTXInfo, RecInfo> ct:srchChain)
					 _prm += ct.info.xmlSearchPattern+" ";

				_zb = database.get(_cpn).size();
				Log.msg(ObjectLinker.class,"searching .dae, into pool "+_cpn+" with params "+_prm+" " , 2);
			}
			
			//parse whole xml for defined pattern for this entity
			sax.parse(srchChain, daeFile);
			
			//log after
			if (Log.level!=0) 
			{
				_za = database.get(_cpn).size();
				Log.msg(ObjectLinker.class,"found: "+(_za-_zb)+" records", 2);
			}
		}		
	}
	
	/**
	 * The schemaFile says how to parse a .dae file into a
	 * valid internal database using searchpatterns to 
	 * fetch collada xml tags, or linking instructions
	 * @param schemaFile with focus to "entities" tags
	 * @param collada file
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public void start(String filename, String daeFile ) throws ParserConfigurationException, SAXException, IOException
	{
		if (Log.level!=0) Log.msg(ObjectLinker.class,"init loading schema "+filename, 1);
		List<SimpleDomParser.Element> entities = initLoadingJobs(filename);
		
		if (Log.level!=0) Log.msg(ObjectLinker.class,"init sax parser for "+daeFile, 1);
		SaxParser<CTXInfo,RecInfo> sax = initSaxParser();

		if (Log.level!=0) Log.msg(ObjectLinker.class,"starting collada parsing and linking"+daeFile, 1);
		execJobs(entities, daeFile, sax);

	}

	
	/**
	 * Digs recursively into &lt;target&gt; tag and its 
	 * subtags in linking schema to get all search 
	 * patterns for SaxParser class
	 * @param aTarget
	 * @param parent or null if root &lt;target&gt;
	 * @return
	 */
	private List<Context<CTXInfo, RecInfo>> initSearchPatterns(SimpleDomParser.Element aTarget, Context<CTXInfo,RecInfo> parent, String poolname)
	{
		
		ArrayList<Context<CTXInfo, RecInfo>> srchChain = new ArrayList<SaxParser.Context<CTXInfo,RecInfo>>();
		for (SimpleDomParser.Element el:aTarget.getChildren()) 
		{
			CTXInfo info = new CTXInfo(el, poolname); //add linker layer info
			Context<CTXInfo,RecInfo> ctx = SaxParser.createSearchContext(info.xmlSearchPattern, parent);
			ctx.info = info;
			srchChain.add(ctx);
			srchChain.addAll(initSearchPatterns(el, ctx,poolname));	
		}
		return srchChain;
		
	}


	/**
	 * Adds a RecInfo(), gives a unique ID (or defined ID by collada)
	 * establish foreign key links/backlinks and does the same
	 * procedure with its children recursively
	 * @param aDaeRec
	 * @param parentRec
	 * @param ctx
	 * @return
	 */
	private SaxParser.Record<CTXInfo, RecInfo> linkRecord(
			SaxParser.Record<CTXInfo, RecInfo> aDaeRec, 
			SaxParser.Record<CTXInfo, RecInfo> parentRec, 
			SaxParser.Context<CTXInfo, RecInfo> ctx)
	{
		//init
		aDaeRec.info = new RecInfo();
		boolean logIdSucc = false, logLnkSucc = false;
		
		//define backtracking link
		aDaeRec.info.back = parentRec; 

		//define ID
		String uniqueID = getValue(aDaeRec.ctx.info.idExpression,aDaeRec);
		if (uniqueID==null)
		{
			logIdSucc = false;
			uniqueID = String.format("%010d", IdCnt++); //define a 10 digits number
		}
		else
			logIdSucc=true;
		aDaeRec.info.uniqueID = uniqueID;

	
		//define next reference and link it back (if available)
		String nextTab = aDaeRec.ctx.info.nextPool;
		String nextRec = getValue(aDaeRec.ctx.info.nextIdExpression, aDaeRec);
		boolean nextDefined = (nextTab!=null) && (nextRec !=null);

		if (nextDefined)
		{
			Record<CTXInfo, RecInfo> next = database.get(nextTab).get(nextRec);
			if (next == null)
				System.out.println("ll");
			next.info.back = aDaeRec; //shouln't be null...
			aDaeRec.info.next = next;
			logLnkSucc = next!=null;;
		}

		
		//fill up children with same procedure as above 
		if (aDaeRec.filteredChildren !=null) 
			for (SaxParser.Record<CTXInfo,RecInfo> c : aDaeRec.filteredChildren)
				linkRecord(c, aDaeRec, ctx);
		

		//logging
		if (Log.level !=0)
		{
			String _msg = "Found collada tag '"+aDaeRec.name+"'";
			_msg += "ID = "+((logIdSucc)?uniqueID:"(auto)")+"; ";
			String _nxtn = (aDaeRec.info.next!=null)?aDaeRec.info.next.name:" ? ";
			String _nxt = "next tag = "+_nxtn+", next ID = "+nextRec+", next pool = "+nextTab;
			_msg += (nextDefined)?_nxt+" Link to it: "+((logLnkSucc)?"success":"failed"):"";
			Log.msg(ObjectLinker.class,_msg, 3);
		}

		return aDaeRec;
	}
	

	
	/**
	 * Returns a Textcontent or an attribute value from 
	 * record, depending on the expression.
	 * Any tabstops or '#' characters will be removed
	 * from the result
	 * @param expression 'myValue' or '#myAttributeName' or '$TEXTCONTENT' or null
	 * @param r current record (child or parent)
	 * @return value a String or null if expression is null
	 */
	private String getValue(String expression, SaxParser.Record<CTXInfo,RecInfo> r)
	{
		if (expression == null)
			return null;
		
		boolean isText = expression.contains("$TEXTCONTENT");
		boolean isAttribName = expression.contains("#");
		boolean isValue = !isAttribName && !isText;
		
		String value = null;
		if (isAttribName)
			value = r.attr.getProperty(expression.replaceAll("#", "")); //goes for attributename and attribute value
		if (isText)
			value = r.text;
			
		if (isValue)
			value = expression;
		
		if (value == null)
			return null;
		else
			return value.replaceAll("#", "").trim();
	}
	
	/**
	 * See notes on the class description
	 * @return the Database
	 */
	public HashMap<String, TreeMap<String, SaxParser.Record<CTXInfo, RecInfo>>> getDatabase() {
		return database;
	}	

	/**
	 * Wrapps all infos needed to build the internal database
	 * This fields is set BEFORE object linker does his work
	 * @author mrcoffee
	 *
	 */
	public class CTXInfo
	{
		CTXInfo(SimpleDomParser.Element subtag, String thisPool) {
			this.idExpression = subtag.getAttrVal("idValueIn");
			this.nextIdExpression = subtag.getAttrVal("nextIdValueIn");
			this.nextPool = subtag.getAttrVal("nextEntity");
			this.thisPool = thisPool;
			this.xmlSearchPattern = subtag.getAttrVal("pattern");
			
		}

		/**
		 * says where ID info inside xml tag can be found
		 */
		public String idExpression; 
		/**
		 * says where foreign key info inside xml tag can be found
		 */
		public String nextIdExpression;
		/**
		 * says in which memory pool (table) is foreign key located
		 */
		public String nextPool;
		/**
		 * says in which memory pool is record located
		 */
		public String thisPool;
		/**
		 * says how to fetch collada tags
		 * i.e. "rootDoc/tags/tag/subtag" or "rootDoc/tags/tag/subtag#attribName=value"
		 */
		public String xmlSearchPattern;
				
	}
	
	/**
	 * this class wrapps individual infos for each DB record
	 * this fields is set AFTER ObjectLinker is done
	 * @author mrcoffee
	 */
	public class RecInfo
	{

		/**
		 * a reference to parent record or a record in another pool
		 */
		public SaxParser.Record<CTXInfo,RecInfo> back;
		/**
		 * a reference to a record in another pool
		 */
		public SaxParser.Record<CTXInfo,RecInfo> next;
		/**
		 * must be unique. The pool name can be read in the context field of record
		 */
		public String uniqueID;
		
	}
	
//	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
//		Log.level = 3;
//		ObjectLinker l = new ObjectLinker();
//		l.start("LinkingSchema_Sketchup.xml", "data/simpleshapes.dae");// unzipped from simpleshapes.kmz
// 	
//	}
	



}
