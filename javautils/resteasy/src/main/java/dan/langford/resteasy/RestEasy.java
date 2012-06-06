package dan.langford.resteasy;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.core.io.Resource;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import dan.langford.resteasy.ssl.EasySSLSocketFactory;

public class RestEasy {

	private static abstract class RestMethod {
		
		private DefaultHttpClient client;
		private ClientHttpRequestFactory reqFac;
		protected RestTemplate rest;
		protected String url;
		protected Map<String,String> urlVariables;
		
		RestMethod(String url) {
		    
		    this.url = url;
			this.client = new DefaultHttpClient();

			this.reqFac = new HttpComponentsClientHttpRequestFactory(this.client);
			this.rest = new RestTemplate(this.reqFac);
			this.rest.setErrorHandler(new EasyResponseErrorHandler());
			
			this.urlVariables = new HashMap<String, String>();
			
		}
		
		protected void doAuth(String username, String password) {
			client.getCredentialsProvider().setCredentials(
					new AuthScope(null, -1),
					new UsernamePasswordCredentials(username, password));
		}
		
		protected void doBadSSL() {
			client.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, new EasySSLSocketFactory()));
		}
		
		protected void doVar(String var, String val) {
			urlVariables.put(var, val);
		}
		
		public RestTemplate getRestTemplate() {
			return rest;
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
		
		public Getter var(String var, String val) {
			doVar(var, val);
			return this;
		}
		
		public Response response() {
			return new Response(rest.getForEntity(url, String.class, this.urlVariables));
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
		
		public Poster var(String var, String val) {
			doVar(var, val);
			return this;
		}

		public Response response() {
			return new Response(rest.postForEntity(url, parts, String.class, this.urlVariables));
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
		
		public Putter var(String var, String val) {
			doVar(var, val);
			return this;
		}
		
		public void go() {
			//rest.put(url, this.urlVariables);
		}

	}
	
	public static class Deleter extends RestMethod {
		Deleter(String url) {
			super(url);
		}
		
		public Deleter var(String var, String val) {
			doVar(var, val);
			return this;
		}
		
		public void go() {
			rest.delete(url, this.urlVariables);
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

}
