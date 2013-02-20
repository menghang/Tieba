package norofox.tieba.sign;

import java.text.SimpleDateFormat;
import java.util.Date;
import norofox.tieba.sign.core.SharedPreferencesManager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
/**
 * 后台签到服务
 * @author 不懂浪漫的狐狸
 *
 */
public class signService extends Service {
	private static final String TAG = "LocalService";
	SharedPreferencesManager spm;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onBind");
		return null;
	}

	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate");
		super.onCreate();
		spm=new SharedPreferencesManager(this.getSharedPreferences("norofox_config", 0));
		
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy");
		// 启动后删除之前我们定义的通知
		NotificationManager notificationManager = (NotificationManager) this
				.getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancel(0);
		spm.setServiceon(false);
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.i(TAG, "onStart");
		super.onStart(intent, startId);
			showNotification("自动签到模式");
			//启动主线程
		new ProcessThread().start();
	}

	private void showNotification(String title) {
		// 创建一个NotificationManager的引用
		NotificationManager notificationManager = (NotificationManager) this
				.getSystemService(android.content.Context.NOTIFICATION_SERVICE);

		// 定义Notification的各种属性
		Notification notification = new Notification(R.drawable.icon1,
				"贴吧签到工具", System.currentTimeMillis());
		notification.flags |= Notification.FLAG_ONGOING_EVENT; // 将此通知放到通知栏的"Ongoing"即"正在运行"组中
		notification.flags |= Notification.FLAG_NO_CLEAR; // 表明在点击了通知栏中的"清除通知"后，此通知不清除，经常与FLAG_ONGOING_EVENT一起使用

		//以下设置信息灯闪烁
		//		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
//		notification.defaults = Notification.DEFAULT_LIGHTS;
//		notification.ledARGB = Color.BLUE;
//		notification.ledOnMS = 5000;

		// 设置通知的事件消息
		CharSequence contentTitle = title; // 通知栏标题
		CharSequence contentText = "设定时间["+spm.getAutoTime()+"],签到工具正在运行..."; // 通知栏内容
		Intent notificationIntent = new Intent(this, mainActivity.class); // 点击该通知后要跳转的Activity
		PendingIntent contentItent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(this, contentTitle, contentText,
				contentItent);

		// 把Notification传递给NotificationManager
		notificationManager.notify(0, notification);
	}

	
	private void autosign(){
		try{mainActivity.instance.finish();}catch(Exception e){}
		Intent i=new Intent(this, mainActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		i.putExtra("autosign", true);
		startActivity(i);
	}
	private class ProcessThread extends Thread {
		public ProcessThread() {
		}

		@Override
		public void run() {
			
			while (spm.getServiceon()) {
				if(new SimpleDateFormat("HH:mm").format(new Date()).equals(spm.getAutoTime())){
					String tody=new SimpleDateFormat("mm-dd").format(new Date());
					if(spm.getSignDay()==null||!spm.getSignDay().equals(tody)){
					 addNotificaction("执行自动签到");
					 spm.setSignDay(tody);
					 autosign();
					}
				}
								 
				
				try {
					Thread.currentThread();
					Thread.sleep(1000 * 5);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private void cancelNotification() {
		NotificationManager manager = (NotificationManager) this
				.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		manager.cancel(1);
	}

	/**
	 * 添加一个notification
	 */
	private void addNotificaction(String msg) {
		NotificationManager manager = (NotificationManager) this
				.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		// 创建一个Notification
		Notification notification = new Notification();
		// 设置显示在手机最上边的状态栏的图标
		notification.icon = R.drawable.icon1;
		// 当当前的notification被放到状态栏上的时候，提示内容
		notification.tickerText = "签到通知！";

		/***
		 * notification.contentIntent:一个PendingIntent对象，当用户点击了状态栏上的图标时，
		 * 该Intent会被触发 notification.contentView:我们可以不在状态栏放图标而是放一个view
		 * notification.deleteIntent 当当前notification被移除时执行的intent
		 * notification.vibrate 当手机震动时，震动周期设置
		 */
		if (!msg.equals("0")) {
			// 添加声音提示
			notification.defaults = Notification.DEFAULT_SOUND;
			// audioStreamType的值必须AudioManager中的值，代表着响铃的模式
			notification.audioStreamType = android.media.AudioManager.ADJUST_LOWER;

			// 下边的两个方式可以添加音乐
			// notification.sound =
			// Uri.parse("file:///sdcard/notification/ringer.mp3");
			// notification.sound =
			// Uri.withAppendedPath(Audio.Media.INTERNAL_CONTENT_URI, "6");

		}
		Intent intent = null;
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				intent, PendingIntent.FLAG_ONE_SHOT);
		// 点击状态栏的图标出现的提示信息设置
		notification.setLatestEventInfo(this, "签到提醒:", msg ,
				pendingIntent);
		manager.notify(1, notification);

	}
	
}
