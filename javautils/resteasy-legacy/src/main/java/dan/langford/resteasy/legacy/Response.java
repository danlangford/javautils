package dan.langford.resteasy.legacy;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.springframework.http.ResponseEntity;

public class Response {

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

	public JsonNode asJson() {
		try {
			return new ObjectMapper().readValue(resp.getBody(), JsonNode.class);
		} catch (IOException e0) {
			String b = resp.getBody();
			int curl = b.indexOf('{');
			int square = b.indexOf('[');
			String newBody;
			if (curl >= 0 && (square == -1 || curl < square)) {
				newBody = b.substring(curl, b.lastIndexOf('}') + 1);
			} else if (square >= 0 && (curl == -1 || square < curl)) {
				newBody = b.substring(square, b.lastIndexOf(']') + 1);
			} else {
				return null;
			}
			try {
				return new ObjectMapper().readValue(newBody, JsonNode.class);
			} catch (IOException e1) {
				return null;
			}
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
