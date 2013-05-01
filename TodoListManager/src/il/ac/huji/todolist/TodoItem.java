package il.ac.huji.todolist;

import java.util.Date;

public class TodoItem implements ITodoItem {
	public String title;
	public Date dueDate;
	boolean isTweet;
	// the tweet id in case of a tweet.
	long tweetId;

	public TodoItem(String title, Date dueDate) {
		this.title = title;
		this.dueDate = dueDate;
		this.isTweet = false;
		this.tweetId = -1;
	}

	public TodoItem(String title, long tweetId) {
		this.title = title;
		this.dueDate = null;
		this.isTweet = true;
		this.tweetId = tweetId;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public Date getDueDate() {
		return dueDate;
	}
	@Override
	public boolean isTweet() {
		return this.isTweet;
	}
	@Override
	public long getTweetId() {
		return this.tweetId;
	}
}
