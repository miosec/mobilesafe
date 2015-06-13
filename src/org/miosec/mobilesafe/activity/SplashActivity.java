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
		// ��ʼ���ؼ�����
		init();
		// ҵ���߼�����
		controller();
	}

	@Override
	public void init() {
		splash_version = (TextView) findViewById(R.id.splash_version);
	}

	@Override
	public void controller() {
		// ͨ����װ,ʹ��һ�仰�㶨��ȡ�汾����ʾ�汾�����ظ��·���.
		LaoUtils.getVersionAndShowUpdateDialog(
				getResources().getString(R.string.update_url), "GET", 5000,
				this, "code", "desc", "update_url", "�°汾",
				R.drawable.ic_launcher, LaoUtils.getSdCardPath()+"/temp.apk",
				splash_version, "org.miosec.mobilesafe.activity.HomeActivity");
	}
}
