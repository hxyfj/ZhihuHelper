package entity;

public class LoginResult {
	/** 登陆状态码 */
	private String r;
	/** 错误信息 */
	private errReason data;

	public String getR() {
		return r;
	}

	public void setR(String r) {
		this.r = r;
	}

	public errReason getData() {
		return data;
	}

	public void setData(errReason data) {
		this.data = data;
	}

	public class errReason {

		private String account;
		private String password;

		public String getAccount() {
			return account;
		}

		public void setAccount(String account) {
			this.account = account;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

	}
}
