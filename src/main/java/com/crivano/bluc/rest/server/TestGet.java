package com.crivano.bluc.rest.server;

import com.crivano.bluc.rest.server.IBlueCrystal.ITestGet;
import com.crivano.bluc.rest.server.IBlueCrystal.TestGetRequest;
import com.crivano.bluc.rest.server.IBlueCrystal.TestGetResponse;
import com.crivano.swaggerservlet.ISwaggerCacheableMethod;

public class TestGet implements ITestGet, ISwaggerCacheableMethod {

	@Override
	public String getContext() {
		return "bluc-test";
	}

	@Override
	public void run(TestGetRequest req, TestGetResponse resp) throws Exception {
		resp.provider = "Blue Crystal REST";
		resp.version = "1.0";
		resp.status = "OK";
	}
}
