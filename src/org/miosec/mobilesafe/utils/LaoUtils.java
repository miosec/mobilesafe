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
			final String update_url, final String target, final View view,
			final String otherActivity) {

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
				try {
					downloadApk(update_url, target, view, (Activity) context,
							Class.forName(otherActivity));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
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
						ShowToast(activity, "下载成功", 0);
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
	 * @param jsonServerUrl
	 *            json文件的url
	 * @param Method
	 *            请求方法
	 * @param ConnectTimeout
	 *            连接超时时间
	 * @param currentActivity
	 *            调用的上下文
	 * @param jsonVersionCodeName
	 *            json文件中版本名称标识
	 * @param jsonDescName
	 *            json文件中版本描述信息标识
	 * @param jsonUpdateurlName
	 *            json文件中最新版本的下载地址描述信息标识
	 * @param title
	 *            更新提示框的title
	 * @param iconId
	 *            更新提示框的iconID
	 * @param target
	 *            下载更新后最新apk安装文件的存放位置
	 * @param view
	 *            显示下载进度的View控件,可以是TextView也可以是进度条
	 * @param otherActivity
	 *            操作完成后要跳转到主界面Activity
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
	 * 无论子线程还是子线程均可显示Toast
	 * 
	 * @param activity
	 *            上下文对象
	 * @param msg
	 *            消息内容
	 * @param duration
	 *            时长 可以选择 0或1
	 */
	public static void ShowToast(final Activity activity, final String msg,
			final int duration) {
		// 判断是否主线程
		if ("main0".equals(Thread.currentThread().getName())) {
			// 当前线程为主线程
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
		// 通过向系统传递安装应用程序包的意图操作进行对下载的更新包的安装 ★ ★ ★ ★ ★
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
