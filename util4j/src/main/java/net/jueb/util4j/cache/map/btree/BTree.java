package net.jueb.util4j.cache.map.btree;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 优化节点非必要属性的内存占用
 * beta for NodeMap5
 * @author juebanlin
 */
public class BTree<V> implements BitTree<V>{
	
	private static final int BIT_NUMS=32;//总bit位数量
	private final MapConfig config;
	private final LayOutNode<V> node;
	private final int[] posCache;
	
	public BTree() {
		this(MaskEnum.MASK_1111);
	}
	
	public BTree(MaskEnum mask) {
		int tmp=mask.getValue();
		int num=0;
		while(tmp!=0)
		{
			tmp=tmp>>>1;
			num++;
		}
		int maskLen=num;
		int nodeSize=2<<maskLen-1;
		int layout=BIT_NUMS/maskLen;
		posCache=new int[layout];
		for(int i=0;i<layout;i++)
		{
			posCache[i]=maskLen*i;
		}
		config=new MapConfig(mask.getValue(), maskLen, layout, nodeSize);
		node=new LayOutNode<V>();
		System.out.println(config);
	}

	/**
	 * 分段掩码
	 * @author juebanlin
	 */
	public static enum MaskEnum{
		MASK_11(0x3),
		MASK_1111(0xF),
		MASK_1111_1111(0xFF),
		;
		private final int value;
		private MaskEnum(int value) {
			this.value=value;
		}
		public int getValue() {
			return value;
		}
	}

	class MapConfig{
		/**
		 * 截取掩码
		 */
		private final int mask;//CONFIG this
		/**
		 * 掩码占位数
		 */
		private final int maskLen;
		/**
		 * 分层(多少个掩码组合)
		 */
		private final int layout;
		/**
		 * 占位掩码的二进制容量
		 */
		private final int nodeSize;
		
		public MapConfig(int mask, int maskLen, int layout, int nodeSize) {
			super();
			this.mask = mask;
			this.maskLen = maskLen;
			this.layout = layout;
			this.nodeSize = nodeSize;
		}
	
		public int getMask() {
			return mask;
		}
	
		public int getMaskLen() {
			return maskLen;
		}
	
		public int getLayout() {
			return layout;
		}
	
		public int getNodeSize() {
			return nodeSize;
		}
	
		@Override
		public String toString() {
			return "MapConfig [bitNum=" + maskLen + ", layout=" + layout + ", nodeSize=" + nodeSize + "]";
		}
	}

	interface Node<V>{
		V getData();
		
		void setData(V data);
		
		public Node<V>[] getSub();
		
		public void setSub(Node<V> sub[]);
		
		public int getNodeSize();
		
		public V _getByNumber(int number,int layout);
		
		public V _setByNumber(int number,int layout,V value);
	}
	
	abstract class AbstractNode<T extends V> implements Node<T>
	{
		
		@Override
		public T _getByNumber(int number,int layout)
		{
			if(layout<0)
			{//超出范围
				return null;
			}
			if(layout==0)
			{
				return getData();
			}
			Node<T>[] sub=getSub();
			if(sub==null)
			{
				return null;
			}
			layout--;
			int p=getMaskValue(number,layout);
			Node<T> node=sub[p];
			if(node==null)
			{
				return null;
			}
			return node._getByNumber(number, layout);
		}
		
		@Override
		public T _setByNumber(int number,int layout,T value)
		{
			if(layout<0)
			{//超出范围
				return null;
			}
			if(layout==0)
			{
				T old=getData();
				setData(value);
				return old;
			}
			layout--;
			int p=getMaskValue(number,layout);
			Node<T>[] sub=getSub();
			Node<T> node=sub[p];
			if(node==null)
			{
				if(layout==0)
				{//layout=0的node具有data属性
					node=new DataNode<T>();
				}else
				{
					node=new LayOutNode<T>();
				}
				sub[p]=node;
			}
			return node._setByNumber(number, layout,value);
		}

		@Override
		public int getNodeSize() {
			return config.getNodeSize();
		}
		
		@Override
		public T getData() {
			return null;
		}

		@Override
		public void setData(T data) {
		}
		
		@Override
		public Node<T>[] getSub() {
			return null;
		}
		@Override
		public void setSub(Node<T>[] sub) {
			
		}
	}
	
	/**
	 * 层节点
	 * @author juebanlin
	 * @param <T>
	 */
	class LayOutNode<T extends V> extends AbstractNode<T>{
		
		@SuppressWarnings("unchecked")
		private Node<T>[] sub=new Node[getNodeSize()];
		
		@Override
		public Node<T>[] getSub() {
			return sub;
		}

		@Override
		public void setSub(Node<T>[] sub) {
			this.sub=sub;
		}
	}

	/**
	 * 可存储数据的节点
	 * @author juebanlin
	 * @param <T>
	 */
	class DataNode<T extends V> extends AbstractNode<T>{
		private T data;
		@Override
		public T getData() {
			return data;
		}
		@Override
		public void setData(T data) {
			this.data=data;
		}
	}

	/**
	 * 取整数某二进制位的值
	 * @param number
	 * @param pos 0开始
	 * @return
	 */
	protected int getMaskValue(int number,int layout)
	{
//		int pos=maskLen*layout;
//		int pos=posCache[layout];
//		int mask=config.mask;
		return (number & (config.mask<<posCache[layout]))>>>posCache[layout];
	}		

	protected V getByNumber(int number)
	{
		return node._getByNumber(number,config.layout);
	}

	protected V setByNumber(int number,V value)
	{
		return node._setByNumber(number,config.layout,value);
	}

	protected final LayOutNode<V> getNode() {
		return node;
	}

	protected final int[] getPosCache() {
		return posCache;
	}

	protected final MapConfig getConfig()
	{
		return config;
	}

	@Override
	public V write(int key, V value) {
		return setByNumber(key, value);
	}

	@Override
	public V read(int key) {
		return getByNumber(key);
	}
	
	@Override
	public void forEach(BitConsumer<V> consumer) {
		forEach(node, config.layout,0,consumer);
	}
	
	/**
	 * 循环搜索路径上存储的k-v
	 * @param currentNode
	 * @param layout
	 * @param number
	 */
	protected void forEach(Node<V> currentNode,int layout,int number,BitConsumer<V> consumer)
	{
		if(layout==0)
		{
			consumer.accept(number, currentNode.getData());
			return ;
		}
		Node<V>[] sub=currentNode.getSub();
		for(int i=0;i<sub.length;i++)
		{
			Node<V> node=sub[i];
			if(node!=null)
			{
				layout--;
				int num=number+i&getConfig().mask<<layout;
				forEach(node, layout, num,consumer);
				layout++;
			}
		}
	}
	
	public static void main(String[] args) {
		BTree<Byte> mtree=new BTree<>();
		long t=System.currentTimeMillis();
		for(int i=0;i<5000000;i++)
		{
			mtree.write(i,(byte) (i+100));
		}
		long t1=System.currentTimeMillis()-t;
		final AtomicInteger i=new AtomicInteger(0);
		t=System.currentTimeMillis();
		mtree.forEach((k,v)->{
			i.incrementAndGet();
//			System.out.println(k+":"+v);
		});
		long t2=System.currentTimeMillis()-t;
		System.out.println(t1+","+t2+","+i.get());
	}
}
