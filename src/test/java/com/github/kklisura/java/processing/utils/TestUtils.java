package com.github.kklisura.java.processing.utils;

/*-
 * #%L
 * props-to-constants-generator
 * %%
 * Copyright (C) 2018 Kenan Klisura
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Test utilities.
 *
 * @author Kenan Klisura
 */
public final class TestUtils {
  private static final String FIXTURES = "fixtures";

  /**
   * Returns a fixture data.
   *
   * @param name Fixture name.
   * @return Fixture URI.
   */
  public static URI getFixtureURI(String name) {
    try {
      return TestUtils.class.getClassLoader().getResource(FIXTURES + "/" + name).toURI();
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Returns a fixture data.
   *
   * @param name Fixture name.
   * @return Fixture data.
   */
  public static String getFixture(String name) {
    InputStream inputStream =
        TestUtils.class.getClassLoader().getResourceAsStream(FIXTURES + "/" + name);
    try {
      return inputStreamToString(inputStream);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Reads a file to string.
   *
   * @param file Input file.
   * @return File contents.
   * @throws IOException If reading fails.
   */
  public static String readFile(File file) throws IOException {
    return inputStreamToString(new FileInputStream(file));
  }

  /**
   * Reads an input stream to string. https://stackoverflow.com/a/35446009/1209358
   *
   * @param inputStream Input stream.
   * @return String.
   * @throws IOException If failed reading from input stream.
   */
  public static String inputStreamToString(InputStream inputStream) throws IOException {
    final int bufferSize = 1024;
    final char[] buffer = new char[bufferSize];
    final StringBuilder out = new StringBuilder();
    Reader in = new InputStreamReader(inputStream, "UTF-8");
    for (; ; ) {
      int rsz = in.read(buffer, 0, buffer.length);
      if (rsz < 0) break;
      out.append(buffer, 0, rsz);
    }
    return out.toString();
  }
}
