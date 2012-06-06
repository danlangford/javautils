package dan.langford.resteasy;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;

public class ZipStream {

	private ZipInputStream input;

	public ZipStream(ZipInputStream input) {
		this.input = input;
	}
	
	public ZipChunk getNext() {
		try {
			ZipEntry entry = input.getNextEntry();
			ZipChunk zc = new ZipChunk(
					entry.getName(),
					IOUtils.toByteArray(input));
			return zc;
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public ZipInputStream getInputStream() {
		return input;
	}
}