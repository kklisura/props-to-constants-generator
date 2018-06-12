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

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.github.kklisura.java.processing.annotations.PropertySourceConstants;
import com.github.kklisura.java.processing.annotations.PropertySourceConstantsContainer;
import com.github.kklisura.java.processing.support.ClassWriter;
import com.github.kklisura.java.processing.support.PropertiesProvider;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class.
 *
 * @author Kenan Klisura
 */
@RunWith(EasyMockRunner.class)
public class PropertySourceConstantsAnnotationProcessorTest extends EasyMockSupport {
  @Mock private Messager messager;

  @Mock private TypeElement typeElement;

  @Mock private PackageElement packageElement;

  @Mock private Filer filer;

  @Mock private RoundEnvironment roundEnv;

  @Mock private ProcessingEnvironment processingEnv;

  @Mock private PropertySourceConstants propertySourceConstants;

  @Mock private PropertySourceConstantsContainer propertySourceConstantsContainer;

  @Mock private ClassWriter classWriter;

  @Mock private PropertiesProvider propertiesProvider;

  private PropertySourceConstantsAnnotationProcessor processor;

  @Before
  public void setUp() {
    processor = new PropertySourceConstantsAnnotationProcessor(classWriter, propertiesProvider);
    processor.init(processingEnv);
  }

  @Test
  public void testProcessNonProcess() {
    expect(roundEnv.processingOver()).andReturn(true);

    replayAll();

    assertEquals(false, processor.process(Collections.emptySet(), roundEnv));

    verifyAll();
    resetAll();

    expect(roundEnv.processingOver()).andReturn(false);

    replayAll();
    assertEquals(false, processor.process(Collections.emptySet(), roundEnv));
    verifyAll();
  }

  @Test
  public void testProcessNoAnnotations() {
    setupProcessingEnvironment();
    setupRoundEnvironment();

    expect(roundEnv.getElementsAnnotatedWith(PropertySourceConstants.class))
        .andReturn(Collections.emptySet());
    expect(roundEnv.getElementsAnnotatedWith(PropertySourceConstantsContainer.class))
        .andReturn(Collections.emptySet());

    replayAll();

    assertEquals(false, processor.process(annotationsSet(), roundEnv));

    verifyAll();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testProcessPropertySourceConstantsAnnotation() throws IOException {
    setupProcessingEnvironment();
    setupRoundEnvironment();

    setupPackageElement("com.github.kklisura");

    expect(roundEnv.getElementsAnnotatedWith(PropertySourceConstants.class))
        .andReturn(elementSet());

    expect(roundEnv.getElementsAnnotatedWith(PropertySourceConstantsContainer.class))
        .andReturn(Collections.emptySet());

    expect(typeElement.getAnnotationsByType(PropertySourceConstants.class))
        .andReturn(new PropertySourceConstants[] {propertySourceConstants});

    expect(propertySourceConstants.resourceName())
        .andReturn("my-properties-file.properties")
        .times(2);
    expect(propertySourceConstants.className()).andReturn("MyTestClass").times(2);

    Properties properties = new Properties();
    properties.setProperty("test", "hello");
    properties.setProperty("test.me", "hello.world");

    expect(propertiesProvider.loadProperties("my-properties-file.properties", processingEnv))
        .andReturn(properties);

    expect(processingEnv.getMessager()).andReturn(messager);
    messager.printMessage(Kind.OTHER, "Generated MyTestClass for my-properties-file.properties");

    classWriter.writeClass(
        "com.github.kklisura", "MyTestClass", set("test.me", "test"), processingEnv);

    replayAll();

    assertEquals(false, processor.process(annotationsSet(), roundEnv));

    verifyAll();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testProcessPropertySourceConstantsAnnotationWithException() throws IOException {
    setupProcessingEnvironment();
    setupRoundEnvironment();

    setupPackageElement("com.github.kklisura");

    expect(roundEnv.getElementsAnnotatedWith(PropertySourceConstants.class))
        .andReturn(elementSet());

    expect(typeElement.getAnnotationsByType(PropertySourceConstants.class))
        .andReturn(new PropertySourceConstants[] {propertySourceConstants});

    expect(propertySourceConstants.resourceName())
        .andReturn("my-properties-file.properties")
        .times(2);

    expect(propertiesProvider.loadProperties("my-properties-file.properties", processingEnv))
        .andThrow(new IOException("exception message"));

    expect(processingEnv.getMessager()).andReturn(messager);
    messager.printMessage(Kind.ERROR, "exception message");

    replayAll();

    try {
      assertEquals(false, processor.process(annotationsSet(), roundEnv));
      fail();
    } catch (RuntimeException e) {
      assertTrue(e.getCause() instanceof IOException);
    }

    verifyAll();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testProcessPropertySourceConstantsContainerAnnotation() throws IOException {
    setupProcessingEnvironment();
    setupRoundEnvironment();

    setupPackageElement("com.github.kklisura");

    expect(roundEnv.getElementsAnnotatedWith(PropertySourceConstants.class))
        .andReturn(Collections.emptySet());

    expect(roundEnv.getElementsAnnotatedWith(PropertySourceConstantsContainer.class))
        .andReturn(elementSet());

    expect(typeElement.getAnnotationsByType(PropertySourceConstantsContainer.class))
        .andReturn(new PropertySourceConstantsContainer[] {propertySourceConstantsContainer});

    expect(propertySourceConstantsContainer.value())
        .andReturn(new PropertySourceConstants[] {propertySourceConstants});

    expect(propertySourceConstants.resourceName())
        .andReturn("my-properties-file.properties")
        .times(2);
    expect(propertySourceConstants.className()).andReturn("MyTestClass").times(2);

    Properties properties = new Properties();
    properties.setProperty("test", "hello");
    properties.setProperty("test.me", "hello.world");

    expect(propertiesProvider.loadProperties("my-properties-file.properties", processingEnv))
        .andReturn(properties);

    expect(processingEnv.getMessager()).andReturn(messager);
    messager.printMessage(Kind.OTHER, "Generated MyTestClass for my-properties-file.properties");

    classWriter.writeClass(
        "com.github.kklisura", "MyTestClass", set("test.me", "test"), processingEnv);

    replayAll();

    assertEquals(false, processor.process(annotationsSet(), roundEnv));

    verifyAll();
  }

  public void setupProcessingEnvironment() {
    expect(processingEnv.getMessager()).andReturn(messager);
    messager.printMessage(Kind.NOTE, "PropertySourceConstants annotation processor");
  }

  public void setupRoundEnvironment() {
    expect(roundEnv.processingOver()).andReturn(false);
  }

  public void setupPackageElement(String packageName) {
    expect(typeElement.getEnclosingElement()).andReturn(packageElement);

    expect(packageElement.getQualifiedName())
        .andReturn(
            new Name() {
              @Override
              public String toString() {
                return packageName;
              }

              @Override
              public boolean contentEquals(CharSequence cs) {
                return false;
              }

              @Override
              public int length() {
                return 0;
              }

              @Override
              public char charAt(int index) {
                return 0;
              }

              @Override
              public CharSequence subSequence(int start, int end) {
                return null;
              }
            });
  }

  public Set<? extends TypeElement> annotationsSet() {
    return Collections.singleton(typeElement);
  }

  public Set elementSet() {
    return Collections.singleton(typeElement);
  }

  private static Set<String> set(String... items) {
    return new LinkedHashSet<>(Arrays.asList(items));
  }
}
