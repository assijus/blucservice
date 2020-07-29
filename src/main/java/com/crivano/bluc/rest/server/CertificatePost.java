package com.crivano.bluc.rest.server;

import java.util.Map;

import com.crivano.blucservice.api.IBlueCrystal.CertDetails;
import com.crivano.blucservice.api.IBlueCrystal.CertificatePostRequest;
import com.crivano.blucservice.api.IBlueCrystal.CertificatePostResponse;
import com.crivano.blucservice.api.IBlueCrystal.ICertificatePost;
import com.crivano.swaggerservlet.ISwaggerCacheableMethod;
import com.crivano.swaggerservlet.Swagger;

import bluecrystal.service.api.CertificateResponse;

public class CertificatePost implements ICertificatePost, ISwaggerCacheableMethod {

	@Override
	public String getContext() {
		return "bluc-certificate";
	}

	@Override
	public void run(CertificatePostRequest req, CertificatePostResponse resp) throws Exception {
		CertificateResponse certificateresp = new CertificateResponse();
		Utils.getBlucutil().certificate(req.certificate, certificateresp);

		resp.subject = certificateresp.getSubject();
		resp.cn = certificateresp.getCn();
		resp.name = certificateresp.getName();
		resp.cpf = certificateresp.getCpf();

		resp.certdetails = new CertDetails();
		fillCertificateDetails(resp.certdetails, certificateresp.getCertdetails());
	}

	public static void fillCertificateDetails(CertDetails certdetails, Map<String, String> map) throws Exception {
		for (String key : map.keySet()) {
			Swagger.set(certdetails, key, map.get(key));
		}
	}
}
