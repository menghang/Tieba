package norofox.tieba.sign;
  
import norofox.tieba.sign.core.SharedPreferencesManager;
import android.content.BroadcastReceiver;   
import android.content.Context;   
import android.content.Intent;   
import android.util.Log;   
  
public class BootBroadCastReceiver extends BroadcastReceiver{   
    public static final String ACTION = "android.intent.action.BOOT_COMPLETED";   
    SharedPreferencesManager spm;
    @Override  
    public void onReceive(Context context, Intent intent) {   
        // TODO Auto-generated method stub   
    	//spm=new SharedPreferencesManager(this.getSharedPreferences("norofox_config", 0));
        if (intent.getAction().equals(ACTION)) {       
            Log.d("TAG","ok");      
            Intent myIntent=new Intent();//intent对象 用于启动服务   
            myIntent.setAction("norofox.tieba.sign.BootService");   
            context.startService(myIntent);//开机 启动服务   
        }   
    }   
}  