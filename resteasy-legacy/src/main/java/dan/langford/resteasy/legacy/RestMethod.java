package dan.langford.resteasy.legacy;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.CommonsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import dan.langford.resteasy.legacy.ssl.EasySSLProtocolSocketFactory;

public abstract class RestMethod<M extends RestMethod<?>> {
	
	private HttpClient client;
	private ClientHttpRequestFactory reqFac;
	protected RestTemplate rest;
	protected String url;
	private Protocol defaultSSL;
	protected Map<String,String> urlVariables;
	private M that;
	
	protected RestMethod<M> with(M that){
		this.that = that;
		return this;
	}
	
	protected RestMethod<M> hit(String url) {
		
		this.defaultSSL = Protocol.getProtocol("https");
	    
	    this.url = url;
		this.client = new HttpClient();

		this.reqFac = new CommonsClientHttpRequestFactory(this.client);
		this.rest = new RestTemplate(this.reqFac);
		this.rest.setErrorHandler(new EasyResponseErrorHandler());
		
		this.urlVariables = new HashMap<String, String>();
		
		return this;
		
	}
	
	public M auth(String username, String password) {
		client.getState().setCredentials(
				new AuthScope(null, -1),
				new UsernamePasswordCredentials(username, password));
		return that;
	}
	
	public M badSSL() {
		Protocol.registerProtocol("https", new Protocol("https", (ProtocolSocketFactory)new EasySSLProtocolSocketFactory(), 443));
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
		Protocol.registerProtocol("https", this.defaultSSL);
		
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



