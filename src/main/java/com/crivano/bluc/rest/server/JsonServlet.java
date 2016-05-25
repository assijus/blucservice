package com.crivano.bluc.rest.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class JsonServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(Utils.class.getName());
	private static final Map<String, byte[]> cache = new HashMap<String, byte[]>();

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			JSONObject req = new JSONObject();
			for (Object key : request.getParameterMap().keySet())
				if (key instanceof String
						&& request.getParameterMap().get(key) instanceof String)
					req.put((String) key, request.getParameterMap().get(key));

			if (isCacheable()) {
				String cache = cacheRetrieveJson(getContext(), req);
				if (cache != null) {
					writeJsonRespFromCache(response, cache);
					return;
				}
			}

			JSONObject resp = new JSONObject();
			run(request, response, req, resp);

			if (isCacheable() && resp.optString("error", null) == null) {
				cacheStoreJson(getContext(), req, resp);
			}
			writeJsonResp(response, resp, getContext());
		} catch (Exception e) {
			writeJsonError(response, "Não foi possível " + getContext(), e,
					getContext());
		}
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			JSONObject req = getJsonReq(request, getContext());

			if (isCacheable()) {
				String cache = cacheRetrieveJson(getContext(), req);
				if (cache != null) {
					writeJsonRespFromCache(response, cache);
					return;
				}
			}

			JSONObject resp = new JSONObject();
			run(request, response, req, resp);

			if (isCacheable()) {
				cacheStoreJson(getContext(), req, resp);
			}
			writeJsonResp(response, resp, getContext());
		} catch (Exception e) {
			writeJsonError(response, "Não foi possível " + getContext(), e,
					getContext());
		}
	}

	abstract protected void run(HttpServletRequest request,
			HttpServletResponse response, JSONObject req, JSONObject resp)
			throws Exception;

	abstract protected String getContext();

	protected boolean isCacheable() {
		return false;
	}

	private String getBody(HttpServletRequest request) throws IOException {

		String body = null;
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferedReader = null;

		try {
			InputStream inputStream = request.getInputStream();
			if (inputStream != null) {
				bufferedReader = new BufferedReader(new InputStreamReader(
						inputStream));
				char[] charBuffer = new char[128];
				int bytesRead = -1;
				while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
					stringBuilder.append(charBuffer, 0, bytesRead);
				}
			} else {
				stringBuilder.append("");
			}
		} catch (IOException ex) {
			throw ex;
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException ex) {
					throw ex;
				}
			}
		}

		body = stringBuilder.toString();
		return body;
	}

	private JSONObject getJsonReq(HttpServletRequest request, String context) {
		try {
			String sJson = getBody(request);
			JSONObject req = new JSONObject(sJson);
			if (context != null)
				log.info(context + " req: " + req.toString(3));
			return req;
		} catch (Exception ex) {
			throw new RuntimeException("Cannot parse request body as JSON", ex);
		}
	}

	private void writeJsonResp(HttpServletResponse response, JSONObject resp,
			String context) throws JSONException, IOException {
		if (context != null)
			log.info(context + " resp: " + resp.toString(3));

		String s = resp.toString(2);
		response.setContentType("application/json; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
		response.setHeader("Access-Control-Allow-Headers",
				"Content-Type, Accept, X-Requested-With");
		response.getWriter().println(s);
	}

	public void writeJsonRespFromCache(HttpServletResponse response, String resp)
			throws JSONException, IOException {
		if (getContext() != null)
			log.info(getContext() + " resp from cache: " + resp);

		response.setContentType("application/json; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(resp);
	}

	private void writeJsonError(HttpServletResponse response, String message,
			final Exception e, String context) {
		JSONObject json = new JSONObject();
		String errmsg = e.getMessage();

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String errstack = sw.toString(); // stack trace as a string

		if (errmsg == null)
			if (e instanceof NullPointerException)
				errmsg = "null pointer.";
		if (message != null)
			errmsg = message + " - " + errmsg;
		try {
			json.put("error", errmsg);

			// Error Details
			JSONArray arr = new JSONArray();
			JSONObject detail = new JSONObject();
			detail.put("context", context);
			detail.put("service", "blucservice");
			detail.put("stacktrace", errstack);
			json.put("errordetails", arr);

			response.setStatus(500);
			writeJsonResp(response, json, context);
		} catch (Exception e1) {
			throw new RuntimeException("Erro retornando mensagem de erro.", e1);
		}
	}

	public static void cacheStore(String sha1, byte[] ba) {
		cache.put(sha1, ba);
	}

	public static byte[] cacheRetrieve(String sha1) {
		if (cache.containsKey(sha1)) {
			byte[] ba = cache.get(sha1);
			return ba;
		}
		return null;
	}

	public static byte[] cacheRemove(String sha1) {
		if (cache.containsKey(sha1)) {
			byte[] ba = cache.get(sha1);
			cache.remove(sha1);
			return ba;
		}
		return null;
	}

	public static String dbStore(String payload) {
		String id = UUID.randomUUID().toString();
		cacheStore(id, payload.getBytes());
		return id;
	}

	public static String dbRetrieve(String id, boolean remove) {
		byte[] ba = null;
		if (remove)
			ba = cacheRemove(id);
		else
			ba = cacheRetrieve(id);
		if (ba == null)
			return null;
		return new String(ba);
	}

	public static String cacheKeyJson(String context, JSONObject json) {
		String key = String.valueOf(json.toString().hashCode());
		return key;
	}

	public static void cacheStoreJson(String context, JSONObject jsonRequest,
			JSONObject jsonResponse) {
		String key = cacheKeyJson(context, jsonRequest);

		byte[] value;
		try {
			value = jsonResponse.toString().getBytes("UTF-8");
			cacheStore(key, value); // Populate cache.
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static String cacheRetrieveJson(String context,
			JSONObject jsonRequest) {

		String key = cacheKeyJson(context, jsonRequest);

		byte[] ab = (byte[]) cacheRetrieve(key); // Read from cache.
		if (ab != null) {
			try {
				return new String(ab, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	public static byte[] calcSha256(byte[] content)
			throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.reset();
		md.update(content);
		byte[] output = md.digest();
		return output;
	}

}
