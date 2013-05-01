package il.ac.huji.todolist;

import java.util.Date;

public class Item implements ITodoItem{
	public String title;
	public Date dueDate;
	public Item(String title,Date dueDate){
		this.title=title;
		this.dueDate = dueDate;
	}
	@Override
	public String getTitle() {
		return title;
	}
	@Override
	public Date getDueDate() {
		return dueDate;
	}



}
