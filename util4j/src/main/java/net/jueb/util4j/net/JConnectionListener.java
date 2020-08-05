package net.jueb.util4j.net;

public interface JConnectionListener<M> {
	
    void messageArrived(JConnection conn,M msg);
    
	void connectionOpened(JConnection connection);

	void connectionClosed(JConnection connection);
}
