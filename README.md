# WechatHook-Dusan
Enjoy hooking wechat,by Xposed,Root,Accessibility,and so on...
类似聊天机器人，自动抢红包，自动发朋友圈这样的功能，都可以开发，但是要花足够的时间研究微信源码（反编译后的都是高度混淆的代码，太痛苦了），从我的项目里面，你一定能获取灵感，一起研究吧。

![auto-wechat-dusan.png](http://upload-images.jianshu.io/upload_images/2001922-59bb02f4ed2cfe65.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


## About 

这是一个有趣的项目，探索android流行的黑科技，一起研究和开发各种辅助神器吧。持续更新，感兴趣的话，请Star or Fork一下吧：

[https://github.com/duqian291902259/WechatHook-Dusan](https://github.com/duqian291902259/WechatHook-Dusan)

工具链：Mac，Android Studio，Root的手机，业余挖掘技术的魅力。
上图源码还没有同步上来，目前可以参考我下面的说明和源码。

## Features

![wechat-hook-dusan](https://github.com/duqian291902259/WechatHook-Dusan/blob/master/screenshot/wechat-hook-dusan.png)


1，本项目应用了xposed框架，实现微信hook，可以自动智能回复聊天信息，实现聊天机器人的功能，还可以刷跳一跳分数。所以需要root权限。

强大的hook神器，需要你花时间研究，本项目代码值得你参考；有一台root权限的测试机，是体验黑科技的前提哈。

2，如果没有root权限，本项目依然有针对微信的有趣尝试。应用android辅助功能，自动添加附近的人为好友，自动打招呼，自动抢红包等。

3，修改微信的位置信息，发朋友圈可以装逼了，出国so easy，内置google地图，需要翻墙哈。

4，跳一跳，小游戏原来可以这样操作，满分！

5，Wechat Hook，持续关注，技术限制了我们更多的想象。。。

![wenchat-tiaotiao](https://github.com/duqian291902259/WechatHook-Dusan/blob/master/screenshot/wenchat-tiaotiao.png)


## Tips
hook 某个app，都是针对某个版本进行的，因为需要反编译apk，分析源码查找hook点，才能改变原有app的执行逻辑或者改变输入输出值，本项目开发比较早，支持的微信版本为：6.3.32 和6.5.7,新版微信没有跟进，如果你要实践可以自行探索，相信你可以从代码中找到灵感。

打开下列链接，下载apk测试：
1，安装release app，用下面链接提供的打包好了的app。
2，已经root过的手机一台，要安装好xposed框架，并加载本项目的WechatHook模块到xposed，重启生效。
3，安装：weixin6.3.32.apk。
4，如果微信底部tab，“通讯录”改成了“杜乾”，发现里面的“扫一扫”变成了“duqian2010@gmail.com"表示hook微信成功了。

[https://github.com/duqian291902259/WechatHook-Dusan/tree/master/hook_release](https://github.com/duqian291902259/WechatHook-Dusan/tree/master/hook_release)

## Future

本项目也是学习和借鉴了很多前辈的各个实践经验，感谢前辈。
只有尝试了才能领悟其中的乐趣。越早期的微信，apk越小，代码量越小，相对容易hook。

欢迎交流：
duqian2010@gmail.com ,杜小菜，微信：dusan2010


