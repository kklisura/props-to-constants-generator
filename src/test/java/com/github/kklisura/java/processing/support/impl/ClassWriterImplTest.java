package com.github.kklisura.java.processing.support.impl;

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

import static com.github.kklisura.java.processing.utils.TestUtils.getFixture;
import static com.github.kklisura.java.processing.utils.TestUtils.readFile;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import com.github.kklisura.java.processing.support.ClassWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;
import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by Kenan Klisura on 12/06/2018.
 *
 * @author Kenan Klisura
 */
@RunWith(EasyMockRunner.class)
public class ClassWriterImplTest extends EasyMockSupport {
  @Mock private ProcessingEnvironment processingEnvironment;

  @Mock private Filer filer;

  @Mock private JavaFileObject javaFileObject;

  private ClassWriter classWriter;

  @Before
  public void setUp() throws Exception {
    classWriter = new ClassWriterImpl();
  }

  @Test
  public void testWriteClass() throws IOException {
    File tmpFile = File.createTempFile("props-to-constants-generator-test", "java");
    FileWriter fileWriter = new FileWriter(tmpFile);

    expect(processingEnvironment.getFiler()).andReturn(filer);
    expect(filer.createSourceFile("com.github.kklisura.test.TestClass")).andReturn(javaFileObject);
    expect(javaFileObject.openWriter()).andReturn(fileWriter);

    replayAll();

    classWriter.writeClass(
        "com.github.kklisura.test",
        "TestClass",
        set("my.property.1", "my.property.2"),
        processingEnvironment);

    verifyAll();

    assertEquals(getFixture("test-class-2-java"), readFile(tmpFile));
  }

  private static Set<String> set(String... items) {
    return new LinkedHashSet<>(Arrays.asList(items));
  }
}
