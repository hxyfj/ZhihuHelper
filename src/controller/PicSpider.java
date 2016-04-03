package controller;

import java.util.Scanner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import utils.HelperUtil;

/**
 * 爬取用户指定问题下的所有图片 答案中的图片和头像图片将分别保存在两个文件夹中
 */
public class PicSpider {

	private HttpClient httpClient;

	public PicSpider() {
		httpClient = HttpClients.createDefault();
		// 抓取问题下的所有图片
		getQuestionPic();
		System.out.println("报告:知乎助手已完成任务!");
	}

	public static void main(String[] args) {
		// 创建爬虫
		new PicSpider();
	}

	private void getQuestionPic() {
		System.out.println("请输入知乎问题的链接地址,按回车确定");
		Scanner scanner = new Scanner(System.in);
		String url = scanner.nextLine();
		HttpGet httpGet = new HttpGet(url);
		int index = 1;

		try {
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			String html = EntityUtils.toString(entity, "UTF-8");

			Document doc = Jsoup.parse(html);
			// 获取问题标题
			Element titleLink = doc.select("h2.zm-item-title.zm-editable-content").first();
			String title = titleLink.text();
			// 创建文件夹
			HelperUtil.createMainFile();
			HelperUtil.createCollectionFile(title);
			// 下载头像
			Elements links = doc.select("img.zm-list-avatar.avatar");
			for (Element link : links) {
				String src = link.attr("src");
				// 将图片替换为大图片
				src = src.replaceAll("_s", "_l");
				HelperUtil.downPic(title, "avatar", src, index++);
			}
			// 下载答案中的图片
			index = 1;
			// 暂存图片链接,如果新链接与该变量相等,则不进行图片下载,从而达到图片去重功能
			String srcTemp = null;
			links = doc.select("img.origin_image");
			for (Element link : links) {
				String src = link.attr("data-original");
				if (!src.equals(srcTemp)) {
					HelperUtil.downPic(title, "img", src, index++);
					srcTemp = src;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpGet.releaseConnection();
		}
		scanner.close();
	}

}
