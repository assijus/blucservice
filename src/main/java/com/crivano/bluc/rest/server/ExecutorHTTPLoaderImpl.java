package com.crivano.bluc.rest.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import com.crivano.swaggerservlet.SwaggerServlet;
import com.crivano.swaggerservlet.SwaggerUtils;

import bluecrystal.service.loader.HttpLoader;

public class ExecutorHTTPLoaderImpl implements HttpLoader {
	static final Logger LOG = Logger.getLogger(ExecutorHTTPLoaderImpl.class.getName());
	private static final int BUFFER_SIZE = 64 * 1024;
	private static final int TIMEOUT_SECONDS = 15;
	private static final String PA_AD_RB_v2_1_DER = "MIIKhjALBglghkgBZQMEAgEwggpTBghgTAEHAQECARgPMjAxMjAzMDcwMDAwMDBaMEuGSUM9QlIsTz1JQ1AtQnJhc2lsLE9VPUluc3RpdHV0byBOYWNpb25hbCBkZSBUZWNub2xvZ2lhIGRhIEluZm9ybWFjYW8gLSBJVEkMggKQRXN0ZSB0aXBvIGRlIGFzc2luYXR1cmEgZGV2ZSBzZXIgdXRpbGl6YWRvIGVtIGFwbGljYWNvZXMgb3UgcHJvY2Vzc29zIGRlIG5lZ29jaW8gbm9zIHF1YWlzIGEgYXNzaW5hdHVyYSBkaWdpdGFsIGFncmVnYSBzZWd1cmFuY2EgYSBhdXRlbnRpY2FjYW8gZGUgZW50aWRhZGVzIGUgdmVyaWZpY2FjYW8gZGUgaW50ZWdyaWRhZGUsIHBlcm1pdGluZG8gc3VhIHZhbGlkYWNhbyBkdXJhbnRlIG8gcHJhem8gZGUgdmFsaWRhZGUgZG9zIGNlcnRpZmljYWRvcyBkb3Mgc2lnbmF0YXJpb3MuIFVtYSB2ZXogcXVlIG5hbyBzYW8gdXNhZG9zIGNhcmltYm9zIGRvIHRlbXBvLCBhIHZhbGlkYWNhbyBwb3N0ZXJpb3Igc28gc2VyYSBwb3NzaXZlbCBzZSBleGlzdGlyZW0gcmVmZXJlbmNpYXMgdGVtcG9yYWlzIHF1ZSBpZGVudGlmaXF1ZW0gbyBtb21lbnRvIGVtIHF1ZSBvY29ycmV1IGEgYXNzaW5hdHVyYSBkaWdpdGFsLiBOZXNzYXMgc2l0dWFjb2VzLCBkZXZlIGV4aXN0aXIgbGVnaXNsYWNhbyBlc3BlY2lmaWNhIG91IHVtIGFjb3JkbyBwcmV2aW8gZW50cmUgYXMgcGFydGVzIGRlZmluaW5kbyBhcyByZWZlcmVuY2lhcyBhIHNlcmVtIHV0aWxpemFkYXMuIFNlZ3VuZG8gZXN0YSBQQSwgZSBwZXJtaXRpZG8gbyBlbXByZWdvIGRlIG11bHRpcGxhcyBhc3NpbmF0dXJhcy4wggdTMCIYDzIwMTIwMzA3MDAwMDAwWhgPMjAyMzA2MjEwMDAwMDBaMIIHI6BBMD8wOTAwBgkqhkiG9w0BCQMGCSqGSIb3DQEJBAYLKoZIhvcNAQkQAg8GCyqGSIb3DQEJEAIvMAChAwoBATACMAChgga/MIIGuzCCBqkwggalMIIGoTCCBImgAwIBAgIBATANBgkqhkiG9w0BAQ0FADCBlzELMAkGA1UEBhMCQlIxEzARBgNVBAoTCklDUC1CcmFzaWwxPTA7BgNVBAsTNEluc3RpdHV0byBOYWNpb25hbCBkZSBUZWNub2xvZ2lhIGRhIEluZm9ybWFjYW8gLSBJVEkxNDAyBgNVBAMTK0F1dG9yaWRhZGUgQ2VydGlmaWNhZG9yYSBSYWl6IEJyYXNpbGVpcmEgdjIwHhcNMTAwNjIxMTkwNDU3WhcNMjMwNjIxMTkwNDU3WjCBlzELMAkGA1UEBhMCQlIxEzARBgNVBAoTCklDUC1CcmFzaWwxPTA7BgNVBAsTNEluc3RpdHV0byBOYWNpb25hbCBkZSBUZWNub2xvZ2lhIGRhIEluZm9ybWFjYW8gLSBJVEkxNDAyBgNVBAMTK0F1dG9yaWRhZGUgQ2VydGlmaWNhZG9yYSBSYWl6IEJyYXNpbGVpcmEgdjIwggIiMA0GCSqGSIb3DQEBAQUAA4ICDwAwggIKAoICAQC6RqQO3edA8rWgfFKVV0X8bYTzhgHJhQOtmKvS8l4Fmcm7b2Jn/XdEuQMHPNIbAGLUcCxCg3lmq5lWroG8akm983QPYrfrWwdmlEIknUasmkIYMPAkqFFB6quV8agrAnhptSknXpwuc8b+I6Xjps79bBtrAFTrAK1POkw85wqIW9pemgtW5LVUOB3yCpNkTsNBklMgKs/8dG7U2zM4YuT+jkxYHPePKk3/xZLZCVK9z3AAnWmaM2qIh0UhmRZRDTTfgr20aah8fNTd0/IVXEvFWBDqhRnLNiJYKnIMmpbeys8IUWG/tAUpBiuGkP7pTcMEBUfLz3bZf3Gmh3sVQOQzgHgHHaTyjptAO8lyUN9pvvAslh+QtdWudONltIwa6Wob+3JcxYJU6uBTB8TMEun33tcv1EgvRz8mYQSxEpoza7WGSxMr0IadR+1p+/yEEmb4VuUOimx2xGsaesKgWhLRI4lYAXwIWNoVjhXZfn03tqRF9QOFzEf6i3lFuGZiM9MmSt4c6dR/5m0muTx9zQ8oCikPm91jq7mmRxqE14WkA2UGBEtSjYM0Qn8xjhEu5rNnlUB+l3pAAPkRbIM4WK0DM1umxMHFsKwNqQbwpmkBNLbp+JRITz6mdQnsSsU74MlesDL/n2lZzzwwbw3OJ1fsWhto/+xPb3gyPnnFtF2VfwIDAQABo4H1MIHyME4GA1UdIARHMEUwQwYFYEwBAQAwOjA4BggrBgEFBQcCARYsaHR0cDovL2FjcmFpei5pY3BicmFzaWwuZ292LmJyL0RQQ2FjcmFpei5wZGYwPwYDVR0fBDgwNjA0oDKgMIYuaHR0cDovL2FjcmFpei5pY3BicmFzaWwuZ292LmJyL0xDUmFjcmFpenYyLmNybDAfBgNVHSMEGDAWgBQMOSA6twEfy9cofUGgx/pKrTIkvjAdBgNVHQ4EFgQUDDkgOrcBH8vXKH1BoMf6Sq0yJL4wDwYDVR0TAQH/BAUwAwEB/zAOBgNVHQ8BAf8EBAMCAQYwDQYJKoZIhvcNAQENBQADggIBAFmaFGkYbX0pQ3B9dpth33eOGnbkqdbLdqQWDEyUEsaQ0YEDxa0G2S1EvLIJdgmAOWcAGDRtBgrmtRBZSLp1YPw/jh0YVXArnkuVrImrCncke2HEx5EmjkYTUTe2jCcK0w3wmisig4OzvYM1rZs8vHiDKTVhNvgRcTMgVGNTRQHYE1qEO9dmEyS3xEbFIthzJO4cExeWyCXoGx7P34VQbTzq91CeG5fep2vb1nPSz3xQwLCM5VMSeoY5rDVbZ8fq1PvRwl3qDpdzmK4pv+Q68wQ2UCzt3h7bhegdhAnu86aDM1tvR3lPSLX8uCYTq6qz9GER+0Vn8x0+bv4qSyZEGp+xouA82uDkBTp4rPuooU2/XSx3KZDNEx3vBijYtxTzW8jJnqd+MRKKeGLE0QW8BgJjBCsNid3kXFsygETUQuwq8/JAhzHVPuIKMgwUjdVybQvm/Y3kqPMFjXUXd5sKufqQkplliDJnQwWOLQsVuzXxYejZZ3ftFuXoAS1rND+Og7P36g9KHj41hJ2MgDQ/qZXow63EzZ7KFBYsGZ7kNou5uaNCJQc+w+XVaE+gZhyms7ZzHJAaP0C5GlZCcIf/by0PEf0e//eFMBUO4xcx7ieVzMnpmR6Xx21bB7UFaj3yRd+6gnkkcC6bgh9mqaVtJ8z2KqLRX4Vv4EadqtKlTlUOMAwwAwoBA6AFMAMKAQOiAjAApBcwFaATMBEwDwYJKoZIhvcNAQELAgIIADAGMAQwAgUABCDdV8mKQxO8E5jOZUPTgCRYlXz3Fq4ylOxNjCYlEpHmwQ==";

