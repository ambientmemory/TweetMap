import twitter4j.GeoLocation;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.*;
import java.util.ArrayList;
//import org.apache.commons.lang3.StringUtils;
/** <p>This is a code example of Twitter4J Streaming API - sample method support.<br>
 * Usage: java twitter4j.examples.PrintSampleStream<br>
 * </p>
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
public final class TweetGet {
    /**
	 * TODO: Merge TweetObject with This beast
	 * @param args
	 * @throws TwitterException
	 */
	static String[] my_keywords = { "birthday", "Justin", "Wales", "March", "life" };
	
	static ArrayList<GeoLocation> word_zero = new ArrayList<GeoLocation>();
	static ArrayList<GeoLocation> word_one = new ArrayList<GeoLocation>();
	static ArrayList<GeoLocation> word_two = new ArrayList<GeoLocation>();
	static ArrayList<GeoLocation> word_three = new ArrayList<GeoLocation>();
	static ArrayList<GeoLocation> word_four = new ArrayList<GeoLocation>();
	
	private static final Object lock = new Object();
	static boolean must_die = false; //Keep track of stream state
	
    public static void main(String[] args) throws TwitterException {
    	//just fill this
    	 ConfigurationBuilder cb = new ConfigurationBuilder();
         cb.setDebugEnabled(true)
           .setOAuthConsumerKey("ca69SsUNt8hFo4qy21F3E22pC")
           .setOAuthConsumerSecret("5Nd8vNgMxccSBxVACCtD9d21FPVVgWeyHrato4sIEZG8u55CpJ")
           .setOAuthAccessToken("2875358986-ptBvVr2iUr5h9fP3iyay0uthWTkT9RwaK9VhR6K")
           .setOAuthAccessTokenSecret("lTDRSfkSDwBdyrWPhedfeR7dX0N1OWOdJgw91XWgb7Mxx");
         
        TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
        
        StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
            	try{
            		for( int i = 0; i < my_keywords.length; i++){
                		if((status.getText().contains(my_keywords[i])) && (!status.getGeoLocation().equals(null))){
                			//System.out.println("Debug: Found keyword: "+my_keywords[i]+" in "+status.getText());
                			tiny_indexer(i).add(status.getGeoLocation());
                			System.out.println("Debug: Tweet repository: "+my_keywords[i]+" now contains "+tiny_indexer(i).size()+" tweets");
                			//perform a database occupancy check: do we have enough tweets to render?
                		}
                	}
                	if(parse_termination()){
                		//must_die = true;
                		synchronized (lock){
                			lock.notify();
                		}
                	}
            	}catch(NullPointerException e){
            		//do nothing. 
            	}
            	
            	//System.exit(0);
            }
            

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                //System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                //System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                //System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }

            @Override
            public void onStallWarning(StallWarning warning) {
                //System.out.println("Got stall warning:" + warning);
            }

            @Override
            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        };
        
        /**
         * The following code is used to filter the location and keyword specific tags from the general stream. 
         */
        
        FilterQuery locationFilter = new FilterQuery();
        double[][] locations = {{-180.0, -90.0}, {180.0, 90.0}};
        locationFilter.locations(locations);
        
        FilterQuery keywordFilter = new FilterQuery();
        keywordFilter.track(my_keywords);
        
        twitterStream.addListener(listener);
        twitterStream.filter(keywordFilter);
        
        try{
        	synchronized(lock) {
        		lock.wait();
        	}
        }catch (InterruptedException e){
        	e.printStackTrace();
        }
        System.out.println("Debug: Terminating stream");
        twitterStream.shutdown();
        debug_printer();
        
    }//end of main
    
    public static ArrayList<GeoLocation> tiny_indexer(int x) {
		if (x == 0)
			return word_zero;
		else if (x == 1)
			return word_one;
		else if (x == 2)
			return word_two;
		else if (x == 3)
			return word_three;
		else
			return word_four;
	}
    
    public static boolean parse_termination() {
		if ((tiny_indexer(0).size() >= 15)
				|| (tiny_indexer(1).size() >= 15)
				|| (tiny_indexer(2).size() >= 15)
				|| (tiny_indexer(3).size() >= 15)
				|| (tiny_indexer(4).size() >= 15)) {
			return true;
		}
		else return false;
	}
    
    public static void debug_printer(){
    	System.out.println("Debug: word_zero contains"+word_zero.size()+" tweets.");
    	System.out.println(word_zero.toString());
    	System.out.println("Debug: word_one contains"+word_one.size()+" tweets.");
    	System.out.println(word_one.toString());
    	System.out.println("Debug: word_two contains"+word_two.size()+" tweets.");
    	System.out.println(word_two.toString());
    	System.out.println("Debug: word_three contains"+word_three.size()+" tweets.");
    	System.out.println(word_three.toString());
    	System.out.println("Debug: word_four contains"+word_four.size()+" tweets.");
    	System.out.println(word_four.toString());
    }
}//end of TweetGet class


