package com.crivano.bluc.rest.server;

import org.json.JSONObject;

import com.crivano.restservlet.ICacheableRestAction;

public class TestGet implements ICacheableRestAction {

	@Override
	public void run(JSONObject req, JSONObject resp) throws Exception {
		resp.put("provider", "Blue Crystal REST");
		resp.put("version", "1.0");
		resp.put("status", "OK");
	}

	@Override
	public String getContext() {
		return "bluc-test";
	}
}
