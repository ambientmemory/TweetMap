import twitter4j.GeoLocation;

import java.util.*;
import java.io.*;

import org.apache.commons.lang3.*;

public class TweetObject {
	String username;
	String content;
	long tweetId;
	GeoLocation geolocation;
	ArrayList<String> keywords = new ArrayList<String>();
	int entry_count = 0;

	TweetObject(String usr, long ID, String said, GeoLocation where){
		username = usr;
		tweetId = ID;
		content = said;
		geolocation = where;
		contentParser(content);
	}

	public void contentParser(String content){
		/**
		 * This method takes in the text of the string and attempts to extract
		 * three keywords from the string. The strategy is as follows: 1)
		 * Highest priority given to hashtags 2) Next highest priority is given
		 * to capitalized words in the sentence that are not the first word
		 * (proper noun sorting)
		 */
		
		String[] temp = content.split("\\s"); // find all words
		for (String x : temp) {
			// The Hashtag test
			if (StringUtils.startsWith(x, "#")
					&& (StringUtils.isAsciiPrintable(x))) {
				keywords.add(x);
			} else if ((StringUtils.isAllUpperCase(x) && (x.length() > 5))
					|| (x.length() > 5)
					&& (!x.contains("@") && (!StringUtils.startsWith(x, "http") && (!x
							.contains("?") && (!x.contains("..") && (StringUtils
							.isAsciiPrintable(x))))))) {
				/** We found a word that is only caps and has more than three letters in it
				* And does not contain "@" as that is used primarily for retweets and mentions
				* We are also not adding URLS as keywords
				 * **/
				keywords.add(x);
			}
		}
		System.out.println("Debug: The keywords in this tweet are: "+ keywords.toString());
		System.out.println("Location of tweets: Lat: "+geolocation.getLatitude()+", Long: "+geolocation.getLongitude());
			
	}
}

