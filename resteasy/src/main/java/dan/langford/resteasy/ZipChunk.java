package dan.langford.resteasy;

import java.io.ByteArrayInputStream;

public class ZipChunk {

	private String name;
	private byte[] content;

	ZipChunk(String name, byte[] content) {
		this.name = name;
		this.content = content;
	}

	public String getName() {
		return name;
	}

	public byte[] getContent() {
		return content;
	}

	public ByteArrayInputStream getContentAsStream() {
		return new ByteArrayInputStream(content);
	}

}
