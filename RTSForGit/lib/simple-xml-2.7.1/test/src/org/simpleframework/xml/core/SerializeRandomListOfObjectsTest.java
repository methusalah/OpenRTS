package org.simpleframework.xml.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.simpleframework.xml.Default;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.core.Persister;

public class SerializeRandomListOfObjectsTest extends TestCase {

   @Default
   public static class MethodInvocation {
      
      private List<Object> arguments;
      private Class[] types;
      private String name;
      
      public MethodInvocation() {
         super();
      }
      
      public MethodInvocation(String name, Class[] types, List<Object> arguments) {
         this.name = name;
         this.types = types;
         this.arguments = arguments;
      }
   }
   
   @Default
   private static class ListArgument {
      @ElementList
      private List list;
      
      public ListArgument() {
         super();
      }
      
      public ListArgument(List list) {
         this.list = list;
      }
   }
   
   public static interface SomeServiceInterface {
      void doSomething(List<String> values);
   }
   
   public void testSerializeRandomList() throws Exception {
      List list = new ArrayList();
      list.add("x");
      list.add(10);
      list.add(new Date());
      ListArgument argument = new ListArgument(list);
      Persister persister = new Persister();
      persister.write(argument, System.out);
      
   }
}
