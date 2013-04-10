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
	public DBHelper(Context context) {
		super(context, DB_NAME, null, 1);
	}

	public void onCreate(SQLiteDatabase db) {
		System.out.println("on create database");
		db.execSQL("create table "+TABLE_NAME+" ( "
				+ " _id integer primary key autoincrement, " + " due long, "
				+ "title string );");
	}

	public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
	    onCreate(db);
	}
}