package dan.langford.resteasy.legacy;


public class ZipChunk extends Transformable {

	private String name;

	ZipChunk(byte[] data, String name) {
		super(data);
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
