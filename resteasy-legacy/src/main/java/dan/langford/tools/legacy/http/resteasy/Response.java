package dan.langford.tools.legacy.http.resteasy;

import org.springframework.http.ResponseEntity;

import dan.langford.tools.legacy.data.Data;

public class Response extends Data {

	private ResponseEntity<byte[]> resp;

	public Response(ResponseEntity<byte[]> resp) {
		super(resp.getBody());
		this.resp = resp;
	}

	public int statusCode() {
		return resp.getStatusCode().value();
	}

}