package com.crivano.bluc.rest.server;

import com.crivano.blucservice.api.BlueCrystalContext;
import com.crivano.blucservice.api.IBlueCrystal.CertDetails;
import com.crivano.blucservice.api.IBlueCrystal.IHashPost;
import com.crivano.swaggerservlet.SwaggerUtils;

import bluecrystal.service.api.HashResponse;

public class HashPost implements IHashPost {

	@Override
	public String getContext() {
		return "bluc-rest";
	}

	@Override
	public void run(Request req, Response resp, BlueCrystalContext ctx) throws Exception {

		// Produce response
		HashResponse hashresp = new HashResponse();
		if (!("AD-RB".equals(req.policy) || "PKCS#7".equals(req.policy)))
			throw new Exception("Parameter 'policy' should be either 'AD-RB' or 'PKCS#7'");

		Utils.getBlucutil().signedAttributes(req.certificate, req.sha1, req.sha256, "AD-RB".equals(req.policy),
				req.time, hashresp);

		resp.hash = SwaggerUtils.base64Decode(hashresp.getHash());
		resp.cn = hashresp.getCn();
		resp.policy = hashresp.getPolicy();
		resp.policyversion = hashresp.getPolicyversion();
		resp.policyoid = hashresp.getPolicyoid();
		// resp.error = hashresp.getError();
		resp.certdetails = new CertDetails();
		CertificatePost.fillCertificateDetails(resp.certdetails, hashresp.getCertdetails());
	}
}
