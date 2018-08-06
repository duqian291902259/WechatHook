# WechatHook-Dusan
Enjoy hooking wechat by Xposed...Accessibility...and so on

开发微信辅助工具，聊天机器人，自动抢红包，自动发朋友圈这样的功能。Xposed可以帮助我们开发很多有趣的插件，难点是要花足够时间研究所要hook的App源码，寻找hook点，看混淆的代码是在太痛苦了，希望该项目对大家有所帮助和启发。

-------- 2018update --------
#### 消息Hook，适配微信6.6.7了，欢迎修bug！待完善ing
-------- 2018update --------

![auto-wechat-dusan.png](http://upload-images.jianshu.io/upload_images/2001922-59bb02f4ed2cfe65.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


## About 

这是一个有趣的项目，探索android流行的黑科技，专注研究和开发wx助手。持续更新，感兴趣的话，请Star or Fork一下吧：

[https://github.com/duqian291902259/WechatHook-Dusan](https://github.com/duqian291902259/WechatHook-Dusan)

本人工具链：Mac，Android Studio，Root的手机，xposed等，业余挖掘技术的魅力。
部分源码未同步，目前可参考以下说明，也可以star源码pull request bugs。

## Features

![wechat-hook-dusan](https://github.com/duqian291902259/WechatHook-Dusan/blob/master/screenshot/wechat-hook-dusan.png)


##### 1，本项目应用了xposed框架，实现wx hook，可以自动智能回复聊天信息，实现聊天机器人的功能，所以需要root权限。

##### 2，如果没有root权限，本项目进行了一些有趣尝试。应用android辅助功能，自动添加附近的人为好友，自动打招呼，自动抢红包等。

##### 3，修改wx的位置信息，发朋友圈可以装逼了，出国so easy，内置google地图，需要翻墙哈。

##### 4，可以刷跳一跳，小游戏原来可以这样操作，但需要Root权限，99分！

#### 5，Wechat Hook，技术限制了我们更多的想象。。。

![wenchat-tiaotiao](https://github.com/duqian291902259/WechatHook-Dusan/blob/master/screenshot/wenchat-tiaotiao.png)


## Tips
hook 某个app，都是针对某个版本进行的，因为需要反编译apk，分析源码查找hook点，才能改变原有app的执行逻辑或者改变输入输出值，本项目开发比较早，支持早期微信版本为：6.3.32。新版本6.6.7只是hook了会话列表和群发消息，如果要开发，可以自行探索或加我交流，相信你可以从代码中找到启发。

打开下列链接，下载apk测试：
##### 1，安装release app，用下面链接提供的打包好了的app。

##### 2，已经root过的手机一台，要安装好xposed框架，并加载本项目的WechatHook模块到xposed，重启生效。

##### 3，安装weixin，该项目只是针对特定版本哈。

##### 4，如果微信底部tab，“通讯录”改成了“杜小菜”，发现里面的“扫一扫”变成了“duqian2010@gmail.com"表示hook微信成功了。

[https://github.com/duqian291902259/WechatHook-Dusan/tree/master/hook_release](https://github.com/duqian291902259/WechatHook-Dusan/tree/master/hook_release)

## Future

正在基于wechat6.6.7以及后期版本，尝试开发更多功能：

1，通讯录模块 

2，消息管理（收发消息，群发图片，视频，小程序）

3，红包模块 

4，群管理 (建群，群成员管理，群发消息)

5，朋友圈模块  

6，数据库本地数据处理

7，个人信息模块

8，自动收款，二维码 

9，自动确认加好友 

10，检测微信是否存在 

11，一键加好友。

12，订单管理

and so on...基于新版wechat开发的以上functions，暂时未加入本项目。

[website------>   http://www.duqian.site](http://www.duqian.site)

[CSDN博客-->  http://blog.csdn.net/dzsw0117](http://blog.csdn.net/dzsw0117)

欢迎交流：
duqian2010@gmail.com ,杜小菜，wechat：dusan2010


