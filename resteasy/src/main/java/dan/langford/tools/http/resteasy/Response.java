package dan.langford.tools.http.resteasy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.ZipInputStream;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.springframework.http.ResponseEntity;

import dan.langford.tools.zip.ZipArchive;

public class Response {

	private ResponseEntity<byte[]> resp;
	private String body;

	public Response(ResponseEntity<byte[]> resp) {
		this.resp = resp;
	}

	public int statusCode() {
		return resp.getStatusCode().value();
	}
	
	public byte[] asRawBytes() {
		return resp.getBody();
	}

	public String asString() {
		if(body==null) {
			body=new String(resp.getBody());
		}
		return body;
	}
	
	@Override
	public String toString() {
		return asString();
	}

	public JsonNode asJson() {
		try {
			return new ObjectMapper().readValue(resp.getBody(), JsonNode.class);
		} catch (IOException e0) {
			String b = asString();
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
			return DocumentHelper.parseText(asString());
		} catch (DocumentException e) {
			return null;
		}
	}
	
	public ZipArchive asZipArchive() {
		ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(asRawBytes()));
		return new ZipArchive(zis);
	}

}