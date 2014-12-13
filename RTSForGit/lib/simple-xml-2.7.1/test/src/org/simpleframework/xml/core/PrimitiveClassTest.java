package org.simpleframework.xml.core;

import java.io.StringWriter;

import org.simpleframework.xml.ValidationTestCase;

public class PrimitiveClassTest extends ValidationTestCase {

   private static class Example {
      private Class[] classes;
      public Example() {
         this.classes = new Class[]{
               int.class,
               double.class,
               float.class,
               long.class,
               void.class,
               byte.class,
               short.class,
               boolean.class,
               char.class
         };
      }
   }
   
   
   public void testPrimitiveClass() throws Exception {
      Example example = new Example();
      Persister persister = new Persister();
      StringWriter writer = new StringWriter();
      
      persister.write(example, writer);
      
      String text = writer.toString();
      System.err.println(text);
      
      assertElementExists(text, "/example");
      assertElementExists(text, "/example/classes");
      assertElementHasAttribute(text, "/example/classes", "length", "9");
      assertElementHasValue(text, "/example/classes/class[1]", "int");
      assertElementHasValue(text, "/example/classes/class[2]", "double");
      assertElementHasValue(text, "/example/classes/class[3]", "float");
      assertElementHasValue(text, "/example/classes/class[4]", "long");
      assertElementHasValue(text, "/example/classes/class[5]", "void");
      assertElementHasValue(text, "/example/classes/class[6]", "byte");
      assertElementHasValue(text, "/example/classes/class[7]", "short");
      assertElementHasValue(text, "/example/classes/class[8]", "boolean");
      assertElementHasValue(text, "/example/classes/class[9]", "char");
   }
}
