import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.*;
import java.io.FileNotFoundException;
import java.io.IOException;
/**
 * <p>This is a code example of Twitter4J Streaming API - sample method support.<br>
 * Usage: java twitter4j.examples.PrintSampleStream<br>
 * </p>
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
public final class TweetGet {
    /**
     * Main entry of this application.
     *
     * @param args
     */
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
                //System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
            	try{
            		TweetObject newTweet = new TweetObject(status.getUser().getScreenName(),
                		status.getId(), status.getText(), status.getGeoLocation());
            	}catch(NullPointerException e){
            		/**
            		 * This catch block exists solely to capture errors in geolocation tags
            		 * so that they are not accidentally parsed into our database. 
            		 */
            		//System.err.println("Geolocation is probably null");
            	}
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
         * The following code is used to filter the location specific tags from the general stream. 
         */
        
        twitterStream.addListener(listener);
        FilterQuery locationFilter = new FilterQuery();
        double[][] locations = {{-180.0, -90.0}, {180.0, 90.0}};
        locationFilter.locations(locations);
        twitterStream.filter(locationFilter);
        
    }
}