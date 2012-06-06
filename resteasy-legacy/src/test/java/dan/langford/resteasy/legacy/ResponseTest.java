package dan.langford.resteasy.legacy;

import static org.testng.Assert.assertEquals;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testng.annotations.Test;


public class ResponseTest {
	
	@Test
	public void JSONPSupport() {

		Response resp0 = new MockOKResponse("undefined({\"someNumber\":1234})");
		Long value0 = resp0.asJson().path("someNumber").getLongValue();
		assertEquals(value0, new Long(1234));
		
		Response resp1 = new MockOKResponse("result = {\"someOtherNumber\":4321}");
		Long value1 = resp1.asJson().path("someOtherNumber").getLongValue();
		assertEquals(value1, new Long(4321));

	}
	
	static class MockOKResponse extends Response {
		public MockOKResponse(String body) {
			super(new ResponseEntity<byte[]>(body.getBytes(), HttpStatus.OK));
		}
	}

}
