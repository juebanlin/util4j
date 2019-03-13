package net.jueb.util4j.beta.delyEvent;

@FunctionalInterface
public interface DelyEventHandler<T extends IDelyEvent>{

	public void handle(T event);
}