package net.jueb.util4j.test.jdk8;

public interface Person {
	
	default void say()
	{
		System.out.println("I��m a Person");
	}
    
    void doSomething();
}