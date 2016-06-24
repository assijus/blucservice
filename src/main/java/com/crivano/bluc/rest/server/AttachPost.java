package com.crivano.bluc.rest.server;

import org.bouncycastle.util.encoders.Base64;
import org.json.JSONObject;

import com.crivano.restservlet.IRestAction;

public class AttachPost implements IRestAction {

	@Override
	public void run(JSONObject req, JSONObject resp) throws Exception {

		String detachedB64 = req.getString("envelope");
		String contentB64 = req.getString("content");

		byte[] detached = Base64.decode(detachedB64);
		byte[] content = Base64.decode(contentB64);

		byte[] attached = Utils.getBlucutil().attachContentsToPKCS7(content,
				detached);

		String attachedB64 = new String(Base64.encode(attached));

		// Produce response
		resp.put("envelope", attachedB64);

		resp.put(
				"sha256hex",
				Utils.bytesToHex(Utils.getBlucutil().getCcServ()
						.calcSha256(attached)));
	}

	@Override
	public String getContext() {
		return "bluc-attach";
	}

}