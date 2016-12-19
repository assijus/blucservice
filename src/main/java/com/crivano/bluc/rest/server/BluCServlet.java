package com.crivano.bluc.rest.server;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crivano.blucservice.api.IBlueCrystal;
import com.crivano.swaggerservlet.SwaggerServlet;
import com.crivano.swaggerservlet.dependency.TestableDependency;

public class BluCServlet extends SwaggerServlet {
	private static final long serialVersionUID = 1756711359239182178L;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		super.setAPI(IBlueCrystal.class);

		super.setActionPackage("com.crivano.bluc.rest.server");

		class HttpGetDependency extends TestableDependency {
			String testsite;

			HttpGetDependency(String service, String testsite, boolean partial) {
				super("network", service, partial);
				this.testsite = testsite;
			}

			@Override
			public String getUrl() {
				return testsite;
			}

			@Override
			public boolean test() throws Exception {
				final URL url = new URL(testsite);
				final URLConnection conn = url.openConnection();
				conn.connect();
				return true;
			}
		}

		addDependency(new HttpGetDependency("internet", "http://www.google.com", false));

		addDependency(
				new HttpGetDependency("icpbrasil", "http://politicas.icpbrasil.gov.br/PA_AD_RB_v2_1.der", false));

		addDependency(new HttpGetDependency("accaixajus", "http://lcr.caixa.gov.br/accaixajusv2.crl", false));
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doPost(request, response);
	}
}
