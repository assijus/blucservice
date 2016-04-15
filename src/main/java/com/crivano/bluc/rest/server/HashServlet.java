package com.crivano.bluc.rest.server;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bouncycastle.util.encoders.Base64;
import org.json.JSONObject;

@SuppressWarnings("serial")
public class HashServlet extends JsonServlet {

	@Override
	protected void run(HttpServletRequest request,
			HttpServletResponse response, JSONObject req, JSONObject resp)
			throws Exception {

		String policy = req.getString("policy");
		String sha1 = req.getString("sha1");
		String sha256 = req.getString("sha256");
		String time = req.getString("time");
		String certificate = req.getString("certificate");
		String crl = req.getString("crl");

		// Produce response
		HashResponse hashresp = new HashResponse();
		if (!("AD-RB".equals(policy) || "PKCS#7".equals(policy)))
			throw new Exception(
					"Parameter 'policy' should be either 'AD-RB' or 'PKCS#7'");

		boolean fPolicy = "AD-RB".equals(policy);
		byte[] baCertificate = Base64.decode(certificate);
		byte[] baSha1 = Base64.decode(sha1);
		byte[] baSha256 = Base64.decode(sha256);
		Date dtSign = javax.xml.bind.DatatypeConverter.parseDateTime(time)
				.getTime();
		boolean verifyCRL = "true".equals(crl);

		Utils.blucutil.produzPacoteAssinavel(baCertificate, baSha1, baSha256,
				fPolicy, dtSign, hashresp);

		resp.put("hash", hashresp.getHash());
		resp.put("cn", hashresp.getCn());
		resp.put("policy", hashresp.getPolicy());
		resp.put("policyversion", hashresp.getPolicyversion());
		resp.put("policyoid", hashresp.getPolicyoid());
		resp.put("error", hashresp.getError());
		resp.put("certdetails", hashresp.getCertdetails());
	}

	@Override
	protected String getContext() {
		return "bluc-rest";
	}
}
