package norofox.tieba.sign.core;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  正则处理类
 * @author 不懂浪漫的狐狸
 *
 */
public class RegMgr {
	public boolean isfind=false;
	ArrayList<String> result=new ArrayList<String>();
	public RegMgr(String str,String regex){
		result=regexAll(str,regex);
		if(result.size()>0)
			isfind=true;
	}
	/**
	 * 是否匹配成功
	 * @return
	 */
	public boolean isFind(){
		return isfind;
	}
	
	/**
	 * 读取匹配结果
	 * @param i
	 * @return
	 */
	public String get(int i){
		if(result.size()<=i)
			return "";
		else
			return result.get(i);
	}
	/**
	 * 返回匹配结果集arraylist<string>
	 * @return
	 */
	public ArrayList<String> getList(){
		return result;
	}
	/**
	 * 完全匹配
	 * @param str
	 * @param regex
	 * @return
	 */
	public static  ArrayList<String> regexAll(String str, String regex) {
		ArrayList<String> list=new ArrayList<String>();
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		while( m.find()){
			list.add(m.group());
		}
		return list;
	}
	/**
	 * 匹配一次
	 * @param str
	 * @param regex
	 * @return
	 */
	public static  String regex(String str, String regex) {
		String r="";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		if( m.find()){
			r=(m.group());
		}
		return r;
	}
}
