package com.crivano.bluc.rest.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;

import bluecrystal.service.loader.FSZipRepoLoader;
import bluecrystal.service.util.PrefsFactory;

public class RepoLoaderImpl extends FSZipRepoLoader {
	static final Logger LOG = Logger.getLogger(RepoLoaderImpl.class.getName());

	static {
		LOG.info("Carregando implementação própria de: " + RepoLoaderImpl.class.getName());
	}

	private String certFolder = PrefsFactory.getCertFolder(); // $NON-NLS-1$
	private byte[] content;

	@Override
	public InputStream load(String key) throws Exception {
		LOG.info("Carregando: " + key);

		if (content == null) {
			LOG.info("loading acrepo.zip");
			InputStream is = RepoLoaderImpl.class.getResourceAsStream("acrepo.zip");
			content = IOUtils.toByteArray(is);
			is.close();
			LOG.info("loaded acrepo.zip");
		}

		InputStream myIs = new ByteArrayInputStream(content);

		ZipInputStream zis = new ZipInputStream(myIs);

		ZipEntry ze = zis.getNextEntry();

		long totalLen = 0l;
		ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
		while (ze != null) {
			if (!ze.isDirectory() && ze.getName().startsWith(key)) {
				totalLen += ze.getSize();
				int len = 0;
				byte[] buffer = new byte[1024];

				while ((len = zis.read(buffer)) > 0) {
					outBuffer.write(buffer, 0, len);
				}
			}
			zis.closeEntry();
			outBuffer.write("\n".getBytes(), 0, 1);
			ze = zis.getNextEntry();
		}

		byte[] b = outBuffer.toByteArray();
		zis.close();
		return new ByteArrayInputStream(b);
	}

	@Override
	public boolean isDir(String object) throws Exception {
		return false;
	}

	@Override
	public String getFullPath(String object) {
		return certFolder + ".zip";
	}

	@Override
	public boolean exists(String object) throws Exception {
		return true;
	}

	@Override
	public String[] list(String object) throws Exception {
		return null;
	}

	@Override
	public InputStream loadFromContent(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String Put(InputStream input, String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String PutInSupport(InputStream input, String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String PutInContent(InputStream input, String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String checkContentByHash(String sha256) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String PutIn(InputStream input, String key, String bucket) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String PutDirect(InputStream input, String key, String bucket) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createAuthUrl(String object) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}