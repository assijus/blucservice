package com.crivano.bluc.rest.server;

import java.util.Date;

import org.bouncycastle.util.encoders.Base64;
import org.json.JSONObject;

import com.crivano.restservlet.IRestAction;

public class EnvelopePost implements IRestAction {

	@Override
	public void run(JSONObject req, JSONObject resp) throws Exception {

		String policy = req.getString("policy");
		String sha1 = req.getString("sha1");
		String sha256 = req.getString("sha256");
		String signature = req.getString("signature");
		String time = req.getString("time");
		String certificate = req.getString("certificate");
		String crl = req.getString("crl");

		// Produce response

		if (!("AD-RB".equals(policy) || "PKCS#7".equals(policy)))
			throw new Exception(
					"Parameter 'policy' should be either 'AD-RB' or 'PKCS#7'");

		boolean fPolicy = "AD-RB".equals(policy);
		byte[] abCertificate = Base64.decode(certificate);
		byte[] abSha1 = Base64.decode(sha1);
		byte[] abSha256 = Base64.decode(sha256);
		byte[] abSignature = Base64.decode(signature);
		Date dtSign = javax.xml.bind.DatatypeConverter.parseDateTime(time)
				.getTime();
		boolean verifyCRL = "true".equals(crl);

		EnvelopeResponse enveloperesp = new EnvelopeResponse();
		Utils.getBlucutil().validarECompletarPacoteAssinavel(abCertificate,
				abSha1, abSha256, abSignature, fPolicy, dtSign, enveloperesp);

		resp.put("envelope", enveloperesp.getEnvelope());
		resp.put("cn", enveloperesp.getCn());
		resp.put("policy", enveloperesp.getPolicy());
		resp.put("policyversion", enveloperesp.getPolicyversion());
		resp.put("policyoid", enveloperesp.getPolicyoid());
		resp.put("error", enveloperesp.getError());
		resp.put("certdetails", enveloperesp.getCertdetails());
	}

	@Override
	public String getContext() {
		return "bluc-envelope";
	}

}