package norofox.tieba.sign.core;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
/**
 * SharedPreferences管理
 * @author 不懂浪漫的狐狸
 *
 */
public class SharedPreferencesManager {
	private SharedPreferences sp;
	public SharedPreferencesManager(SharedPreferences sp){
		this.sp=sp;
	}
	public void setServiceon(boolean on) {

		Editor editor = sp.edit();// 获取编辑器
		editor.putBoolean("serviceon", on);
		editor.commit();// 提交修改
	}

	public boolean getServiceon() {
		return sp.getBoolean("serviceon", false);
	}
	/**
	 * 设置自动签到时间
	 * @param t
	 */
	public void setAutoTime(String t) {

		Editor editor = sp.edit();// 获取编辑器
		editor.putString("AutoTime", t);
		editor.commit();// 提交修改
	}

	public String getAutoTime() {
		return sp.getString("AutoTime", null);
	}
	/**
	 * 设置背景图片路径
	 * @param t
	 */
	public void setImagePath(String t) {

		Editor editor = sp.edit();// 获取编辑器
		editor.putString("ImagePath", t);
		editor.commit();// 提交修改
	}

	public String getImagePath() {
		return sp.getString("ImagePath", null);
	}
	/**
	 * 设置执行签到日期
	 * @param t
	 */
	public void setSignDay(String t) {

		Editor editor = sp.edit();// 获取编辑器
		editor.putString("SignDay", t);
		editor.commit();// 提交修改
	}
	public String getSignDay() {
		return sp.getString("SignDay", null);
	}
	/**
	 * 设置是否可以正常使用
	 * @param t
	 */
	public void setAvailable(Boolean b) {

		Editor editor = sp.edit();// 获取编辑器
		editor.putBoolean("Available", b);
		editor.commit();// 提交修改
	}
	public Boolean getAvailable() {
		return sp.getBoolean("Available", true);
	}
	/**
	 * 不签到的贴吧
	 * @param t
	 */
	public void setSkipList(String t) {

		Editor editor = sp.edit();// 获取编辑器
		editor.putString("SkipList", t);
		editor.commit();// 提交修改
	}
	public String getSkipList() {
		return sp.getString("SkipList", "--");
	}
	/**
	 * 登陆接口
	 * @param t
	 */
	public void setLoginMethod(int t) {

		Editor editor = sp.edit();// 获取编辑器
		editor.putInt("LoginMethod", t);
		editor.commit();// 提交修改
	}
	public int getLoginMethod() {
		return sp.getInt("LoginMethod", 1);
	}
	/**
	 * 签到间隔
	 * @param t
	 */
	public void setSignDelay(int t) {

		Editor editor = sp.edit();// 获取编辑器
		editor.putInt("SignDelay", t);
		editor.commit();// 提交修改
	}
	public int getSignDelay() {
		return sp.getInt("SignDelay", 0);
	}
	/**
	 * 签到间隔随机因子
	 * @param t
	 */
	public void setSignRand(int t) {

		Editor editor = sp.edit();// 获取编辑器
		editor.putInt("SignRand", t);
		editor.commit();// 提交修改
	}
	public int getSignRand() {
		return sp.getInt("SignRand", 0);
	}
}
