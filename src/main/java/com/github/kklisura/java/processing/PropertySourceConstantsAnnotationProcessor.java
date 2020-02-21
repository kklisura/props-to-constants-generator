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

import com.github.kklisura.java.processing.annotations.PropertySourceConstants;
import com.github.kklisura.java.processing.annotations.PropertySourceConstantsContainer;
import com.github.kklisura.java.processing.support.ClassWriter;
import com.github.kklisura.java.processing.support.PropertiesProvider;
import com.github.kklisura.java.processing.support.impl.ClassWriterImpl;
import com.github.kklisura.java.processing.support.impl.PropertiesProviderImpl;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import org.apache.commons.lang3.StringUtils;

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
  private ClassWriter classWriter;
  private PropertiesProvider propertiesProvider;

  public PropertySourceConstantsAnnotationProcessor() {
    this(new ClassWriterImpl(), new PropertiesProviderImpl());
  }

  public PropertySourceConstantsAnnotationProcessor(
      ClassWriter classWriter, PropertiesProvider propertiesProvider) {
    this.classWriter = classWriter;
    this.propertiesProvider = propertiesProvider;
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    if (roundEnv.processingOver() || annotations.isEmpty()) {
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
    try {
      Properties properties =
          propertiesProvider.loadProperties(annotation.resourceName(), processingEnv);
      final Set<String> propertyNames =
          stripPropertyKeys(properties.stringPropertyNames(), annotation);
      classWriter.writeClass(packageName, annotation.className(), propertyNames, processingEnv);

      processingEnv
          .getMessager()
          .printMessage(
              Kind.OTHER,
              MessageFormat.format(
                  "Generated {0} for {1}", annotation.className(), annotation.resourceName()));
    } catch (IOException e) {
      processingEnv.getMessager().printMessage(Kind.ERROR, e.getMessage());
      throw new RuntimeException("Failed reading resource " + annotation.resourceName(), e);
    }
  }

  private Set<String> stripPropertyKeys(
      Set<String> existingPropertyKeys, PropertySourceConstants annotation) {
    final String prefix = annotation.stripPrefix();
    if (StringUtils.isBlank(prefix)) {
      return existingPropertyKeys;
    }

    return existingPropertyKeys
        .stream()
        .map(key -> stripPropertyKey(key, prefix))
        .filter(StringUtils::isNotBlank)
        .collect(Collectors.toSet());
  }

  private String stripPropertyKey(String existingKey, String prefix) {
    if (existingKey.startsWith(prefix)) {
      if (existingKey.length() <= prefix.length()) {
        return null;
      }

      return existingKey.substring(prefix.length() + 1);
    }

    return existingKey;
  }

  @FunctionalInterface
  private interface AnnotatedElement {
    void apply(String packageName, Element element);
  }
}
