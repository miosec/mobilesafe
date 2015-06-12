/**
 * @author miosec
 */
package org.miosec.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class LaoUtils {
	protected static final String TAG = "LaoUtils";

	/**
	 * 获取PackageInfo
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
	 * 获取应用程序版本号
	 * 
	 * @param context
	 *            上下文对象
	 * @return 获取应用程序版本号
	 * @throws NameNotFoundException
	 */
	public static int getVersionCode(Activity activity)
			throws NameNotFoundException {
		return getPackageInfo(activity).versionCode;
	}

	/**
	 * 获取应用程序版本名
	 * 
	 * @param context
	 *            上下文对象
	 * @return 获取应用程序版本名
	 * @throws NameNotFoundException
	 */
	public static String getVersionName(Context context)
			throws NameNotFoundException {
		return getPackageInfo(context).versionName;
	}

	/**
	 * 从服务器获取打开指定url返回的结果
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
	 * 从服务器获取打开指定url返回的结果
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
	 * 将InputStream转换为String
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
	 * 显示升级对话框
	 * 
	 * @param context
	 *            上下文对象
	 * @param title
	 *            升级对话框的标题
	 * @param versionCode
	 *            升级对话框显示的版本号
	 * @param iconId
	 *            升级对话框的图标
	 * @param message
	 *            升级对话框显示的内容
	 * @param otherActivity
	 *            点击取消后跳转的Activity
	 * @return
	 */
	public static void showUpdateDialog(final Context context, String title,
			int versionCode, int iconId, String message,
			final String update_url, final String otherActivity) {
		AlertDialog.Builder builder = new Builder(context);
		// 对应属性若果非Null则设置
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

		builder.setPositiveButton("升级", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				downloadApk(update_url);
			}
		});
		builder.setNegativeButton("取消", new OnClickListener() {
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
	 * 该工具类使用xutils的下载相关方法,后期会想办法给集成进来
	 * @param update_url
	 */
	public static void downloadApk(String update_url) {
		
	}

	/**
	 * 跳转到指定的Activity
	 * 
	 * @param context
	 * @param class1
	 */
	public static void gotoActivity(Activity activity, Class<?> class1)
			throws ClassNotFoundException {
		activity.startActivity(new Intent(activity, class1));
	}
 
	/**
	 * 从服务获取版本并决定是否显示更新版本及下载更新安装的方法
	 * 
	 * @param activity
	 *            上下文对象
	 * @param title
	 *            要设置的更新对话框标题
	 * @param iconId
	 *            要设置的更新对话框图标
	 * @param otherActivity
	 *            取消更新后要跳转的Activity
	 */
	public static void getVersionAndShowUpdateDialog(
			final String jsonServerUrl, final String Method,
			final int ConnectTimeout, final Activity currentActivity,
			final String jsonVersionCodeName, final String jsonDescName,
			final String jsonUpdateurlName, final String title,
			final int iconId, final String otherActivity) {
		new Thread() {
			public void run() {
				try {
					// 从服务器获取JSONObject对象
					JSONObject jsonObject = LaoUtils.getJsonObjectFromServer(
							jsonServerUrl, Method, ConnectTimeout);
					// 解析Json对象
					final int serverVersionCode = jsonObject
							.getInt(jsonVersionCodeName);
					final String desc = jsonObject.getString(jsonDescName);
					final String update_url = jsonObject
							.getString(jsonUpdateurlName);
					// 获取自己的版本
					int versionCode = LaoUtils.getVersionCode(currentActivity);
					if (serverVersionCode != versionCode) {
						// 显示升级对话框
						currentActivity.runOnUiThread(new Runnable() {
							public void run() {
								LaoUtils.showUpdateDialog(currentActivity,
										title, serverVersionCode, iconId, desc,
										update_url, otherActivity);
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
}
