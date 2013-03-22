package il.ac.huji.todolist;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

public class AddNewTodoItemActivity extends Activity {

	private Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_new_item);
		// intent = getIntent();
		Button btnOK = (Button) findViewById(R.id.btnOK);
		btnOK.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				sendResultOK();
			}
		});
		Button btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				sendResultCancel();
			}
		});

	}

	public void sendResultOK() {
		DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);
		int day = datePicker.getDayOfMonth();
		int month = datePicker.getMonth();
		int year = datePicker.getYear();
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day);
		Intent result = new Intent();
		EditText title = (EditText) findViewById(R.id.edtNewItem);
		result.putExtra("dueDate", calendar.getTime());
		//result.putExtra("dueDate", (Date)null);
		result.putExtra("title", title.getText().toString());
		setResult(RESULT_OK, result);
		finish();
	}

	public void sendResultCancel() {

		Intent result = new Intent();
		setResult(RESULT_CANCELED, result);
		finish();
	}

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_todo_list_manager, menu);

		return true;
	}*/

}