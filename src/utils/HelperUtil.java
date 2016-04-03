package utils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

import entity.Question;

public class HelperUtil {

	/**
	 * 创建收藏夹对应的文件夹
	 */
	public static void createFile(String collectionTitle) {
		try {
			// 创建主文件夹
			File file = new File("d:\\zhihu");
			// 如果文件夹不存在,则创建文件夹
			if (!file.exists() && !file.isDirectory()) {
				file.mkdir();
			}
			// 创建收藏夹文件夹
			file = new File("d:\\zhihu\\" + collectionTitle);
			if (!file.exists() && !file.isDirectory()) {
				file.mkdir();
			}
			System.out.println("收藏夹文件创建成功:" + collectionTitle);
			System.out.println("开始下载保存收藏夹下的提问");
			System.out.println();
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
			File file = new File("d:\\zhihu\\" + collectionTitle + "\\" + questionTitle + ".txt");
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
	public static void downPic(String collectionTitle, String questionTitle, String url, int index) {
		try {
			// 创建问题对应的图片文件夹
			File file = new File("d:\\zhihu\\" + collectionTitle + "\\" + questionTitle);
			if (!file.exists() && !file.isDirectory()) {
				file.mkdir();
			}
			file = new File("d:\\zhihu\\" + collectionTitle + "\\" + questionTitle + "\\" + index + ".jpg");
			FileOutputStream fos = new FileOutputStream(file);
			DataInputStream dis = new DataInputStream((new URL(url)).openStream());
			byte[] buffer = new byte[1024];
			int length;
			// 开始填充数据
			while ((length = dis.read(buffer)) > 0) {
				fos.write(buffer, 0, length);
			}
			System.out.println("正在下载图片:" + questionTitle + ",图片代号" + index);
			dis.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 处理文件名中的非法字符
	 * 文件名不能含有\/:*?"<>|
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
}
