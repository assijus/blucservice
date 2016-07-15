package com.crivano.bluc.rest.server;

import java.util.Date;

import org.bouncycastle.util.encoders.Base64;
import org.json.JSONObject;

import com.crivano.restservlet.ICacheableRestAction;

public class ValidatePost implements ICacheableRestAction {

	@Override
	public void run(JSONObject req, JSONObject resp) throws Exception {

		String envelope = req.getString("envelope");
		String sha1 = req.getString("sha1");
		String sha256 = req.getString("sha256");
		String time = req.getString("time");
		String crl = req.getString("crl");

		byte[] sign = Base64.decode(envelope);
		byte[] abSha1 = Base64.decode(sha1);
		byte[] abSha256 = Base64.decode(sha256);
		Date dtSign = javax.xml.bind.DatatypeConverter.parseDateTime(time)
				.getTime();
		boolean verifyCRL = "true".equals(crl);

		// Produce response
		ValidateResponse validateresp = new ValidateResponse();
		Utils.getBlucutil().validateSign(sign, abSha1, abSha256, dtSign,
				verifyCRL, validateresp);

		resp.put("certdetails", validateresp.getCertdetails());
		resp.put("cn", validateresp.getCn());
		resp.put("policy", validateresp.getPolicy());
		resp.put("policyversion", validateresp.getPolicyversion());
		resp.put("policyoid", validateresp.getPolicyoid());
		resp.put("error", validateresp.getError());
		resp.put("status", validateresp.getStatus());
	}

	@Override
	public String getContext() {
		return "bluc-validate";
	}
}
