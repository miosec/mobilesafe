package org.miosec.mobilesafe.activity;

import org.miosec.mobilesafe.R;
import org.miosec.mobilesafe.utils.LaoUtils;

import android.os.Bundle;
import android.widget.TextView;

public class SplashActivity extends BasicActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
	}
 
	@Override
	public void init() {
		System.out.println("init");
		TextView splash_version = (TextView) findViewById(R.id.splash_version);
	}

	@Override
	public void controller() {
		System.out.println("controller");
		/**
		 * 想要实现的功能 :
		 * 
		 * 去服务器获取版本信息,进行比对,如果相同,则跳转到主的Activity,否则则去更新为服务器的版本.
		 * 
		 * 分析:
		 * 
		 * 1.网络获取服务器版本进行比对
		 * 2.如果不同则弹出版本更新对话框,标题为最新版本:+ServerVersionCode,内容为json返回的desc信息,
		 * 选择升级则进入下载更新线程,否则跳转到主的activity
		 * 3.下载更新使用xutils,并且使用一个textview控件连显示实时更新的进度
		 * .更新完毕,跳转到系统的安装apk的意图进行替换安装,如果失败则跳转到主activity
		 * 
		 */

		//通过封装一句话搞定获取版本和显示版本及下载更新方法.
		LaoUtils.getVersionAndShowUpdateDialog(
				getResources().getString(R.string.update_url), "GET", 5000,
				this, "code", "desc", "update_url", "新版本",
				R.drawable.ic_launcher, "org.miosec.mobilesafe.HomeActivity");
	}
}
