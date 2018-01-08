package com.enation.app.shop.component.payment.plugin.alipay.mobile.util.app;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

public class Util {
	
	private static final String TAG = "SDK_Sample.Util";
	
	private static class SSLSocketFactoryEx extends SSLSocketFactory {      
	      
	    SSLContext sslContext = SSLContext.getInstance("TLS");      
	      
	    public SSLSocketFactoryEx(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {      
	        super(truststore);      
	      
	        TrustManager tm = new X509TrustManager() {      
	      
	            @Override
				public X509Certificate[] getAcceptedIssuers() {      
	                return null;      
	            }      
	      
				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
				}

				@Override
				public void checkServerTrusted(X509Certificate[] chain,	String authType) throws java.security.cert.CertificateException {
				}  
	        };      
	      
	        sslContext.init(null, new TrustManager[] { tm }, null);      
	    }      
	      
		@Override
		public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host,	port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		} 
	}  

	private static HttpClient getNewHttpClient() { 
	   try { 
	       KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType()); 
	       trustStore.load(null, null); 

	       SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore); 
	       sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER); 

	       HttpParams params = new BasicHttpParams(); 
	       HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1); 
	       HttpProtocolParams.setContentCharset(params, HTTP.UTF_8); 

	       SchemeRegistry registry = new SchemeRegistry(); 
	       registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80)); 
	       registry.register(new Scheme("https", sf, 443)); 

	       ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry); 

	       return new DefaultHttpClient(ccm, params); 
	   } catch (Exception e) { 
	       return new DefaultHttpClient(); 
	   } 
	}
	
	
	
	public static String sha1(String str) {
		if (str == null || str.length() == 0) {
			return null;
		}
		
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		
		try {
			MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
			mdTemp.update(str.getBytes());
			
			byte[] md = mdTemp.digest();
			int j = md.length;
			char buf[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
				buf[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(buf);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static List<String> stringsToList(final String[] src) {
		if (src == null || src.length == 0) {
			return null;
		}
		final List<String> result = new ArrayList<String>();
		for (int i = 0; i < src.length; i++) {
			result.add(src[i]);
		}
		return result;
	}
}
