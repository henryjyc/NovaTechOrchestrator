package com.sst.nt.lms.orch.util;

import java.io.IOException;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

	@Override
	public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
		return false;
	}
	
	@Override
	public void handleError(ClientHttpResponse httpResponse) {
	}
}
