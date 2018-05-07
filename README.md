# TimeKeeper


---

## 相关实现

### 1.后台数据：
    使用Bmob后端云完成登陆之类的数据存储

### 2.锁屏界面    

#### 返回键拦截：
     1）重写onKeyDown方法


#### Home键拦截：  
    让Home键的处理转到自己的代码，曲线拦截
    1）将对应Activity设置为默认桌面
    2）查看桌面应用，列表让用户选择，使用sharedpreferences储存起来
    3）每次按下Home键就会跳转到自己的Activity，此时再跳转到用户选择的桌面应用即可

#### 防止跳转：    
    除了Home键和back键，还有如通知栏长按可以跳转至设置界面，从而跳出锁屏界面，需要跳回
    1）开启service，循环检测当前Activity的包名
    2）若不是锁屏Activity的包名，则跳回

### 2.锁屏界面（另一个方法）
    使用悬浮窗（注意权限）
    1）layoutinflater加载一个View；
    2）LayoutParams 的Type设置成TYPE_SYSTEM_ALERT，即可直接实现返回键拦截、Home键拦截、通知栏不可下拉，除非重启否则跳不出去
    3）windowManager.addView()生成悬浮窗
    4）windowManager.removeView()取消悬浮窗

### 3.进程保活
    保不住

### 4.进程拉活
    网络上有挺多双进程守护等等保活的方法，但实际测试来说都没有什么效果。最好还是老老实实告诉用户添加到白名单里吧hhhh。另外说一下两个时而有用时而无用的拉活方法：
    1）继承NotificationListenerService的Service会在被杀死后被系统拉起，延迟时间不稳定，拉活效果也不稳定，部分坑：
         首先需要授权“有权限查看通知”（MIUI系统下：设置-更多设置-系统安全-使用通知权限）
         启动service方式只能通过重启、清除权限-授予权限两种方法由系统启动，通过startservice（）启动的无效果；
    2）继承AccessibilityService，原理与继承NotificationListenerService类似，通过系统拉活service

---

## 总结感想
比较久远了，文档就写个大概吧，以后还是边写项目边写文档吧

主要精力在研究进程保活这一块上面了，但其实在无root权限的情况下，几乎不可能实现稳定的保活和拉活（至少对于我来说_(:зゝ∠)_），特别是在一些国产ROM下进程杀得十分彻底
另外找了很多第三方实现的UI，很方便很漂亮_(:зゝ∠)_大佬大佬


