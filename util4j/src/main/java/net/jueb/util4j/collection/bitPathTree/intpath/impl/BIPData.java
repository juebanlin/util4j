package net.jueb.util4j.collection.bitPathTree.intpath.impl;

import net.jueb.util4j.collection.bitPathTree.intpath.BitIntPathData;
import net.jueb.util4j.collection.bitPathTree.intpath.BitMaskEnum;

import java.util.Iterator;
import java.util.function.Consumer;

/**
 * 优化节点非必要属性的内存占用
 * 分解层数越小,内存占用越低,速度越快
 * 减少了key的存储,占用内存更小速度更快
 * @author juebanlin
 */
public class BIPData<V> implements BitIntPathData<V>{
	
	private static final int BIT_NUMS=32;//总bit位数量
	private final MapConfig config;
	private final LayNode<V> root;
	private final int[] posCache;
	private final int[] posCache2;
	private int size;
	private Node<V> firstAdd;//最先加入的节点
	private Node<V> lastAdd;//最后一次加入的节点
	
	public BIPData() {
		this(BitMaskEnum.MASK_1111_1111);
	}
	
	public BIPData(BitMaskEnum mask) {
		int tmp=mask.getValue();
		int num=0;
		while(tmp!=0)
		{
			tmp=tmp>>>1;
			num++;
		}
		int maskLen=num;//bit长度
		int nodeSize=2<<maskLen-1;//层节点数量
		int layout=BIT_NUMS/maskLen;//分解层级
		config=new MapConfig(mask.getValue(), maskLen, layout, nodeSize);
		root=new LayNode<V>();

		posCache=new int[layout];//层级数组
		posCache2=new int[layout];//层级数组
		for(int i=0;i<layout;i++)
		{
			posCache[i]=maskLen*i;
			posCache2[i]=mask.getValue()<<posCache[i];
		}
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
		private final int lay;
		/**
		 * 占位掩码的二进制容量
		 */
		private final int nodeSize;
		
		public MapConfig(int mask, int maskLen, int lay, int nodeSize) {
			super();
			this.mask = mask;
			this.maskLen = maskLen;
			this.lay = lay;
			this.nodeSize = nodeSize;
		}
	
		public int getMask() {
			return mask;
		}
	
		public int getMaskLen() {
			return maskLen;
		}
	
		public int getLay() {
			return lay;
		}
	
		public int getNodeSize() {
			return nodeSize;
		}
	
		@Override
		public String toString() {
			return "MapConfig [bitNum=" + maskLen + ", layout=" + lay + ", nodeSize=" + nodeSize + "]";
		}
	}

	interface Node<V>{
		
		Node<V>[] getSub();
		
		void setSub(Node<V> sub[]);

		int getNodeSize();

		int getNumber();
		
		default V getValue() {
			return null;
		}
		default V setValue(V value) {
			return null;
		}
		
		void setPre(Node<V> node);
		void setNext(Node<V> node);
		/**
		 * 前一个
		 * @return
		 */
		Node<V> getPre();
		/**
		 * 后一个
		 * @return
		 */
		Node<V> getNext();
	}
	
//	abstract class AbstractNode<V1> implements Node<V1>
//	{
//		public boolean equals(Object o) {
//			if (!(o instanceof Node))
//				return false;
//			Node<?> e = (Node<?>)o;
//			return (getValue()==null ? e.getValue()==null : getValue().equals(e.getValue()));
//		}
//
//		public int hashCode() {
//			int valueHash = (getValue()==null ? 0 : getValue().hashCode());
//			return valueHash;
//		}
//
//		public String toString() {
//			return "value =" + getValue();
//		}
//
//		@Override
//		public int getNodeSize() {
//			return config.getNodeSize();
//		}
//	}
	
	/**
	 * 层节点
	 * @author juebanlin
	 * @param <V1>
	 */
	class LayNode<V1> implements Node<V1>{
		
		@SuppressWarnings("unchecked")
		private Node<V1>[] sub=new Node[getNodeSize()];

		@Override
		public Node<V1>[] getSub() {
			return sub;
		}

		@Override
		public void setSub(Node<V1>[] sub) {
			this.sub=sub;
		}

		@Override
		public int getNumber() {
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
		public void setNext(Node<V1> node) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setPre(Node<V1> node) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Node<V1> getNext() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Node<V1> getPre() {
			throw new UnsupportedOperationException();
		}

		public boolean equals(Object o) {
			if (!(o instanceof Node))
				return false;
			Node<?> e = (Node<?>)o;
			return (getValue()==null ? e.getValue()==null : getValue().equals(e.getValue()));
		}

		public int hashCode() {
			int valueHash = (getValue()==null ? 0 : getValue().hashCode());
			return valueHash;
		}

		public String toString() {
			return "value =" + getValue();
		}

		@Override
		public int getNodeSize() {
			return config.getNodeSize();
		}
	}

