package dan.langford;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import dan.langford.ssl.OldEasySSLSocketFactory;

public class RestEasy {

	public static class Response {

		private ResponseEntity<String> resp;

		public Response(ResponseEntity<String> resp) {
			this.resp = resp;
		}

		public int statusCode() {
			return resp.getStatusCode().value();
		}
		
		public String asString() {
			return resp.getBody();
		}

		public JSONObject asJSON() {
			try {
				return new JSONObject(resp.getBody());
			} catch (JSONException e) {
				return null;
			}
		}

		public Document asXML() {
			try {
				return DocumentHelper.parseText(resp.getBody());
			} catch (DocumentException e) {
				return null;
			}
		}

	}

	private static abstract class RestMethod {
		
		private DefaultHttpClient client;
		private ClientHttpRequestFactory reqFac;
		protected RestTemplate rest;
		protected String url;
		
		RestMethod(String url) {
		    
		    this.url = url;
			this.client = new DefaultHttpClient();

			this.reqFac = new HttpComponentsClientHttpRequestFactory(this.client);
			this.rest = new RestTemplate(this.reqFac);
			
		}
		
		protected void doAuth(String username, String password) {
			client.getCredentialsProvider().setCredentials(
					new AuthScope(null, -1),
					new UsernamePasswordCredentials(username, password));
		}
		
		protected void doBadSSL() {
			//client.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, new NewEasySSLSocketFactory()));
			client.getConnectionManager().getSchemeRegistry().register(new Scheme("https", new OldEasySSLSocketFactory(), 443));
		}
		
	}
	
	

	public static class Getter extends RestMethod {
		Getter(String url) {
			super(url);
		}
		
		public Getter auth(String u, String p) {
			doAuth(u, p);
			return this;
		}
		
		public Getter badSSL() {
			doBadSSL();
			return this;
		}
		
		public Response response() {
			return new Response(rest.getForEntity(url, String.class));
		}

	}
	
	public static class Poster extends RestMethod {
		
		MultiValueMap<String, Object> parts;
		
		Poster(String url) {
			super(url);
			parts = new LinkedMultiValueMap<String, Object>();
		}

		public Poster auth(String u, String p) {
			doAuth(u, p);
			return this;
		}
		
		public Poster badSSL() {
			doBadSSL();
			return this;
		}

		public Response response() {
			return new Response(rest.postForEntity(url, parts, String.class));
		}

		public Poster attachFile(String id, Resource file) {
			parts.add(id, file);
			return this;
		}
		
		public Poster attachFile(Resource file) {
			return attachFile("file"+parts.size()+1, file);
		}
		
		public Poster attachData(String id, Object data) {
			parts.add(id, data);
			return this;
		}
		
		public Poster attachData(Object data) {
			return attachData("part"+parts.size()+1, data);
		}

	}
	
	public static class Putter extends RestMethod {
		Putter(String url) {
			super(url);
		}

	}
	
	public static class Deleter extends RestMethod {
		Deleter(String url) {
			super(url);
		}
		
		public void go() {
			rest.delete(url);
		}
	}

	public static Getter get(String url) {
		return new Getter(url);
	}
	
	public static Poster post(String url) {
		return new Poster(url);
	}
	
	public static Putter put(String url) {
		return new Putter(url);
	}
	
	public static Deleter delete(String url) {
		return new Deleter(url);
	}
	
	public static void trustSelfSignedSSL() {
	    try {
	        SSLContext ctx = SSLContext.getInstance("TLS");
	        X509TrustManager tm = new X509TrustManager() {

	            public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
	            }

	            public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
	            }

	            public X509Certificate[] getAcceptedIssuers() {
	                return null;
	            }
	        };
	        ctx.init(null, new TrustManager[]{tm}, null);
	        SSLContext.setDefault(ctx);
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	}

}
