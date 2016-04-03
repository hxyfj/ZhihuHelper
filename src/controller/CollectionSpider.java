package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import entity.Collection;
import entity.Question;
import utils.HelperUtil;
import utils.SystemConfig;

/**
 * 爬取用户收藏的所有回答 分类下载保存到本地 注意:一个知乎提问,只爬取用户自己所收藏的答案,其他答案忽略
 */
public class CollectionSpider {

	private HttpClient httpClient;

	private String userId;

	private List<Collection> collections;

	public CollectionSpider(String action) {
		httpClient = HttpClients.createDefault();
		if (action.equals("0")) {
			// 登陆知乎
			loginZhihu();
			// 进入知乎首页获取用户信息
			getUserInfo();
		} else {
			userId = SystemConfig.getUserId();
			System.out.println("知乎助手开始工作!");
		}
		// 获取所有收藏夹信息
		getCollections();
		// 循环读取每个收藏夹收藏的信息
		getQuestions();
		System.out.println("报告:知乎助手已完成任务!");
	}

	public static void main(String[] args) {
		System.out.println("请选择您要执行的功能(0为爬取自己的收藏夹,1为爬取指定用户的收藏夹)");
		Scanner scanner = new Scanner(System.in);
		String action = scanner.nextLine();
		// 当输入的是0或1时才能跳出循环
		while (!action.equals("0") && !action.equals("1")) {
			System.out.println("0为爬取自己的收藏夹,1为爬取指定用户的收藏夹");
			action = scanner.nextLine();
		}
		scanner.close();
		// 创建收藏夹爬虫
		new CollectionSpider(action);
	}

	private void getQuestions() {
		HelperUtil.createUserFile(userId);
		for (int i = 0; i < collections.size(); i++) {
			String collectionTitle = collections.get(i).getTitle();
			HelperUtil.createCollectionFile(collectionTitle);
			System.out.println("收藏夹文件创建成功:" + collectionTitle);
			System.out.println("开始下载保存收藏夹下的提问");
			System.out.println();
			// 收藏的回答保存在相应收藏夹标题命名的文件夹下
			getDetails(collectionTitle, collections.get(i).getUrl());
		}
		// getDetails("test", "https://www.zhihu.com/collection/43767604");
	}

	private void getDetails(String collectionTitle, String url) {
		HttpGet httpGet = new HttpGet(url);
		List<Question> questions = new ArrayList<>();
		// 记录标题,若出现一个问题收藏了多个最佳答案,则第二个以后的答案的标题即为之前记录的标题(知乎只为第一个最佳答案显示标题)
		String titleTemp = null;
		// 图片代号,重新进入一个收藏夹代号要初始化为1
		int index = 1;

		try {
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			String html = EntityUtils.toString(entity, "UTF-8");
			// 获取页码
			Document doc = Jsoup.parse(html);
			Elements links = doc.select("div.zm-invite-pager a");
			String pageString = "1";
			for (Element link : links) {
				String text = link.text();
				// 记录页数
				if (!text.equals("下一页")) {
					pageString = text;
				}
			}
			int page = Integer.parseInt(pageString);
			// 循环爬取每页的信息
			for (int i = 1; i <= page; i++) {
				httpGet = new HttpGet(url + "?page=" + i);
				response = httpClient.execute(httpGet);
				entity = response.getEntity();
				html = EntityUtils.toString(entity, "UTF-8");
				// 获取每页的知乎提问
				doc = Jsoup.parse(html);

				links = doc.select("div.zm-item");
				for (Element link : links) {
					Question question = new Question();

					Element titleLink = link.select("h2.zm-item-title > a").first();
					if (titleLink != null) {
						// 每当一个新的问题时,图片代号需要重新初始化
						index = 1;
						titleTemp = HelperUtil.handleFileName(titleLink.text());
					}
					question.setTitle(titleTemp);
					// System.out.println(titleTemp);
					Element urlLink = link.select("div.zm-item-rich-text").first();
					// 如果收藏的回答被知乎官方要求修改或删除,将获取不到url和answer
					if (urlLink != null) {
						question.setUrl("https://www.zhihu.com" + urlLink.attr("data-entry-url"));
						Element answerLink = urlLink.select("textarea.content.hidden").first();
						// 处理答案并下载答案中包含的图片
						question.setAnswer(
								HelperUtil.parseAnswer(collectionTitle, titleTemp, answerLink.text(), index));
					} else {
						question.setAnswer("该回答暂时不能显示,可能已被知乎官方要求修改或删除");
					}

					questions.add(question);

					HelperUtil.writeQuestion(collectionTitle, question);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpGet.releaseConnection();
		}
		// for (int i = 0; i < questions.size(); i++) {
		// System.out.println(questions.get(i));
		// }
	}

	private void getCollections() {
		String collectionsUrl = "https://www.zhihu.com/people/" + userId + "/collections";

		collections = new ArrayList<>();
		HttpGet httpGet = new HttpGet(collectionsUrl);

		try {
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			String html = EntityUtils.toString(entity, "UTF-8");

			Document doc = Jsoup.parse(html);
			Elements links = doc.select("a.zm-profile-fav-item-title");

			for (Element link : links) {
				Collection collection = new Collection();
				// 删除文件名不能包含的非法字符
				String title = HelperUtil.handleFileName(link.text());
				collection.setTitle(title);
				collection.setUrl("https://www.zhihu.com" + link.attr("href"));
				collections.add(collection);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpGet.releaseConnection();
		}
		// for (int i = 0; i < collections.size(); i++) {
		// System.out.println(collections.get(i));
		// }
	}

	private void getUserInfo() {
		String index = "https://www.zhihu.com";

		HttpGet httpGet = new HttpGet(index);

		try {
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			String html = EntityUtils.toString(entity, "UTF-8");
			// 解析html
			Document doc = Jsoup.parse(html);
			Element link = doc.select("div.top-nav-profile > a").first();
			// href = "people/id",不同用户id不同
			String href = link.attr("href");
			// 截取id
			userId = href.substring(8, href.length());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpGet.releaseConnection();
		}
	}

	private void loginZhihu() {

		String loginUrl = SystemConfig.getLoginWay().equals("0") ? HelperUtil.phoneLoginUrl : HelperUtil.emailLoginUrl;
		String account = SystemConfig.getLoginWay().equals("0") ? "phone_num" : "email";

		HttpPost httpPost = new HttpPost(loginUrl);
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair(account, SystemConfig.getAccount()));
		params.add(new BasicNameValuePair("password", SystemConfig.getPassword()));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(params));
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			String html = EntityUtils.toString(entity, "UTF-8");
			// 解析登陆结果
			HelperUtil.parseData(html);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpPost.releaseConnection();
		}
	}

}
