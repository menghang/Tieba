package norofox.tieba.sign.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * web交互核心
 * @author 不懂浪漫的狐狸
 *
 */
public class HttpMgr {
	public  HttpClient hc = null;
	boolean EXPECT_CONTINUE=false;
	public ArrayList<BasicNameValuePair> headers=null;
	public HttpMgr(){
		hc = new DefaultHttpClient();
		//hc.getParams().setParameter("User-Agent", "Mozilla/4.0 (compatible; MSIE 10.0; Windows NT 5.1)");
		headers=new ArrayList<BasicNameValuePair>();
		headers.add(new BasicNameValuePair("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/4.0; QQDownload 717; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E)"));
		//headers.add(new BasicNameValuePair("Accept", "*/*"));
		headers.add(new BasicNameValuePair("Accept-Language", "zh-CN"));
		//headers.add(new BasicNameValuePair("Accept-Encoding", "gzip, deflate"));
		//headers.add(new BasicNameValuePair("", ""));
		//headers.add(new BasicNameValuePair("", ""));

		
//		hc = new DefaultHttpClient(new ThreadSafeClientConnManager());
		HttpParams params = hc.getParams();
	     HttpConnectionParams.setConnectionTimeout(params, 5000);
	     HttpConnectionParams.setSoTimeout(params, 10000);
	     HttpClientParams.setCookiePolicy(hc.getParams(), CookiePolicy.BROWSER_COMPATIBILITY);   
	     params.setParameter("http.protocol.cookie-policy", CookiePolicy.BROWSER_COMPATIBILITY);
	}
	
	/**
	 * GET请求
	 * @param URL
	 * @param code
	 * @return
	 */
	public  String getHTML(String URL, String code) {
		HttpGet httpget = new HttpGet(URL);
		httpget.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, EXPECT_CONTINUE);
		for(BasicNameValuePair b:headers){
			httpget.setHeader(b.getName(), b.getValue());
		}
		HttpResponse response = null;
		String tmphttpstr = "";
		try {
			response = hc.execute(httpget);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			tmphttpstr = "Err:1:"+e.getMessage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			tmphttpstr = "Err:2:"+e.getMessage();
		}

		if (response != null) {
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				try {
					tmphttpstr += new String(EntityUtils.toByteArray(response
									.getEntity()), code);
					// errcode=0;
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
					tmphttpstr = "Err:3:"+e.getMessage();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
					tmphttpstr = "Err:4:"+e.getMessage();
				}
			}
		}

		return tmphttpstr;
	}
	/**
	 * 下载图片
	 * @param URL
	 * @return
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public  Bitmap getImg(String URL) throws IllegalStateException, IOException {
		HttpGet httpget = new HttpGet(URL);
		httpget.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, EXPECT_CONTINUE);
		for (BasicNameValuePair b : headers) {
			httpget.setHeader(b.getName(), b.getValue());
		}
		HttpResponse response = null;
		response = hc.execute(httpget);
		if (response != null) {
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				return BitmapFactory.decodeStream(response.getEntity().getContent());
			}
		}

		return null;
	}
	/**
	 * post请求
	 * @param URL
	 * @param code
	 * @param data
	 * @return
	 */
	public  String postHTML(String URL,String code,List<BasicNameValuePair> data){
		HttpPost httppost=new HttpPost(URL);
		httppost.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, EXPECT_CONTINUE);
		for(BasicNameValuePair b:headers){
			httppost.setHeader(b.getName(), b.getValue());
		}
		try {
			httppost.setEntity( new UrlEncodedFormEntity(data,HTTP.UTF_8));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		HttpResponse response = null;
		
		String tmphttpstr = "";
		try {
			response = hc.execute(httppost);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			tmphttpstr = "Err:1:"+e.getMessage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			tmphttpstr = "Err:2:"+e.getMessage();
		}
		if (response != null) {
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				try {
					tmphttpstr += new String(EntityUtils.toByteArray(response
									.getEntity()), code);
					// errcode=0;
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
					tmphttpstr = "Err:3:"+e.getMessage();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
					tmphttpstr = "Err:4:"+e.getMessage();
				}
			}
		}
		return tmphttpstr;
	}
	
	public static  String encode(String str) {
		StringBuffer sb = new StringBuffer(1024);
		sb.setLength(0);
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c > 255) {
				String tmp=c+"";
				try {
					sb.append(URLEncoder.encode(tmp,"gb2312"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} else {
				sb.append(c);
			}
		}
		return (new String(sb));
	}
}
