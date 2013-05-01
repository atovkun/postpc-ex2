package il.ac.huji.todolist;

public class TweetItem {
	private long id;
	private String text;
	public TweetItem(long id,String text){
		this.id = id;
		this.text = text;
	}
	public long getId(){
		return id;
	}
	public String getText(){
		
		return text;
	}
	
}
