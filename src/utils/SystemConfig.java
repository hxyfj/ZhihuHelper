package utils;

import java.io.InputStream;
import java.util.Properties;

public class SystemConfig {

	private static Properties props = new Properties();

	static {
		try {
			InputStream in = SystemConfig.class.getResourceAsStream("/system.properties");
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

	private static String filePath;

	private static void putProperties() {
		loginWay = props.getProperty("loginWay");
		account = props.getProperty("account");
		password = props.getProperty("password");
		filePath = props.getProperty("filePath");
	}

	public static String getLoginWay() {
		return loginWay;
	}

	public static void setLoginWay(String loginWay) {
		SystemConfig.loginWay = loginWay;
	}

	public static String getAccount() {
		return account;
	}

	public static void setAccount(String account) {
		SystemConfig.account = account;
	}

	public static String getPassword() {
		return password;
	}

	public static void setPassword(String password) {
		SystemConfig.password = password;
	}

	public static String getFilePath() {
		return filePath;
	}

	public static void setFilePath(String filePath) {
		SystemConfig.filePath = filePath;
	}

}