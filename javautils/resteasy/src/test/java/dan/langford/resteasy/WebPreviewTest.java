package dan.langford.resteasy;

import static org.testng.Assert.assertEquals;

import org.springframework.web.client.RestTemplate;
import org.testng.annotations.Test;

public class WebPreviewTest {
	
	
	@Test
	public void testWebPreviewUrl() {
		

		
		
		RestTemplate rest = RestEasy.get("")
				.auth("admin", "ldschurch").getRestTemplate();
		


		
		
		byte[] os = rest.getForObject("http://l6040.ldschurch.org:8080/alfresco/s/api/node/workspace/SpacesStore/34a3a08f-22d2-4d34-adcc-81ede51f8ba3/content/thumbnails/webpreview?c=force"
				, byte[].class);
		
		

	String b = new String(os);
		
		
		
		assertEquals(b, "asdf");
		
		
		
	}

}
