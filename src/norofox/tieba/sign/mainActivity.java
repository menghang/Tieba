package norofox.tieba.sign;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import norofox.tieba.sign.core.HttpMgr;
import norofox.tieba.sign.core.RegMgr;
import norofox.tieba.sign.core.SharedPreferencesManager;
import norofox.tieba.sign.core.SqliteMgr;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.gfan.sdk.statitistics.GFAgent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 主activity
 * @author 不懂浪漫的狐狸
 *
 */
public class mainActivity extends Activity {
    /** Called when the activity is first created. */
    
    TextView tv_log,tv_author;
    EditText et_user;
    Button bt_go;
    HttpMgr hm;
    String version="";
    String newversion="";
    boolean keyboardon=false;
    RelativeLayout rl_main;
    ArrayList<UserModel> list;
    String checkcode=null;
    ScrollView sv;
    SharedPreferencesManager spm;
    String skipList="";
   
    public static mainActivity instance = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.main);
        instance=this;
        spm=new SharedPreferencesManager(this.getSharedPreferences("norofox_config", 0));
        if(getAnn())
        	showAnno();
        sv=(ScrollView)findViewById(R.id.scrollView);
        
        rl_main=(RelativeLayout)findViewById(R.id.rl_main);
        tv_log=(TextView)findViewById(R.id.tv_log);
        tv_author=(TextView)findViewById(R.id.tv_author);
       
        et_user=(EditText)findViewById(R.id.et_user);
        bt_go=(Button)findViewById(R.id.bt_go);
       
        bt_go.setOnClickListener(listener);
        et_user.setOnClickListener(listener);
        
        tv_author.setText("by 不懂浪漫的狐狸");

        loadBG();
        getData();
        skipList=spm.getSkipList();
        
        hm=new HttpMgr();
        
        String pName = "norofox.tieba.sign";   
    	try {   
    	        PackageInfo pinfo = getPackageManager().getPackageInfo(pName,PackageManager.GET_CONFIGURATIONS);   
    	        version = pinfo.versionName;   
    	        //int versionCode = pinfo.versionCode;  
    	        ((TextView)findViewById(R.id.tv_notice)).setText(version);
    	} catch (NameNotFoundException e) {   
    	}
    	//检查新版本
    	new Thread() {
			@Override
			public void run() {
				String tmp=checkupdate("http://hi.baidu.com/nrfox/item/3ebe6a1a1f233cf8756a8415");
				if(tmp==null || !tmp.contains("tieba_sign_android"))
					tmp=checkupdate("http://norofox.eicp.net:8099/tieba/sign/update.html");
			}
		}.start();
		
		//服务调用，自动执行
		Intent i=this.getIntent();
        if(i!=null&&i.getBooleanExtra("autosign", false)&&spm.getAvailable()){
        	
        	if(check()){
				bt_go.setEnabled(false);
			new Thread() {
				@Override
				public void run() {

					for(UserModel u:list){
						process(u);
						try {
							Thread.currentThread();
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
						}
					}
					Message msg=new Message();
					msg.what=MESSAGE_LOG;
					msg.obj="执行完毕！\n";
					handler.sendMessage(msg);
					msg=new Message();
					msg.what=MESSAGE_BTN_ENABLE;
					handler.sendMessage(msg);
					
				}
			}.start();
			}
        }
        if(!spm.getAvailable()){
        	Message msg=new Message();
        	msg.what=MESSAGE_NOT_AVAILABLE;
        	handler.sendMessage(msg);
        }
    }
     
    OnClickListener listener=new OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.bt_go:
			{
				if(check()){
					bt_go.setEnabled(false);
				new Thread() {
					@Override
					public void run() {

						for(UserModel u:list){
							process(u);
							try {
								Thread.currentThread();
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								//e.printStackTrace();
							}
						}
						Message msg=new Message();
						msg.what=MESSAGE_LOG;
						msg.obj="执行完毕！\n";
						handler.sendMessage(msg);
						msg=new Message();
						msg.what=MESSAGE_BTN_ENABLE;
						handler.sendMessage(msg);
						
					}
				}.start();
				}
				break;
				
			}
			case R.id.et_user:
				Intent intent =new Intent();
				intent.setClass(mainActivity.this,userActivity.class);
				startActivityForResult(intent,0);
				break;
			default:break;
			}
			
		}
	};
	static final int MESSAGE_LOG = -1;
	static final int MESSAGE_SUCC = 0;
	static final int MESSAGE_ERR = 1;
	static final int MESSAGE_BTN_ENABLE = 2;
	static final int MESSAGE_BTN_DISABLE = 3;
	static final int MESSAGE_SHOW_CHECKCODE = 10;
	static final int MESSAGE_NEW_VERSION = 4;
	static final int MESSAGE_NOT_AVAILABLE = 20;
	static final int MESSAGE_AVAILABLE = 21;


	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case MESSAGE_LOG:
				tv_log.setText(tv_log.getText().toString()+msg.obj);
				sv.fullScroll(View.FOCUS_DOWN);
				break;
			case MESSAGE_SUCC:
				bt_go.setEnabled(true);
				break;
			case MESSAGE_ERR:
				Toast.makeText(getApplicationContext(), (String)msg.obj, 1000).show();
				bt_go.setEnabled(true);
				break;
			case MESSAGE_BTN_ENABLE:
				bt_go.setEnabled(true);
				break;
			case MESSAGE_BTN_DISABLE:
				bt_go.setEnabled(false);
				break;
			case MESSAGE_NEW_VERSION:
				Toast.makeText(mainActivity.this,(String)msg.obj, 10000).show();
				break;
			case MESSAGE_SHOW_CHECKCODE:
				showCheckcode((String)msg.obj);
				break;
			case MESSAGE_NOT_AVAILABLE:
				bt_go.setEnabled(false);
				tv_log.setText("粗大事了！作者停用了本程序，为了安全起见还是手动签到吧~");
				break;
				
			default:
				break;
			}
		}
		
	};
	/**
	 * 执行单用户完整流程
	 * @param u 用户
	 */
	private void process(UserModel u) {
		 checkcode=null;
		 hm=new HttpMgr();
		String url = ("http://wappass.baidu.com/passport/");
		Message msg0=new Message();
		msg0.what=MESSAGE_LOG;
		msg0.obj="【"+u.id+"】开始登录...\n";
		handler.sendMessage(msg0);
		String html;
		
		boolean b=false;
		if(spm.getLoginMethod()==1)
			b=login(u,false,null);
		else
			b=loginWEB(u,false,null);
		ArrayList<TbModel> tbnames=new ArrayList<TbModel>();
		if (b) {
			//html=hm.getHTML("http://tieba.baidu.com","gb2312");
			
			if(spm.getLoginMethod()==1){
				int i=0;
				tbnames=getTbWap();
				if(tbnames.size()==0)
					tbnames=getTbWap();
				Message msg1=new Message();
				msg1.what=MESSAGE_LOG;
				msg1.obj="检测到贴吧["+tbnames.size()+"]个！\n";
				handler.sendMessage(msg1);
				for(TbModel tb:tbnames){
					i++;
					if(skipList.contains("-"+tb.name+"-")){
						Message msg=new Message();
						msg.what=MESSAGE_LOG;
						msg.obj="["+i+"] "+tb.name+"吧("+tb.exp+"/lv."+tb.level+")...跳过(已设置不签到)！\n";
						handler.sendMessage(msg);
						continue;
					}
					signWap(tb, i);
					try {
						Thread.currentThread();
						Thread.sleep(1000*(spm.getSignDelay()+(int)Math.random()*spm.getSignRand()));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}else{
				for(int i=1;i<6;i++){
					html=hm.getHTML("http://tieba.baidu.com/f/like/mylike?pn="+i,"gb2312");
//					RegMgr rm=new RegMgr(html, "(?<=( title=\"))[^\"]*(?=(\">))");
					RegMgr rm=new RegMgr(html, "<tr[.\\s\\S]*?</tr>");
					// title="[^"]*">
					if(!rm.isfind||rm.getList().size()<2){
						Log.d("tbs_page_count","not find :"+i);
						break;
					}
					for(String tmp:rm.getList()){
						String name=regex(tmp, "(?<=( title=\"))[^\"]*(?=(\">))");
						if(name.length()<1)
							continue;
						String exp=regex(tmp, "(?<=(>))\\d+(?=(</span>))");
						String level=regex(tmp, "(?<=(>))\\d+(?=(</div>))");
						tbnames.add(new TbModel(name, exp, level));
					}
//					tbnames.addAll(rm.getList());
					if(!html.contains(">下一页</a>")){
						Log.d("tbs_page_count","no next :"+i);
						break;
					}
				}
				if(tbnames.size()>0){
					int i=0;
					Message msg1=new Message();
					msg1.what=MESSAGE_LOG;
					msg1.obj="检测到贴吧["+tbnames.size()+"]个！\n";
					handler.sendMessage(msg1);
					for(TbModel tb:tbnames){
						String name=tb.name;
						i++;
						if(skipList.contains("-"+name+"-")){
							Message msg=new Message();
							msg.what=MESSAGE_LOG;
							msg.obj="["+i+"] "+name+"吧("+tb.exp+"/lv."+tb.level+")...跳过(已设置不签到)！\n";
							handler.sendMessage(msg);
							continue;
						}
						html=hm.getHTML("http://tieba.baidu.com/sign/info?kw="+urlencode(name,"gb2312"), "utf-8");
						name+="吧";
						if(html.contains("is_sign_in\":1")){
							Message msg=new Message();
							msg.what=MESSAGE_LOG;
							msg.obj="["+i+"] "+name+"("+tb.exp+"/lv."+tb.level+")...跳过(已签)！\n";
							handler.sendMessage(msg);
							continue;
						}
						if(html.contains("is_on\":false")){
							Message msg=new Message();
							msg.what=MESSAGE_LOG;
							msg.obj="["+i+"] "+name+"("+tb.exp+"/lv."+tb.level+")...跳过(无需签到)！\n";
							handler.sendMessage(msg);
							continue;
						}
						sign(tb,i);
						try {
							Thread.currentThread();
							Thread.sleep(1000*(spm.getSignDelay()+(int)Math.random()*spm.getSignRand()));
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					
				}else{
						Message msg=new Message();
						msg.what=MESSAGE_LOG;
						msg.obj="未识别到任何贴吧，建议切换网络再试！\n";
						handler.sendMessage(msg);
					
				}
			}
			

		} else {
			//登录失败
			Message msg=new Message();
			msg.what=MESSAGE_LOG;
			msg.obj="登录失败！\n";
			handler.sendMessage(msg);
		}
	}
	/**
	 * 登陆
	 * @param u
	 * @param needCode
	 * @param source
	 * @return
	 */
	private boolean login(UserModel u,boolean needCode,String source){
		
		List<BasicNameValuePair> data = new ArrayList<BasicNameValuePair>();
        String tmp;
        String url = "http://wappass.baidu.com/passport/";
        if (needCode)
        {
        	data.add(new BasicNameValuePair("login_verifycode", checkcode));
        	data.add(new BasicNameValuePair("login_bdverify", regex(source,"(?<=(name=\"login_bdverify\" value=\"))[\\d\\w]+")));
        	data.add(new BasicNameValuePair("login_username", u.id));
        	data.add(new BasicNameValuePair("login_loginpass", u.pwd));
        	data.add(new BasicNameValuePair("login_save", regex(source,"(?<=(<input type=\"hidden\" name=\"login_save\" value=\"))[\\d\\w]+")));
        	data.add(new BasicNameValuePair("login_bdstoken", regex(source,"(?<=(<input type=\"hidden\" name=\"login_bdstoken\" value=\"))[\\d\\w]+")));
        	data.add(new BasicNameValuePair("login_bdtime", regex(source,"(?<=(<input type=\"hidden\" name=\"login_bdtime\" value=\"))[\\d\\w]+")));
        	data.add(new BasicNameValuePair("login_is_wid", regex(source,"(?<=(<input type=\"hidden\" name=\"login_is_wid\" value=\"))[\\d\\w]+")));
        	data.add(new BasicNameValuePair("login", regex(source,"(?<=(<input type=\"hidden\" name=\"login\" value=\"))[\\d\\w]+")));
        	data.add(new BasicNameValuePair("uid", regex(source,"(?<=(<input type=\"hidden\" name=\"uid\" value=\"))[\\d\\w]+")));
        	data.add(new BasicNameValuePair("aaa", "登录"));
        }
        else
        {
        	//String t=hm.getHTML("https://passport.baidu.com/v2/api/?getapi&class=login&tpl=mn&tangram=true", "uft-8");
        	
        	data.add(new BasicNameValuePair("login_username", u.id));
    		data.add(new BasicNameValuePair("login_loginpass", u.pwd));
    		data.add(new BasicNameValuePair("login_save", "0"));
    		data.add(new BasicNameValuePair("can_input", "0"));
    		data.add(new BasicNameValuePair("login", "yes"));
    		data.add(new BasicNameValuePair("aaa", "登录"));
    		
        }
        tmp =hm.postHTML(url, "utf-8", data);
        if (tmp.contains("验证码"))
        {
            checkcode = null;

            Message msg=new Message();
			msg.what=MESSAGE_SHOW_CHECKCODE;
			msg.obj=regex(tmp, "http://wappass.baidu.com/cgi-bin/genimage[^\"]*");
			handler.sendMessage(msg);
            
            while (true)
            {
                if (checkcode != null)
                {
                    return login(u,true, tmp);

                }
                
                try {
					Thread.currentThread();
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
            }
        }
        else if (tmp.contains("注册") || tmp.contains("登录错误"))
        {
            return false;
        }
        else
        {
            return true;
        }
	}
	/**
	 * 使用WEB浏览接口登陆
	 * @param u
	 * @param needCode
	 * @param source
	 * @return
	 */
		private boolean loginWEB(UserModel u,boolean needCode,String source){
		
		List<BasicNameValuePair> data = new ArrayList<BasicNameValuePair>();
		String s0=hm.getHTML("http://www.baidu.com/cache/user/html/login-1.2.html", "utf-8");
		String s1=hm.getHTML("https://passport.baidu.com/v2/api/?getapi&class=login&tpl=mn&tangram=true", "utf-8");
		String token=regex(s1,"(?<=(login_token='))[^']+");
		data.add(new BasicNameValuePair("tpl", "mn"));
		data.add(new BasicNameValuePair("index", "0"));
		data.add(new BasicNameValuePair("safeflg", "0"));
		data.add(new BasicNameValuePair("staticpage", "http://www.baidu.com/cache/user/html/jump.html"));
		data.add(new BasicNameValuePair("callback", "parent.bdPass.api.login._postCallback"));
		data.add(new BasicNameValuePair("username", u.id));
		data.add(new BasicNameValuePair("password", u.pwd));
		data.add(new BasicNameValuePair("loginType", "1"));
		data.add(new BasicNameValuePair("isPhone", "false"));
		data.add(new BasicNameValuePair("charset", "utf-8"));
		data.add(new BasicNameValuePair("token",token));
		String s2=hm.postHTML("https://passport.baidu.com/v2/api/?login", "utf-8", data);
		String r=regex(s2, "(?<=(jump.html\\?error=))\\d+");
		
		if(r.equals("0")){
			return true;
		}else{
			Message msg=new Message();
			msg.what=MESSAGE_LOG;
			if(r.equals("1"))
				{msg.obj="账号格式错误！\n";handler.sendMessage(msg);}
			if(r.equals("2"))
				{msg.obj="账号不存在！\n";handler.sendMessage(msg);}
			if(r.equals("4"))
				{msg.obj="密码错误！\n";handler.sendMessage(msg);}
			
			return false;
		}
      
	}
	/**
	 * 正则匹配
	 * @param str
	 * @param regex
	 * @return
	 */
	private String regex(String str, String regex) {
		// TODO Auto-generated method stub
		
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		if( m.find()){
			return(m.group());
		}
		return "";
	}
	/**
	 * url编码
	 * @param url
	 * @param code
	 * @return
	 */
	private String  urlencode(String url,String code){
		
		try {
			return URLEncoder.encode(url,code);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return url;
		}
	
	}
	/**
	 * unicode转字符串
	 * @param unicode unicode串
	 * @return 字符串
	 */
	private static String unicode2str(String unicode){
		if(!unicode.contains("\\u"))
			return unicode;
		String[] t=unicode.replace("\\", "").split("u");
		String[] s = new String[t.length-1];
		String r="";
		for(int i=1;i<t.length;i++){
			s[i-1]=t[i].replace(";", "");
		}
		for (int i = 0; i < s.length; i++) {
			char c = (char) Integer.valueOf(s[i], 16).intValue();
			r+=c;
		}
		return r;
	}
	/**
	 * 执行签到
	 * @param name
	 * @param i
	 * @return
	 */
	private boolean sign(TbModel tb,int i){
		String name=tb.name+"吧";
		String tbs = null;
		String html;
		html = hm.getHTML("http://tieba.baidu.com/dc/common/tbs",
				"utf-8");
		if (html.contains("tbs")) {

			tbs = html.substring(html.indexOf("tbs\":\"") + 6);
			tbs = tbs.substring(0, tbs.indexOf("\""));
			//System.err.println(tbs);

			ArrayList<BasicNameValuePair> data = new ArrayList<BasicNameValuePair>();
			data.add(new BasicNameValuePair("ie", "utf-8"));
			data.add(new BasicNameValuePair("kw", name.substring(0,name.length()-1)));
			data.add(new BasicNameValuePair("tbs", tbs));
			html = hm.postHTML("http://tieba.baidu.com/sign/add","utf-8", data);
			//System.out.println(html);
			JSONObject jsonObject;
			int  no=-1; 
		    String error=null; 
		    int rank=-1;
			try {
				jsonObject = new JSONObject(html);
				 	
				    no = jsonObject.getInt("no"); 
				    error = jsonObject.getString("error"); 
				    Log.e("JSON", "no="+no+",error="+error);
				    if(no==0){
				    	rank=jsonObject.getJSONObject("data").getJSONObject("uinfo").getInt("user_sign_rank");
				    	 Log.e("JSON", "rank="+rank);
				    }else{
				    	Message msg=new Message();
						msg.what=MESSAGE_LOG;
						if(error!=null &&error.endsWith("\u3002"))
							error=error.substring(0,error.length()-1);
						msg.obj="["+i+"] "+name+"("+tb.exp+"/lv."+tb.level+")...失败("+error+")！\n";
						handler.sendMessage(msg);
						return false;
				    }
			} catch (Exception e) {
				// TODO Auto-generated catch block
				 rank=-1;
			} 

			if(rank>-1){
				Message msg=new Message();
				msg.what=MESSAGE_LOG;
				msg.obj="["+i+"] "+name+"("+tb.exp+"/lv."+tb.level+")...成功("+rank+")！\n";
				handler.sendMessage(msg);
				return true;
			}else{
				Message msg=new Message();
				msg.what=MESSAGE_LOG;
				msg.obj="["+i+"] "+name+"("+tb.exp+"/lv."+tb.level+")...异常(返回数据异常，无法判断是否成功)！\n";
				handler.sendMessage(msg);
				return false;
			}
		}
		Message msg=new Message();
		msg.what=MESSAGE_LOG;
		msg.obj="["+i+"] "+name+"("+tb.exp+"/lv."+tb.level+")...跳过(无法获取必要参数)！\n";
		handler.sendMessage(msg);
		return false;
	}
	/**
	 * 检查运行条件
	 * @return
	 */
	private boolean check(){

		if(list==null||list.size()<1)
		{
			Toast.makeText(mainActivity.this, "未设置账号！", 2000).show();
			return false;
		}
		
		return true;
	}

	/**
	 * 读取签到用户列表
	 */
	private void getData(){
		SqliteMgr sm=new SqliteMgr(this);
		list=sm.GetSignRecords();
		String t="";
		for(UserModel u:list){
			t+=u.id+";";
			
		}
		et_user.setText(t);
		et_user.setSelection(t.length());
	}
	/**
	 * 保存声明是否显示
	 * @param b
	 * @return
	 */
	 private boolean saveAnn(Boolean b){
			SharedPreferences sharep = this.getSharedPreferences("norofox_config", 0);
	    	Editor ed = sharep.edit();
	    	ed.putBoolean("showAnn", b);
	    	ed.commit();
	    	return true;
		}
	 /**
	  * 读取声明是否显示
	  * @return
	  */
	private boolean getAnn(){
			SharedPreferences sharep = this.getSharedPreferences("norofox_config", 0);
			
			return sharep.getBoolean("showAnn", true);
	    	
		}
	
	//菜单
	@Override  
    public boolean onCreateOptionsMenu(Menu menu) {  
    super.onCreateOptionsMenu(menu);  
    menu.add(0, R.id.user, 1, "账号");
    menu.add(0, R.id.setting, 1, "设置");
    menu.add(0, R.id.about, 1, "关于");
    menu.add(0, R.id.exit, 1, "退出");
   
    return true;  
    } 
    @Override  
    public boolean onOptionsItemSelected(MenuItem item) {  
    switch (item.getItemId()) {  
   
    case R.id.user:
    	Intent intent =new Intent();
		intent.setClass(mainActivity.this,userActivity.class);
		startActivityForResult(intent,0);
    	return true;
    case R.id.about:
    	
    	 Dialog  dialogAbout = new AlertDialog.Builder(this)
   	   // 	.setIcon(iconId)
   			.setTitle("关于本程序")
   			.setMessage("当前版本:"+version+"\n最新版本:"+newversion+"\n\n作者：不懂浪漫的狐狸\n常去狐狸的百度空间看看会有惊喜哦\nhttp://hi.baidu.com/new/nrfox")
   			.setPositiveButton("确定",
   					new DialogInterface.OnClickListener() {
   						public void onClick(DialogInterface dialog,
   								int whichButton) {
   						}
   					})
   					.create();
    	 dialogAbout.show();
    	return true;
    case R.id.exit:
    	finish();
    	return true;	
    case R.id.setting:
    	startActivity(new Intent(mainActivity.this,settingActivity.class));
    	return true;
    }  
    
    return false;  
    } 
    /**
     * 显示声明
     */
    private void showAnno(){
    	
    	Dialog  dialogAbout = new AlertDialog.Builder(this)
    	   // 	.setIcon(iconId)
    			.setTitle("用前必读")
    			.setMessage(getString(R.string.notice))
    			.setPositiveButton("同意",
    					new DialogInterface.OnClickListener() {
    						public void onClick(DialogInterface dialog,
    								int whichButton) {
    							saveAnn(false);
    						}
    					})
    					.setNegativeButton("不同意",
    					new DialogInterface.OnClickListener() {
    						public void onClick(DialogInterface dialog,
    								int whichButton) {
    							finish();
    						}
    					}).create();
     	 dialogAbout.show();
    }
    
    @Override  
       protected void onActivityResult(int requestCode, int resultCode, Intent data)  
       {  
    	getData();
        }  
    /**
     * 显示验证码输入框
     * @param url
     */
private void showCheckcode(String url){
		
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.dialog_checkcode,(ViewGroup) findViewById(R.id.dialog));
		EditText et_checkcode=(EditText) layout.findViewById(R.id.et_checkcode);
		ImageView iv_checkcode=(ImageView) layout.findViewById(R.id.iv_checkcode);
	
		for(int i=0;i<3;i++){
			try {
				iv_checkcode.setImageBitmap(returnBitMap(url));
				break;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				if(i==2)
					et_checkcode.setText("不显示验证码？在设置里选择接口二试一下");
			}
		}
		
		
		new AlertDialog.Builder(this)
			.setTitle("输入验证码")
			.setView(layout)
		    .setPositiveButton("确定", new android.content.DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					AlertDialog alertDialog = (AlertDialog)dialog;
					String code=((EditText)alertDialog.findViewById(R.id.et_checkcode)).getText().toString();
					checkcode=code;
						
				}
		    })
		   // .setNegativeButton("取消", null)
		    .show();

	}
/**
 * 验证码
 * @param url
 * @return
 * @throws IOException 
 */
	public Bitmap returnBitMap(String url) throws IOException {
		URL myFileUrl = null;
		Bitmap bitmap = null;
			myFileUrl = new URL(url);

			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept-Encoding","");
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		
		return bitmap;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			//super.onKeyDown(keyCode, event);
			showDialog(APP_EXIT);
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}
    int APP_EXIT=1;
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == APP_EXIT) {
			return new AlertDialog.Builder(mainActivity.this)
					.setMessage("是否退出程序?")
					.setTitle("确认")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									android.os.Process
											.killProcess(android.os.Process
													.myPid());
									finish();

								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();

								}
							}).create();

		}
		return null;

	}
	private int getLevel(String exp){
		int r=-1;
		try{
		int e=Integer.parseInt(exp);
		
		}catch (Exception e) {
			// TODO: handle exception
		}
		return r;
	}
	private int getLevel(int exp){
		if(exp<5)
			return 1;
		else if(exp<15)
			return 2;
		else if(exp<30)
			return 3;
		else if(exp<50)
			return 4;
		else if(exp<100)
			return 5;
		else if(exp<200)
			return 6;
		else if(exp<500)
			return 7;
		else if(exp<1000)
			return 8;
		else if(exp<2000)
			return 9;
		else if(exp<3000)
			return 10;
		else if(exp<6000)
			return 11;
		else if(exp<10000)
			return 12;
		else if(exp<18000)
			return 13;
		else if(exp<30000)
			return 14;
		else if(exp<60000)
			return 15;
		else if(exp<100000)
			return 16;
		else if(exp<300000)
			return 17;
		else
			return 18;
	}
	
	private void loadBG(){
		if(spm.getImagePath()!=null){
			File f = new File(spm.getImagePath());
			System.out.println(f.length());
			if (f.length() > 1000000) {
				spm.setImagePath(null);
			} else {
				try {
					Bitmap bit = BitmapFactory.decodeFile(spm.getImagePath());
					rl_main.setBackgroundDrawable(new BitmapDrawable(bit));
				} catch (Exception e) {
					// TODO: handle exception
					Toast.makeText(this, e.getMessage(), 2000).show();
					spm.setImagePath(null);
				}
			}
		}else{
			rl_main.setBackgroundResource(R.drawable.bj_pj);
		}
	}
	/**
	 * 检查更新
	 * 
	 */
	private String checkupdate(String url){
		HttpMgr hm=new HttpMgr();
    	try{
    		String tmp=hm.getHTML(url, "utf-8");
    		RegMgr rm=new RegMgr(tmp, "(?<=(tieba_sign_android=))[\\.\\d]+");
    			newversion=rm.get(0);
    			if(newversion.compareTo(version)>0){
    				Message msg=new Message();
					msg.what=MESSAGE_NEW_VERSION;
					msg.obj="有新版本了("+newversion+")，去狐狸的空间找找吧！";
					handler.sendMessage(msg);
    				
    		}
    			String available=regex(tmp, "tieba_sign_android_available=\\d");
    			if(available.contains("=0")){
    				spm.setAvailable(false);
    				Message msg=new Message();
					msg.what=MESSAGE_NOT_AVAILABLE;
					handler.sendMessage(msg);
    			}else{
    				spm.setAvailable(true);
    			}
    			return tmp;
    	}catch (Exception e) {
			// TODO: handle exception
    		GFAgent.onError(mainActivity.this, e);
    		return null;
		}
	
	}
