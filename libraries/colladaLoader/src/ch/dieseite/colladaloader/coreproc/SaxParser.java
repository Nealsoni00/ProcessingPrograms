package ch.dieseite.colladaloader.coreproc;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * <p>
 * SaxParser scans a XML stream and compares it to a search pattern.
 * </p>
 * A pattern is wrapped in a SaxParser.Context and matches if XML depth 
 * and XML tag name of stream is arrived. In this case a &quot;SaxParser.Record&quot; 
 * object is created. It can be either:</p>
 * <li>a) added as a child into a current parent Record object (on nested patterns) or</li>
 * <li>b) be returned via callback (if pattern is parent)</li>
 * <p>
 * SaxParser process more than one patterns as &quot;OR&quot; and nested patterns as
 * &quot;AND&quot;. Nested patterns implicate children, top levels parents. 
 * </p>
 * <p>
 * Its a good practice to define exactly one parent 
 * and zero or more children per scan. The less children
 * is defined, the less memory is used. Max allowed xml 
 * depth is Long.BYTES*8.
 * </p>
 * <p>
 * A pattern can either match by tag name (Syntax: &quot;tagName&quot;)
 * or tag name and attribute value	(Syntax: &quot;tagName#attribName=value&quot;)
 * what gives it a stricter fine search. Each xml depth is separated by 
 * &quot;/&quot;. An Example can be found in linking schema specifications
 *  under &lt;target&gt;
 * </p>
 * <p>
 * Note: This Class has no depencies to other classes and contains a Main() testMethod
 * </p>
 *
 * <p>This source is free; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License and by nameing of the originally author</p>
 *
 * @author Markus Zimmermann <a href="http://www.die-seite.ch">http://www.die-seite.ch</a>
 * @version 3.1
 */
public class SaxParser<CI,RI> {
	
	
	private RecordHandler<CI,RI> handler;
	
	private SaxParser(){}
	
	/**
	 * handler is a callback (see interface descriptions)
	 * @param handler mustn't be null
	 */
	public SaxParser(RecordHandler<CI,RI> handler)
	{
		this.handler = handler;
	}

	/**
	 * Starts xml scan
	 * 
	 * @param params , created with createSearchContext()
	 * @param filename (absolute Path)
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public void parse(Context<CI,RI>[] params, String filename) throws ParserConfigurationException, SAXException, IOException
	{
		if (params==null || params[0] == null)
			throw new RuntimeException("missing valid sax search params (at least 1 entry that is a parent tag )");
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		SaxHandler<CI,RI> h = new SaxHandler<CI,RI>(params, handler);
		saxParser.parse(filename,h);
	}
	

	/**
	 * Creates a search context containing search pattern
	 * If context is nested then add parent context 
	 * @param xmlSearchPattern i.e. "rootDoc/tags/tag/subtag" or "rootDoc/tags/tag/subtag#attribName=value"
	 * @param parent or null if it's the first parent context
	 * @return
	 */
	public static <CI, RI> Context<CI, RI> createSearchContext(String xmlSearchPattern, Context <CI, RI> parent)
	{
		String[] srchpttrns = xmlSearchPattern.split("/");
		Context<CI, RI> param = new Context<CI, RI>(parent,srchpttrns );
		return param;
	}
	
	private static class SaxHandler<CI,RI> extends DefaultHandler
	{
		
		//user patterns
		private RecordHandler<CI,RI> handler;
		//end userpatterns
		
		//callback caches
		private Context<CI,RI>[] params;
			
		/**
		 * @param searchParams must at least contain 1 Entry which is a parent
		 * @param handler invokes if one or more record in doc has matched
		 */
		private SaxHandler(Context<CI,RI>[] searchParams, RecordHandler<CI,RI> handler) {

			this.params = searchParams;
			this.handler = handler;
			
		}
		
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes)
				throws SAXException 
		{
			for (Context<CI,RI> ctx : params) {
				ctx.incrementHistory(); //count next level
				ctx.updateHistory(qName, attributes);//check if is a possible fetch tree
				
				if (ctx.isMatch())
				{
					//deepcopy of xml attributes to properties
					Properties p = new Properties();
					for (int i = 0; i< attributes.getLength();i++)
						p.setProperty(attributes.getQName(i),attributes.getValue(i));
					ctx.targetAttrVals = p;
				}
			}
		}
		
	
		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException 
		{
			for (Context<CI,RI> ctx : params) {
				if (ctx.isMatch()) //send something that matched
				{
					Record<CI,RI> r = new Record<CI,RI>();
					r.name = qName;
					r.text = ctx.cData;
					r.attr = ctx.targetAttrVals;
					r.ctx = ctx;
					ctx.cData = null;
					if (r.text!=null)r.text = r.text.trim(); //polish up
										
					if (ctx.filteredChildren.size()!=0) //fill up children
					{
						r.filteredChildren = (Record<CI,RI>[])ctx.filteredChildren.toArray(new Record[1]);
						ctx.filteredChildren.clear(); //flush cache
					}
					
					if (ctx.parent == null) //is parent
						handler.foundParent(r); //send callback

					else
						ctx.parent.filteredChildren.add(r);
				}
				ctx.decrementHistory(); //count prev level
			}
		}
		
		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			
			for (Context<CI,RI> ctx : params) {
				if (ctx.isMatch())
				{
					if (ctx.cData==null) ctx.cData = "";
					ctx.cData += new String(ch, start, length); //.trim(); don't trim: sometimes nested spaces is required ;-)
				}
			}
		}
			
	}
	
	/**
	 * A wrapper that contains xml tag datas
	 * @author mrcoffee
	 *
	 */
	public static class Record<CI,RI>
	{
		
		/**
		 * contains records if defined by nested context
		 */
		public Record<CI,RI>[] filteredChildren = null;
		/**
		 * textcontent of xml tag
		 */
		public String text;
		/**
		 * xml tagName
		 */
		public String name; 
		/**
		 * xml tag Attributes
		 */
		public Properties attr;
		/**
		 * infos to find certain xml tree, callback caches and more..
		 */
		public Context<CI,RI> ctx; 
		
		/**
		 * can be used by upper application Layer
		 * Sax parser does not use it.
		 */
		public RI info;
		
	}
	
	static interface RecordHandler<CI,RI>
	{
		/**
		 * fires zero or more times, each time a xml parent 
		 * tag is found that match to context search patterns.
		 * Any Child tags that match in nested Context is stored
		 * in the array too
		 * @param r
		 */
		void foundParent(Record<CI,RI> r);
	}
	
	/**
	 * <p>A helper class </p>
	 * <li>a) to cache callbacks from SaxParser</li>
	 * <li>b) to define a certain tag in a certain xml tree
	 *    to be found and</li>
	 * <li>c) additional infos what to do next with that Record</li>
	 * @author mrcoffee
	 */
	public static class Context<CI,RI>
	{
		/**
		 * search params
		 */
		private String[] xmlSearchPattern;
		/**
		 * the context from relative parent Tag
		 */
		private Context<CI,RI> parent;
		
		//***************callback caches
		/**
		 * callback cache:
		 * 0=true (stays true if all tree walk through is match)
		 */
		private long matchHistory; 
		/**
		 * callback cache:
		 * walkthrough counter in xml tree
		 */
		private int currentDepth;
		/**
		 * callback cache
		 */
		private Properties targetAttrVals;
		/**
		 * callback cache
		 */
		private String cData;
		/**
		 * callback cache:
		 * contains records if defined by nested context
		 */
		private List<Record<CI,RI>> filteredChildren;
		
		//********************************

		private Context(){}
		
		private Context(Context<CI,RI> parent, String[] xmlSearchPattern) {
			currentDepth = -1;
			matchHistory = 0;
			this.parent = parent;
			this.xmlSearchPattern = xmlSearchPattern;
			this.filteredChildren = new ArrayList<Record<CI,RI>>();
		}
		
		/**
		 * can be used by upper application Layer
		 * Sax parser does not use it.
		 */
		public CI info;

		
		/**
		 * says if this level can be parsed
		 * Important; this method works only
		 * if inc/dec/updateHistory() is done before correctly
		 * i.e. oneOrMore: ( 1. inc(), 2. [dec()] update() )
		 * Because size of history cache, the xml depth is limited to Long.BYTES*8 (=64)  
		 * @param currentDepth
		 * @return
		 */
		private boolean isMatch()
		{
			int targetDepth = xmlSearchPattern.length-1;
			return matchHistory == 0 && currentDepth == targetDepth;
		}
		
		/**
		 * focus to next deeper level of xml depth
		 */
		private void incrementHistory()
		{
			currentDepth ++;
			matchHistory = (matchHistory << 1);
			
			if (currentDepth >=xmlSearchPattern.length)
				matchHistory = matchHistory | 1; //set error bit if out of range
		}
		/**
		 * focus back to prev upper level of xml depth
		 */		
		private void decrementHistory()
		{
			currentDepth--;
			matchHistory = (matchHistory >> 1);
		}
		
		/**
		 * check if current depth is a possible fetch tree. It sets
		 * true/false to the current focused history stack
		 * The history keeps true if the whole walkthrough until target xml leaf tag is match
		 * @param currentDepth
		 * @param tagName can be a "name" or fine search "name#attribName=value" this can help to distinguish same tags using their attributes
		 * @param tagAttributes
		 */
		private void updateHistory(String tagName, Attributes attributes)
		{
			if (currentDepth < xmlSearchPattern.length)
			{
				String tmp = ""+xmlSearchPattern[currentDepth]; //get tag
				String[] fine = tmp.split("(#|=)"); //if xml struct reached this can help to distinguish same tags using their attributes
				if (fine[0].equals(tagName) && 
						(fine.length==1||(""+attributes.getValue(fine[1])).equals(fine[2])))
					matchHistory = matchHistory & -2; //clear lsb (=match)
				else
					matchHistory = matchHistory | 1; //set lsb (= no match)
			}

		}


	}

