package controller;

import java.util.ArrayList;
import java.util.List;

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

public class Helper {

	private HttpClient httpClient;

	private String userId;

	private List<Collection> collections;

	public Helper() {
		httpClient = HttpClients.createDefault();
		// 登陆知乎
		loginZhihu();
		// 进入知乎首页获取用户信息
		getUserInfo();
		// 获取所有收藏夹信息
		getCollections();
		// 循环读取每个收藏夹收藏的信息
		getQuestions();
	}

	public static void main(String[] args) {
		// 创建知乎助手实例
		Helper helper = new Helper();
	}

	private void getQuestions() {
		for (int i = 0; i < collections.size(); i++) {
			String title = collections.get(i).getTitle();
			HelperUtil.createFile(title);
			// 收藏的回答保存在相应收藏夹标题命名的文件夹下
			getDetails(title, collections.get(i).getUrl());
		}
//		getDetails("https://www.zhihu.com/collection/43767303");
	}

	private void getDetails(String title, String url) {
		HttpGet httpGet = new HttpGet(url);
		List<Question> questions = new ArrayList<>();

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
				// 记录标题,若出现一个问题收藏了多个最佳答案,则第二个以后的答案的标题即为之前记录的标题(知乎只为第一个最佳答案显示标题)
				String titleTemp = null;
				for (Element link : links) {
					Question question = new Question();

					Element titleLink = link.select("h2.zm-item-title > a").first();
					if (titleLink != null) {
						titleTemp = titleLink.text();
					} 
					question.setTitle(titleTemp);
//					System.out.println(titleTemp);
					Element urlLink = link.select("div.zm-item-rich-text").first();
					question.setUrl("https://www.zhihu.com" + urlLink.attr("data-entry-url"));
					Element answerLink = urlLink.select("textarea.content.hidden").first();
					question.setAnswer(HelperUtil.parseAnswer(answerLink.text()));

					questions.add(question);
					
					HelperUtil.writeQuestion(title, question);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpGet.releaseConnection();
		}
		System.out.println("报告:知乎助手已完成任务!");
//		for (int i = 0; i < questions.size(); i++) {
//			System.out.println(questions.get(i));
//		}
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
				collection.setTitle(link.text());
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
		String phoneLoginUrl = "https://www.zhihu.com/login/phone_num";
		String eamilLoginUrl = "https://www.zhihu.com/login/email";

		HttpPost httpPost = new HttpPost(eamilLoginUrl);
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("email", "315360570@qq.com"));
		params.add(new BasicNameValuePair("password", "zhihuhelper"));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(params));
			httpClient.execute(httpPost);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpPost.releaseConnection();
		}
	}
}
