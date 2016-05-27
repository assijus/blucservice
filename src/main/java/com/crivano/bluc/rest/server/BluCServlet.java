package com.crivano.bluc.rest.server;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crivano.swaggerservlet.Swagger;
import com.crivano.swaggerservlet.SwaggerServlet;

public class BluCServlet extends SwaggerServlet {
	private static final long serialVersionUID = 1756711359239182178L;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		super.setActionPackage("com.crivano.bluc.rest.server");

		Swagger sw = new Swagger();
		sw.loadFromInputStream(this.getServletContext().getResourceAsStream(
				"/api/v1/swagger.yaml"));

		super.setSwagger(sw);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Swagger sw = new Swagger();
		sw.loadFromInputStream(this.getServletContext().getResourceAsStream(
				"/api/v1/swagger.yaml"));

		super.setSwagger(sw);

		super.doPost(request, response);
	}

	@Override
	protected String getService() {
		return "blue-crystal";
	}
}
