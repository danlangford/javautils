package dan.langford.tools.legacy.http.resteasy;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import dan.langford.tools.legacy.http.resteasy.Response;
import dan.langford.tools.legacy.http.resteasy.RestEasy;


public class EasyResponseErrorHandlerTest {
	
	@Test
	public void noExceptionErrors() {

		Response resp0 = RestEasy.get("https://github.com/somepaththatdoesnotexist").response();
		assertEquals(resp0.statusCode(),404);
		assertEquals(resp0.asJson().path("error").getTextValue(),"Not Found");

	}

}
