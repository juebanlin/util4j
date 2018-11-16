package net.jueb.util4j.cache.tree.bitTree.impl;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import net.jueb.util4j.cache.tree.bitTree.BitIntTree;
import net.jueb.util4j.cache.tree.bitTree.BitMaskEnum;

/**
 * 优化节点非必要属性的内存占用
 * 
 * @author juebanlin
 */
public class BITree<V> implements BitIntTree<V>{
	
	private static final int BIT_NUMS=32;//总bit位数量
	private final MapConfig config;
	private final LayOutNode<Integer,V> root;
	private final int[] posCache;
	private int size;
	private Node<Integer,V> firstAdd;//最先加入的节点
	private Node<Integer,V> lastAdd;//最后一次加入的节点
	
	public BITree() {
		this(BitMaskEnum.MASK_1111_1111);
	}
	
	public BITree(BitMaskEnum mask) {
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
		root=new LayOutNode<Integer,V>();
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
	public Node<Integer,V> arraivedNode(int number,int layout,Node<Integer,V> currentNode,boolean create)
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
		Node<Integer,V>[] sub=currentNode.getSub();
		Node<Integer,V> node=sub[p];
		if(node==null)
		{
			if(!create)
			{//如果不是新建路径则返回null表示不可达
				return null;
			}
			if(layout==0)
			{//layout=0的node具有data属性
				node=new DataNode<Integer,V>();
			}else
			{
				node=new LayOutNode<Integer,V>();
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

	protected final LayOutNode<Integer,V> getRootNode() {
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
	public V write(int bitNumber,V value) {
		Node<Integer,V> node=arraivedNode(bitNumber, config.layout, root, true);
		V oldValue=node.getValue();
		if(node.getKey()!=null)
		{//仅仅是覆盖值
			node.setValue(value);
			return oldValue;
		}
		//新键值
		node.setKey(bitNumber);
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
		return oldValue;
	}

	@Override
	public V read(int bitNumber) {
		Node<Integer,V> node=arraivedNode(bitNumber, config.layout, root, false);
		if(node==null)
		{
			return null;
		}
		return node.getValue();
	}
	
	@SuppressWarnings("unchecked")
	public final void clear()
	{
		getRootNode().setSub(new Node[getConfig().getNodeSize()]);
		lastAdd=null;
		firstAdd=null;
		size=0;
	}
	
	public final int size()
	{
		return size;
	}
	
	@Override
	public void forEach(Consumer<V> consumer) {
		Node<Integer,V> node=firstAdd;
		for(;node!=null;)
		{
			consumer.accept(node.getValue());
			node=node.getNext();
		}
	}
	
	public static void main(String[] args) {
		BitIntTree<String> b=new BITree<>();
		for(int i=0;i<10;i++)
		{
			b.write(i,"i="+i);
		}
		b.forEach((v)->{
			System.out.println(v);
		});
	}
}
