package dan.langford.resteasy.legacy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.ZipInputStream;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

public class Transformable {

	
	private byte[] data;
	private String strCache;

	public Transformable(byte[] data) {
		this.data = data;
	}
	
	public byte[] asRawBytes() {
		return data;
	}
	

	public ByteArrayInputStream asStream() {
		return new ByteArrayInputStream(data);
	}

	public String asString() {
		if(strCache==null) {
			strCache=new String(data);
		}
		return strCache;
	}
	
	@Override
	public String toString() {
		return asString();
	}

	public JsonNode asJson() {
		try {
			return new ObjectMapper().readValue(data, JsonNode.class);
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
	
	public ZipStream asZipArchive() {
		ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(asRawBytes()));
		return new ZipStream(zis);
	}
	
	
}
