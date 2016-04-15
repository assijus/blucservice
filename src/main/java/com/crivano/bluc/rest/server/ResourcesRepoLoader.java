/*
    Blue Crystal: Document Digital Signature Tool
    Copyright (C) 2007-2015  Sergio Leal

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.crivano.bluc.rest.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import bluecrystal.service.interfaces.RepoLoader;

public class ResourcesRepoLoader implements RepoLoader {

	@Override
	public InputStream load(String key) throws Exception {
		FileInputStream fis = null;
		fis = new FileInputStream(getFullPath(key));
		return fis;
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

	@Override
	public boolean isDir(String object) throws Exception {
		File f = new File(getFullPath(object));
		return f.isDirectory();
	}

	@Override
	public String getFullPath(String object) {
		return "/var/lib/blucservice/acrepo" + File.separator + object;
	}

	@Override
	public boolean exists(String object) throws Exception {
		File f = new File(getFullPath(object));
		return f.exists();
	}

	@Override
	public String[] list(String object) throws Exception {
		final String path = "acrepo";
		final File jarFile = new File(getClass().getProtectionDomain()
				.getCodeSource().getLocation().getPath());

		if (jarFile.isFile()) { // Run with JAR file
			final JarFile jar = new JarFile(jarFile);
			final Enumeration<JarEntry> entries = jar.entries(); // gives ALL
																	// entries
																	// in jar
			while (entries.hasMoreElements()) {
				final String name = entries.nextElement().getName();
				if (name.startsWith(path + "/")) { // filter according to the
													// path
					System.out.println(name);
				}
			}
			jar.close();
		} else { // Run with IDE
			final URL url = getClass().getResource("/" + path + "/" + object);
			if (url != null) {
				try {
					final File apps = new File(url.toURI());
					for (File app : apps.listFiles()) {
						System.out.println(app);
					}
				} catch (URISyntaxException ex) {
					// never happens
				}
			}
		}

		File f = new File(getFullPath(object));
		String[] fList = f.list();

		String[] ret = new String[fList.length];

		for (int i = 0; i < fList.length; i++) {
			ret[i] = object + File.separator + fList[i];
		}

		return ret;
	}

}
