package dan.langford.resteasy.legacy;

import org.springframework.http.ResponseEntity;

public class Response extends Transformable {

	private ResponseEntity<byte[]> resp;

	public Response(ResponseEntity<byte[]> resp) {
		super(resp.getBody());
		this.resp = resp;
	}

	public int statusCode() {
		return resp.getStatusCode().value();
	}

}