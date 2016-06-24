package com.crivano.bluc.rest.server;

import org.bouncycastle.util.encoders.Base64;
import org.json.JSONObject;

import com.crivano.restservlet.ICacheableRestAction;

public class CertificatePost implements ICacheableRestAction {

	@Override
	public void run(JSONObject req, JSONObject resp) throws Exception {
		String certificate = req.getString("certificate");

		byte[] abCertificate = Base64.decode(certificate);

		CertificateResponse certificateresp = new CertificateResponse();
		Utils.getBlucutil().certificado(abCertificate, certificateresp);

		resp.put("subject", certificateresp.getSubject());
		resp.put("cn", certificateresp.getCn());
		resp.put("name", certificateresp.getName());
		resp.put("cpf", certificateresp.getCpf());
		resp.put("error", certificateresp.getError());
		resp.put("certdetails", certificateresp.getCertdetails());
	}

	@Override
	public String getContext() {
		return "bluc-certificate";
	}

}
