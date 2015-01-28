package net.jueb.util4j;

public class Test2 {

	public static void main(String[] args) {
		HotSwapMannager h=new HotSwapMannager();
		HotSwap l=h.getHotSwap(1);
		l.show();
	}
}
