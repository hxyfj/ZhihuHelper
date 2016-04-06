package utils;

import java.util.List;

/**
 * 下载图片
 */
public class DownPicThread extends Thread {

	private static int threadCount = SystemConfig.getThreadCount();

	private List<String> srcs;

	private String title;

	private String type;

	public DownPicThread(String title, String type, List<String> srcs) {
		this.title = title;
		this.type = type;
		this.srcs = srcs;
	}

	@Override
	public void run() {
		String src = null;
		// flag为true 表明获取到了新的链接
		boolean flag = false;
		while (true) {
			synchronized (srcs) {
				if (srcs.size() == 0 && !GetPicThread.isLive) {
					threadCount--;
					break;
				}
				if (srcs.size() != 0) {
					src = srcs.remove(0);
					flag = true;
				}
			}
			if (flag) {
				HelperUtil.downPic(title, type, src);
				flag = false;
			}
			// 当存在的线程数为1时,表明当前线程为最后一个线程,下载完图片就将结束
			if (threadCount == 1) {
				System.out.println("报告:知乎助手已完成任务!");
			}
		}
	}


}
