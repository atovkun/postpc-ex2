package il.ac.huji.todolist;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

public class TodoListManagerActivity extends Activity {

	private ArrayAdapter<String> adapter;
	ListView list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		adapter = new ToDoListAdapter(this);
		setContentView(R.layout.activity_todo_list_manager);
		list = (ListView) findViewById(R.id.lstTodoItems);
		list.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_todo_list_manager, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuItemAdd:
			EditText newItem = (EditText) findViewById(R.id.edtNewItem);
			if (newItem.getText().toString() != "")
				adapter.add(new String(newItem.getText().toString()));
			newItem.setText("");
			break;
		case R.id.menuItemDelete:

			if (list.getSelectedItemPosition() != -1) {
				adapter.remove(list.getSelectedItem().toString());
			}
			break;
		}
		return true;
	}
}
