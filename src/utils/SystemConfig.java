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

	private static String userId;

	private static String loginWay;

	private static String account;

	private static String password;

	private static String filePath;

	private static void putProperties() {
		userId = props.getProperty("userId");
		loginWay = props.getProperty("loginWay");
		account = props.getProperty("account");
		password = props.getProperty("password");
		filePath = props.getProperty("filePath");
	}

	public static String getUserId() {
		return userId;
	}

	public static String getLoginWay() {
		return loginWay;
	}

	public static String getAccount() {
		return account;
	}

	public static String getPassword() {
		return password;
	}

	public static String getFilePath() {
		return filePath;
	}

}