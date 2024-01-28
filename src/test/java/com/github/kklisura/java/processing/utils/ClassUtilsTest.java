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

import static com.github.kklisura.java.processing.utils.TestUtils.getFixture;
import static org.junit.Assert.assertEquals;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Test;

/**
 * Class utils test
 *
 * @author Kenan Klisura
 */
public class ClassUtilsTest {
  @Test
  public void testBuildConstantsClass() {
    String classDeclaration =
        ClassUtils.buildConstantsClass("com.github.kklisura", "MyClass", set("hello"));
    assertEquals(getFixture("test-class-java"), classDeclaration);
  }

  @Test
  public void testBuildConstantsDeclarations() {
    assertEquals(constant("HELLO", "hello"), ClassUtils.buildConstantsDeclarations(set("hello")));
    assertEquals(
        constant("MESSAGE_TEST", "message.test"),
        ClassUtils.buildConstantsDeclarations(set("message.test")));
    assertEquals(
        constant("MESSAGE_TEST", "message-test"),
        ClassUtils.buildConstantsDeclarations(set("message-test")));

    assertEquals(
        constant("MESSAGE_1", "message.1") + constant("MESSAGE_2", "message.2", 1),
        ClassUtils.buildConstantsDeclarations(set("message.1", "message.2")));
  }

  @Test
  public void testBuildPackageDeclaration() {
    assertEquals("", ClassUtils.buildPackageDeclaration(""));
    assertEquals("", ClassUtils.buildPackageDeclaration(null));
    assertEquals(
        "package com.github.kklisura;\n",
        ClassUtils.buildPackageDeclaration("com.github.kklisura"));
  }

  @Test
  public void testPropertyNameToConstantName() {
    assertEquals("TEST", ClassUtils.propertyNameToConstantName("tEsT"));
    assertEquals("TEST_TEST", ClassUtils.propertyNameToConstantName("test.test"));
    assertEquals("TEST_TEST", ClassUtils.propertyNameToConstantName("test_test"));
    assertEquals("TEST_TEST", ClassUtils.propertyNameToConstantName("test-test"));
  }

  private static String constant(String propertyName, String value) {
    return String.format(
        "\t/** Value 1 */\n\tpublic static final String %s = \"%s\";\n", propertyName, value);
  }

  private static String constant(String propertyName, String value, int index) {
    return String.format(
        "\t/** Value %d */\n\tpublic static final String %s = \"%s\";\n",
        index + 1, propertyName, value);
  }

  private static Map<String, String> set(String... items) {
    final Map<String, String> result = new LinkedHashMap<>();
    for (int i = 0; i < items.length; i++) {
      result.put(items[i], "Value " + (i + 1));
    }
    return result;
  }
}
