package il.ac.huji.todolist;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ToDoListAdapter  extends ArrayAdapter<String>{

	public ToDoListAdapter(Context context) {
		super(context, android.R.layout.simple_list_item_1);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		String item = (String) getItem(position);
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View view = inflater.inflate(R.layout.row_item, null);
		TextView txtName = (TextView)view.findViewById(R.id.txtRowItem);
		
		txtName.setText(item);
		if(position%2==0)
			txtName.setTextColor(Color.RED);
		else
			txtName.setTextColor(Color.BLUE);
		return view;
	}

}
