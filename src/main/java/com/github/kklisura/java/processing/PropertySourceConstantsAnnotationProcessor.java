package com.github.kklisura.java.processing;

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

import static com.github.kklisura.java.processing.utils.PropertiesUtils.loadProperties;

import com.github.kklisura.java.processing.annotations.PropertySourceConstants;
import com.github.kklisura.java.processing.annotations.PropertySourceConstantsContainer;
import com.github.kklisura.java.processing.utils.ClassUtils;
import com.github.kklisura.java.processing.utils.IoUtils;
import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

/**
 * PropertySourceConstants annotation processor.
 *
 * @author Kenan Klisura
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({
  "com.github.kklisura.java.processing.annotations.PropertySourceConstants",
  "com.github.kklisura.java.processing.annotations.PropertySourceConstantsContainer"
})
public class PropertySourceConstantsAnnotationProcessor extends AbstractProcessor {
  private static final String EMPTY_PACKAGE = "";

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    if (roundEnv.processingOver() || annotations.size() == 0) {
      return false;
    }

    processingEnv
        .getMessager()
        .printMessage(Kind.NOTE, "PropertySourceConstants annotation processor");

    process(roundEnv);
    return false;
  }

  private void process(RoundEnvironment roundEnv) {
    forEachAnnotationElement(
        roundEnv, PropertySourceConstants.class, this::processPropertySourceConstants);
    forEachAnnotationElement(
        roundEnv,
        PropertySourceConstantsContainer.class,
        this::processPropertySourceConstantsContainer);
  }

  private void forEachAnnotationElement(
      RoundEnvironment roundEnv,
      Class<? extends Annotation> annotation,
      AnnotatedElement annotatedElement) {
    Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);

    for (Element element : elements) {
      String packageName =
          ((PackageElement) element.getEnclosingElement()).getQualifiedName().toString();

      annotatedElement.apply(packageName, element);
    }
  }

  private void processPropertySourceConstantsContainer(String packageName, Element element) {
    PropertySourceConstantsContainer[] annotations =
        element.getAnnotationsByType(PropertySourceConstantsContainer.class);

    for (PropertySourceConstantsContainer annotation : annotations) {
      for (PropertySourceConstants childAnnotation : annotation.value()) {
        processPropertySourceConstants(packageName, childAnnotation);
      }
    }
  }

  private void processPropertySourceConstants(String packageName, Element element) {
    PropertySourceConstants[] annotations =
        element.getAnnotationsByType(PropertySourceConstants.class);

    for (PropertySourceConstants annotation : annotations) {
      processPropertySourceConstants(packageName, annotation);
    }
  }

  private void processPropertySourceConstants(
      String packageName, PropertySourceConstants annotation) {
    Writer classWriter = null;

    try {
      Filer filer = processingEnv.getFiler();
      String name = packageName + "." + annotation.className();

      classWriter = filer.createSourceFile(name).openWriter();

      Properties properties = loadPropertiesFromResource(annotation, filer);

      ClassUtils.writePropertiesConstantsClass(
          classWriter, packageName, annotation.className(), properties.stringPropertyNames());

      processingEnv
          .getMessager()
          .printMessage(
              Kind.OTHER,
              MessageFormat.format(
                  "Generated {0} for {1}", annotation.className(), annotation.resourceName()));
    } catch (IOException e) {
      processingEnv.getMessager().printMessage(Kind.ERROR, e.getMessage());
      throw new RuntimeException("Failed reading resource " + annotation.resourceName(), e);
    } finally {
      IoUtils.closeSilently(classWriter);
    }
  }

  private Properties loadPropertiesFromResource(PropertySourceConstants annotation, Filer filer)
      throws IOException {
    FileObject resource =
        filer.getResource(StandardLocation.CLASS_OUTPUT, EMPTY_PACKAGE, annotation.resourceName());
    return loadProperties(resource);
  }

  private interface AnnotatedElement {
    void apply(String packageName, Element element);
  }
}
