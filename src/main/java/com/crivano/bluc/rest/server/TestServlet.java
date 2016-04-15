package com.crivano.bluc.rest.server;

import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("serial")
public class TestServlet extends JsonServlet {

	@Override
	protected void run(HttpServletRequest request,
			HttpServletResponse response, JSONObject req, JSONObject resp)
			throws Exception {
		String result = "Blue Crystal REST OK!";

		resp.put("provider", "Blue Crystal REST");
		resp.put("version", "1.0");
		resp.put("status", "OK");
	}

	@Override
	protected String getContext() {
		return "bluc-test";
	}

	@Override
	protected boolean isCacheable() {
		return true;
	}
}
