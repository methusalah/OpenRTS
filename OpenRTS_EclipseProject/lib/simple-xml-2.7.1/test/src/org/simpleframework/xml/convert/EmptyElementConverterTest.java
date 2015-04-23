package org.simpleframework.xml.convert;

import junit.framework.TestCase;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

public class EmptyElementConverterTest extends TestCase {

   private static final String SOURCE =
   "<root>\r\n"+
   "   <text></text>\r\n"+
   "</root>\r\n";
         
   @Root
   private static class RootExample {
      
      @Element
      @Convert(TextConverter.class)
      private String text;
   }
   
   private static class TextConverter implements Converter {

      public Object read(InputNode node) throws Exception {
         String value = node.getValue();
         
         if(value == null) {
            return "";
         }
         return value;
      }

      public void write(OutputNode node, Object value) throws Exception {
         node.setValue(String.valueOf(value));
      }
   }    
   
   public void testConverter() throws Exception {
      Strategy strategy = new AnnotationStrategy();
      Persister persister = new Persister(strategy);
      RootExample example = persister.read(RootExample.class, SOURCE);
      
      assertEquals(example.text, "");
   }
}
