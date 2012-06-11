package dan.langford.tools.legacy.http.resteasy;

import dan.langford.tools.legacy.http.resteasy.RestMethod.Deleter;
import dan.langford.tools.legacy.http.resteasy.RestMethod.Getter;
import dan.langford.tools.legacy.http.resteasy.RestMethod.Poster;
import dan.langford.tools.legacy.http.resteasy.RestMethod.Putter;

public class RestEasy {

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
