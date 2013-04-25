package il.ac.huji.todolist;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ClipData.Item;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class TodoDAL {
	private DBHelper helper;
	public SQLiteDatabase db;
	private Context context;
	public long time;

	public TodoDAL(Context context) {

		Parse.initialize(context,
				context.getResources().getString(R.string.parseApplication),
				context.getResources().getString(R.string.clientKey));
		ParseUser.enableAutomaticUser();
		ParseACL defaultACL = new ParseACL();
		ParseACL.setDefaultACL(defaultACL, true);
		helper = new DBHelper(context);
		db = helper.getWritableDatabase();
		this.context = context;
	}

	public SQLiteDatabase getDB() {
		return db;
	}

	public long getMaxTweet(String hashtag) {
		try {
			Cursor tweetCursor = db.query(DBHelper.TWITTER_TABLE_NAME,
					new String[] { DBHelper.MAX_TWEET_COLUMN },
					DBHelper.HASHTAG_COLUMN + "='" + hashtag + "'", null,
					null, null, null);
			if (tweetCursor.getCount() == 0) {
				return -1;
			}
			tweetCursor.moveToFirst();
			return tweetCursor.getLong(0);
		} catch (Exception e) {
		}
		return -1;

	}

	/*
	 * Inserts a new row to twitter hashtags table. Insert the max tweetId for
	 * the given hashtag from the latest twitter update.
	 */

	public void insertHashtagToTweetsTable(String hashtag, long maxTweet) {
		try {
			ContentValues values = new ContentValues();
			values.put(DBHelper.HASHTAG_COLUMN, hashtag);
			values.put(DBHelper.MAX_TWEET_COLUMN, maxTweet);
			db.insert(DBHelper.TWITTER_TABLE_NAME, null, values);
		} catch (Exception e) {
		}
	}

	/*
	 * Updates the max tweetId of an existing hashtag from the latest twitter
	 * update.
	 */
	public void updateHashtagToTweetsTable(String hashtag, long maxTweet) {
		
		try {
			ContentValues values = new ContentValues();
			values.put(DBHelper.MAX_TWEET_COLUMN, maxTweet);
			db.update(DBHelper.TWITTER_TABLE_NAME, values,
					DBHelper.HASHTAG_COLUMN + "='" + hashtag + "'", null);
		} catch (Exception e) {
		}
	}

	public boolean insert(ITodoItem todoItem) {
		insertParse(todoItem);
		boolean result = inserDB(todoItem);
		((TodoListManagerActivity) context).getCursor().requery();
		((TodoListManagerActivity) context).cursorAdapter
				.notifyDataSetChanged();
		return result;
	}

	private boolean inserDB(ITodoItem todoItem) {
		try {
			ContentValues values = new ContentValues();
			values.put(DBHelper.TITLE_COLUMN, todoItem.getTitle());
			if (todoItem.getDueDate() == null)
				values.put(DBHelper.DUE_COLUMN, -1);
			else
				values.put(DBHelper.DUE_COLUMN, todoItem.getDueDate().getTime());
			if (todoItem.isTweet()) {
				values.put(DBHelper.TWITTER_ID_COLUMN, todoItem.getTweetId());
			}

			db.insert(DBHelper.TABLE_NAME, null, values);
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	private void insertParse(ITodoItem todoItem) {
		ParseObject item = new ParseObject("todo");
		item.put("title", todoItem.getTitle());
		if (todoItem.getDueDate() == null)
			item.put("due", -1);
		else
			item.put("due", todoItem.getDueDate().getTime());
		if (todoItem.isTweet())
			item.put("tweet_id", todoItem.getTweetId());
		item.put("user", ParseUser.getCurrentUser());
		item.setACL(new ParseACL(ParseUser.getCurrentUser()));
		item.saveInBackground();

	}

	public boolean update(ITodoItem todoItem) {

		if (updateItemFromDB(todoItem)) {
			updateItemFromParse(todoItem);
			((TodoListManagerActivity) context).getCursor().requery();
			((TodoListManagerActivity) context).cursorAdapter
					.notifyDataSetChanged();
			return true;
		}
		return false;

	}

	public boolean delete(ITodoItem todoItem) {
		deleteItemFromParse(todoItem);
		boolean result = deleteItemFromDB(todoItem);
		((TodoListManagerActivity) context).getCursor().requery();
		((TodoListManagerActivity) context).cursorAdapter
				.notifyDataSetChanged();
		return result;
	}

	private void updateItemFromParse(ITodoItem todoItem) {

		time = todoItem.getDueDate().getTime();
		ParseQuery query = new ParseQuery("todo");
		query.whereEqualTo("title", todoItem.getTitle());
		query.findInBackground(new FindCallback() {
			public void done(List<ParseObject> matches, ParseException e) {
				if (e == null) {
					for (int i = 0; i < matches.size(); i++) {
						ParseObject updated = (matches.get(i));
						updated.put("due", time);
						updated.put("cheatMode", true);
						updated.saveInBackground();
					}
				}
			}
		});

	}

	private void deleteItemFromParse(ITodoItem todoItem) {
		ParseQuery query = new ParseQuery("todo");
		query.whereEqualTo("title", todoItem.getTitle());
		query.findInBackground(new FindCallback() {
			public void done(List<ParseObject> todoList, ParseException e) {
				if (e == null) {
					for (int i = 0; i < todoList.size(); i++) {
						(todoList.get(i)).deleteInBackground();
					}
				}
			}
		});
	}

	private boolean updateItemFromDB(ITodoItem todoItem) {
		try {
			ContentValues values = new ContentValues();
			values.put(DBHelper.DUE_COLUMN, todoItem.getDueDate().getTime());
			db.update(DBHelper.TABLE_NAME, values, DBHelper.TITLE_COLUMN + "='"
					+ todoItem.getTitle() + "'", null);
			return true;
		} catch (Exception e) {

			return false;
		}

	}

	private boolean deleteItemFromDB(ITodoItem todoItem) {
		try {

			db.delete(DBHelper.TABLE_NAME, DBHelper.TITLE_COLUMN + "='"
					+ todoItem.getTitle() + "'", null);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	// Perform a query to search an item with a matching tweetId.
	// If exists return true, else return false;
	public boolean tweetExist(long tweetId) {
		Cursor tweetCursor = db.query(DBHelper.TABLE_NAME,
				new String[] { DBHelper.TWITTER_ID_COLUMN },
				DBHelper.TWITTER_ID_COLUMN + "=" + tweetId, null, null, null,
				null);

		return tweetCursor.getCount() != 0;

	}

	public List<ITodoItem> all() {
		List<ITodoItem> all = new ArrayList<ITodoItem>();
		Cursor allCursor = db.query(DBHelper.TABLE_NAME, new String[] {
				DBHelper.TITLE_COLUMN, DBHelper.DUE_COLUMN }, null, null, null,
				null, null);

		if (allCursor.moveToFirst()) {
			do {
				Date date = new Date(allCursor.getLong(1));
				all.add((ITodoItem) new TodoItem(allCursor.getString(0), date));

			} while (allCursor.moveToNext());
		}
		return all;
	}

}
