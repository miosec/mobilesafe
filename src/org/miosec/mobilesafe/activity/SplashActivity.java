 package org.miosec.mobilesafe.activity;

import org.miosec.mobilesafe.R;
import org.miosec.mobilesafe.utils.LaoUtils;

import android.os.Bundle;
import android.widget.TextView;

public class SplashActivity extends BasicActivity {

	private TextView splash_version;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		// 初始化控件方法
		init();
		// 业务逻辑控制
		controller();
	}

	@Override
	public void init() {
		splash_version = (TextView) findViewById(R.id.splash_version);
	}

	@Override
	public void controller() {
		// 通过封装,使用一句话搞定获取版本和显示版本及下载更新方法.
		LaoUtils.getVersionAndShowUpdateDialog(
				getResources().getString(R.string.update_url), "GET", 5000,
				this, "code", "desc", "update_url", "新版本",
				R.drawable.ic_launcher, LaoUtils.getSdCardPath()+"/temp.apk",
				splash_version, "org.miosec.mobilesafe.activity.HomeActivity");
	}
}
