package com.crivano.bluc.rest.server;

import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bouncycastle.util.encoders.Base64;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("serial")
public class CertificateServlet extends JsonServlet {

	@Override
	protected void run(HttpServletRequest request,
			HttpServletResponse response, JSONObject req, JSONObject resp)
			throws Exception {
		String certificate = req.getString("certificate");

		byte[] abCertificate = Base64.decode(certificate);

		CertificateResponse certificateresp = new CertificateResponse();
		Utils.blucutil.certificado(abCertificate, certificateresp);

		resp.put("subject", certificateresp.getSubject());
		resp.put("cn", certificateresp.getCn());
		resp.put("name", certificateresp.getName());
		resp.put("cpf", certificateresp.getCpf());
		resp.put("error", certificateresp.getError());
		resp.put("certdetails", certificateresp.getCertdetails());
	}

	@Override
	protected String getContext() {
		return "bluc-certificate";
	}

	@Override
	protected boolean isCacheable() {
		return true;
	}

}
