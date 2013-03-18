package il.ac.huji.todolist;

import java.util.Date;

public class Item {
	String title;
	Date dueDate;
	public Item(String title,Date dueDate){
		this.title=title;
		this.dueDate = dueDate;
	}
	
	public String getTitle(){
		return title;
	}
	public Date getDate(){
		return dueDate;
	}

}
