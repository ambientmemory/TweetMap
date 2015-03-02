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
import java.io.*;

/**
 * <p> This is a code example of Twitter4J Streaming API - sample method support.<br>
 * Usage: java twitter4j.examples.PrintSampleStream<br>
 * </p>
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
public final class TweetGet {

	static String[] my_keywords = { "birthday", "Justin", "Wales", "March",
			"life" };

	static ArrayList<GeoLocation> word_zero = new ArrayList<GeoLocation>();
	static ArrayList<GeoLocation> word_one = new ArrayList<GeoLocation>();
	static ArrayList<GeoLocation> word_two = new ArrayList<GeoLocation>();
	static ArrayList<GeoLocation> word_three = new ArrayList<GeoLocation>();
	static ArrayList<GeoLocation> word_four = new ArrayList<GeoLocation>();

	private static final Object lock = new Object();
	
	/**
	 * The TweetGet main performs the following operations.
	 * 1) It establishes a twitter stream using the user credentials and
	 * the Twitter4j libraries. 
	 * 2) It filters tweets with a certain criteria: they match the keywords
	 * or their geotags are not null. If a tweet satisfies both conditions, 
	 * it is placed into its respective ArrayList<Geolocation>.
	 * A small function called tiny_indexer has been used to maintain 
	 * correspondence between which arraylist is associated with which member
	 * of the keywords array.  
	 * 3) Once the arraylists reach a certain size (prototype: 15), the stream
	 * is terminated and the relevant keywords associated with the tweets as 
	 * well as their geographic co-ordinates are written out to a .txt file in
	 * the src folder of this project. 
	 * @param args
	 * @throws TwitterException
	 * @throws IOException
	 */
	
	public static void main(String[] args) throws TwitterException, IOException {
		// just fill this
		ConfigurationBuilder cb = new ConfigurationBuilder();
		//establishin user credentials
		cb.setDebugEnabled(true)
				.setOAuthConsumerKey("ca69SsUNt8hFo4qy21F3E22pC")
				.setOAuthConsumerSecret(
						"5Nd8vNgMxccSBxVACCtD9d21FPVVgWeyHrato4sIEZG8u55CpJ")
				.setOAuthAccessToken(
						"2875358986-ptBvVr2iUr5h9fP3iyay0uthWTkT9RwaK9VhR6K")
				.setOAuthAccessTokenSecret(
						"lTDRSfkSDwBdyrWPhedfeR7dX0N1OWOdJgw91XWgb7Mxx");

		TwitterStream twitterStream = new TwitterStreamFactory(cb.build())
				.getInstance();

		StatusListener listener = new StatusListener() {
			@Override
			public void onStatus(Status status) {
				try {
					for (int i = 0; i < my_keywords.length; i++) {
						//We want to collect tweets that only contain the keyword
						//and that also contain valid geolocation tags. 
						if ((status.getText().contains(my_keywords[i]))
								&& (!status.getGeoLocation().equals(null))) {
							tiny_indexer(i).add(status.getGeoLocation());
							System.out.println("Debug: Tweet repository: "
									+ my_keywords[i] + " now contains "
									+ tiny_indexer(i).size() + " tweets");
						}
					}
					/**
					 * We must now test if the latest additions to the respective array
					 * lists have resulted in any one of them reaching capacity. 
					 * If so, then we terminate the stream. Otherwise, we continue
					 * reading tweets from the stream. 
					 */
					if (parse_termination()) {
						//The following code is used to establish a block request to the stream. 
						synchronized (lock) {
							lock.notify();
						}
					}
				} catch (NullPointerException e) {
					/**This exception clause exists only so that Geolocation with null data content
					* are not sent through to the if() statement. Without this clause, the code
					* throws NullPointerExceptions even before the code checks for valid Geolocations.
					* Usually, a continue or discard_current_tweet() operation would have been performed
					* But since this is a stream, we don't have to do anything about that. 
					**/
				}
			}
			
			/**
			 * The following functions are required to be implemented from the definition
			 * of the Tweet Stream Status Listener class. 
			 */
			@Override
			public void onDeletionNotice(
					StatusDeletionNotice statusDeletionNotice) {}

			@Override
			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}

			@Override
			public void onScrubGeo(long userId, long upToStatusId) {}

			@Override
			public void onStallWarning(StallWarning warning) {}

			@Override
			public void onException(Exception ex) {
				ex.printStackTrace();
			}
		};

		/**
		 * The following code is used to filter the location and keyword
		 * specific tags from the general stream.
		 */

		FilterQuery locationFilter = new FilterQuery();
		double[][] locations = { { -180.0, -90.0 }, { 180.0, 90.0 } };
		locationFilter.locations(locations);

		FilterQuery keywordFilter = new FilterQuery();
		keywordFilter.track(my_keywords);
		
		//add the listener to the twitter stream to enable filtering 
		twitterStream.addListener(listener);
		twitterStream.filter(keywordFilter);
		
		//Check if we have are still receiving input from
		//the stream. 
		try {
			synchronized (lock) {
				lock.wait();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//This part of the code is executed only after the listener reaches the termination condition
		twitterStream.shutdown();
		debug_printer();

	}// end of main

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

	/**
	 * This method determines if it is time to stop reading tweets
	 * from the tweet stream (i.e. we have acquired enough tweets to draw
	 * the first version of our map). 
	 * @return boolean true if arraylist containing geolocations of
	 * keywords exceeds a certain size. 
	 */
	public static boolean parse_termination() {
		if ((tiny_indexer(0).size() >= 15) || (tiny_indexer(1).size() >= 15)
				|| (tiny_indexer(2).size() >= 15)
				|| (tiny_indexer(3).size() >= 15)
				|| (tiny_indexer(4).size() >= 15)) {
			return true;
		} else
			return false;
	}

	public static void debug_printer() throws IOException,
			FileNotFoundException {
		PrintWriter writer = new PrintWriter("draw_db.txt");
		writer.println("Keyword"+"\t"+"Latitude"+"\t"+"Longitude");
		for (int i = 0; i < my_keywords.length; i++) {
			for( int j = 0; j < tiny_indexer(i).size(); j++){
				writer.println(my_keywords[i]+"\t"+tiny_indexer(i).get(j).getLatitude()+"\t"+tiny_indexer(i).get(j).getLongitude());
			}
		}
		writer.close();
	}
}// end of TweetGet class

