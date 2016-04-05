package utils;

import java.util.ArrayList;
import java.util.List;

public class PicThread extends Thread {

	private static int threadCount = SystemConfig.getThreadCount();
	
	private static List<String> srcs = new ArrayList<>();
	
	private String title;
	
	private String type;
	
	public PicThread(String title, String type){
		this.title = title;
		this.type = type;
	}

	@Override
	public void run() {
		String src = null;
		while (true) {
			synchronized (srcs) {
				if (srcs.size() == 0) {
					threadCount--;
					break;
				}
				src = srcs.remove(0);
			}
			HelperUtil.downPic(title, type, src);
			// 当存在的线程数为1时,表明当前线程为最后一个线程,下载完图片就将结束
			if (threadCount == 1) {
				System.out.println("报告:知乎助手已完成任务!");
			}
		}
	}

	public static void setSrcs(List<String> srcs) {
		PicThread.srcs = srcs;
	}
	
}
