package il.ac.huji.todolist;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

//public class ToDoListAdapter extends ArrayAdapter<Item> {
@SuppressLint("NewApi")
public class ToDoListAdapter extends SimpleCursorAdapter {
	Cursor c;
	Context context;
	Activity activity;

	public ToDoListAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		// super(context, android.R.layout.simple_list_item_1);
		super(context, R.layout.row_item, c, from, to,0);
        this.c = c;
        this.context=context;
        this.activity=(Activity) context;
        
        
	}
	public ITodoItem getItem(int position){
		System.out.println("in get item. position:"+position+" c count:"+c.getCount());
		c.moveToPosition(position);
		ITodoItem item = new Item(c.getString(1),new Date(c.getLong(2)));
		return item;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {	
		 if(convertView == null)
	            convertView = View.inflate(context, R.layout.row_item, null);
	        View view = convertView;
	        System.out.println("in get view position :"+position);
	        c.moveToPosition(position);

	        TextView title = (TextView) convertView.findViewById(R.id.txtTodoTitle);
	        TextView date = (TextView) convertView.findViewById(R.id.txtTodoDueDate);
	        Date dueDate = new Date (c.getLong(2));
	        title.setText(c.getString(1));
	        //System.out.println("title:"+c.getString(1)+ " duedate:"+dueDate);
	      //  dueDate.setText(convertedDate.);
	        
	     
	/*	Item item = (Item) getItem(position);
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View view = inflater.inflate(R.layout.row_item, null);
		TextView title = (TextView) view.findViewById(R.id.txtTodoTitle);
		String titleText = item.getTitle();
		title.setText(titleText);
		TextView date = (TextView) view.findViewById(R.id.txtTodoDueDate);
		Date dueDate = new Date(item.getDate());
		*/
		if (c.getLong(1) == -1) { 
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
