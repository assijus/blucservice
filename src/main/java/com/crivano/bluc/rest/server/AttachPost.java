package com.crivano.bluc.rest.server;

import org.bouncycastle.util.encoders.Base64;
import org.json.JSONObject;

import com.crivano.bluc.rest.server.IBlueCrystal.AttachPostRequest;
import com.crivano.bluc.rest.server.IBlueCrystal.AttachPostResponse;
import com.crivano.bluc.rest.server.IBlueCrystal.IAttachPost;

public class AttachPost implements IAttachPost {

	@Override
	public String getContext() {
		return "bluc-attach";
	}

	@Override
	public void run(AttachPostRequest req, AttachPostResponse resp)
			throws Exception {
		byte[] attached = Utils.getBlucutil().attachContentsToPKCS7(
				req.content, req.envelope);

		resp.envelope = attached;

		resp.sha256hex = Utils.bytesToHex(Utils.getBlucutil().getCcServ()
				.calcSha256(attached));
	}
}