/**
 * WAP方式读取贴吧
 */
	private ArrayList<TbModel> getTbWap(){
		ArrayList<TbModel> tbs=new ArrayList<TbModel>();
		String htm;
		htm=hm.getHTML("http://wapp.baidu.com", "utf-8");
		String url=RegMgr.regex(htm, "/f/q[^\"]*?tab=favorite");
		uid=RegMgr.regex(url, "(?<=(/f/)).*(?=(/))");
		htm=hm.getHTML("http://wapp.baidu.com"+url, "utf-8");
		
		RegMgr rm=new RegMgr(htm, "<tr.*?/tr>");
		for(String tbstr:rm.getList()){
			String n=RegMgr.regex(tbstr, "(?<=(>))[^<]*?(?=(</a>))");
			String e=RegMgr.regex(tbstr, "(?<=(经验值))\\d+");
			String l=RegMgr.regex(tbstr, "(?<=(等级))\\d+");
			String u=RegMgr.regex(tbstr, "(?<=(href=\"))[^\"]*");
			if(n==null||n.length()<1)
				continue;
			TbModel tb=new TbModel(n, e, l);
			tb.url=u.replace("&amp;", "&");
			tbs.add(tb);
		}
		return tbs;
	}
	private String uid;
	/**
	 * wap签到
	 */
	private boolean signWap(TbModel tb,int i){
		String name=tb.name+"吧";
		String tbs = null;
		String htm;
		htm = hm.getHTML("http://wapp.baidu.com/"+uid+"/"+tb.url,
				"utf-8");
		if(htm.contains("已签到"))
		{
			Message msg=new Message();
			msg.what=MESSAGE_LOG;
			msg.obj="["+i+"] "+name+"("+tb.exp+"/lv."+tb.level+")...跳过(已签到)！\n";
			handler.sendMessage(msg);
			return false;
		}
		String url=RegMgr.regex(htm, "[^\"]*/sign\\?[^\"]*");
		if(url==null||url.length()<1){
			Message msg=new Message();
			msg.what=MESSAGE_LOG;
			msg.obj="["+i+"] "+name+"("+tb.exp+"/lv."+tb.level+")...跳过(未识别签到)！\n";
			handler.sendMessage(msg);
			return false;
		}
		htm=hm.getHTML("http://wapp.baidu.com"+url.replace("&amp;", "&"),"utf-8");
			if(htm.contains("签到成功")){
				String exp;
				String tmp=RegMgr.regex(htm, "签到成功.*?\\d+<");
				exp="+"+RegMgr.regex(tmp,"\\d+");
				Message msg=new Message();
				msg.what=MESSAGE_LOG;
				msg.obj="["+i+"] "+name+"("+tb.exp+"/lv."+tb.level+")...成功("+exp+")！\n";
				handler.sendMessage(msg);
				return true;
			}else{
				Message msg=new Message();
				msg.what=MESSAGE_LOG;
				msg.obj="["+i+"] "+name+"("+tb.exp+"/lv."+tb.level+")...异常(返回数据异常，无法判断是否成功)！\n";
				handler.sendMessage(msg);
				return false;
			}
	}
	//机锋统计
	@Override
	protected void onResume(){
	super.onResume();
	loadBG();
	skipList=spm.getSkipList();
	//GFAgent.onResume (this);
	}
	@Override
	protected void onPause(){
	super.onPause();
	loadBG();
	skipList=spm.getSkipList();
	//GFAgent.onPause(this);
	}
}