package org.miosec.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;

public abstract class BasicActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 初始化控件方法
		init();
		// 业务逻辑控制
		controller();
	}

	public abstract void init();

	public abstract void controller();
}
