package net.jueb.util4j.cache.tree.bitTree.impl;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.jueb.util4j.cache.tree.bitTree.BitIntTreeMap;
import net.jueb.util4j.cache.tree.bitTree.BitMaskEnum;

/**
 * 优化节点非必要属性的内存占用
 * 分解层数越小,内存占用越低,速度越快
 * beta for NodeMap5
 * @author juebanlin
 */
public class BITreeMap<K,V> implements BitIntTreeMap<K,V>{
	
	private static final int BIT_NUMS=32;//总bit位数量
	private final MapConfig config;
	private final LayOutNode<K,V> root;
	private final int[] posCache;
	private int size;
	private Node<K,V> firstAdd;//最先加入的节点
	private Node<K,V> lastAdd;//最后一次加入的节点
	
	public BITreeMap() {
		this(BitMaskEnum.MASK_1111_1111);
	}
	
	public BITreeMap(BitMaskEnum mask) {
		int tmp=mask.getValue();
		int num=0;
		while(tmp!=0)
		{
			tmp=tmp>>>1;
			num++;
		}
		int maskLen=num;
		int nodeSize=2<<maskLen-1;//层节点数量
		int layout=BIT_NUMS/maskLen;//分解层级
		posCache=new int[layout];//层级数组
		for(int i=0;i<layout;i++)
		{
			posCache[i]=maskLen*i;
		}
		config=new MapConfig(mask.getValue(), maskLen, layout, nodeSize);
		root=new LayOutNode<K,V>();
	}

	class MapConfig{
		/**
		 * 截取掩码
		 */
		private final int mask;
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

	interface Node<K,V> extends Entry<K,V>{
		
		public Node<K,V>[] getSub();
		
		public void setSub(Node<K,V> sub[]);
		
		public int getNodeSize();
		
		public void setKey(K key);
		
		public void setPre(Node<K,V> node);
		public void setNext(Node<K,V> node);
		/**
		 * 前一个
		 * @return
		 */
		public Node<K,V> getPre();
		/**
		 * 后一个
		 * @return
		 */
		public Node<K,V> getNext();
	}
	
	abstract class AbstractNode<K1,V1> implements Node<K1,V1>
	{
        public boolean equals(Object o) {
        	 if (!(o instanceof Map.Entry))
                 return false;
             Map.Entry<?,?> e = (Map.Entry<?,?>)o;
             return (getKey()==null ? e.getKey()==null : getKey().equals(e.getKey())) &&
                (getValue()==null ? e.getValue()==null : getValue().equals(e.getValue()));
        }

        public int hashCode() {
            int keyHash = (getKey()==null ? 0 : getKey().hashCode());
            int valueHash = (getValue()==null ? 0 : getValue().hashCode());
            return keyHash ^ valueHash;
        }

        public String toString() {
            return getKey() + "=" + getValue();
        }

		@Override
		public int getNodeSize() {
			return config.getNodeSize();
		}
		
		@Override
		public Node<K1,V1>[] getSub() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void setSub(Node<K1,V1>[] sub) {
			throw new UnsupportedOperationException();
		}
		@Override
		public void setKey(K1 key) {
			throw new UnsupportedOperationException();
		}
		@Override
		public K1 getKey() {
			throw new UnsupportedOperationException();
		}

		@Override
		public V1 getValue() {
			throw new UnsupportedOperationException();
		}

		@Override
		public V1 setValue(V1 value) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void setNext(Node<K1, V1> node) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void setPre(Node<K1, V1> node) {
			throw new UnsupportedOperationException();
		}
		@Override
		public Node<K1, V1> getNext() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public Node<K1, V1> getPre() {
			throw new UnsupportedOperationException();
		}
	}
	
	/**
	 * 层节点
	 * @author juebanlin
	 * @param <T>
	 */
	class LayOutNode<K1,V1> extends AbstractNode<K1,V1>{
		
		@SuppressWarnings("unchecked")
		private Node<K1,V1>[] sub=new Node[getNodeSize()];
		
		@Override
		public Node<K1,V1>[] getSub() {
			return sub;
		}

		@Override
		public void setSub(Node<K1,V1>[] sub) {
			this.sub=sub;
		}
	}

