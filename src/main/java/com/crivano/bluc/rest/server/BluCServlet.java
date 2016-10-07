package com.crivano.bluc.rest.server;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crivano.blucservice.api.IBlueCrystal;
import com.crivano.swaggerservlet.SwaggerServlet;

public class BluCServlet extends SwaggerServlet {
	private static final long serialVersionUID = 1756711359239182178L;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		super.setAPI(IBlueCrystal.class);

		super.setActionPackage("com.crivano.bluc.rest.server");
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.doPost(request, response);
	}

	@Override
	protected String getService() {
		return "blue-crystal";
	}
}