//	public static <CI,RI> void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
//		
//		//inputdata
//		Context<CI,RI> target0 = SaxParser.createSearchContext("root/a", null);
//		Context<CI,RI> target1 = SaxParser.createSearchContext("root/a/b", target0); 
//		Context<CI,RI> target2 = SaxParser.createSearchContext("root/a/b/c", target1); 
//		String filename = "data/test.xml";
//		Context<CI,RI>[] ctx = (Context<CI,RI>[]) new Context[]{target0,target1,target2};
//		
//	
//		RecordHandler<CI,RI> rh = new RecordHandler<CI,RI>() {			
//			@Override
//			public void foundParent(Record<CI,RI> r) {
//
//				System.out.println("************* Parent "+r.name+" **************\nID "+r.attr.getProperty("??")+"\nText "+r.text);
//				for (Record<CI,RI> c : r.filteredChildren)
//				{
//					System.out.println("============ child "+c.name+" ==============\n\tID "+c.attr.getProperty("url")+"\n\tText "+c.text);
//					if (c.filteredChildren!=null)
//						for (Record<CI,RI> c3:c.filteredChildren)
//							System.out.println("----------- child "+c3.name+" ---------------\n\t\tID "+c3.attr.getProperty("id")+"\n\t\tText "+c3.text);	
//				
//				}
//				System.out.println("**************");
//			}
//	
//		};
//		
//				
//		SaxParser<CI,RI> parser = new SaxParser<CI,RI>(rh);
//		parser.parse(ctx, filename);
//	
//	}
}

//a Test xml for main method

//<root>
//<a>
//	<b url="url12"><c id="id1"></c><c id="id2"></c></b>
//	<b url="url3">p3
//		<c id="id3">3</c>part2
//	</b>
//</a>
//
//<d>
//	<b>
//		<c>no1</c>
//	</b>
//</d>
//
//<b>
//	<c>no2</c>
//	<c>no3</c>
//</b>
//<a>
//	<b url="url4">p4
//		<c id="id4">4</c>
//	</b>
//</a>
//<a>some 5 text
//	<b url="url5">p5</b>
//</a>
//
//</root>
