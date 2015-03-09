/**
 * This is a code example of Twitter4J Streaming API - sample method support.<br>
 * Usage: java twitter4j.examples.PrintSampleStream<br>
 * 
 * @author Yusuke Yamamoto - yusuke@mac.com
 */
package tweet_server;
//import twitter4j.GeoLocation;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.*;


public final class TweetGet {
	/**
	 * The TweetGet performs the following operations.
	 * 1) It establishes a twitter stream using the user credentials and the Twitter4j libraries.
	 * 2) It filters tweets with two criteria: 
	 *    (a) they match the keywords
	 *    (b) their geotags are not null.
	 * If a tweet satisfies both conditions, it is placed into its respective file.
	 * 
	 * @throws TwitterException
	 */
	private static final Object lock = new Object();
	static TwitterStream twitterStream = null;

public TweetGet(String[] hash_list, markerObjects[] hash_data) throws TwitterException {
	//This is the constructor. This initializes the stream establishing user credentials
	ConfigurationBuilder cb = new ConfigurationBuilder();
	
	cb.setDebugEnabled(true)
		.setOAuthConsumerKey("CONS_KEY")
		.setOAuthConsumerSecret(
				"CONS_SECRET")
		.setOAuthAccessToken(
				"ACC_KEY")
		.setOAuthAccessTokenSecret(
				"ACC_SECRET");

	twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
	
	FilterQuery locationFilter = new FilterQuery();
	double[][] locations = { { -180.0, -90.0 }, { 180.0, 90.0 } };
	locationFilter.locations(locations);

	FilterQuery keywordFilter = new FilterQuery();
	keywordFilter.track(hash_list);


	StatusListener listener = new StatusListener() {
	@Override
		public void onStatus(Status status) {
			//This gets called when a tweet arrives...
			//
			try {
				for (int i = 0; i < hash_list.length; i++) {
					// We want to collect tweets that only contain the hashtag and also contain valid geolocation tags.
					if ((status.getText().contains(hash_list[i])) && (!status.getGeoLocation().equals(null))) {
						//System.out.println("@"+status.getUser().getScreenName()+" :"+status.getText());
						hash_data[i].putLocation(status.getGeoLocation());
					} //End of if()
				}
			} catch (NullPointerException e) {
				//System.out.println("Debug : Null pointer on listener");
				/**
				 * This exception clause exists only so that Geolocation with null data content are not sent through to the try{}
				 * Without this clause, the code throws a NullPointerExceptions even before the code checks for valid Geolocations.
				 **/
			}

		}

		/**
		 * The following functions are required to be implemented from the
		 * definition of the Tweet Stream Status Listener class.
		 */
		@Override
		public void onDeletionNotice(
				StatusDeletionNotice statusDeletionNotice) {
		}

		@Override
		public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
		}

		@Override
		public void onScrubGeo(long userId, long upToStatusId) {
		}

		@Override
		public void onStallWarning(StallWarning warning) {
		}

		@Override
		public void onException(Exception ex) {
			ex.printStackTrace();
		}
	}; //End of StatusListener

	/**
	 * The following code is used to filter the location and keyword
	 * specific tags from the general stream.
	 */

	// add the listener to the twitter stream to enable filtering
	twitterStream.addListener(listener);
	twitterStream.filter(keywordFilter);

	//System.out.println("Debug: Reached end of tweet processing.");
	// db_printer();
} // end of constructor TweetGet(String[])

public void stop_stream() {
	// This part of the code is executed only after the listener reaches the termination condition
	twitterStream.shutdown();
	// Check if we have are still receiving input from the stream.
	try {
		synchronized (lock) {
			lock.wait();
		}
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
}
} //End of TweetGet Class
