package net.jueb.util4j.beta.tools.avlMap2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;
import java.util.Map;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.HashSet;
import java.util.ArrayList;

public class BSTMapTest {



    @Test
        public void testRemove() {
            BSTMap<Integer, String> um = new BSTMap<Integer, String>();
            
            System.out.println("Kiiki");
            
            um.put(1,"a");
            um.put(2,"b");
            um.put(3,"c");
            um.put(4,"d");
            um.put(5,"e");

            //um.remove(3);
            //System.out.println("size is:!!! " + um.size());
            //assertFalse(um.hasKey(3));
            //System.out.println(um.inOrder());
            //System.out.println(um.entries());
            System.out.println(um.keys());
            System.out.println(um.values());
            System.out.println("chang " + um.remove(1));
            um.remove(5);
            System.out.println("1  and 5 are removed");
            System.out.println(um.keys());
            System.out.println(um.values());
            System.out.println(um.entries());
            System.out.println("size should be 3" + um.size());
            //um.remove(10);
            //System.out.println("nothing should be changed");
            //System.out.println(um.keys());
            //System.out.println(um.values());
        }



    @Test
        public void testIterator() {
            BSTMap<Integer, String> um = new BSTMap<Integer, String>();            
            um.put(1,"a");
            um.put(2,"b");
            um.put(3,"c");
            um.put(4,"d");
            um.put(5,"e");
            System.out.println("um map size is: " + um.size());
            Iterator it = um.iterator();
            String str = "[";
            while (it.hasNext()) {
                String s = it.next().toString();
                //System.out.print(s + " ");
                str += s;
              //  str += "]";
                if (it.hasNext()) {
                    str += ", ";
                } 
        }
         str += "]";
            String inOrder = um.inOrder().toString();
            System.out.println("String inOrder " + inOrder);
            assertEquals(inOrder, str);
            
        }






    @Test
    public void testSubMap() {
        BSTMap<Integer, String> um = new BSTMap<Integer, String>();
        
        System.out.println("hello");
        
        um.put(1,"a");
        um.put(2,"b");
        um.put(3,"c");
        um.put(4,"d");
        um.put(5,"e");
        BSTMap<Integer, String> temp = new BSTMap<Integer, String>();
        temp = um.subMap(2,4);
        System.out.println(temp.keys());
        System.out.println(temp.values());
        System.out.println(temp.size());
        System.out.println("um size is:" + um.size());
    }

    @Test
    public void testhasValue() {
        BSTMap<Integer, String> um = new BSTMap<Integer, String>();
        //System.out.println("hello");
        //BNode<Integer, String> temp = new BNode<Integer, String>(10, "f");
        um.put(1,"a");
        um.put(2,"b");
        um.put(3,"c");
        um.put(4,"d");
        um.put(5,"e");
        System.out.println(um.hasValue("c"));
        //System.out.println(um.hasKey(1));
        //assertFalse(um.hasKey(6));
        //System.out.println(um.firstKey(temp));
        //System.out.println(um.lastKey());
    }

    @Test
    public void testPutandHasKey() {
        BSTMap<Integer, String> um = new BSTMap<Integer, String>();
        //assertFalse(um.getRoot() == null);
        System.out.println("hello");
        
        um.put(1,"a");
        um.put(2,"b");
        um.put(3,"c");
        um.put(4,"d");
        System.out.println(um.size());
        //assertTrue(um.hasKey(2));
        //um.firstKey();
        //assertTrue(um.hasKey(3));
        System.out.println(um.inOrder());
        System.out.println(um.entries());
        System.out.println(um.keys());
        System.out.println(um.values());

        String result = um.get(4);
        assertTrue(um.hasKey(4));
        assertEquals("d", result);
        System.out.println("world");

    }
     
     @Test
        public void testPut() {
            //Arrange
            BSTMap<String, String> um = new BSTMap<String, String>();
            System.out.println("before");
            um.put("Gracias", "Dios Basado");
            
            //Act
            String result = um.get("Gracias");

            assertTrue(um.hasKey("Gracias"));
            //assertFalse(um.hasValue("Dios Basado"));
            System.out.println("after");
            //Assert
            //assertEquals("Dios Basado", result);
        }

    @Test
    public void testHasValue() {
        BSTMap<Integer, String> um = new BSTMap<Integer, String>();
        
        um.put(1,"a");
        um.put(2,"b");
        um.put(3,"c");
        um.put(4,"d");
        
        assertTrue(um.hasValue("a"));
        assertTrue(um.hasValue("b"));
        assertTrue(um.hasValue("c"));
        assertTrue(um.hasValue("d"));
        assertFalse(um.hasValue("WRONG"));
    }
    
    @Test
    public void testClearAndisEmpty() {
        BSTMap<Integer, String> um = new BSTMap<Integer, String>();
        um.put(1,"a");
        um.put(2,"b");
        um.put(3,"c");
        um.put(4,"d");
        assertFalse(um.isEmpty());
        um.clear();
        assertTrue(um.isEmpty());
    }

    @Test
    public void testGet() {
        BSTMap<Integer, String> um = new BSTMap<Integer, String>();
        
        um.put(1,"a");
        um.put(2,"b");
        um.put(3,"c");
        um.put(4,"d");
        
        assertEquals("a", um.get(1));
        assertEquals("b", um.get(2));
        assertEquals("c", um.get(3));
        assertEquals("d", um.get(4));
        assertNull(um.get(3145));        
    }

    @Test
    public void testRemove2() {
        BSTMap<Integer, String> um = new BSTMap<Integer, String>();
        
        System.out.println("hello");
        
        um.put(1,"a");
        um.put(2,"b");
        um.put(3,"c");
        um.put(4,"d");
        um.put(5,"e");
        um.remove(3);
        System.out.println("size is:!!! " + um.size());
        assertFalse(um.hasKey(3));
        System.out.println(um.inOrder());
        System.out.println(um.entries());
        System.out.println(um.keys());
        System.out.println(um.values());
        um.remove(1);
        assertFalse(um.hasKey(1));
        System.out.println("1 is removed");
        System.out.println(um.keys());
        System.out.println(um.values());
        um.remove(10);
        assertEquals(3, um.size());
        System.out.println("nothing should be changed");
        System.out.println(um.keys());
        System.out.println(um.values());
    }
    @Test
    public void testPut2() {
        //Arrange
        BSTMap<String, String> um = new BSTMap<String, String>();
        System.out.println("before");
        um.put("Gracias", "Dios Basado");
        //Act
        String result = um.get("Gracias");
        assertTrue(um.hasKey("Gracias"));
        //assertFalse(um.hasValue("Dios Basado"));
        System.out.println("after");
        //Assert
        //assertEquals("Dios Basado", result);
    }

}
