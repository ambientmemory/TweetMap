import de.fhpotsdam.unfolding.*;
import de.fhpotsdam.unfolding.marker.*;
import de.fhpotsdam.unfolding.data.*;
import de.fhpotsdam.unfolding.events.*;
import de.fhpotsdam.unfolding.geo.*;
import de.fhpotsdam.unfolding.providers.*;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.mapdisplay.*;
import de.fhpotsdam.unfolding.interactions.*;
import processing.core.*;

import java.util.*; 
import java.io.*;

public class Map_render extends PApplet {
	
	static ArrayList<SimplePointMarker> markers_to_draw = new ArrayList<SimplePointMarker>();
	UnfoldingMap map;
	static String filename = ""; 
	
	/**
	 * This method lays out the canvas before the rendering 
	 * of the map. It establishes canvas size, mouse action event listeners
	 * and marker locations. 
	 */
	public void setup(){
		size(800,800); //setting the map size
		
		map = new UnfoldingMap(this);
		//adding mouse controls over hover and zoom on map. 
		MapUtils.createDefaultEventDispatcher(this, map);
		
		//We now populate the Location markers we are going to show on our screen
		populateMap();
		
		/**Location berlinLocation = new Location(52.5, 13.4);
		 * Location dublinLocation = new Location(53.5, -6.26);
		 * SimplePointMarker berlinMarker = new SimplePointMarker(berlinLocation);
		 * SimplePointMarker dublinMarker = new SimplePointMarker(dublinLocation);
		 */
		for( SimplePointMarker x : markers_to_draw){
			map.addMarkers(x);
		}
		
	}
	
	/**
	 * Required to begin rendering on the screen. 
	 */
	public void draw(){
		map.draw();
	}
	
	/**
	 * This function reads in the .txt database file as created by TweetGet.java and
	 * then  parses it's contents for keywords and their associated geotags. 
	 * TODO: assign keyword relevance (draw larger dots for more relevant keywords, or show
	 * a legend and all keywords of the same color)
	 */
	public void populateMap(){
		String line;
		try{
			//Begins reading in the file
			FileReader scan = new FileReader(filename);
			BufferedReader in = new BufferedReader(scan);
			while((line = in.readLine()) != null){
				System.out.println("Debug: current line read in is: "+line);
				String[] temp = line.split("\\t");
				System.out.println("Debug: Read in coordinates "+ temp[1]+","+temp[2]);
				//Parsing valid co-ordinates into the ArrayList of map markers. 
				markers_to_draw.add(new SimplePointMarker
						(new Location(Double.parseDouble(temp[1]), Double.parseDouble(temp[2]))));
				
			}
			System.out.println("Debug: read in data successfully");
			scan.close();
		} catch(FileNotFoundException e){
			System.out.println("Debug: can't find the file");
			//System.err.println("Error file not found!");
			//e.printStackTrace();
		}catch( IOException e){
			//do nothing
		}
		
	}
	
}
