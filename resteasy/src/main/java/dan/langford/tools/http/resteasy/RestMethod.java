package dan.langford.tools.http.resteasy;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import dan.langford.tools.http.ssl.EasySSLSocketFactory;

public abstract class RestMethod<M extends RestMethod<?>> {
	
	private DefaultHttpClient client;
	private ClientHttpRequestFactory reqFac;
	protected RestTemplate rest;
	protected String url;
	protected Map<String,String> urlVariables;
	private M that;
	
	protected RestMethod<M> with(M that){
		this.that = that;
		return this;
	}
	
	protected RestMethod<M> hit(String url) {
		
		this.url = url;
		this.client = new DefaultHttpClient();

		this.reqFac = new HttpComponentsClientHttpRequestFactory(this.client);
		this.rest = new RestTemplate(this.reqFac);
		this.rest.setErrorHandler(new EasyResponseErrorHandler());
		
		this.urlVariables = new HashMap<String, String>();
		
		return this;
		
	}
	
	public M auth(String username, String password) {
		client.getCredentialsProvider().setCredentials(
				new AuthScope(null, -1),
				new UsernamePasswordCredentials(username, password));
		return that;
	}
	
	public M badSSL() {
		client.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, new EasySSLSocketFactory()));
		return that;
	}
	
	public M var(String var, String val) {
		urlVariables.put(var, val);
		return that;
	}
	
	protected abstract Response doResponse();
	
	public Response response() {
		Response r = doResponse();
		// any last minute changes? re-config? fixes?
		// no
		return r;
	}
	
	
	public static class Getter extends RestMethod<Getter> {
		
		Getter(String url) {
			hit(url).with(this);
		}
		
		public Response doResponse() {
			return new Response(rest.getForEntity(url, byte[].class, this.urlVariables));
		}

	}

	public static class Poster extends RestMethod<Poster> {
		
		MultiValueMap<String, Object> parts;
		
		Poster(String url) {
			hit(url).with(this);
			parts = new LinkedMultiValueMap<String, Object>();
		}

		public Response doResponse() {
			return new Response(rest.postForEntity(url, parts, byte[].class, this.urlVariables));
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

	public static class Putter extends Poster {
		
		// *NOTE* this class will have to change once Putter and Poster APIs vary
		// curerntly im assuming they are the same
		
		Putter(String url) {
			super(url);
		}
		
		@Override
		public Response doResponse() {
			HttpEntity<MultiValueMap<String, Object>> body = new HttpEntity<MultiValueMap<String, Object>>(parts, null);
			return new Response(rest.exchange(url, HttpMethod.PUT, body, byte[].class, this.urlVariables));
		}

	}

	public static class Deleter extends RestMethod<Deleter> {
		Deleter(String url) {
			hit(url).with(this);
		}
		
		public Response doResponse() {
			return new Response(rest.exchange(url, HttpMethod.DELETE, null, byte[].class, this.urlVariables));
		}
	}
	
}



