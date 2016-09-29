package com.crivano.bluc.rest.server;

import java.util.Date;

import org.bouncycastle.util.encoders.Base64;
import org.json.JSONObject;

import com.crivano.bluc.rest.server.IBlueCrystal.CertDetails;
import com.crivano.bluc.rest.server.IBlueCrystal.HashPostRequest;
import com.crivano.bluc.rest.server.IBlueCrystal.HashPostResponse;
import com.crivano.bluc.rest.server.IBlueCrystal.IHashPost;
import com.crivano.swaggerservlet.SwaggerUtils;

public class HashPost implements IHashPost {

	@Override
	public String getContext() {
		return "bluc-rest";
	}

	@Override
	public void run(HashPostRequest req, HashPostResponse resp)
			throws Exception {

		// Produce response
		HashResponse hashresp = new HashResponse();
		if (!("AD-RB".equals(req.policy) || "PKCS#7".equals(req.policy)))
			throw new Exception(
					"Parameter 'policy' should be either 'AD-RB' or 'PKCS#7'");

		Utils.getBlucutil().produzPacoteAssinavel(req.certificate, req.sha1,
				req.sha256, "AD-RB".equals(req.policy), req.time, hashresp);

		resp.hash = SwaggerUtils.base64Decode(hashresp.getHash());
		resp.cn = hashresp.getCn();
		resp.policy = hashresp.getPolicy();
		resp.policyversion = hashresp.getPolicyversion();
		resp.policyoid = hashresp.getPolicyoid();
		// resp.error = hashresp.getError();
		resp.certdetails = new CertDetails();
		CertificatePost.fillCertificateDetails(resp.certdetails,
				hashresp.getCertdetails());
	}
}
