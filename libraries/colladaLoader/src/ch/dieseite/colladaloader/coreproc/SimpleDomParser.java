package ch.dieseite.colladaloader.coreproc;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
/**
* <p>This source is free; you can redistribute it and/or modify it under
* the terms of the GNU General Public License and by nameing of the originally author</p>
*
* @author Markus Zimmermann <a href="http://www.die-seite.ch">http://www.die-seite.ch</a>
* @version 3.1
*/
public class SimpleDomParser {

	
	public Element loadXMLDataFromJar(String filename) throws ParserConfigurationException, SAXException, IOException
	{
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    InputStream is = getClass().getResourceAsStream("/ch/dieseite/colladaloader/coreproc/"+filename); //read from jar
	    Document document = builder.parse(is);
	    is.close();
		Element xmlDoc = new Element(document.getFirstChild());
    	parseNode(document.getFirstChild(),xmlDoc);

	    return xmlDoc;
	}
	public Element loadXMLDataFromFile(String filename) throws ParserConfigurationException, SAXException, IOException
	{
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    Document document = builder.parse(filename);

		Element xmlDoc = new Element(document.getFirstChild());
    	parseNode(document.getFirstChild(),xmlDoc);

	    return xmlDoc;
	}

	/**
	 * reads all elements and children recursively
	 * @param current
	 * @param parent
	 */
	private void parseNode(Node current, Element parent)
	{
		
		if (current.getNodeType()==Node.ELEMENT_NODE)//take elements only
		{
			Element e = new Element(current);
			if (current.hasChildNodes())
			{//falls Array bzw subnodes
				NodeList nl = current.getChildNodes();
			    for (int i=0; i< nl.getLength();i++)
			    {
			    	parseNode(nl.item(i),e);
			    }
			}

			parent.addChild(e);
			
		}
	}
	
	
	public static class Element
	{
		private  ArrayList<Element> allchilds;
		private HashMap<String, ArrayList<Element>> childgroups;

		private Node node;


		public Element(Node n) {
			allchilds = new ArrayList<SimpleDomParser.Element>();
			childgroups = new HashMap<String, ArrayList<Element>>();
			node = n;
			
		}
		
		private void addChild(Element e)
		{
			if (e!=null)
			{
				String cldnm = e.node.getNodeName();
				ArrayList<Element> grp = childgroups.get(cldnm);
				if (grp == null)
				{
					grp = new ArrayList<SimpleDomParser.Element>();
					childgroups.put(cldnm, grp);
				}
		
				grp.add(e);
				allchilds.add(e);
			}
		}
		public String getText(){return node.getTextContent();}
		public String getName(){return node.getNodeName();}

		/**
		 * returns elements in natural order
		 */
		public List<Element> getChildren(){return allchilds;}
		/**
		 * returns named elements in natural order
		 */
		public List<Element> getChildrenByName(String n)
		{
			ArrayList<Element> e = childgroups.get(n);
			return (e==null)?new ArrayList<Element>():e;
		}
		public String getAttrVal(String key){
			Node n =node.getAttributes().getNamedItem(key);
			return (n==null)?null:n.getTextContent();
		}
		
		
	}

}
