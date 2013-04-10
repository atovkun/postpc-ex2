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
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class TodoDAL {
	private DBHelper helper;
	public SQLiteDatabase db;
	private Context context;
	private Cursor cursor;
	private ToDoListAdapter adapter;
	public long time;

	public TodoDAL(Context context) {
		this.context = context;
		// init parser
		Parse.initialize(context,
				context.getResources().getString(R.string.parseApplication),
				context.getResources().getString(R.string.clientKey));

		helper = new DBHelper(context);
		db = helper.getWritableDatabase();
		adapter = null;
	}

	public SQLiteDatabase getDB() {
		return db;
	}

	public void setAdapter(ToDoListAdapter adapter) {
		this.adapter = adapter;
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
		ParseObject student = new ParseObject("todo");
		student.put("title", todoItem.getTitle());
		student.put("due", todoItem.getDueDate().getTime());
		student.saveInBackground();

	}

	public boolean update(ITodoItem todoItem) {
		System.out.println("in update:"+todoItem.getTitle()+" "+todoItem.getDueDate());
		if(updateItemFromDB(todoItem)){
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
		System.out.println("in updateItemFromParse:"+todoItem.getTitle()+" "+todoItem.getDueDate());
		ParseQuery query = new ParseQuery("todo");
		query.whereEqualTo("title", todoItem.getTitle());
		time = todoItem.getDueDate().getTime();
		query.findInBackground(new FindCallback() {
			public void done(List<ParseObject> matches, ParseException e) {
				if (e != null) {

					for (ParseObject obj : matches) {
						System.out.println("FOUND MATCH: updating date to:"+ new Date (time));
						obj.put("due", time);
						obj.put("cheatMode", true);
						obj.saveInBackground();
					}
				} else {

				}
			}
		});

	}

	private void deleteItemFromParse(ITodoItem todoItem) {

		ParseQuery query = new ParseQuery("todo");
		query.whereEqualTo("title", todoItem.getTitle());
		System.out.println("In deleteItemFromParse: item - "
				+ todoItem.getTitle() + " due - "
				+ todoItem.getDueDate().getTime());
		query.whereEqualTo("due", todoItem.getDueDate().getTime());
		query.findInBackground(new FindCallback() {
			public void done(List<ParseObject> matches, ParseException e) {
				System.out.println("DONE:" + matches.size());
				if (e != null) {
					for (ParseObject obj : matches) {
						System.out.println("found:"+obj.getString("title"));
						obj.deleteInBackground();

					}
				} else {
					System.err.println("error:" + e);
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
		Cursor allCursor = db.query(DBHelper.TABLE_NAME, new String[] { "*" },
				null, null, null, null, null);
		if (allCursor.moveToFirst()) {
			do {
				ITodoItem item = new Item(allCursor.getString(1), new Date(
						allCursor.getLong(2)));
				all.add(item);
			} while (allCursor.moveToNext());
		}
		return all;
	}

}
