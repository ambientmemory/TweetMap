import twitter4j.GeoLocation;
import twitter4j.Place;

public class TweetObject {
	String username; 
	String content;
	String profileLocation;
    long tweetId; 
    GeoLocation geolocation;
    String[] keywords;
    double[] coordinates;
    
    
    TweetObject(String usr, String profLoc, long ID, String said, GeoLocation where){
    	username = usr;
    	profileLocation = profLoc;
    	tweetId = ID;
    	content = said;
    	geolocation = where;
    	contentParser(content);
    }
    
    public void contentParser(String content){
    	/**
    	 * This method takes in the text of the string and attempts to extract
    	 * three keywords from the string. The strategy is as follows: 
    	 * 1) Highest priority given to hashtags
    	 * 2) Next highest priority is given to capitalized words in the sentence
    	 * that are not the first word (proper noun sorting)
    	 */
    	System.out.println("Debug: tweet content: "+content);
    }

}
