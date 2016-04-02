package entity;

/**
 * 收藏夹中的提问
 * 对于提问的回答,只读取用户收藏的那个回答
 */
public class Question {

	private String title;

	private String url;

	private String answer;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	@Override
	public String toString() {
		return "Question:\r\n" + "title=" + title + ";\r\n" + "url=" + url + ";\r\n" + "answer=" + answer;
	}

}
