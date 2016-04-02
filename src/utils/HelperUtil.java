package utils;

import java.io.File;
import java.io.FileOutputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import entity.Question;

public class HelperUtil {

	/**
	 * 替换answer中的<br>
	 * </p>
	 * 为\r\n 处理a标签和img标签 删除所有的<>html标签 img处理 TODO
	 */
	public static String parseAnswer(String answer) {
		// 处理答案中包含的超链接
		Document doc = Jsoup.parse(answer);
		Elements links = doc.select("a.wrap.external");
		for (Element link : links) {
			String href = link.attr("href");
			String text = link.text();
			// 正则中如果仅有左括号或仅有右括号会出错,为了避免这个错误将小括号转换为中括号
			text = text.replaceAll("\\(", "<");
			text = text.replaceAll("\\)", ">");
			answer = answer.replaceAll(text, "(" + text + ",超链接至: " + href + " )");
		}
		answer = answer.replaceAll("<br>", "\r\n");
		answer = answer.replaceAll("</p>", "\r\n");
		answer = answer.replaceAll("<.*?>", "");
		return answer;
	}

	/**
	 * 创建收藏夹对应的文件夹
	 */
	public static void createFile(String fileName) {
		try {
			// 创建主文件夹
			File file = new File("d:\\zhihu");
			// 如果文件夹不存在,则创建文件夹
			if (!file.exists() && !file.isDirectory()) {
				file.mkdir();
			}
			// 创建收藏夹文件夹
			file = new File("d:\\zhihu\\" + fileName);
			if (!file.exists() && !file.isDirectory()) {
				file.mkdir();
			}
			System.out.println("收藏夹文件创建成功:" + fileName);
			System.out.println("开始保存收藏夹下的提问");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 写入文件
	 */
	public static void writeQuestion(String title, Question question) {
		try {
			// 文件名不能含有\/:*?"<>|,进行处理
			String fileName = handleFileName(question.getTitle());
			File file = new File("d:\\zhihu\\" + title + "\\" + fileName + ".txt");
			if (!file.exists())
				file.createNewFile();
			// 因为同一个提问可能收藏了多个回答,所以写入方式为追加
			FileOutputStream out = new FileOutputStream(file, true);
			// 注意需要转换对应的字符集
			out.write(("----------------网页链接:" + question.getUrl() + "----------------\r\n").getBytes());
			out.write(("----------------收藏的回答内容如下----------------\r\n\r\n" + question.getAnswer() + "\r\n\r\n").getBytes());
			out.close();

			System.out.println("知乎回答保存成功:" + fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 处理文件名中的非法字符
	 */
	public static String handleFileName(String fileName) {
		fileName = fileName.replaceAll("\\\\|/|:|\\*|\\?|\"|<|>", " ");
		return fileName;
	}
}
