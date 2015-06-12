package org.miosec.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;

public abstract class BasicActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public abstract void init();

	public abstract void controller();
}
