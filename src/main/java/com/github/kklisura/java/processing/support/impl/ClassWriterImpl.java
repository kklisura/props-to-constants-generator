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

import static com.github.kklisura.java.processing.utils.ClassUtils.buildConstantsClass;
import static com.github.kklisura.java.processing.utils.ClassUtils.buildEnumClass;

import com.github.kklisura.java.processing.annotations.PropertySourceConstants;
import com.github.kklisura.java.processing.support.ClassWriter;
import com.github.kklisura.java.processing.utils.IoUtils;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;

/**
 * Class writer implementation.
 *
 * @author Kenan Klisura
 */
public class ClassWriterImpl implements ClassWriter {
  @Override
  public void writeClass(
      String packageName,
      String className,
      Map<String, String> properties,
      PropertySourceConstants.Style style,
      ProcessingEnvironment processingEnvironment)
      throws IOException {

    Writer classWriter = null;
    try {
      int lastDot = className.lastIndexOf(".");
      if (lastDot != -1) {
        packageName = className.substring(0, lastDot);
        className = className.substring(lastDot + 1);
      }
      String name = packageName + "." + className;
      Filer filer = processingEnvironment.getFiler();

      classWriter = filer.createSourceFile(name).openWriter();

      switch (style) {
        case CONSTANTS:
          classWriter.write(buildConstantsClass(packageName, className, properties));
          break;
        case ENUM:
          classWriter.write(buildEnumClass(packageName, className, properties));
          break;
      }

    } finally {
      IoUtils.closeSilently(classWriter);
    }
  }
}
