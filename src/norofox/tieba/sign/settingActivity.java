package norofox.tieba.sign;

import java.io.File;
import java.util.Date;
import java.util.List;
import norofox.tieba.sign.core.SharedPreferencesManager;
import com.gfan.sdk.statitistics.GFAgent;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;
/**
 * 设置
 * @author 不懂浪漫的狐狸
 *
 */
public class settingActivity extends Activity {
    /** Called when the activity is first created. */
	SharedPreferencesManager spm;
    TextView tv_statues,tv_image,tv_sign_delay,tv_sign_rand;
    CheckBox cb_autosign;
    Button bt_choose,bt_reset,bt_tbs;
    LinearLayout ll_main;
    TimePicker tp_sign;
    EditText et_tbs;
    RadioGroup rg_login;
    SeekBar sb_delay,sb_rand;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.setting);
        this.setTitle("参数设置");
//        tv_statues=(TextView)findViewById(R.id.tv_statues);
        spm=new SharedPreferencesManager(this.getSharedPreferences("norofox_config", 0));
        rg_login=(RadioGroup)findViewById(R.id.rg_login);
        cb_autosign=(CheckBox)findViewById(R.id.cb_autosign);
        bt_choose=(Button)findViewById(R.id.bt_choose);
        bt_reset=(Button)findViewById(R.id.bt_reset);
        bt_tbs=(Button)findViewById(R.id.bt_tbs);
        ll_main=(LinearLayout)findViewById(R.id.ll_main);
        tv_image=(TextView)findViewById(R.id.tv_image);
        tp_sign=(TimePicker)findViewById(R.id.tp_sign);
        et_tbs=(EditText)findViewById(R.id.et_tbs);
        sb_delay=(SeekBar)findViewById(R.id.sb_delay);
        sb_rand=(SeekBar)findViewById(R.id.sb_rand);
        tv_sign_delay=(TextView)findViewById(R.id.tv_sign_delay);
        tv_sign_rand=(TextView)findViewById(R.id.tv_sign_rand);
        
        sb_delay.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				int i=seekBar.getProgress();
				spm.setSignDelay(i);
				tv_sign_delay.setText("签到间隔("+spm.getSignDelay()+"+随机(0-"+spm.getSignRand()+")秒)");
			}
		});
        sb_rand.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				int i=seekBar.getProgress();
				spm.setSignRand(i);
				tv_sign_rand.setText("随机因子("+spm.getSignRand()+"秒)");
				tv_sign_delay.setText("签到间隔("+spm.getSignDelay()+"+随机(0-"+spm.getSignRand()+")秒)");
			}
		});
        
        et_tbs.setText(spm.getSkipList().substring(0,spm.getSkipList().length()-1).substring(1));
        
        tp_sign.setIs24HourView(true);
        tp_sign.setOnTimeChangedListener(new OnTimeChangedListener() {
			
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				// TODO Auto-generated method stub
				spm.setAutoTime((tp_sign.getCurrentHour()<10?"0":"")+tp_sign.getCurrentHour()+":"+(tp_sign.getCurrentMinute()<10?"0":"")+tp_sign.getCurrentMinute());
			}
		});
        bt_choose.setOnClickListener(new OnClickListener() {
        	   public void onClick(View v) {
        	     Intent i=new Intent();
        	     i.setType("image/*");
        	     i.setAction(Intent.ACTION_GET_CONTENT);
        	     Intent intent=Intent.createChooser(i, "选择图片");
        	     startActivityForResult(intent, 2);
        	   }
        	  });
        bt_tbs.setOnClickListener(new OnClickListener() {
     	   public void onClick(View v) {
     	    spm.setSkipList("-"+et_tbs.getText().toString()+"-");
     	    Toast.makeText(settingActivity.this, "操作成功！", 2000).show();
     	   }
     	  });
        bt_reset.setOnClickListener(new OnClickListener() {
     	   public void onClick(View v) {
      	    spm.setImagePath(null);
      	  tv_image.setText("自定义背景:默认背景");
      	spm.setSignDay(null);
      	   }
      	  });
        cb_autosign.setOnClickListener(listener);
        ((RadioButton)findViewById(R.id.rb_2)).setChecked(spm.getLoginMethod()==2);
        rg_login.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				spm.setLoginMethod(rg_login.getCheckedRadioButtonId()==R.id.rb_1?1:2);
				Toast.makeText(settingActivity.this, "登录方式设置为："+((RadioButton)findViewById(rg_login.getCheckedRadioButtonId())).getText().toString(),1000).show();
			}
		});
        
        
        	
        tv_sign_delay.setText("签到间隔("+spm.getSignDelay()+"秒)");tv_sign_delay.setText("签到间隔("+spm.getSignDelay()+"+随机(0-"+spm.getSignRand()+")秒)");
        tv_sign_rand.setText("随机因子("+spm.getSignRand()+"秒)");
        sb_delay.setProgress(spm.getSignDelay());
        sb_rand.setProgress(spm.getSignRand());
        boolean b = CheckServiceIsStart("norofox.tieba.sign.signService");
		spm.setServiceon(b);//更新当前服务启动状态

		cb_autosign.setChecked(b);
		if(spm.getImagePath()!=null){
			tv_image.setText("自定义背景:"+spm.getImagePath());
		}else{
			tv_image.setText("自定义背景:默认背景");
		}
		
		if(spm.getAutoTime()==null){
			tp_sign.setCurrentHour(new Date().getHours());
			tp_sign.setCurrentMinute(new Date().getMinutes());
			
		}else{
			String t=spm.getAutoTime();
			String h=t.substring(0, 2);
			String m=t.substring(3, 5);
			tp_sign.setCurrentHour(Integer.parseInt(h));
			tp_sign.setCurrentMinute(Integer.parseInt(m));
		}
			
    }
    OnClickListener listener =new OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v==cb_autosign){
				if(cb_autosign.isChecked()){
					String h=(tp_sign.getCurrentHour()<10?"0":"")+tp_sign.getCurrentHour();
					String m=(tp_sign.getCurrentMinute()<10?"0":"")+tp_sign.getCurrentMinute();
					spm.setAutoTime(h+":"+m);
					spm.setServiceon(true);
					startService(new Intent(settingActivity.this,
							signService.class));
				}else{
					spm.setServiceon(false);
					stopService(new Intent(settingActivity.this,
							signService.class));
					
				}
			}
		}
	};
   
    
    /**
     * 判断服务是否已经运行
     * @param mServiceList
     * @param className
     * @return
     */
    private boolean CheckServiceIsStart(String className) {
    	  ActivityManager mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
          List<ActivityManager.RunningServiceInfo> mServiceList = mActivityManager
  				.getRunningServices(30);
		for (int i = 0; i < mServiceList.size(); i++) {
			if (className.equals(mServiceList.get(i).service.getClassName())) {
				return true;
			}
		}
		return false;
	}
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 2:
			Uri imageUri = null;
			String imagePath = "";
			if (data != null) {
				imageUri = data.getData();

				String[] projection = { MediaColumns.DATA };
				Cursor cursor = managedQuery(imageUri, projection, null, null,
						null);
				if (cursor != null) {
					int colomn_index = cursor
							.getColumnIndexOrThrow(MediaColumns.DATA);
					cursor.moveToFirst();
					imagePath = cursor.getString(colomn_index);
					File f = new File(imagePath);
					if(f.length()>1000000){
						Toast.makeText(this, "图片太大啦!换个小一点的试试~", 2000).show();
					}else{
						try {
							Bitmap bit = BitmapFactory.decodeFile(imagePath);
							ll_main.setBackgroundDrawable(new BitmapDrawable(
									bit));
							spm.setImagePath(imagePath);
							tv_image.setText("自定义背景:"+imagePath);
						} catch (Exception e) {
							// TODO: handle exception
							Toast.makeText(this, e.getMessage(), 2000).show();
						}
					}
				}else{
					spm.setImagePath(null);
				}
			}

			break;
		}
	}
	//机锋统计
	@Override
	protected void onResume(){
	super.onResume();
	//GFAgent.onResume (this);
	}
	@Override
	protected void onPause(){
	super.onPause();
	//GFAgent.onPause(this);
	}
}