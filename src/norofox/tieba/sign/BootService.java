package norofox.tieba.sign;
import norofox.tieba.sign.core.SharedPreferencesManager;
import android.app.Service;   
import android.content.Intent;   
import android.os.IBinder;   
import android.util.Log;   
  /**
   * 自启动服务
   * @author 不懂浪漫的狐狸
   *
   */
public class BootService extends Service{   
	//定义service
	SharedPreferencesManager spm;
    @Override  
    public IBinder onBind(Intent intent) {   
        // TODO Auto-generated method stub   
        return null;   
    }   
    @Override  
    public void onCreate() {   
        // TODO Auto-generated method stub   
        Log.d("TAG","BootService onCreate");   
       //初始化
        spm=new SharedPreferencesManager(this.getSharedPreferences("norofox_config", 0));
        super.onCreate();   
    }   
    @Override  
    public void onStart(Intent intent, int startId) {   
        // TODO Auto-generated method stub   
        Log.d("TAG","BootService onStart");   
        //启动
        if(spm.getServiceon())
        startService(new Intent(this,
				signService.class));
        stopSelf();
        //super.onStart(intent, startId);   
    }   
}  
