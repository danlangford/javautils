package dan.langford.tools.zip;

import java.io.IOException;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;

import dan.langford.tools.data.Data;

public class ZipArchive extends HashMap<String, ZipChunk> {

	private static final long serialVersionUID = 1L;
	
	private ZipInputStream input;

	public ZipArchive(ZipInputStream input) {
		this.input = input;
		
		// ingest
		ZipChunk next = getNext();
		while(next != null) {
			if(next.isDirectory()) {
				// skip it // we dont support directories right now
			} else {
				this.put(next.getName(), next);
			}
			next = getNext();
		}
	}
	
	private ZipChunk getNext() {
		try {
			ZipEntry entry = input.getNextEntry();
			ZipChunk zc = new ZipChunk(
					IOUtils.toByteArray(input),
					entry.getName());
			zc.setComments(entry.getComment());
			zc.setDirectory(entry.isDirectory());
			zc.setSize(entry.getSize());
			zc.setTime(entry.getTime());
			zc.setExtra(new Data(entry.getExtra()));
			return zc;
		}
		catch (IOException e) {
			//e.printStackTrace();
			return null;
		}
	}
	

}