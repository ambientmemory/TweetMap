package tweet_server;
import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import java.util.TimerTask;
import java.util.concurrent.*;
import java.time.LocalTime;

import twitter4j.TwitterException;


/**
 * @author Piyali Mukherjee pm2678@columbia.edu
 * This is the main server program. This version is implemented without any databases.
 * We implement this server as a public url initiated by the programmer. Once initiated,
 * this servlet has 3 sections - init(), service() and destroy().
 * 
 * We create and initialize the tweet stream, and form the arraylists in the init() section.
 * Then, in the service section we serve the HTTP GET requests for a particular hashtag, and return 
 * the content of the arraylist.
 * The destroy is not explicitly invoked, but contains the code for stopping the tweet stream. 
 */
public class Tweet_map_server extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6249784281980102348L;
	static int count_of_tags = 5;		//This tracks the maximum number of hashtags
	static String[] hash_list = new String[count_of_tags];
	static markerObjects[] hash_data = new markerObjects[count_of_tags];
	static TweetGet tweet_stream = null;
	private ScheduledExecutorService scheduler;
	LocalTime last_cleanup_time = null;
	
	public class remove_old_tweets extends TimerTask {
		@Override
		public void run() {
			//We delete all tweets older than 24 hrs here
			last_cleanup_time = LocalTime.now();
			LocalTime cutoff_time = last_cleanup_time.minusHours(24);
			//LocalTime cutoff_time = LocalTime.now().minusMinutes(30);		//Used for debugging, once done comment and uncomment above
			for (int i = 0 ; i < hash_data.length; i++) {
				int j = hash_data[i].get_size() - 1;	//As indexes in an arraylist start from [0..size-1]
				if (j < 0) continue;					//No entries in this arraylist
				do {
					if (hash_data[i].tweets.get(j).time_stamp.isBefore(cutoff_time)) {
						//System.out.println("Deleting for hashtag : "+hash_data[i].hashtag+" record at locn : "+j+" with time stamp : "+hash_data[i].tweets.get(j).time_stamp);
						hash_data[i].tweets.remove(j);
					}
					j--;
				} while (j >= 0);
			}
		}
	}
	
	public void contextInitialized() {
		scheduler = Executors.newSingleThreadScheduledExecutor();	//Will execute a scheduled task
		//scheduler.scheduleAtFixedRate(new remove_old_tweets(), 0, 30,TimeUnit.MINUTES);		//Used for debugging
		scheduler.scheduleAtFixedRate(new remove_old_tweets(), 0, 24,TimeUnit.HOURS);
	}

	public void contextDestroyed() {
		scheduler.shutdownNow();
	}

	
	@Override
	public void init(ServletConfig conf) throws ServletException {
		super.init(conf);
		//Step 1 : Read the parameters from the web.xml file and build the list of hashtags
		String tag_list = getInitParameter("tag_list");
		hash_list = tag_list.split(",");
		for (int i = 0; i < count_of_tags; i++) {
			hash_data[i] = new markerObjects(hash_list[i]);		//Allocate a markerObjects object - it creates an array list
		}

		//for (int i = 0; i < count_of_tags; i++) {
			//System.out.println("Debug : "+hash_list[i]+"  "+hash_data[i].get_size());
		//}
		//Step 2 : We initialize the tweeter stream object.
		try {
			tweet_stream = new TweetGet(hash_list, hash_data);
		} catch (TwitterException t) {
			t.printStackTrace();
		}
		contextInitialized();		//Set off the timer...
	} //End of init()
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String all_tags = "?";
		String req_hash_tag = req.getParameter("search_hashtag");
		String req_age_string = req.getParameter("max_age");
		//We first handle the two null situations
		int req_age = 0;
		if (req_age_string == null) { 
			//System.out.println("Debug : age given is null"); 
			req_age = 0;
		}
		else req_age = Integer.parseInt(req_age_string);
		if (req_hash_tag == null) req_hash_tag = all_tags;
		//Next we process the "?"
		if (req_hash_tag.equals(all_tags)) {
			//User wants to get the list of all tags
			resp.setContentType("text/plain");
			for (int i = 0; i < hash_list.length; i++) {
				resp.getWriter().println(hash_list[i]);
			}
			resp.getWriter().println("Total count of tags : "+hash_list.length);
			return;
		}
		//This one we keep for debugging enquiry
		if (req_hash_tag.equals("??")) {
			//User wants to get the list of all tags
			resp.setContentType("text/plain");
			for (int i = 0; i < hash_list.length; i++) {
				resp.getWriter().println("Size of hash_tag :"+hash_list[i]+" : "+hash_data[i].get_size());
			}
			resp.getWriter().println("Last Cleanup done at : "+last_cleanup_time);
			return;
		} //End of debugging request
		int hash_index = 0;
		while ((hash_index < hash_list.length) && (!hash_list[hash_index].equals(req_hash_tag))) hash_index++;
		//System.out.println("Debug : The requested hash tag is "+req_hash_tag+" and its index is "+hash_index+" and requested max_age is : "+req_age); 
		resp.setContentType("text/plain");
		//resp.getWriter().println("Debug : Your requested tag is : "+req_hash_tag+" and req age is : "+req_age);
		if (hash_index == count_of_tags) {
			resp.getWriter().println("Your requested tag did not match our database");
			return;
		}
		//We first process the possibility that no tweet on this topic has been received
		int rec_count = 0;		//Tracks count of valid return records
		if (hash_data[hash_index].get_size() == 0) {
			resp.getWriter().println("The total size of the stream is : "+rec_count);
			return;
		}
		//if ((req_age < 0) || (req_age > 30)) req_age = 30;	//Used for debugging
		//LocalTime cutoff_time = LocalTime.now().minusMinutes(req_age);	//Used for debugging, once done comment and uncomment below
		if ((req_age < 0) || (req_age > 24)) req_age = 24;
		LocalTime cutoff_time = LocalTime.now().minusHours(req_age);
		int j = hash_data[hash_index].get_size() - 1;	//As indexes in an arraylist start from [0..size-1]
		do {
			if (hash_data[hash_index].tweets.get(j).time_stamp.isAfter(cutoff_time)) {
				resp.getWriter().println(hash_data[hash_index].get_lat(j)+","+hash_data[hash_index].get_long(j));
				rec_count++;	//Tracks count of records
			}
			j--;
		} while (j >= 0);
		resp.getWriter().println("The total size of the stream is : "+rec_count);
	} //end of doGet
	
	@Override
	public void destroy() {
		tweet_stream.stop_stream();
		contextDestroyed();
		//System.out.println("Streaming stopped...");
	}
}
