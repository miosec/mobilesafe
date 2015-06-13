/**
 * @author miosec
 */
package org.miosec.mobilesafe.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class LaoUtils {
	protected static final String TAG = "LaoUtils";

	/**
	 * ��ȡPackageInfo
	 * 
	 * @param context
	 * @return
	 * @throws NameNotFoundException
	 */
	public static PackageInfo getPackageInfo(Context context)
			throws NameNotFoundException {
		return context.getPackageManager().getPackageInfo(
				context.getPackageName(), 0);
	}

	/**
	 * ��ȡӦ�ó���汾��
	 * 
	 * @param context
	 *            �����Ķ���
	 * @return ��ȡӦ�ó���汾��
	 * @throws NameNotFoundException
	 */
	public static int getVersionCode(Activity activity)
			throws NameNotFoundException {
		return getPackageInfo(activity).versionCode;
	}

	/**
	 * ��ȡӦ�ó���汾��
	 * 
	 * @param context
	 *            �����Ķ���
	 * @return ��ȡӦ�ó���汾��
	 * @throws NameNotFoundException
	 */
	public static String getVersionName(Context context)
			throws NameNotFoundException {
		return getPackageInfo(context).versionName;
	}

	/**
	 * �ӷ�������ȡ��ָ��url���صĽ��
	 * 
	 * @param server_url
	 * @return
	 * @throws IOException
	 */
	public static String getServerResult(String serverUrl, String method,
			int connectionTimeout) throws IOException {
		URL url = new URL(serverUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod(method);
		conn.setConnectTimeout(connectionTimeout);
		int responseCode = conn.getResponseCode();
		System.out.println(responseCode + "");
		if (responseCode == 200) {
			InputStream in = conn.getInputStream();
			String result = parseStream(in);
			System.out.println(result);
			return result;
		} else {
			return "";
		}
	}

	/**
	 * �ӷ�������ȡ��ָ��url���صĽ��
	 * 
	 * @param server_url
	 * @return
	 * @throws IOException
	 *             ,JSONException
	 * @throws JSONException
	 */
	public static JSONObject getJsonObjectFromServer(String serverUrl,
			String method, int connectionTimeout) throws IOException,
			JSONException {
		return new JSONObject(getServerResult(serverUrl, method,
				connectionTimeout));
	}

	/**
	 * ��InputStreamת��ΪString
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static String parseStream(InputStream in) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		StringWriter sw = new StringWriter();
		String str = null;
		while ((str = br.readLine()) != null) {
			sw.write(str);
		}
		sw.close();
		br.close();
		return sw.toString();
	}

	/**
	 * ��ʾ�����Ի���
	 * 
	 * @param context
	 *            �����Ķ���
	 * @param title
	 *            �����Ի���ı���
	 * @param versionCode
	 *            �����Ի�����ʾ�İ汾��
	 * @param iconId
	 *            �����Ի����ͼ��
	 * @param message
	 *            �����Ի�����ʾ������
	 * @param otherActivity
	 *            ���ȡ������ת��Activity
	 * @return
	 */
	public static void showUpdateDialog(final Context context, String title,
			int versionCode, int iconId, String message,
			final String update_url, final String target, final View view,
			final String otherActivity) {

		AlertDialog.Builder builder = new Builder(context);
		// ��Ӧ����������Null������
		if (title != null) {
			if (versionCode == 0) {
				builder.setTitle(title);
			} else {
				builder.setTitle(title + versionCode + "");
			}
		}

		builder.setCancelable(false);
		builder.setIcon(iconId);
		builder.setMessage(message);

		builder.setPositiveButton("����", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				try {
					downloadApk(update_url, target, view, (Activity) context,
							Class.forName(otherActivity));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		});
		builder.setNegativeButton("ȡ��", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				try {
					gotoActivity((Activity) context,
							Class.forName(otherActivity));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					Log.i(TAG, otherActivity + "not found!");
				}
			}
		});

		builder.show();
	}

	/**
	 * �ù�����ʹ��xutils��������ط���,���ڻ���취�����ɽ���
	 * 
	 * @param update_url
	 */
	public static void downloadApk(String update_url, final String target,
			final View view, final Activity activity, final Class<?> class1) {
		System.out.println("downloadApk");
		System.out.println("downloadApk--------"+update_url);
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.download(update_url, target, new RequestCallBack<File>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				try {
					gotoActivity(activity, class1);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onSuccess(ResponseInfo<File> arg0) {
				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						ShowToast(activity, "���سɹ�", 0);
						installApk(activity, target);
					}
				});
			}

			@Override
			public void onLoading(final long total, final long current,
					boolean isUploading) {
				super.onLoading(total, current, isUploading);
				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						((TextView) view).setText(current + "/" + total);
						;
					}
				});
			}

		});
	}

	/**
	 * ��ת��ָ����Activity
	 * 
	 * @param context
	 * @param class1
	 */
	public static void gotoActivity(Activity activity, Class<?> class1)
			throws ClassNotFoundException {
		activity.startActivity(new Intent(activity, class1));
	}

	/**
	 * �ӷ����ȡ�汾�������Ƿ���ʾ���°汾�����ظ��°�װ�ķ���
	 * 
	 * @param jsonServerUrl
	 *            json�ļ���url
	 * @param Method
	 *            ���󷽷�
	 * @param ConnectTimeout
	 *            ���ӳ�ʱʱ��
	 * @param currentActivity
	 *            ���õ�������
	 * @param jsonVersionCodeName
	 *            json�ļ��а汾���Ʊ�ʶ
	 * @param jsonDescName
	 *            json�ļ��а汾������Ϣ��ʶ
	 * @param jsonUpdateurlName
	 *            json�ļ������°汾�����ص�ַ������Ϣ��ʶ
	 * @param title
	 *            ������ʾ���title
	 * @param iconId
	 *            ������ʾ���iconID
	 * @param target
	 *            ���ظ��º�����apk��װ�ļ��Ĵ��λ��
	 * @param view
	 *            ��ʾ���ؽ��ȵ�View�ؼ�,������TextViewҲ�����ǽ�����
	 * @param otherActivity
	 *            ������ɺ�Ҫ��ת��������Activity
	 */

	public static void getVersionAndShowUpdateDialog(
			final String jsonServerUrl, final String Method,
			final int ConnectTimeout, final Activity currentActivity,
			final String jsonVersionCodeName, final String jsonDescName,
			final String jsonUpdateurlName, final String title,
			final int iconId, final String target, final View view,
			final String otherActivity) {
		new Thread() {
			public void run() {
				try {

					// �ӷ�������ȡJSONObject����
					JSONObject jsonObject = LaoUtils.getJsonObjectFromServer(
							jsonServerUrl, Method, ConnectTimeout);
					// ����Json����
					final int serverVersionCode = jsonObject
							.getInt(jsonVersionCodeName);
					final String desc = jsonObject.getString(jsonDescName);
					final String update_url = jsonObject
							.getString(jsonUpdateurlName);
					// ��ȡ�Լ��İ汾
					int versionCode = LaoUtils.getVersionCode(currentActivity);
					if (serverVersionCode != versionCode) {
						// ��ʾ�����Ի���
						currentActivity.runOnUiThread(new Runnable() {
							public void run() {
								LaoUtils.showUpdateDialog(currentActivity,
										title, serverVersionCode, iconId, desc,
										update_url, target, view, otherActivity);
							}
						});
					} else {
						gotoActivity(currentActivity,
								Class.forName(otherActivity));
					}
				} catch (Exception e) {

				}
			};
		}.start();
	}

	/**
	 * �������̻߳������߳̾�����ʾToast
	 * 
	 * @param activity
	 *            �����Ķ���
	 * @param msg
	 *            ��Ϣ����
	 * @param duration
	 *            ʱ�� ����ѡ�� 0��1
	 */
	public static void ShowToast(final Activity activity, final String msg,
			final int duration) {
		// �ж��Ƿ����߳�
		if ("main0".equals(Thread.currentThread().getName())) {
			// ��ǰ�߳�Ϊ���߳�
			Toast.makeText(activity, msg, duration).show();
		} else {
			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(activity, msg, duration).show();
				}
			});
		}
	}
	public static void installApk(Activity activity,String target){
		// ͨ����ϵͳ���ݰ�װӦ�ó��������ͼ�������ж����صĸ��°��İ�װ �� �� �� �� ��
		System.out.println(target);
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setDataAndType(Uri.fromFile(new File(target)),
				"application/vnd.android.package-archive");
		activity.startActivityForResult(intent, 99);
		activity.finish();
	}
	public static String getSdCardPath(){
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			return Environment.getExternalStorageDirectory().getAbsolutePath();
		}else{
			return "";
		}
	}
}
