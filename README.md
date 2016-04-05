# 知乎助手
### 项目简介
爬取用户收藏,指定用户收藏,指定问题下的图片头像等各种信息并下载到本地

### 玩法推荐
知乎作为一个可以花式骗图的平台,骗图的提问数不胜数.

比如[生活中你见过最美的女生长什么样？](https://www.zhihu.com/question/28202126/answer/47733159 "生活中你见过最美的女生长什么样？")

[你见过最美的汉服照是什么样的？](https://www.zhihu.com/question/40792901 "你见过最美的汉服照是什么样的？")

![效果图1](http://myhebut.oss-cn-hangzhou.aliyuncs.com/github%2Fhanfu.png "你见过最美的汉服照是什么样的？")

[你最喜欢的一张壁纸图片是哪一张？](https://www.zhihu.com/question/21904576 "你最喜欢的一张壁纸图片是哪一张？")

![效果图2](http://myhebut.oss-cn-hangzhou.aliyuncs.com/github%2Fbizhi.png "你最喜欢的一张壁纸图片是哪一张？")

使用知乎助手,一键下载所有图源,想象是不是棒极了~

### 项目结构
项目中使用到的第三方库jar包:
* HttpClient -用于网络请求爬取数据
* Jsoup -用于解析html
* Gson -用于解析json数据  

### 使用方法
###### 爬取指定问题下的所有回答者的头像以及答案中包含的图片(推荐)
1. 将项目下载到本地,eclipse直接导入项目
2. 运行controller包下的PicSpider,知乎助手开始工作
3. 知乎助手默认在d:\\zhihu路径下以问题题目为标题创建文件夹,并下载保存头像以及图片

###### 爬取指定用户的收藏夹的信息
1. 将项目下载到本地,eclipse直接导入项目
2. 修改system.properties的内容,根据提示填写用户id
3. 运行controller包下的CollectionSpider,知乎助手开始工作
4. 知乎助手默认在d:\\zhihu路径下以用户id为标题创建文件夹,以用户的各个收藏夹为命名新建子文件夹,收藏夹相应的收藏提问会以txt的形式保存在对应的子文件夹目录下

###### 爬取自己的收藏夹的信息(因为私密收藏夹仅自己可见,所以这里需要登陆)
1. 将项目下载到本地,eclipse直接导入项目
2. 修改system.properties的内容,根据提示写入登陆方式,登入账号,账号密码
3. 运行controller包下的CollectionSpider,知乎助手开始工作
4. 知乎助手默认在d:\\zhihu路径下以用户id为标题创建文件夹,以用户的各个收藏夹为命名新建子文件夹,收藏夹相应的收藏提问会以txt的形式保存在对应的子文件夹目录下


