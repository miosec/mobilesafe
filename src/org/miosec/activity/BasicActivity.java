package org.miosec.activity;

import android.app.Activity;
import android.os.Bundle;

public abstract class BasicActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ��ʼ���ؼ�����
		init();
		// ҵ���߼�����
		controller();
	}

	public abstract void init();

	public abstract void controller();
}
