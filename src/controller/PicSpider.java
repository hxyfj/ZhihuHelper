package controller;

import java.util.ArrayList;
import java.util.List;
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

import utils.DownPicThread;
import utils.GetPicThread;
import utils.HelperUtil;
import utils.SystemConfig;

/**
 * 爬取用户指定问题下的所有图片 答案中的图片和头像图片将分别保存在两个文件夹中
 */
public class PicSpider {

	private HttpClient httpClient;

	public PicSpider() {
		httpClient = HttpClients.createDefault();
		// 抓取问题下的所有图片
		getQuestionPic();
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
		String id = url.substring(url.length() - 8, url.length());

		try {
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();

			System.out.println("功能选择:0为下载用户头像,1为下载答案中的图片");
			String function = scanner.nextLine();
			System.out.println("知乎助手开始工作!");

			String html = EntityUtils.toString(entity, "UTF-8");
			
			Document doc = Jsoup.parse(html);
			// 获取问题标题
			Element titleLink = doc.select("h2.zm-item-title.zm-editable-content").first();
			String title = HelperUtil.handleFileName(titleLink.text());
			// 创建文件夹
			HelperUtil.createMainFile();
			HelperUtil.createCollectionFile(title);
			// 下载头像
			HelperUtil.initIndex();
			List<String> srcs = new ArrayList<>();
			if (function.equals("0")) {
				// 开启多线程爬取更多
				new GetPicThread(srcs, id).start();;
	
				for (int i = 0; i < SystemConfig.getThreadCount(); i++) {
					new DownPicThread(title, "avatar", srcs).start();
				}
			}
			// 下载答案中的图片
			HelperUtil.initIndex();
			srcs = new ArrayList<>();
			if (function.equals("1")) {
				// 开启多线程爬取更多
				new GetPicThread(srcs, id).start();;
	
				for (int i = 0; i < SystemConfig.getThreadCount(); i++) {
					new DownPicThread(title, "image", srcs).start();
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
