package il.ac.huji.todolist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

public class TwitterHelper extends AsyncTask<Void, Void, Void> {

	private final int TWEETS_NUMBER = 100;

	TodoDAL todoDAL;
	Context context;
	// Cursor cursor;
	private ProgressDialog dialog;
	private ArrayList<TodoItem> latestTweets;

	public TwitterHelper(TodoDAL todoDAL, Context context) {
		this.todoDAL = todoDAL;
		latestTweets = new ArrayList<TodoItem>();
		this.context = context;
	}

	/* shows the processing dialog */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		dialog = new ProgressDialog(context);
		dialog.setMessage("Searching for new tweets...");
		dialog.show();

	}

	/*
	 * Fetches the 100 latest tweets with given hashtag (or less). Eliminates
	 * the tweets that already exist in the list. Returns a list of with all the
	 * new tweets that are not in the list. Note: the is no point in looking for
	 * tweets that are older than the ones in the list, they are not "relevant".
	 */
	@Override
	protected Void doInBackground(Void... params) {

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		// default hashtag todoapp
		String tweetHashtag = prefs.getString("hashtag", "todoapp");
		this.latestTweets = new ArrayList<TodoItem>();
		URL searchUrl = null;
		try {
			searchUrl = new URL("http://search.twitter.com/search.json?q=%23"
					+ tweetHashtag + "&rpp=" + this.TWEETS_NUMBER);
			//connecting to twitter and reading input stream.
			BufferedReader br = new BufferedReader(new InputStreamReader(
					searchUrl.openConnection().getInputStream()));
			String result = "";
			String inputLine;
			while ((inputLine = br.readLine()) != null) {
				result += inputLine;

			}
			JSONObject json = new JSONObject(result);
			long currMaxTweetId = json.getLong("max_id");
			long prevMaxTweetId = this.todoDAL.getMaxTweet(tweetHashtag);
			/*
			 * if prevMaxTweetId>=currMaxTweetId that means that there are no
			 * new tweets that haven't been prompted in previous launches.
			 * Therefor the asyncTask will return without proccessing the
			 * tweets.
			 */
			if (prevMaxTweetId >= currMaxTweetId)
				return null;
			JSONArray arr = json.getJSONArray("results");
			for (int i = 0; i < TWEETS_NUMBER && i < arr.length(); i++) {
				JSONObject tweet = arr.getJSONObject(i);
				TodoItem tweetItem = new TodoItem(tweet.getString("text"),
						tweet.getLong("id"));
				// if current tweetId is greater than previous max tweetId and
				// not exist
				// in the list, add the tweet to tweetsList.
				if (prevMaxTweetId < tweetItem.getTweetId()
						&& this.todoDAL.tweetExist(tweetItem.getTweetId()) == false) {
					latestTweets.add(tweetItem);
				}

			}

			// if hashtag is not in the database yet, insert the hashtag with
			// current max tweetId
			if (prevMaxTweetId == -1)
				todoDAL.insertHashtagToTweetsTable(tweetHashtag, currMaxTweetId);
			// Hashtag exists in the db, but the max tweetId needs to be
			// updated.
			else
				todoDAL.updateHashtagToTweetsTable(tweetHashtag, currMaxTweetId);
		} catch (MalformedURLException e) {
			return null;

		} catch (IOException ioe) {
			return null;

		} catch (JSONException ej) {
			return null;

		}
		return null;
	}

	/*
	 * Prompts a window that asks the user to add the tweets to the todolist. If
	 * user clicks yes - adds the new tweets to the list. If clicks no, nothing
	 * happends. Note: I didn't pass the tweets list between doInBackground and
	 * onPostExecute because in this function there is a callback function that
	 * can read only class variable and not local (latestTweets).
	 */
	public void promtWindow() {
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					// Yes button clicked
					// Add the new tweets to the list
					for (TodoItem tweet : latestTweets) {
						todoDAL.insert(tweet);
					}
					break;

				case DialogInterface.BUTTON_NEGATIVE:
					// No button clicked
					// Do nothing. return.
					break;
				}
			}
		};
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Twitter updates");
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		String tweetHashtag = prefs.getString("hashtag", "todoapp");
		builder.setMessage(
				"There are " + latestTweets.size()
						+ " new tweets with hashtag #" + tweetHashtag
						+ ".\nDo you want to add them as new tasks?")
				.setPositiveButton("Yes", dialogClickListener)
				.setNegativeButton("No", dialogClickListener).show();

	}

	/*
	 * After fetching the newest tweets, this function is called. Checks if there are
	 * any new tweets, and if so calls prompWindow function
	 * that asks the user to add them to the list
	 */
	@Override
	protected void onPostExecute(Void nullValue) {
		dialog.dismiss();
		if (latestTweets.size() > 0) {
			promtWindow();
		}
	}

}
