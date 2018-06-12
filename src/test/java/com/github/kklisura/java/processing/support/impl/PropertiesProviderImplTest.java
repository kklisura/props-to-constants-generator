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

import static com.github.kklisura.java.processing.utils.TestUtils.getFixtureURI;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.github.kklisura.java.processing.support.PropertiesProvider;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.FileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardLocation;
import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Properties provider test.
 *
 * @author Kenan Klisura
 */
@RunWith(EasyMockRunner.class)
public class PropertiesProviderImplTest extends EasyMockSupport {
  @Mock private Filer filer;

  @Mock private ProcessingEnvironment processingEnvironment;

  @Mock private InputStream inputStream;

  private PropertiesProvider propertiesProvider;

  @Before
  public void setUp() throws Exception {
    propertiesProvider = new PropertiesProviderImpl();
  }

  @Test
  public void testLoadProperties() throws IOException {
    FileObject simpleJavaFileObject = new FixtureFileObject("test-properties");

    expect(processingEnvironment.getFiler()).andReturn(filer);
    expect(filer.getResource(StandardLocation.CLASS_OUTPUT, "", "test-properties"))
        .andReturn(simpleJavaFileObject);

    replayAll();

    Properties properties =
        propertiesProvider.loadProperties("test-properties", processingEnvironment);

    verifyAll();

    assertNotNull(properties);

    assertTrue(properties.containsKey("hello"));
    assertTrue(properties.containsKey("hello.world"));
  }

  @Test(expected = RuntimeException.class)
  public void testLoadPropertiesThrowsException() throws IOException {
    FileObject simpleJavaFileObject = new ThrowableFixtureFileObject("test-properties");

    expect(processingEnvironment.getFiler()).andReturn(filer);
    expect(filer.getResource(StandardLocation.CLASS_OUTPUT, "", "test-properties"))
        .andReturn(simpleJavaFileObject);

    propertiesProvider.loadProperties("test-properties", processingEnvironment);
  }

  @Test
  public void testLoadPropertiesThrowsException2() throws IOException {
    FileObject simpleJavaFileObject = new InputStreamFileObject("test-properties", inputStream);

    expect(processingEnvironment.getFiler()).andReturn(filer);
    expect(filer.getResource(StandardLocation.CLASS_OUTPUT, "", "test-properties"))
        .andReturn(simpleJavaFileObject);

    expect(inputStream.read(anyObject())).andThrow(new IOException("exception"));

    inputStream.close();

    replayAll();

    try {
      propertiesProvider.loadProperties("test-properties", processingEnvironment);
      fail();
    } catch (RuntimeException e) {
      // Ok
    }

    verifyAll();
  }

  public static class FixtureFileObject extends SimpleJavaFileObject {
    protected FixtureFileObject(String fixture) {
      super(getFixtureURI(fixture), Kind.OTHER);
    }

    @Override
    public InputStream openInputStream() throws IOException {
      return toUri().toURL().openStream();
    }
  }

  public static class ThrowableFixtureFileObject extends SimpleJavaFileObject {
    protected ThrowableFixtureFileObject(String fixture) {
      super(URI.create(fixture), Kind.OTHER);
    }

    @Override
    public InputStream openInputStream() throws IOException {
      throw new IOException("Exception");
    }
  }

  public static class InputStreamFileObject extends SimpleJavaFileObject {
    private InputStream inputStream;

    protected InputStreamFileObject(String fixture, InputStream inputStream) {
      super(URI.create(fixture), Kind.OTHER);
      this.inputStream = inputStream;
    }

    @Override
    public InputStream openInputStream() throws IOException {
      return inputStream;
    }
  }
}
