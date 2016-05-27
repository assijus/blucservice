package com.crivano.bluc.rest.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.crivano.restservlet.ICacheableRestAction;

public class TestGet implements ICacheableRestAction {

	@Override
	public void run(HttpServletRequest request, HttpServletResponse response,
			JSONObject req, JSONObject resp) throws Exception {
		String result = "Blue Crystal REST OK!";

		resp.put("provider", "Blue Crystal REST");
		resp.put("version", "1.0");
		resp.put("status", "OK");
	}

	@Override
	public String getContext() {
		return "bluc-test";
	}
}
