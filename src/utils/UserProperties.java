package utils;

import java.io.InputStream;
import java.util.Properties;

public class UserProperties {

	private static Properties props = new Properties();

	static {
		try {
			InputStream in = UserProperties.class.getResourceAsStream("/user.properties");
			props.load(in);
			in.close();
			putProperties();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private static String loginWay;

	private static String account;

	private static String password;

	private static void putProperties() {
		loginWay = props.getProperty("loginWay");
		account = props.getProperty("account");
		password = props.getProperty("password");
	}

	public static String getLoginWay() {
		return loginWay;
	}

	public static void setLoginWay(String loginWay) {
		UserProperties.loginWay = loginWay;
	}

	public static String getAccount() {
		return account;
	}

	public static void setAccount(String account) {
		UserProperties.account = account;
	}

	public static String getPassword() {
		return password;
	}

	public static void setPassword(String password) {
		UserProperties.password = password;
	}

}