package dan.langford.resteasy;

import dan.langford.resteasy.RestMethod.Deleter;
import dan.langford.resteasy.RestMethod.Getter;
import dan.langford.resteasy.RestMethod.Poster;
import dan.langford.resteasy.RestMethod.Putter;

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
