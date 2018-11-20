package net.jueb.util4j.collection.bitPathTree;

/**
 * 分段掩码
 * 分解层数越少(掩码越长),内存占用越低,速度越快
 * @author juebanlin
 */
public enum BitMaskEnum{
	MASK_1(0x1),
	MASK_11(0x3),
	MASK_1111(0xF),
	MASK_1111_1111(0xFF),
	;
	private final int value;
	private BitMaskEnum(int value) {
		this.value=value;
	}
	public int getValue() {
		return value;
	}
}