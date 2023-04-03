package com.crivano.bluc.rest.server;

import com.crivano.blucservice.api.BlueCrystalContext;
import com.crivano.blucservice.api.IBlueCrystal.IAttachPost;

public class AttachPost implements IAttachPost {

	@Override
	public String getContext() {
		return "bluc-attach";
	}

	@Override
	public void run(Request req, Response resp, BlueCrystalContext ctx) throws Exception {
		byte[] attached = Utils.getBlucutil().attachContentsToPKCS7(req.content, req.envelope, req.time,
				req.crl != null ? req.crl : false);

		resp.envelope = attached;

		resp.sha256hex = Utils.bytesToHex(Utils.getBlucutil().getCcServ().calcSha256(attached));
	}
}