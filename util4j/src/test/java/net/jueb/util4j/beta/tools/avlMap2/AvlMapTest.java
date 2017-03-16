package net.jueb.util4j.beta.tools.avlMap2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Test;



public class AvlMapTest {


    @Test
    public void testPut() {
               AvlMap<Integer, String> um = new AvlMap<Integer, String>();
          
          System.out.println("heeeee");
        um.put(1,"a");
        um.put(2,"b");
        um.put(6,"c");
        um.put(4,"d");
        um.put(3,"e");

        System.out.println(um.preOrder());

        System.out.println(um.inOrder());

        System.out.println(um.postOrder());

        System.out.println("now change 4's value");

        um.put(4, "f");

        System.out.println(um.preOrder());

        System.out.println(um.inOrder());

        System.out.println(um.postOrder());

        System.out.println("4's new value should be f " + um.get(4));

        System.out.println("size should be 5 " + um.size());


        um.remove(2);
        um.remove(1);
 
        System.out.println(um.preOrder());
        System.out.println(um.inOrder());
        System.out.println(um.postOrder());
        System.out.println("size should be 3 " + um.size());


        System.out.println("hesdfadrg");
    }

/*

    @Test
    public void testItr() {
        AvlMap<Integer, String> um = new AvlMap<Integer, String>();
          
          System.out.println("heeeee");
        um.put(1,"a");
        um.put(2,"b");
        um.put(6,"c");
        um.put(4,"d");
        um.put(3,"e");

        Iterator<Map.Entry<K, V>> itr = um.iterator();
        while (itr.hasNext()) {
            System.out.println(itr.next());
        }
    }
*/


    @Test
        public void testIterator() {
            AvlMap<Integer, String> um = new AvlMap<Integer, String>();            
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
        public void testRemove() {
            AvlMap<Integer, String> um = new AvlMap<Integer, String>();
            
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
    public void testSubMap() {
        AvlMap<Integer, String> um = new AvlMap<Integer, String>();
        
        System.out.println("hello");
        
        um.put(1,"a");
        um.put(2,"b");
        um.put(3,"c");
        um.put(4,"d");
        um.put(5,"e");
        BSTMap<Integer, String> temp = new AvlMap<Integer, String>();
        temp =  um.subMap(2,4);
        System.out.println(temp.keys());
        System.out.println(temp.values());
        System.out.println(temp.size());
        System.out.println("um size is:" + um.size());
    }

    @Test
    public void testhasValue() {
        AvlMap<Integer, String> um = new AvlMap<Integer, String>();
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
        AvlMap<Integer, String> um = new AvlMap<Integer, String>();
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
        public void testPut2() {
            //Arrange
            AvlMap<String, String> um = new AvlMap<String, String>();
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
        AvlMap<Integer, String> um = new AvlMap<Integer, String>();
        
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
        AvlMap<Integer, String> um = new AvlMap<Integer, String>();
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
        AvlMap<Integer, String> um = new AvlMap<Integer, String>();
        
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
        AvlMap<Integer, String> um = new AvlMap<Integer, String>();
        
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
    public void testPut3() {
        //Arrange
        AvlMap<String, String> um = new AvlMap<String, String>();
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





/*

	@Test
	public void testadd() {
	   
	   AvlMap<Integer, String> um = new AvlMap<Integer, String>();
          
        um.add(1,"a");
        um.add(2,"b");
        um.add(6,"c");
        um.add(4,"d");
        um.add(3,"e");

        System.out.println(um.preOrder());

        System.out.println(um.inOrder());

        System.out.println(um.postOrder());

        System.out.println("now change 4's value");

        um.add(4, "f");

        System.out.println(um.preOrder());

        System.out.println(um.inOrder());

        System.out.println(um.postOrder());

        System.out.println("4's new value should be f " + um.get(4));

        System.out.println("size should be 5 " + um.size());


        um.delete(2);
        um.delete(4);
        um.delete(1);
 
        System.out.println(um.preOrder());
        System.out.println(um.inOrder());
        System.out.println(um.postOrder());
        System.out.println("size should be 2 " + um.size());


    }

*/
/*

    @Test
        public void testIterator() {
            AvlMap<Integer, String> um = new AvlMap<Integer, String>();            
            um.add(1,"a");
            um.add(2,"b");
            um.add(3,"c");
            um.add(4,"d");
            um.add(5,"e");
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

*/



/*
	@Test
	public void testadd() {
	   
	   ReclAvlMap<Integer, String> um = new RecAvlMap<Integer, String>();
        
        um.recadd(1,"a");
        um.recadd(2,"b");
        um.recadd(6,"c");
        um.recadd(4,"d");
        um.recadd(3,"e");

  
        assertTrue(um.hasValue("a"));
        assertTrue(um.hasValue("b"));
        assertTrue(um.hasValue("c"));
        assertTrue(um.hasValue("d"));
        assertFalse(um.hasValue("WRONG"));
        System.out.println(um.preOrder());

        System.out.println(um.inOrder());

        System.out.println(um.postOrder());
    }

*/


}