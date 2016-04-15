package com.crivano.bluc.rest.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bouncycastle.util.encoders.Base64;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("serial")
public class EnvelopeServlet extends JsonServlet {

	@Override
	protected void run(HttpServletRequest request,
			HttpServletResponse response, JSONObject req, JSONObject resp)
			throws Exception {

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
		Utils.blucutil.validarECompletarPacoteAssinavel(abCertificate, abSha1,
				abSha256, abSignature, fPolicy, dtSign, enveloperesp);

		resp.put("envelope", enveloperesp.getEnvelope());
		resp.put("cn", enveloperesp.getCn());
		resp.put("policy", enveloperesp.getPolicy());
		resp.put("policyversion", enveloperesp.getPolicyversion());
		resp.put("policyoid", enveloperesp.getPolicyoid());
		resp.put("error", enveloperesp.getError());
		resp.put("certdetails", enveloperesp.getCertdetails());
	}

	@Override
	protected String getContext() {
		return "bluc-envelope";
	}

}