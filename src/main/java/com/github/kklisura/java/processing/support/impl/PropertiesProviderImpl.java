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

import com.github.kklisura.java.processing.support.PropertiesProvider;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

/**
 * Properties provider implementation.
 *
 * @author Kenan Klisura
 */
public class PropertiesProviderImpl implements PropertiesProvider {
  private static final String EMPTY_PACKAGE = "";

  @Override
  public Properties loadProperties(String resourceName, ProcessingEnvironment processingEnv)
      throws IOException {
    FileObject resource =
        processingEnv
            .getFiler()
            .getResource(StandardLocation.CLASS_OUTPUT, EMPTY_PACKAGE, resourceName);
    return loadProperties(resource);
  }

  private Properties loadProperties(FileObject fileObject) {
    Properties properties = new Properties();

    InputStream inputStream = null;
    try {
      inputStream = fileObject.openInputStream();
      properties.load(inputStream);
    } catch (IOException e) {
      throw new RuntimeException("Failed reading properties file " + fileObject.getName(), e);
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException e) {
          // Ignore this exception.
        }
      }
    }

    return properties;
  }
}