	private static ExecutorService executor = Executors
			.newFixedThreadPool(new Integer(SwaggerServlet.getProperty("threadpool.size")));

	static {
		LOG.info("Carregando implementação própria de: " + ExecutorHTTPLoaderImpl.class.getName());
	}

	public ExecutorHTTPLoaderImpl() {
		super();
	}

	public byte[] get(String url) throws MalformedURLException, IOException {
		if ("http://politicas.icpbrasil.gov.br/PA_AD_RB_v2_1.der".equals(url))
			return SwaggerUtils.base64Decode(PA_AD_RB_v2_1_DER);

		try {
			LOG.info("Downloading url: " + url);

			Future<Resp> fut = executor.submit(new Req(url));
			Resp resp = fut.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
			if (resp.cause != null)
				throw new RuntimeException(resp.cause);
			if (resp.errormsg != null)
				throw new Exception(
						"Temporariamente não é possível processar a assinatura digital em decorrência de falha na comunicação com a Autoridade Certificadora. Favor realizar nova tentativa em 5 minutos. ("
								+ url + ") - " + resp.errormsg + " - " + resp.errorbody);

			LOG.info("Downloaded url: " + url);
			return resp.body;
		} catch (TimeoutException e) {
			throw new RuntimeException(
					"Temporariamente não é possível processar a assinatura digital em decorrência de demora na comunicação com a Autoridade Certificadora. Favor realizar nova tentativa em 5 minutos. ("
							+ url + ")");
		} catch (Throwable e) {
			LOG.severe("Could not download: " + url + "(" + e.getMessage() + ")");
			throw new RuntimeException(e);
		}
	}

