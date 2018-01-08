package com.enation.app.shop.component.payment.plugin.weixin.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.google.protobuf.TextFormat.ParseException;

/**
 * post是sslsf方式的安全请求
 * httpclient方式的post和get的请求
 * @author yexf
 *
 */
public class HttpXmlClient {  
      
	public static String postXml(String url, String requestXML) {  
        //DefaultHttpClient httpclient = new DefaultHttpClient();  
        
        // Trust own CA and all self-signed certs
        SSLContext sslcontext = null;
        FileInputStream instream = null;
        String body = null;
        CloseableHttpClient httpclient = null;
		try {
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			instream = new FileInputStream(new File(
					"C:/Users/Administrator/Desktop/cert1/apiclient_cert.p12"));
			keyStore.load(instream, "1337386301".toCharArray());
			
			sslcontext = SSLContexts.custom()
					.loadKeyMaterial(keyStore, "1337386301".toCharArray())
					.build();
			
			// Allow TLSv1 protocol only
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
					sslcontext,
					new String[] { "TLSv1" },
					null,
					SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
			httpclient = HttpClients.custom()
					.setSSLSocketFactory(sslsf).build();
			body = null;
			HttpPost post = new HttpPost(url);//请求地址
			StringEntity entity = null;
			try{
				entity = new StringEntity(requestXML, "UTF-8");
			}catch(Exception e){
				e.printStackTrace();
			}
			post.setEntity(entity);
			body = invoke(httpclient, post);
			post.releaseConnection();
		} catch (Exception e) {
			
		} finally{
			try {
				instream.close();
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return body;
		
    }
	
    public static String post(String url, SortedMap<Object, Object> params) {  
        //DefaultHttpClient httpclient = new DefaultHttpClient();  
        
        // Trust own CA and all self-signed certs
        SSLContext sslcontext = null;
        FileInputStream instream = null;
        String body = null;
        CloseableHttpClient httpclient = null;
		try {
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			instream = new FileInputStream(new File(
					"C:/Users/Administrator/Desktop/cert1/apiclient_cert.p12"));
			keyStore.load(instream, "1337386301".toCharArray());
			
			sslcontext = SSLContexts.custom()
					.loadKeyMaterial(keyStore, "1337386301".toCharArray())
					.build();
        
			
			// Allow TLSv1 protocol only
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
					sslcontext,
					new String[] { "TLSv1" },
					null,
					SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
			httpclient = HttpClients.custom()
					.setSSLSocketFactory(sslsf).build();
			body = null;
			HttpPost post = postForm(url, params);
			body = invoke(httpclient, post);
		} catch (Exception e) {
			
		} finally{
			try {
				instream.close();
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return body;
		
    }  
      
    public static String get(String url) {  
        DefaultHttpClient httpclient = new DefaultHttpClient();  
        String body = null;  
          
        HttpGet get = new HttpGet(url);  
        body = invoke(httpclient, get);  
          
        httpclient.getConnectionManager().shutdown();  
          
        return body;  
    }  
          
      
    private static String invoke(CloseableHttpClient httpclient,  
            HttpUriRequest httpost) {  
          
        HttpResponse response = sendRequest(httpclient, httpost);  
        String body = paseResponse(response);  
          
        return body;  
    }  
  
    private static String paseResponse(HttpResponse response) {  

    	HttpEntity entity = response.getEntity();  
          
        String charset = EntityUtils.getContentCharSet(entity);  
          
        String body = null;  
        try {  
            body = EntityUtils.toString(entity, "utf-8");  
        } catch (ParseException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
          
        return body;  
    }  
  
    private static HttpResponse sendRequest(CloseableHttpClient httpclient,  
            HttpUriRequest httpost) {  
        HttpResponse response = null;  
          
        try {  
            response = httpclient.execute(httpost);  
        } catch (ClientProtocolException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return response;  
    }  
  
    private static HttpPost postForm(String url, SortedMap<Object, Object> params){  
          
        HttpPost httpost = new HttpPost(url);  
        List<BasicNameValuePair> nvps = new ArrayList <BasicNameValuePair>();  
          
        Set<Object> keySet = params.keySet();  
        for(Object key : keySet) {  
            nvps.add(new BasicNameValuePair(key.toString(), (String) params.get(key.toString())));  
        }  
          
        try {  
            httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));  
        } catch (UnsupportedEncodingException e) {  
            e.printStackTrace();  
        }  
          
        return httpost;  
    }  
}