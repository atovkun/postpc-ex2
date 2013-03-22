package il.ac.huji.todolist;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ToDoListAdapter extends ArrayAdapter<Item> {

	public ToDoListAdapter(Context context) {
		super(context, android.R.layout.simple_list_item_1);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {	
		Item item = (Item) getItem(position);
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View view = inflater.inflate(R.layout.row_item, null);
		TextView title = (TextView) view.findViewById(R.id.txtTodoTitle);
		String titleText = item.getTitle();
		title.setText(titleText);
		TextView date = (TextView) view.findViewById(R.id.txtTodoDueDate);
		Date dueDate = item.getDate();
		
		if (dueDate == null) { 
			date.setText("No due date");
			date.setTextColor(Color.BLACK);
			title.setTextColor(Color.BLACK);
		} else {
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			date.setText(df.format(dueDate));
			Date today = new Date();
			if (today.after(dueDate)) {
				date.setTextColor(Color.RED);
				title.setTextColor(Color.RED);
			}
			else{
				date.setTextColor(Color.BLACK);
				title.setTextColor(Color.BLACK);
				
			}
		}
		return view;
	}

}