	private static class Req implements Callable<Resp> {
		private String url;

		public Req(String url) {
			this.url = url;
		}

		@Override
		public Resp call() throws Exception {
			return fetch(new URL(url));
		}
	}

	private static class Resp {
		byte[] body;
		String errormsg;
		String errorbody;
		Throwable cause;
	}

	public static Resp fetch(URL url) {
		Resp resp = new Resp();
		HttpURLConnection con = null;
		try {
			con = (HttpURLConnection) url.openConnection();

			con.setRequestMethod("GET");
			int responseCode = con.getResponseCode();
			if (responseCode >= 400 && responseCode < 600) {
				resp.errorbody = convertStreamToString(con.getErrorStream());
				String errormsg = "HTTP ERROR: " + Integer.toString(responseCode);
				if (con.getResponseMessage() != null)
					errormsg = errormsg + " - " + con.getResponseMessage();
			}

			resp.body = convertStreamToByteArray(con.getInputStream(), BUFFER_SIZE);
		} catch (Exception e) {
			resp.cause = e;
		}
		return resp;
	}

	public static String convertStreamToString(java.io.InputStream is) {
		@SuppressWarnings("resource")
		java.util.Scanner s = new java.util.Scanner(is, "UTF-8").useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

	public static byte[] convertStreamToByteArray(InputStream stream, long size) throws IOException {
		// check to ensure that file size is not larger than Integer.MAX_VALUE.
		if (size > Integer.MAX_VALUE) {
			return new byte[0];
		}

		byte[] buffer = new byte[(int) size];
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		int line = 0;
		// read bytes from stream, and store them in buffer
		while ((line = stream.read(buffer)) != -1) {
			// Writes bytes from byte array (buffer) into output stream.
			os.write(buffer, 0, line);
		}
		stream.close();
		os.flush();
		os.close();
		return os.toByteArray();
	}

	@Override
	public byte[] post(String url, String contentType, byte[] body) throws MalformedURLException, IOException {
		LOG.info("Posting to url: " + url);
		URL u = new URL(url);
		HttpURLConnection con = (HttpURLConnection) u.openConnection();

		con.setAllowUserInteraction(false);
		con.setDoInput(true);
		con.setDoOutput(true);
		con.setUseCaches(false);
		con.setInstanceFollowRedirects(false);
		con.setRequestMethod("POST");

		con.setRequestProperty("Content-Length", Integer.toString(body.length));
		con.setRequestProperty("Content-Type", contentType);

		con.connect();
		OutputStream os = con.getOutputStream();
		os.write(body);
		os.close();

		if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
			throw new IOException("Server did not respond with HTTP_OK(200) but with " + con.getResponseCode());
		}

		if ((con.getContentType() == null) || !con.getContentType().equals("application/ocsp-response")) {
			throw new IOException("Response MIME type is not application/ocsp-response");
		}

		// Read response
		InputStream reader = con.getInputStream();

		int resplen = con.getContentLength();
		byte[] ocspResponseEncoded = new byte[resplen];

		int offset = 0;
		int bread;
		while ((resplen > 0) && (bread = reader.read(ocspResponseEncoded, offset, resplen)) != -1) {
			offset += bread;
			resplen -= bread;
		}

		reader.close();
		con.disconnect();
		LOG.info("Posted to url: " + url);
		return ocspResponseEncoded;
	}

}