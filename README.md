# mobilesafe
2015-06-13 2:11
	/**
		 * ��Ҫʵ�ֵĹ��� :
		 * 
		 * ȥ��������ȡ�汾��Ϣ,���бȶ�,�����ͬ,����ת������Activity,������ȥ����Ϊ�������İ汾.
		 * 
		 * ����:
		 * 
		 * 1.�����ȡ�������汾���бȶ�
		 * 2.�����ͬ�򵯳��汾���¶Ի���,����Ϊ���°汾:+ServerVersionCode,����Ϊjson���ص�desc��Ϣ,
		 * ѡ��������������ظ����߳�,������ת������activity
		 * 3.���ظ���ʹ��xutils,����ʹ��һ��textview�ؼ�����ʾʵʱ���µĽ���
		 * .�������,��ת��ϵͳ�İ�װapk����ͼ�����滻��װ,���ʧ������ת����activity
		 * 
		 */
�ù���ʹ���Լ��ķ�װ��LaoUtils��һ�仰ʵ��
	//ͨ����װһ�仰�㶨��ȡ�汾����ʾ�汾�����ظ��·���.
		LaoUtils.getVersionAndShowUpdateDialog(
				getResources().getString(R.string.update_url), "GET", 5000,
				this, "code", "desc", "update_url", "�°汾",
				R.drawable.ic_launcher, "org.miosec.mobilesafe.HomeActivity");