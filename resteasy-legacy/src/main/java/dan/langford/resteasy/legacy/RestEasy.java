package dan.langford.resteasy.legacy;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.CommonsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import dan.langford.resteasy.legacy.ssl.EasySSLProtocolSocketFactory;

public class RestEasy {

	private static abstract class RestMethod {
		
		private HttpClient client;
		private ClientHttpRequestFactory reqFac;
		protected RestTemplate rest;
		protected String url;
		private Protocol defaultSSL;
		
		RestMethod(String url) {
			
			this.defaultSSL = Protocol.getProtocol("https");
		    
		    this.url = url;
			this.client = new HttpClient();

			this.reqFac = new CommonsClientHttpRequestFactory(this.client);
			this.rest = new RestTemplate(this.reqFac);
			
		}
		
		protected void doAuth(String username, String password) {
			client.getState().setCredentials(
					new AuthScope(null, -1),
					new UsernamePasswordCredentials(username, password));
		}
		
		protected void doBadSSL() {
			Protocol.registerProtocol("https", new Protocol("https", (ProtocolSocketFactory)new EasySSLProtocolSocketFactory(), 443));
		}
		
		protected void doFinalize() {
			Protocol.registerProtocol("https", this.defaultSSL);
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
			Response resp = new Response(rest.getForEntity(url, String.class));
			doFinalize();
			return resp;
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
			Response resp = new Response(rest.postForEntity(url, parts, String.class));
			doFinalize();
			return resp;
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
		
		public void go() {
			//rest.put(url);
			doFinalize();
		}

	}
	
	public static class Deleter extends RestMethod {
		Deleter(String url) {
			super(url);
		}
		
		public void go() {
			rest.delete(url);
			doFinalize();
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
