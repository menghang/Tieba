package norofox.tieba.sign.core;

import java.util.ArrayList;

import norofox.tieba.sign.UserModel;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
/**
 * 数据库管理
 * @author SharedPreferences
 *
 */
public class SqliteMgr {
	MySQLiteHelper myHelper;
	
	public SqliteMgr(Context c){
		myHelper=new MySQLiteHelper(c, "data.db", null, 1);
	}
	public void AddRecord(UserModel u){
		
		SQLiteDatabase db = myHelper.getWritableDatabase();
		String sql = "insert into user(uid,id,pwd,sign) values('" + u.uid
				+ "','" + u.id + "','" + u.pwd + "'," + (u.sign ? 1 : 0) + ")";
		//Log.e("SQL-INSERT", sql);
		db.execSQL(sql);
		db.close();
	}
	public void UpdateRecord(UserModel u){
		SQLiteDatabase db = myHelper.getWritableDatabase();
		String sql = "update user set id='" + u.id + "',pwd='" + u.pwd
				+ "',sign=" + (u.sign ? 1 : 0) + " where uid='" + u.uid + "'";
		//Log.e("SQL-UPDATE", sql);
		db.execSQL(sql);
		db.close();
		
	}
	public void DeleteRecord(UserModel u){
		SQLiteDatabase db = myHelper.getWritableDatabase();
		String sql = "delete from user where uid='"+u.uid+"'";
		//Log.e("SQL-DELETE", sql);
		db.execSQL(sql);
		db.close();
	
	}
	public ArrayList<UserModel> GetAllRecords(){
		
		return GetRecords("SELECT * FROM user order by id asc");
		
	}
	
	public ArrayList<UserModel> GetSignRecords(){

		return GetRecords("SELECT * FROM user where sign=1 order by id asc limit 0,10");
		
	}
	public ArrayList<UserModel> GetRecords(String sql){
		ArrayList<UserModel> list=new ArrayList<UserModel>();
		SQLiteDatabase db = myHelper.getReadableDatabase();
    	
    	Cursor cursor = db.rawQuery(sql,null);
    	int nameIndex = cursor.getColumnIndex("id");   
    	int urlIndex = cursor.getColumnIndex("pwd"); 
    	int descIndex = cursor.getColumnIndex("sign");   
    	int uidIndex = cursor.getColumnIndex("uid");   

    	for (cursor.moveToFirst();!(cursor.isAfterLast());cursor.moveToNext()) {   
    		try{
        	String id=cursor.getString(nameIndex);
        	String pwd=cursor.getString(urlIndex);
        	boolean sign=false;
        	int s=cursor.getInt(descIndex);   
        	String uid=cursor.getString(uidIndex);   
        	if(s==1)
        		sign=true;
        	list.add(new UserModel(id,pwd,sign,uid));
    		}
    		catch (Exception e) {
				// TODO: handle exception
			}
    	}     
    	cursor.close();
    	db.close();
		return list;
		
	}
	
}
class MySQLiteHelper extends SQLiteOpenHelper {
	// 调用父类构造器
	public MySQLiteHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	/**
	 * 当数据库首次创建时执行该方法，一般将创建表等初始化操作放在该方法中执行. 重写onCreate方法，调用execSQL方法创建表
	 * */
	@Override
	public void onCreate(SQLiteDatabase db) {
		//db.execSQL("drop table if exists user");
		db.execSQL("create table if not exists user(uid varchar primary key,id varchar,pwd varchar,sign varchar)");
	}

	// 当打开数据库时传入的版本号与当前的版本号不同时会调用该方法
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}