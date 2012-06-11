package dan.langford.tools.zip;

import dan.langford.tools.data.Data;

public class ZipChunk extends Data {

	private String name;
	private String comment;
	private boolean directory;
	private long size;
	private long time;
	private Data extra;

	ZipChunk(byte[] data, String name) {
		super(data);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setComments(String comment) {
		this.comment=comment;
	}

	public void setDirectory(boolean directory) {
		this.directory = directory;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public void setExtra(Data extra) {
		this.extra = extra;
	}

	public boolean isDirectory() {
		return directory;
	}
	
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public long getSize() {
		return size;
	}

	public long getTime() {
		return time;
	}

	public Data getExtra() {
		return extra;
	}

	public void setName(String name) {
		this.name = name;
	}

}
