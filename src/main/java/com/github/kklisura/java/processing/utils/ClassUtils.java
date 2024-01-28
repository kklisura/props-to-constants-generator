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

import com.github.kklisura.java.processing.PropertySourceConstantsAnnotationProcessor;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;

/**
 * Class utils.
 *
 * @author Kenan Klisura
 */
public final class ClassUtils {
  private static final String TAB = "\t";
  private static final String NEWLINE = "\n";

  private static final String CLASS =
      "${packageDeclaration}"
          + NEWLINE
          + "import javax.annotation.processing.Generated;"
          + NEWLINE
          + NEWLINE
          + "@Generated(value = \"${processorName}\")"
          + NEWLINE
          + "public final class ${className} {"
          + NEWLINE
          + "${constantsDeclarations}"
          + NEWLINE
          + TAB
          + "private ${className}() {}"
          + NEWLINE
          + "}"
          + NEWLINE;

  private static final String ENUM =
      "${packageDeclaration}"
          + NEWLINE
          + "import javax.annotation.processing.Generated;"
          + NEWLINE
          + NEWLINE
          + "@Generated(value = \"${processorName}\")"
          + NEWLINE
          + "public enum ${className} {"
          + NEWLINE
          + "${enumDeclarations}"
          + TAB
          + ";"
          + NEWLINE
          + NEWLINE
          + TAB
          + "private String key;"
          + NEWLINE
          + NEWLINE
          + TAB
          + "private ${className}(String key) { this.key = key; }"
          + NEWLINE
          + NEWLINE
          + TAB
          + "public String getKey() { return this.key; }"
          + NEWLINE
          + NEWLINE
          + TAB
          + "public String from(java.util.ResourceBundle resourceBundle) { return resourceBundle.getString(this.key); }"
          + NEWLINE
          + NEWLINE
          + TAB
          + "public String from(java.util.Properties properties) { return properties.getProperty(this.key); }"
          + NEWLINE
          + "}"
          + NEWLINE;

  private static final String CONSTANT_DEFINITION =
      TAB + "public static final String ${name} = \"${value}\";" + NEWLINE;

  private static final String ENUM_DEFINITION = TAB + "${name}(\"${value}\")," + NEWLINE;

  /** Private ctor. */
  private ClassUtils() {
    // Empty ctor.
  }

  /**
   * Builds a constants class.
   *
   * @param packageName Package name.
   * @param className Class name.
   * @param propertyNames Property names.
   * @return Constants class.
   */
  public static String buildConstantsClass(
      String packageName, String className, Set<String> propertyNames) {
    Map<String, String> params = new HashMap<>();

    params.put("className", className);
    params.put("processorName", PropertySourceConstantsAnnotationProcessor.class.getName());
    params.put("constantsDeclarations", buildConstantsDeclarations(propertyNames));
    params.put("packageDeclaration", buildPackageDeclaration(packageName));

    StringSubstitutor substitutor = new StringSubstitutor(params);
    return substitutor.replace(CLASS);
  }

  public static String buildEnumClass(
      String packageName, String className, Set<String> propertyNames) {
    Map<String, String> params = new HashMap<>();

    params.put("className", className);
    params.put("processorName", PropertySourceConstantsAnnotationProcessor.class.getName());
    params.put("enumDeclarations", buildEnumDeclarations(propertyNames));
    params.put("packageDeclaration", buildPackageDeclaration(packageName));

    StringSubstitutor substitutor = new StringSubstitutor(params);
    return substitutor.replace(ENUM);
  }

  /**
   * Build constants declarations string.
   *
   * @param propertyNames Property names.
   * @return Constants declaration.
   */
  public static String buildConstantsDeclarations(final Set<String> propertyNames) {
    StringBuilder stringBuilder = new StringBuilder();

    for (final String propertyName : propertyNames) {
      if (StringUtils.isNotEmpty(propertyName)) {
        Map<String, String> params = new HashMap<>();

        params.put("name", propertyNameToConstantName(propertyName));
        params.put("value", propertyName);

        StringSubstitutor substitutor = new StringSubstitutor(params);
        stringBuilder.append(substitutor.replace(CONSTANT_DEFINITION));
      }
    }

    return stringBuilder.toString();
  }

  public static String buildEnumDeclarations(final Set<String> propertyNames) {
    StringBuilder stringBuilder = new StringBuilder();

    for (final String propertyName : propertyNames) {
      if (StringUtils.isNotEmpty(propertyName)) {
        Map<String, String> params = new HashMap<>();

        params.put("name", propertyNameToConstantName(propertyName));
        params.put("value", propertyName);

        StringSubstitutor substitutor = new StringSubstitutor(params);
        stringBuilder.append(substitutor.replace(ENUM_DEFINITION));
      }
    }

    return stringBuilder.toString();
  }

  /**
   * Builds a package declaration given a package name.
   *
   * @param packageName Package name.
   * @return Package declaration.
   */
  public static String buildPackageDeclaration(String packageName) {
    if (StringUtils.isNotEmpty(packageName)) {
      return "package " + packageName + ";" + NEWLINE;
    }

    return "";
  }

  /**
   * Converts a property name to constant name.
   *
   * @param propertyName Property name.
   * @return Constant name.
   */
  public static String propertyNameToConstantName(String propertyName) {
    return propertyName.replaceAll("[-.]", "_").toUpperCase();
  }
}
