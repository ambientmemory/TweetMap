/**
 * 
 */
import twitter4j.GeoLocation;
import java.util.*;
/**
 * @author Piyali
 *
 */
public class markerObject {
	
	String keyword;
	ArrayList<Coordinates> places = new ArrayList<Coordinates>();
	//Using an array list here to add each tweet as we get it
	
	/**markerObject(GeoLocation locales, String hashtag){
		places.add(new Coordinates(locales.getLatitude(), locales.getLongitude()));
		keyword = hashtag;
	}**/
	
	markerObject(String hashtag){
		keyword = hashtag;
		//assign a geoLocation extraction function here
	}
	
	public void putLocation(GeoLocation x){
		places.add(new Coordinates(x.getLatitude(), x.getLongitude()));
	}
	
	public String getKeyword(){
		return this.keyword;
	}
	
	public int getCapacity(){
		return places.size();
	}
	
	/**
	 * This method is likely to be called by the Javascript rendering, so we
	 * are passing the list of coordinates in a finalized format.  
	 * @return
	 */
	public Coordinates[] getCoordinates(){
		Coordinates[] result = new Coordinates[places.size()];
		for(int i = 0; i < result.length; i++){
			result[i] = places.get(i);
		}
		return result;
	}
	
	
}


