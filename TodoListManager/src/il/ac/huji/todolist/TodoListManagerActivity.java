package il.ac.huji.todolist;

import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;

public class TodoListManagerActivity extends Activity {

	TodoDAL todoDAL;
	public SQLiteDatabase db;
	public Cursor cursor;
	private ListView list;
	public ToDoListAdapter cursorAdapter;
	private final String[] fields = { DBHelper.ID_COLUMN,
			DBHelper.TITLE_COLUMN, DBHelper.DUE_COLUMN };
	private final String[] from = new String[] { DBHelper.TITLE_COLUMN,
			DBHelper.DUE_COLUMN };
	private final int[] to = new int[] { R.id.txtTodoTitle, R.id.txtTodoDueDate };
	private TwitterHelper twitterHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		todoDAL = new TodoDAL(this);
		db = todoDAL.getDB();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_list_manager);
		list = (ListView) findViewById(R.id.lstTodoItems);
		setAdapter();
		registerForContextMenu(list);

	}

	public Cursor getCursor() {

		return this.cursor;
	}

	// Executes twitter updates.
	@Override
	protected void onStart() {

		super.onStart();
		twitterHelper = new TwitterHelper(todoDAL, this);
		twitterHelper.execute();
	}

	private void setAdapter() {

		cursor = db.query(DBHelper.TABLE_NAME, fields, null, null, null, null,
				null);
		cursorAdapter = new ToDoListAdapter(this, R.layout.row_item, cursor,
				from, to);
		list.setAdapter(cursorAdapter);

	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo info) {
		super.onCreateContextMenu(menu, v, info);
		getMenuInflater().inflate(R.menu.context_menu, menu);
		AdapterContextMenuInfo infoAdapter = (AdapterContextMenuInfo) info;
		String taskTitle = (cursorAdapter.getItem(infoAdapter.position))
				.getTitle();
		menu.setHeaderTitle(taskTitle);
		if (taskTitle.startsWith("Call ")) {
			MenuItem call = (MenuItem) menu.findItem(R.id.menuItemCall);
			call.setTitle(taskTitle);
		} else {
			menu.removeItem(R.id.menuItemCall);

		}

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		int selectedItemIndex = info.position;
		switch (item.getItemId()) {
		case R.id.menuItemDelete:
			this.todoDAL.delete(cursorAdapter.getItem(selectedItemIndex));
			cursor.requery();
			cursorAdapter.notifyDataSetChanged();
			break;
		case R.id.menuItemCall:
			String title = (String) item.getTitle();
			String number = title.split("Call ")[1];
			Intent call = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
					+ number));
			startActivity(call);
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.todo_list_manager, menu);

		return true;
	}

	@Override
	protected void onActivityResult(int reqCode, int resCode, Intent data) {

		if (resCode == RESULT_OK && reqCode == 42) {
			String title = data.getStringExtra("title");
			Date dueDate = (Date) data.getSerializableExtra("dueDate");
			ITodoItem todoItem = new TodoItem(title, dueDate);
			this.todoDAL.insert(todoItem);
			cursor.requery();
			cursorAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuItemAdd:
			Intent intent = new Intent(this, AddNewTodoItemActivity.class);
			startActivityForResult(intent, 42);

			break;
		case R.id.menuItemShowPref:
			Intent prefIntent = new Intent(this, PrefsActivity.class);
			startActivity(prefIntent);
		}
		return true;
	}
}
