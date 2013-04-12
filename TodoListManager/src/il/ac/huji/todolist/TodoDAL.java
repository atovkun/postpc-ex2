package il.ac.huji.todolist;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
	private Cursor cursor;
	public long time;
	

	public TodoDAL(Context context) {
		this.context = context;
		// init parser
		Parse.initialize(context,
				context.getResources().getString(R.string.parseApplication),
				context.getResources().getString(R.string.clientKey));
		ParseUser.enableAutomaticUser();
		ParseACL defaultACL = new ParseACL();
		ParseACL.setDefaultACL(defaultACL, true);
		helper = new DBHelper(context);
		db = helper.getWritableDatabase();
	}

	public SQLiteDatabase getDB() {
		return db;
	}

	public boolean insert(ITodoItem todoItem) {
		insertParse(todoItem);
		return inserDB(todoItem);
	}

	private boolean inserDB(ITodoItem todoItem) {
		try {
			ContentValues values = new ContentValues();
			values.put(DBHelper.TITLE_COLUMN, todoItem.getTitle());
			values.put(DBHelper.DUE_COLUMN, todoItem.getDueDate().getTime());
			db.insert(DBHelper.TABLE_NAME, null, values);
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	private void insertParse(ITodoItem todoItem) {
		ParseObject item = new ParseObject("todo");
		item.put("title", todoItem.getTitle());
		item.put("due", todoItem.getDueDate().getTime());
		item.put("user", ParseUser.getCurrentUser());
		item.setACL(new ParseACL(ParseUser.getCurrentUser()));
		item.saveInBackground();

	}

	public boolean update(ITodoItem todoItem) {

		if (updateItemFromDB(todoItem)) {
			updateItemFromParse(todoItem);
			return true;
		}
		return false;

	}

	public boolean delete(ITodoItem todoItem) {
		deleteItemFromParse(todoItem);
		return deleteItemFromDB(todoItem);
	}

	private void updateItemFromParse(ITodoItem todoItem) {

		time = todoItem.getDueDate().getTime();
		ParseQuery query = new ParseQuery("todo");
		query.whereEqualTo("title", todoItem.getTitle());
		query.findInBackground(new FindCallback() {
			public void done(List<ParseObject> matches, ParseException e) {
				if (e == null) {
					for (int i = 0; i < matches.size(); i++) {
						ParseObject updated=(matches.get(i));
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
		query.whereEqualTo("due", todoItem.getDueDate().getTime());
		query.findInBackground(new FindCallback() {
			public void done(List<ParseObject> matches, ParseException e) {
				if (e == null) {
					for (int i = 0; i < matches.size(); i++) {
						(matches.get(i)).deleteInBackground();
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

	public List<ITodoItem> all() {
		List<ITodoItem> all = new ArrayList<ITodoItem>();
		cursor = db.query(DBHelper.TABLE_NAME, new String[] {
				DBHelper.TITLE_COLUMN, DBHelper.DUE_COLUMN }, null, null, null,
				null, null);

		if (cursor.moveToFirst()) {
			do {
				all.add(new Item(cursor.getString(0), new Date(cursor
						.getLong(1))));

			} while (cursor.moveToNext());
		}
		return all;
	}

}