	/**
	 * 可存储数据的节点
	 * @author juebanlin
	 * @param <V1>
	 */
	class DataNode<V1> implements Node<V1>{
		private final int number;
		private V1 value;
		//用于迭代使用
		private Node<V1> pre;
		private Node<V1> next;
		
		public DataNode(int number) {
			this.number=number;
		}
		
		public int getNumber() {
			return number;
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
		public Node<V1> getPre() {
			return pre;
		}
		public void setPre(Node<V1> pre) {
			this.pre = pre;
		}
		public Node<V1> getNext() {
			return next;
		}
		public void setNext(Node<V1> next) {
			this.next = next;
		}

		@Override
		public Node<V1>[] getSub() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setSub(Node<V1>[] sub) {
			throw new UnsupportedOperationException();
		}

		public boolean equals(Object o) {
			if (!(o instanceof Node))
				return false;
			Node<?> e = (Node<?>)o;
			return (getValue()==null ? e.getValue()==null : getValue().equals(e.getValue()));
		}

		public int hashCode() {
			int valueHash = (getValue()==null ? 0 : getValue().hashCode());
			return valueHash;
		}

		public String toString() {
			return "value =" + getValue();
		}

		@Override
		public int getNodeSize() {
			return config.getNodeSize();
		}
	}


	/**
	 * 抵达节点
	 * @param number
	 * @param layout
	 * @param currentNode
	 * @param isWrite 是否是写操作
	 * @return
	 */
	protected Node<V> arraivedNode(int number,int layout,Node<V> currentNode,boolean isWrite)
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
		Node<V>[] sub=currentNode.getSub();
		Node<V> node=sub[p];
		if(node==null)
		{
			if(!isWrite)
			{//如果不是新建路径则返回null表示不可达
				return null;
			}
			if(layout==0)
			{//layout=0的node具有data属性
				node=new DataNode<>(number);
			}else
			{
				node=new LayNode<>();
			}
			sub[p]=node;
		}
		return arraivedNode(number, layout,node,isWrite);
	}
	
	/**
	 *  清理路径
	 * @param number
	 * @param layout
	 * @param currentNode
	 * @return
	 */
	protected Node<V> cleanNodePath(int number,int layout,Node<V> currentNode)
	{
		if(layout<0)
		{//超出范围
			return null;
		}
		layout--;
		int p=getMaskValue(number,layout);
		Node<V>[] sub=currentNode.getSub();
		Node<V> node=sub[p];
		if(node==null)
		{//不可达
			return null;
		}
		if(layout==0)
		{//到达终点
			sub[p]=null;
			return node;
		}
		Node<V> next=cleanNodePath(number, layout,node);
		return next;
	}
	
	/**
	 *  清理节点
	 * @param bitNumber
	 * @return
	 */
	protected Node<V> cleanNode(int bitNumber)
	{
		Node<V> node=cleanNodePath(bitNumber, config.lay, root);
		if(node!=null)
		{
			Node<V> pre=node.getPre();
			Node<V> next=node.getNext();
			if(pre!=null)
			{
				pre.setNext(next);
				node.setPre(null);
			}else
			{//设置头节点
				firstAdd=next;
			}
			if(next!=null)
			{
				next.setPre(pre);
				node.setNext(null);
			}else
			{//设置尾节点
				lastAdd=pre;
			}
			size--;
		}
		return node;
	}
	
	/**
	 * 取整数某二进制位的值
	 * @param number
	 * @param lay
	 * @return
	 */
	protected int getMaskValue(int number,int lay)
	{
//		int bitPos=config.maskLen*lay;
//		return (number & (config.mask<<bitPos))>>>bitPos;

//		return (number & (config.mask<<posCache[lay]))>>>posCache[lay];
		return (number & posCache2[lay])>>>posCache[lay];
	}

	protected final LayNode<V> getRootNode() {
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
		Node<V> node=arraivedNode(bitNumber, config.lay, root, true);
		V oldValue=node.getValue();
		if(node.getPre()!=null)
		{//仅仅是覆盖值
			node.setValue(value);
			return oldValue;
		}
		//新键值
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
		Node<V> node=arraivedNode(bitNumber, config.lay, root, false);
		if(node==null)
		{
			return null;
		}
		return node.getValue();
	}
	
	@Override
	public V clean(int bitNumber) {
		Node<V> node=cleanNode(bitNumber);
		if(node!=null)
		{
			return node.getValue();
		}
		return null;
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
	public Iterator<V> iterator() {
		return new Iterator<V>() {
			Node<V> node=firstAdd;
			@Override
			public boolean hasNext() {
				return node!=null;
			}
			@Override
			public V next() {
				if(node==null)
				{
					return null;
				}
				V result=node.getValue();
				node=node.getNext();
				return result;
			}
			@Override
			public void remove() {
				if(node!=null)
				{
					clean(node.getNumber());
				}
			}
		};
	}
	
	@Override
	public void forEach(Consumer<? super V> action)  {
		Node<V> node=firstAdd;
		for(;node!=null;)
		{
			action.accept(node.getValue());
			node=node.getNext();
		}
	}
}