	/**
	 * 可存储数据的节点
	 * @author juebanlin
	 * @param <T>
	 */
	class DataNode<K1,V1> extends AbstractNode<K1,V1>{
		private K1 key;
		private V1 value;
		//用于迭代使用
		private Node<K1,V1> pre;
		private Node<K1,V1> next;
		@Override
		public void setKey(K1 key) {
			this.key=key;
		}
		@Override
		public K1 getKey() {
			return key;
		}
		@Override
		public V1 getValue() {
			return value;
		}
		
		@Override
		public V1 setValue(V1 value) {
			V1 old=this.value;
			this.value=value;
			return old;
		}
		public Node<K1, V1> getPre() {
			return pre;
		}
		public void setPre(Node<K1, V1> pre) {
			this.pre = pre;
		}
		public Node<K1, V1> getNext() {
			return next;
		}
		public void setNext(Node<K1, V1> next) {
			this.next = next;
		}
	}

	/**
	 * 抵达节点
	 * @param number
	 * @param layout
	 * @param currentNode
	 * @return
	 */
	public Node<K,V> arraivedNode(int number,int layout,Node<K,V> currentNode,boolean create)
	{
		if(layout<0)
		{//超出范围
			return null;
		}
		if(layout==0)
		{
			return currentNode;
		}
		layout--;
		int p=getMaskValue(number,layout);
		Node<K,V>[] sub=currentNode.getSub();
		Node<K,V> node=sub[p];
		if(node==null)
		{
			if(!create)
			{//如果不是新建路径则返回null表示不可达
				return null;
			}
			if(layout==0)
			{//layout=0的node具有data属性
				node=new DataNode<K,V>();
			}else
			{
				node=new LayOutNode<K,V>();
			}
			sub[p]=node;
		}
		return arraivedNode(number, layout,node,create);
	}
	
	/**
	 * 取整数某二进制位的值
	 * @param number
	 * @param pos 0开始
	 * @return
	 */
	protected int getMaskValue(int number,int layout)
	{
		return (number & (config.mask<<posCache[layout]))>>>posCache[layout];
	}		

	protected final LayOutNode<K,V> getRootNode() {
		return root;
	}

	protected final int[] getPosCache() {
		return posCache;
	}

	protected final MapConfig getConfig()
	{
		return config;
	}

	@Override
	public Entry<K,V> write(int bitNumber,K key, V value) {
		Node<K,V> node=arraivedNode(bitNumber, config.layout, root, true);
		if(node.getPre()!=null)
		{//仅仅是覆盖值
			node.setKey(key);
			node.setValue(value);
			return node;
		}
		//新键值
		node.setKey(key);
		node.setValue(value);
		if(firstAdd==null)
		{
			firstAdd=node;
		}
		if(lastAdd!=null)
		{
			lastAdd.setNext(node);
			node.setPre(lastAdd);
		}
		lastAdd=node;
		size++;
		return node;
	}

	@Override
	public Entry<K,V> read(int bitNumber) {
		return arraivedNode(bitNumber, config.layout, root, false);
	}
	
	@SuppressWarnings("unchecked")
	public final void clear()
	{
		getRootNode().setSub(new Node[getConfig().getNodeSize()]);
		firstAdd=null;
		lastAdd=null;
		size=0;
	}
	
	public final int size()
	{
		return size;
	}
	
	@Override
	public void forEach(BiConsumer<K,V> consumer) {
		Node<K,V> node=firstAdd;
		for(;node!=null;)
		{
			consumer.accept(node.getKey(),node.getValue());
			node=node.getNext();
		}
	}
	
	@Override
	public void forEach(Consumer<Entry<K,V>> consumer) {
		Node<K,V> node=firstAdd;
		for(;node!=null;)
		{
			consumer.accept(node);
			node=node.getNext();
		}
	}
	
	public static void main(String[] args) {
		BitIntTreeMap<Integer,String> b=new BITreeMap<>();
		for(int i=0;i<10;i++)
		{
			b.write(i,i,"i="+i);
		}
		b.forEach((k,v)->{
			System.out.println(k+":"+v);
		});
	}
}
