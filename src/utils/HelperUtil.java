package utils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;

import entity.LoginResult;
import entity.Question;

public class HelperUtil {

	public final static String phoneLoginUrl = "https://www.zhihu.com/login/phone_num";

	public final static String emailLoginUrl = "https://www.zhihu.com/login/email";
	// 图标代号
	private static int index = 1;

	private static String filePath;

	/**
	 * 解析登陆返回数据
	 */
	public static void parseData(String html) {
		Gson gson = new Gson();
		LoginResult loginResult = gson.fromJson(html, LoginResult.class);
		if (loginResult.getR().equals("0")) {
			System.out.println("登陆成功,知乎助手开始工作!");
		} else {
			if (loginResult.getData().getAccount() != null) {
				System.out.println("登陆失败:" + HelperUtil.convertUnicode(loginResult.getData().getAccount()));
			} else {
				System.out.println("登陆失败:" + HelperUtil.convertUnicode(loginResult.getData().getPassword()));
			}
			System.exit(0);
		}
	}

	/**
	 * 替换answer中的html标签 为\r\n 处理a标签和img标签 删除所有的<>html标签 img处理
	 */
	public static String parseAnswer(String collectionTitle, String questionTitle, String answer) {
		Document doc = Jsoup.parse(answer);
		// 处理答案中包含的超链接
		Elements links = doc.select("a.wrap.external");
		for (Element link : links) {
			String href = link.attr("src");
			String text = link.text();
			// 正则表达式若出现括号没有成对的情况会报错,为了避免这个错误,将小括号替换为尖括号
			text = text.replaceAll("\\(", "<");
			text = text.replaceAll("\\)", ">");
			answer = answer.replaceAll(text, "(" + text + ",超链接至: " + href + " )");
		}
		// 处理答案中包含的图片
		links = doc.select("img");
		// 暂存当前代号,下载图片后回复到当前代号
		int tempIndex = index;
		List<String> srcs = new ArrayList<>();
		for (Element link : links) {
			String src = link.attr("src");
			srcs.add(src);
		}
		for (int i = 0; i < srcs.size(); i++) {
			HelperUtil.downPic(collectionTitle, questionTitle, srcs.get(i), srcs.size());
		}

		index = tempIndex;
		Pattern p = Pattern.compile("(<img.*?>)");
		Matcher m = p.matcher(answer);
		while (m.find()) {
			answer = answer.replaceAll("\\(", "<");
			answer = answer.replaceAll("\\)", ">");
			answer = answer.replaceAll(m.group(1), "(这里是一张图片哦~代号为" + (index++) + ",已保存在当前目录下");
		}
		answer = answer.replaceAll("<br>", "\r\n");
		answer = answer.replaceAll("</p>", "\r\n");
		answer = answer.replaceAll("<.*?>", "");
		return answer;
	}

	/**
	 * 创建主文件夹
	 */
	public static void createMainFile() {
		try {
			filePath = SystemConfig.getFilePath();
			// 创建主文件夹
			File file = new File(filePath);
			// 如果文件夹不存在,则创建文件夹
			if (!file.exists() && !file.isDirectory()) {
				file.mkdir();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 创建用户文件夹
	 */
	public static void createUserFile(String userId) {
		try {
			// 创建主文件夹
			createMainFile();
			// 将filePath定位到用户文件夹下
			filePath = filePath + "\\" + userId;
			// 创建收藏夹文件夹
			File file = new File(filePath);
			if (!file.exists() && !file.isDirectory()) {
				file.mkdir();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 创建收藏夹对应的文件夹
	 */
	public static void createCollectionFile(String fileName) {
		try {
			// 创建收藏夹文件夹
			File file = new File(filePath + "\\" + fileName);
			if (!file.exists() && !file.isDirectory()) {
				file.mkdir();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 写入文件
	 */
	public static void writeQuestion(String collectionTitle, Question question) {
		try {
			String questionTitle = question.getTitle();
			File file = new File(filePath + "\\" + collectionTitle + "\\" + questionTitle + ".txt");
			if (!file.exists())
				file.createNewFile();
			// 因为同一个提问可能收藏了多个回答,所以写入方式为追加
			FileOutputStream out = new FileOutputStream(file, true);
			// 注意需要转换对应的字符集
			out.write(("----------------网页链接:" + question.getUrl() + "----------------\r\n").getBytes());
			out.write(("----------------收藏的回答内容如下----------------\r\n\r\n" + question.getAnswer() + "\r\n\r\n")
					.getBytes());
			out.close();

			System.out.println("知乎回答下载保存成功:" + questionTitle);
			System.out.println();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 下载图片
	 */
	public static void downPic(String fileDir1, String fileDir2, String url, int size) {
		try {
			// 创建问题对应的图片文件夹
			File file = new File(filePath + "\\" + fileDir1 + "\\" + fileDir2);
			if (!file.exists() && !file.isDirectory()) {
				file.mkdir();
			}
			// 线程安全性
			file = getSyncFile(fileDir1, fileDir2, size);

			FileOutputStream fos = new FileOutputStream(file);
			DataInputStream dis = new DataInputStream((new URL(url)).openStream());
			byte[] buffer = new byte[1024];
			int length;
			// 开始填充数据
			while ((length = dis.read(buffer)) > 0) {
				fos.write(buffer, 0, length);
			}
			dis.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized static File getSyncFile(String fileDir1, String fileDir2, int size) {
		if (fileDir2.equals("avatar")) {
			System.out.println("正在下载第" + index + "张头像,共" + size + "个头像");
		} else {
			System.out.println("正在下载第" + index + "张图片,共" + size + "张图片");
		}
		return new File(filePath + "\\" + fileDir1 + "\\" + fileDir2 + "\\" + (index++) + ".jpg");
	}

	/**
	 * 处理文件名中的非法字符 文件名不能含有\/:*?"<>|
	 */
	public static String handleFileName(String fileName) {
		fileName = fileName.replaceAll("\\\\|/|:|\\*|\\?|\"|<|>", " ");
		return fileName;
	}

	/**
	 * Unicode转中文
	 */
	public static String convertUnicode(String ori) {
		char aChar;
		int len = ori.length();
		StringBuffer outBuffer = new StringBuffer(len);
		for (int x = 0; x < len;) {
			aChar = ori.charAt(x++);
			if (aChar == '\\') {
				aChar = ori.charAt(x++);
				if (aChar == 'u') {
					// Read the xxxx
					int value = 0;
					for (int i = 0; i < 4; i++) {
						aChar = ori.charAt(x++);
						switch (aChar) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							value = (value << 4) + aChar - '0';
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							value = (value << 4) + 10 + aChar - 'a';
							break;
						case 'A':
						case 'B':
						case 'C':
						case 'D':
						case 'E':
						case 'F':
							value = (value << 4) + 10 + aChar - 'A';
							break;
						default:
							throw new IllegalArgumentException("Malformed   \\uxxxx   encoding.");
						}
					}
					outBuffer.append((char) value);
				} else {
					if (aChar == 't')
						aChar = '\t';
					else if (aChar == 'r')
						aChar = '\r';
					else if (aChar == 'n')
						aChar = '\n';
					else if (aChar == 'f')
						aChar = '\f';
					outBuffer.append(aChar);
				}
			} else
				outBuffer.append(aChar);

		}
		return outBuffer.toString();
	}

	public static void initIndex() {
		index = 1;
	}
}
