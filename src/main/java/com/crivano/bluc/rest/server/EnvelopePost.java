package com.crivano.bluc.rest.server;

import java.util.Date;

import org.bouncycastle.util.encoders.Base64;
import org.json.JSONObject;

import com.crivano.bluc.rest.server.IBlueCrystal.CertDetails;
import com.crivano.bluc.rest.server.IBlueCrystal.EnvelopePostRequest;
import com.crivano.bluc.rest.server.IBlueCrystal.EnvelopePostResponse;
import com.crivano.bluc.rest.server.IBlueCrystal.IEnvelopePost;
import com.crivano.swaggerservlet.SwaggerUtils;

public class EnvelopePost implements IEnvelopePost {

	@Override
	public String getContext() {
		return "bluc-envelope";
	}

	@Override
	public void run(EnvelopePostRequest req, EnvelopePostResponse resp)
			throws Exception {

		// Produce response
		EnvelopeResponse enveloperesp = new EnvelopeResponse();
		if (!("AD-RB".equals(req.policy) || "PKCS#7".equals(req.policy)))
			throw new Exception(
					"Parameter 'policy' should be either 'AD-RB' or 'PKCS#7'");

		Utils.getBlucutil().validarECompletarPacoteAssinavel(req.certificate,
				req.sha1, req.sha256, req.signature,
				"AD-RB".equals(req.policy), req.time, enveloperesp);

		resp.envelope = SwaggerUtils.base64Decode(enveloperesp.getEnvelope());
		resp.cn = enveloperesp.getCn();
		resp.policy = enveloperesp.getPolicy();
		resp.policyversion = enveloperesp.getPolicyversion();
		resp.policyoid = enveloperesp.getPolicyoid();
		// resp.error = enveloperesp.getError();
		resp.certdetails = new CertDetails();
		CertificatePost.fillCertificateDetails(resp.certdetails,
				enveloperesp.getCertdetails());
	}
}