package net.jueb.util4j.tools.math;

public enum FruitType {

	/**
	 * 苹果
	 */
	Apple(1),
	/**
	 * 橘子
	 */
	Orange(2),
	/**
	 * 西瓜
	 */
	Watermelon(3),
	/**
	 * 菠萝
	 */
	Pineapple(4),
	/**
	 * 火龙果
	 */
	Pitaya(5);
	
	private int value;
	private FruitType(int value) {
		this.value=value;
	}
	
	public int getValue()
	{
		return value;
	}
	
	public static FruitType valueOf(int value)
	{
		for(FruitType type:values())
		{
			if(type.getValue()==value)
			{
				return type;
			}
		}
		return null;
	}
	
	/**
	 * 赔率
	 * @return
	 */
	public int odds()
	{
		int odds=1;
		switch (this) {
		case Apple:
			odds=3;
			break;
		case Orange:
			odds=3;
			break;
		case Pineapple:
			odds=6;
			break;
		case Watermelon:
			odds=6;
			break;
		case Pitaya:
			odds=8;
			break;
		default:
			break;
		}
		return odds;
	}
}
