package com.crivano.bluc.rest.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

import bluecrystal.service.loader.HttpLoader;

public class ExecutorHTTPLoaderImpl implements HttpLoader {
	static final Logger LOG = Logger.getLogger(ExecutorHTTPLoaderImpl.class.getName());
	private static final int BUFFER_SIZE = 64 * 1024;
	private static final int TIMEOUT_SECONDS = 15;

	private static ExecutorService executor = Executors
			.newFixedThreadPool(new Integer(SwaggerServlet.getProperty("threadpool.size")));

	static {
		LOG.info("Carregando implementação própria de: " + ExecutorHTTPLoaderImpl.class.getName());
	}

	public ExecutorHTTPLoaderImpl() {
		super();
	}

	public byte[] getfromUrl(String url) throws MalformedURLException, IOException {
		try {
			LOG.info("Baixando url: " + url);
			Future<Resp> fut = executor.submit(new Req(url));
			Resp resp = fut.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
			if (resp.cause != null)
				throw new RuntimeException(resp.cause);
			if (resp.errormsg != null)
				throw new Exception(
						"Temporariamente não é possível processar a assinatura digital em decorrência de falha na comunicação com a Autoridade Certificadora. Favor realizar nova tentativa em 5 minutos. ("
								+ url + ") - " + resp.errormsg + " - " + resp.errorbody);
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

}