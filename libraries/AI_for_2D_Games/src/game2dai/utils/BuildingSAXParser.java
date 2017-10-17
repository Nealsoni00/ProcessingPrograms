package game2dai.utils;

import game2dai.entities.Building;
import game2dai.maths.Vector2D;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import processing.core.PApplet;

public class BuildingSAXParser extends EntitySAXParser {
	

	private static int BASE_LEVEL = 101;
	private static int CONTOUR_LEVEL = 102;
	
	private ArrayList<Building> buildList = new ArrayList<Building>();
	private ArrayList<Vector2D> ctourList = new ArrayList<Vector2D>();
	
	private String name = "";
	private Vector2D pos = null;
	private double x, y;
	private Vector2D[] contour = null;
	private Building[] buildings = null;
	
	private int mode = BASE_LEVEL;
	
	public Building[] get(){
		if(!error) {	
			try {
				saxParser.parse(this.istream, this);
			} catch (SAXException e) {
				error = true;
				Message.println("General SAX parser for file {0}", filename);
			} catch (IOException e) {
				error = true;
				Message.println("IO error for file {0}", filename);
			}
		}
		if(error)
			return new Building[0];
		else
			return buildings;
	}
	
	public void startDocument() throws SAXException {
		Message.println("Parsing document {0}", filename);
	}
	
	public void endDocument() throws SAXException {
		System.out.println("End of document");
	}
	
	public void startElement(String namespaceURI, String localName, 
			String qName, Attributes attr) throws SAXException
	{
		if (qName.equals("buildings")){
			buildList = new ArrayList<Building>();
		}
		else if (qName.equals("building")){
			name = "";
			pos = null;
			x = y = 0;
			contour = null;	
			mode = BASE_LEVEL;
		}
		else if (qName.equals("contour")){
			ctourList.clear();
			mode = CONTOUR_LEVEL;
		}
		else if (qName.equals("pos")){
			x = y = 0;
		}
		else if (qName.equals("point")){
			x = y = 0;
		}
		// Process any attributes
		for (int i = 0 ; i < attr.getLength(); i++){
			storeElementValue(attr.getQName(i), attr.getValue(i));
		}
		buffer.reset();
	}
	
	public void endElement(String namespceURI, String localName, String qName) throws SAXException {
		storeElementValue(qName, buffer.toString());
		if (qName.equals("pos")){
			pos = new Vector2D(x, y);
		}
		else if (qName.equals("point") && mode == CONTOUR_LEVEL){
			ctourList.add(new Vector2D(x,y));
		}
		else if (qName.equals("contour")){
			mode = BASE_LEVEL;
			contour = ctourList.toArray(new Vector2D[ctourList.size()]);
			ctourList.clear();
		}
		else if (qName.equals("building")){
			mode = BASE_LEVEL;
			buildList.add(new Building(name, pos, contour));
		}
		else if (qName.equals("buildings")){
			buildings = buildList.toArray(new Building[buildList.size()]);
			buildList.clear();
		}
	}
	
	public void characters(char[] ch, int start, int length) throws SAXException {
		buffer.write(ch, start, length);
	}

	// This function is called when the closing tag is found
	private void storeElementValue(String elementName, String elementValue){
		if (elementName.equals("x")) {
			x = getDouble(elementValue);
		}
		else if (elementName.equals("y")) {
			y = getDouble(elementValue);
		}
		else if (elementName.equals("name")) {
			name = elementValue;
		}
	}

	/**
	 * @param aFile
	 */
	public BuildingSAXParser(File aFile) {
		super(aFile);
	}

	/**
	 * @param app
	 * @param fname
	 */
	public BuildingSAXParser(PApplet app, String fname) {
		super(app, fname);
	}

	/**
	 * @param fname
	 */
	public BuildingSAXParser(String fname) {
		super(fname);
	}

}
