package net.jueb.util4j.beta.tools.avlMap2;

import org.junit.Test;

public class RecAvlMapTest {



	@Test
	public void testinsert() {
	   
	   RecAvlMap<Integer, String> um = new RecAvlMap<Integer, String>();
        
        
/*
        um.insert(1,"a");
        um.insert(2,"b");
        um.insert(6,"c");
        um.insert(4,"d");
        um.insert(3,"e");

*/

        um.insert(1,"a");
        um.insert(2,"b");
        um.insert(6,"c");
        um.insert(4,"d");
        um.insert(3,"e");



  /*
        assertTrue(um.hasValue("a"));
        assertTrue(um.hasValue("b"));
        assertTrue(um.hasValue("c"));
        assertTrue(um.hasValue("d"));
        assertFalse(um.hasValue("WRONG"));
*/
        System.out.println(um.preOrder());

        System.out.println(um.inOrder());

        System.out.println(um.postOrder());
    }


/*
	@Test
	public void testinsert() {
	   
	   ReclAvlMap<Integer, String> um = new RecAvlMap<Integer, String>();
        
        um.recInsert(1,"a");
        um.recInsert(2,"b");
        um.recInsert(6,"c");
        um.recInsert(4,"d");
        um.recInsert(3,"e");

  
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
