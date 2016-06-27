package utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 获取图片URL
 */
public class GetPicThread extends Thread {

	private List<String> srcs;
		
	private String id;
	
	private String type;
	
	public static boolean isLive = true;
		
	public GetPicThread(List<String> srcs, String id, String type) {
		this.srcs = srcs;
		this.id = id;
		this.type = type;
	}

	@Override
	public void run() {
		String moreData;
		HttpClient httpClient = HttpClients.createDefault();
		int offset = 0;
		Document doc;

		HttpPost httpPost = new HttpPost("https://www.zhihu.com/node/QuestionAnswerListV2");
		List<NameValuePair> params = new ArrayList<NameValuePair>();


		params.add(new BasicNameValuePair("method", "next"));
		try {
			do {
				params.add(new BasicNameValuePair("params",
						"{\"url_token\":" + id + ",\"pagesize\":20,\"offset\":" + offset + "}"));
				httpPost.setEntity(new UrlEncodedFormEntity(params));
				HttpResponse response = httpClient.execute(httpPost);
				HttpEntity entity = response.getEntity();
				moreData = EntityUtils.toString(entity, "UTF-8");
				
				doc = Jsoup.parse(HelperUtil.convertUnicode(moreData));			
				
				String srcTemp = null;
				// 下载头像
				if ("avatar".equals(type)) {
					Elements links = doc.select("img.zm-list-avatar.avatar");
					for (Element link : links) {
						String src = link.attr("src");
						// 将图片替换为大图片
						src = src.replaceAll("_s", "_l");
						if (!src.equals(srcTemp)) {
							srcs.add(src);
							srcTemp = src;
							HelperUtil.addSize();
						}
					}
				} else {
					// 暂存图片链接,如果新链接与该变量相等,则不进行图片下载,从而达到图片去重功能
					Elements links = doc.select("img.origin_image");
					for (Element link : links) {
						String src = link.attr("data-original");
						if (!src.equals(srcTemp)) {
							srcs.add(src);
							srcTemp = src;
							HelperUtil.addSize();
						}
						
					}
				}
				
				offset += 20;
			} while (moreData.indexOf("\"msg\": []") == -1);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			isLive = false;
		}
	}

}
