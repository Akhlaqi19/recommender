package io.recommender.project.forms;
import javax.validation.constraints.Size;

public class Post {
	
	@Size(min=5, max=35)
	private String userName;
	
	@Size(min=4, max= 1000)
	private String password;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}