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
import java.io.FileWriter;
import java.io.IOException;
import org.junit.Test;

/**
 * Test for io utils.
 *
 * @author Kenan Klisura
 */
public class IoUtilsTest {
  @Test
  public void testCloseSilently() throws IOException {
    IoUtils.closeSilently(null);
    IoUtils.closeSilently(new FileWriter(File.createTempFile("props-to-constants-test", "writer")));

    File writer = File.createTempFile("props-to-constants-test", "writer");
    writer.delete();

    IoUtils.closeSilently(new FileWriter(writer));

    File writer2 = File.createTempFile("props-to-constants-test", "writer");
    FileWriter fileWriter2 = new FileWriter(writer2);
    fileWriter2.close();

    IoUtils.closeSilently(fileWriter2);
  }
}
