/**
 * 
 */
package tweet_server;
import twitter4j.GeoLocation;
import java.util.ArrayList;
import java.time.LocalTime;

/**
 * @author Piyali Mukherjee pm2678@columbia.edu
 *
 */
public class markerObjects {

	
	public class Coordinates {
		double latitude = 0.0;
		double longitude = 0.0;
		public Coordinates(double x, double y){
			latitude = x;
			longitude = y;
		}
	}
	
	public class tweet_info {
		Coordinates place =  null;
		LocalTime	time_stamp;		//We will store the time stamp so that the server can clean the data basis age of the tweet
		public tweet_info(double x, double y, LocalTime creation_time) {
			place = new Coordinates(x,y);
			time_stamp = creation_time;
		}
	}
	
	String hashtag;
	ArrayList<tweet_info> tweets = null;	//Using an array list here to add each tweet as we get it
	
	public markerObjects(String given_tag){	//Constructor
		hashtag = given_tag;
		tweets = new ArrayList<tweet_info>(75);	//We allocate default 75 spaces
	}
	
	public void putLocation(GeoLocation x){
		LocalTime time_now = LocalTime.now();
		tweet_info a_tweet = new tweet_info(x.getLatitude(), x.getLongitude(), time_now);
		this.tweets.add(a_tweet);
	}
	
	public String get_tag(){
		return this.hashtag;
	}
	
	public int get_size(){
		return this.tweets.size();
	}
	
	public double get_lat(int i) {
		return(this.tweets.get(i).place.latitude);
	}

	public double get_long(int i) {
		return(this.tweets.get(i).place.longitude);
	}
	
	public LocalTime get_time(int i) {
		return(this.tweets.get(i).time_stamp);
	}

}
