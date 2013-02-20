package norofox.tieba.sign;

import java.io.File;
import java.util.ArrayList;
import norofox.tieba.sign.core.SharedPreferencesManager;
import norofox.tieba.sign.core.SqliteMgr;

import com.gfan.sdk.statitistics.GFAgent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import android.util.Log;
import android.view.ContextMenu;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
/**
 * 用户管理
 * @author 不懂浪漫的狐狸
 *
 */
public class userActivity extends Activity {
    /** Called when the activity is first created. */
	ArrayList<UserModel> clist ;
	SqliteMgr sm=new SqliteMgr(this);
	String randomSource="abcdefgABCDEFH0123456789";
	ListView list;
	int selectedindex=-1;
	LinearLayout ll_main;
    SharedPreferencesManager spm;
    @Override
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);   
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.list);
        spm=new SharedPreferencesManager(this.getSharedPreferences("norofox_config", 0));
        ll_main=(LinearLayout)findViewById(R.id.ll_main);
       loadBG();
        
        creatlist();
       list=(ListView) findViewById(R.id.MyListView);
        showlist();
        if(clist==null||clist.size()<1){
         	showEditor(null);
         }

	   
    }  
    UserAdapter adapter;
    private void showlist(){
    	
    	adapter = new UserAdapter(userActivity.this);
		adapter.setList(clist);
		list.setAdapter(adapter);

        list.setOnItemClickListener(new OnItemClickListener() {   
  
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,   
                    long arg3) {     
            	//Log.e("选项的UID", clist.get(arg2).uid+"");
            	UserModel u=clist.get(arg2);
            	u.sign=!u.sign;
            	updatedb(u);
            	creatlist();
    			showlist();
            }   
        });  
        list.setOnItemLongClickListener(new OnItemLongClickListener() {   
        	  
        	        public boolean onItemLongClick(AdapterView<?> arg0, View arg1,   
        	                int arg2, long arg3) {   
        	            // TODO Auto-generated method stub   
        	            Log.e("设置指针变量", arg2+"");   
        	            selectedindex=arg2;
        	            return false;   
        	        }   
        	            
        	  
        	      }); 
        list.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				// TODO Auto-generated method stub
				menu.setHeaderTitle("菜单");
				menu.add(0, 1, 0, "新建");
				menu.add(0, 0, 0, "修改:" + clist.get(selectedindex).id);

				menu.add(0, 2, 0, "删除:" + clist.get(selectedindex).id);
				
			}
		});
    }
    
   
    private void creatlist(){
    	clist = sm.GetAllRecords();
    	
    	 
    }
    

	private void updatedb(UserModel u) {
		sm.UpdateRecord(u);
	};

	private void insertdb(UserModel u) {
		sm.AddRecord(u);
	};
	private void deletedb(UserModel u) {
		sm.DeleteRecord(u);
	};

    @Override  
    public boolean onContextItemSelected(MenuItem item) {   
        //setTitle("点击了长按菜单里面的第"+item.getItemId()+"个项目");  
        switch(item.getItemId()){
        case 0:
        	showEditor(clist.get(selectedindex));
        	break;
        case 1:
        	showEditor(null);
        	break;
        case 2:
        	deletedb(clist.get(selectedindex));
        	creatlist();
			showlist();
        	break;
        }
        return super.onContextItemSelected(item);   
    }   

   
    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent intent){

	}
    
	
	/**
	 * 生成指定范围指定长度的随机字符串
	 * @param source 参与随机的字符，如"abcdefghijklmnopqrstuvwxyz"
	 * @param length 长度
	 * @return 随机字符串
	 */
	private String randomStr(String source,int length){
		try{
			char s[]=source.toCharArray();
			String r="";
			for(int i=0;i<length;i++){
				int a=(int)(Math.random()*s.length);
				r+=s[a];
			}
			return r;
		}catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}
	
	private void showEditor(final UserModel um){
		
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.dialog_editor,(ViewGroup) findViewById(R.id.dialog));
		EditText et_id=(EditText) layout.findViewById(R.id.et_id);
		EditText et_pwd=(EditText) layout.findViewById(R.id.et_pwd);
		CheckBox cb_sign=(CheckBox) layout.findViewById(R.id.cb_sign);
		if(um!=null){
			et_id.setText(um.id);
			et_id.setSelection(um.id.length());
			et_pwd.setText(um.pwd);
			cb_sign.setChecked(um.sign);
		}
		new AlertDialog.Builder(this)
			.setTitle("编辑账号")
			.setView(layout)
		    .setPositiveButton("确定", new OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					AlertDialog alertDialog = (AlertDialog)dialog;
					String newid=((EditText)alertDialog.findViewById(R.id.et_id)).getText().toString();
					String newpwd=((EditText)alertDialog.findViewById(R.id.et_pwd)).getText().toString();
					boolean newsign=((CheckBox)alertDialog.findViewById(R.id.cb_sign)).isChecked();
						if (newid.trim().length() > 0 && newpwd.length() > 0) {
							if (um != null) {
								um.id = newid.trim();
								um.pwd = newpwd;
								um.sign = newsign;
								updatedb(um);
							} else {
								UserModel u = new UserModel(newid, newpwd,
										newsign, randomStr(randomSource, 6));
								insertdb(u);
							}
							creatlist();
							showlist();
						}else{
							
						Toast.makeText(userActivity.this, "输入有误，数据未修改！", 2000).show();
						}
				}
		    })
		    .setNegativeButton("取消", null)
		    .show();

	}
	
	//菜单
		@Override  
	    public boolean onCreateOptionsMenu(Menu menu) {  
	    super.onCreateOptionsMenu(menu);  
	    MenuInflater inflater = getMenuInflater();  
	    inflater.inflate(R.layout.menu2, menu);  return true;  
	    } 
	    @Override  
	    public boolean onOptionsItemSelected(MenuItem item) {  
	    switch (item.getItemId()) {  
	   
	    case R.id.back:
	    	finish();
	    	
	    	return true;
	    case R.id.add:
	    	showEditor(null);
	    	return true;
	 
	    }  
	    return false;  
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
						ll_main.setBackgroundDrawable(new BitmapDrawable(bit));
					} catch (Exception e) {
						// TODO: handle exception
						Toast.makeText(this, e.getMessage(), 2000).show();
						spm.setImagePath(null);
					}
				}
			}
	}
	
	//机锋统计
		@Override
		protected void onResume(){
		super.onResume();
		loadBG();
		//GFAgent.onResume (this);
		}
		@Override
		protected void onPause(){
		super.onPause();
		//GFAgent.onPause(this);
		}

    }