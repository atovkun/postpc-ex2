package il.ac.huji.todolist;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TodoListManagerActivity extends Activity {

	private ArrayAdapter<Item> adapter;
	ListView list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		adapter = new ToDoListAdapter(this);
		setContentView(R.layout.activity_todo_list_manager);
		list = (ListView) findViewById(R.id.lstTodoItems);
		list.setAdapter(adapter);
		registerForContextMenu(list);

	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo info) {
		super.onCreateContextMenu(menu, v, info);
		getMenuInflater().inflate(R.menu.context_menu, menu);
		menu.setHeaderTitle(((TextView) v.findViewById(R.id.txtTodoTitle))
				.getText());
		TextView task = (TextView) v.findViewById(R.id.txtTodoTitle);
		String taskTitle = (String) task.getText();
		if (taskTitle.contains("Call ")) {
			MenuItem call = (MenuItem) menu.findItem(R.id.menuItemCall);
			call.setTitle(taskTitle);
			call.setVisible(true);
			call.setEnabled(true);

		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		int selectedItemIndex = info.position;
		switch (item.getItemId()) {
		case R.id.menuItemDelete:
			adapter.remove(adapter.getItem(selectedItemIndex));
			break;

		case R.id.menuItemCall:
			String title = (String) item.getTitle();
			String number = "tel:" + title.split("Call ")[1];
			Intent call = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
					+ number));
			startActivity(call);
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_todo_list_manager, menu);

		return true;
	}

	@Override
	protected void onActivityResult(int reqCode, int resCode, Intent data) {

		if (resCode == RESULT_OK && reqCode == 42) {
			String title = data.getStringExtra("title");
			Date dueDate = (Date) data.getSerializableExtra("dueDate");
			System.out.println("title:" + title + " dueDate:"
					+ dueDate.toString());

			adapter.add(new Item(title, dueDate));

		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuItemAdd:
			Intent intent = new Intent(this, AddNewTodoItemActivity.class);
			startActivityForResult(intent, 42);
			/*
			 * EditText newItem = (EditText) findViewById(R.id.edtNewItem); if
			 * (newItem.getText().toString() != "") adapter.add(new
			 * String(newItem.getText().toString())); newItem.setText("");
			 */
			break;
		case R.id.menuItemDelete:
			/*
			 * if (list.getSelectedItemPosition() != -1) {
			 * adapter.remove(list.getSelectedItem().toString()); } break;
			 */
		}
		return true;
	}
}
