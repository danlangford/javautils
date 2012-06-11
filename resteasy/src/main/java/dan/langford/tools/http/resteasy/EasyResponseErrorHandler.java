package dan.langford.tools.http.resteasy;

import java.io.IOException;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

public class EasyResponseErrorHandler implements ResponseErrorHandler {

	public boolean hasError(ClientHttpResponse response) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	public void handleError(ClientHttpResponse response) throws IOException {
		// TODO Auto-generated method stub

	}

}
