package il.ac.huji.todolist;

import java.util.Date;

public interface ITodoItem {
	public String getTitle();

	public Date getDueDate();

	/*
	 * Checks if todolist item used to be a tweet. Returns true if is a tweet,
	 * false otherwise
	 */

	public boolean isTweet();

	/* Returns the tweet Id. If todolist item not a tweet return -1 */
	public long getTweetId();
}
