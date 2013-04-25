package il.ac.huji.todolist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	public static String TABLE_NAME = "todo";
	public static String DB_NAME = "todo_db";
	public static String ID_COLUMN = "_id";
	public static String DUE_COLUMN = "due";
	public static String TITLE_COLUMN = "title";
	public static String TWITTER_ID_COLUMN = "tweet_id";
	public static String TWITTER_TABLE_NAME = "tweets_table";
	public static String MAX_TWEET_COLUMN = "max_tweet";
	public static String HASHTAG_COLUMN = "hashtag";
	
	
	public DBHelper(Context context) {
		super(context, DB_NAME, null, 1);

	}

	/*
	 * Updated db that include thumbnail text field and tweetId field. the tweet
	 * text is the title field. if not a tweet (regular task), the tweet_id
	 * field will be -1
	 */
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL("create table " + TABLE_NAME + " ( "
				+ " _id integer primary key autoincrement, " + DUE_COLUMN
				+ " long, " + TITLE_COLUMN + " text, " + TWITTER_ID_COLUMN
				+ " long );");
		db.execSQL("create table " +TWITTER_TABLE_NAME + " ( "
				+ " _id integer primary key autoincrement, "+  HASHTAG_COLUMN  + " text, " +  MAX_TWEET_COLUMN
				+ " long );");
	}

	public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + TWITTER_TABLE_NAME);
		onCreate(db);
	}
}