/*
 * Copyright (C) 2016 Stefan Henß
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.insightml.utils.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.insightml.utils.Check;
import com.insightml.utils.Pair;

public final class IoUtils {

	private IoUtils() {
	}

	public static File[] files(final File dir) {
		final File[] files = dir.listFiles();
		if (files == null) {
			return null;
		}
		Arrays.sort(files);
		return files;
	}

	public static String readFile(final InputStream stream, final Charset charset) {
		try (InputStreamReader reader = new InputStreamReader(stream, charset)) {
			return CharStreams.toString(reader);
		} catch (final IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static String readFile(final File file) {
		try {
			if (file.getName().endsWith(".gz")) {
				return readFile(new GZIPInputStream(new FileInputStream(file)), Charsets.UTF_8);
			}
			return Files.toString(file, Charsets.UTF_8);
		} catch (final IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static String readFile(final String file) {
		return readFile(file, Charsets.UTF_8);
	}

	private static String readFile(final String file, final Charset charset) {
		return readFile(Check.notNull(IoUtils.class.getResourceAsStream(file), file), charset);
	}

	public static LineReader lines(final File file) {
		return new LineReader(file);
	}

	public static LineReader lines(final String file) {
		return new LineReader(IoUtils.class.getResourceAsStream(file));
	}

	public static void write(final String text, final File file) {
		write(text, file, Charsets.UTF_8);
	}

	public static void write(final String text, final File file, final Charset charset) {
		try {
			Files.write(Check.length(text, 0, 1999999999), file, charset);
		} catch (final IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static String process(final String command) throws IOException {
		return process(Runtime.getRuntime().exec(command));
	}

	public static String process(final String[] args) throws IOException {
		return process(Runtime.getRuntime().exec(args));
	}

	private static String process(final Process proc) throws IOException {
		final StringBuilder out = new StringBuilder();
		try (final BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));) {
			String s;
			while ((s = stdInput.readLine()) != null) {
				out.append(s);
				out.append('\n');
			}
		} finally {
			proc.destroy();
		}
		return out.toString();
	}

	public static List<File> filesWithEnd(final File folder, final String postfix) {
		final List<File> files = new LinkedList<>();
		if (!folder.exists()) {
			return files;
		}
		final String postlc = postfix.toLowerCase();
		for (final File file : folder.listFiles()) {
			if (file.getName().toLowerCase().endsWith(postlc)) {
				files.add(file);
			}
		}
		return files;
	}

	public static void append(final File file, final String text) {
		try {
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			try (FileWriter fw = new FileWriter(file, true); BufferedWriter out = new BufferedWriter(fw)) {
				out.write(text);
			}
		} catch (final IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public static BufferedWriter writer(final File file, final boolean gzip) {
		try {
			final int sz = (int) Math.pow(1024, 2);
			if (gzip) {
				return new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(file))), sz);
			}
			return new BufferedWriter(new FileWriter(file), sz);
		} catch (final IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static void delete(final File f) {
		if (f.isDirectory()) {
			for (final File c : f.listFiles()) {
				delete(c);
			}
		}
		f.delete();
	}

	public static void zip(final List<File> files, final File target) {
		final List<Pair<InputStream, String>> streams = new LinkedList<>();
		for (final File file : files) {
			addFile(file, "", streams, target);
		}
		zip2(streams, target);
	}

	private static void addFile(final File file, final String dirBase, final List<Pair<InputStream, String>> streams,
			final File target) {
		if (file.isDirectory()) {
			for (final File fil : file.listFiles()) {
				addFile(fil, dirBase + file.getName() + "/", streams, target);
			}
		} else {
			try {
				if (!file.getName().equals(target.getName())) {
					streams.add(new Pair<InputStream, String>(new FileInputStream(file), dirBase + file.getName()));
				}
			} catch (final FileNotFoundException e) {
				throw new IllegalArgumentException(e);
			}
		}
	}

	public static void zip2(final List<Pair<InputStream, String>> files, final File target) {
		final byte[] buffer = new byte[1024];
		try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(target))) {
			for (final Pair<InputStream, String> file : files) {
				final ZipEntry ze = new ZipEntry(file.getSecond());
				zos.putNextEntry(ze);

				int len;
				while ((len = file.getFirst().read(buffer)) > 0) {
					zos.write(buffer, 0, len);
				}

				file.getFirst().close();
				zos.closeEntry();
			}
		} catch (final IOException e) {
			throw new IllegalStateException(e);
		}
	}
}
