package net.jueb.util4j.test.bytebuffTest;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Deque;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class Client {

	public static int DEFALUT_MAX_PACK_SIZE = 64 * 1024 * 1024;
	private int maxPackSize = DEFALUT_MAX_PACK_SIZE;
	String host="";
	int port=0;
	Socket socket=new Socket();
	public Client(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}
	
	public boolean connect() throws IOException
	{
		socket.connect(new InetSocketAddress(host, port));
		return socket.isConnected();
	}
	
	/**
	 * �Ѿ���ȡ����Ϣ
	 */
	private Queue<GameMessage> readMsgs=new LinkedBlockingQueue<>();
	/**
	 * �����͵���Ϣ
	 */
	private Deque<GameMessage> writeMsgs=new LinkedBlockingDeque<>();
	
	public boolean isConnect()
	{
		return socket!=null && socket.isConnected();
	}
	
	public void sendMsg(GameMessage msg)
	{
		writeMsgs.add(msg);
	}
	
	public void sendMsgNow(GameMessage msg)
	{
		writeMsgs.addFirst(msg);
	}
	
	protected void onMessage(GameMessage msg)
	{
		
	}
	
	protected void onConnected(){
		
	}
	
	public void start() throws IOException
	{
		if(connect())
		{
			onConnected();
			readStart();//IO���߳�
			writeStart();//IOд�߳�
			new Thread(){//ҵ���߳�
				public void run() {
					for(;;)
					{
						GameMessage msg=readMsgs.poll();
						if(msg!=null)
						{
							if(!socket.isConnected())
							{
								return;
							}
							onMessage(msg);
						}
					}
				};
			}.start();
		}
	}
	
	public void readStart()
	{
		readMsgs.clear();
		new Thread(){
			public void run() {
				try {
					InputStream in=socket.getInputStream();
					ByteBuffer readBuffer=new ByteBuffer();
					for(;;)
					{
						if(!socket.isConnected())
						{
							break;
						}
						int size=in.available();
						if(size<6)
						{//��ͷ+��ϢID��С6���ֽ�
							continue;
						}
						byte[] buff=new byte[size];
						int readLen=in.read(buff);
						readBuffer.writeBytes(buff,0, readLen);
						for(;;)
						{
							boolean hasMsg=decoder(readBuffer,readMsgs,socket);//������Ϣ
							if(!hasMsg)
							{//���û����Ϣ���Խ���
								//���ճ��,�����ʣ�°����Ϣ�����򱣴�
//								byte[] data=readBuffer.readBytes(readBuffer.readableBytes()).getBytes();//ʣ���ֽ�
								byte[] data=readBuffer.getReadableBytes();//ʣ���ֽ�
								readBuffer.clear();//���ö�д����
								readBuffer.writeBytes(data);//���»�����
								break;
							}
						}
					}
				} catch (Exception e) {
				}
			};
		}.start();
	}
	
	public boolean decoder(ByteBuffer in,Queue<GameMessage> out,Socket socket) throws IOException
	{
		int readableBytes = in.readableBytes();
		if (readableBytes < 6) {//6=�ܳ���ͷ(4)+��Ϣ��(2)
			return false;
		}
		in.markReaderIndex();
		int lengthHead = in.readInt();//����ͷ4�ֽ�
		short code=in.readShort();//��Ϣ��2�ֽ�
		int dataLen=lengthHead-2;//ȥ��У��λ��С��Ϣͷ�����ݳ���
		//��������
		if(maxPackSize > 0 && lengthHead>maxPackSize)
		{
			in.resetReaderIndex();
			socket.close();
			return false;
		}
		//������ݳ���
		if(dataLen<0)
		{
			in.resetReaderIndex();
			socket.close();
			return false;
		}
		//��������Ƿ�����
		if(dataLen>0 && in.readableBytes()<dataLen)
		{//���ʣ�����ݻ�δ������ȴ���һ�ζ��¼�(�ų�����)
			in.resetReaderIndex();
			return false;
		}
		byte[] contentData=new byte[dataLen];//��Ϣ����
		in.readBytes(contentData, 0, contentData.length);
		GameMessage message = new GameMessage(code,new ByteBuffer(contentData));
		out.add(message);
		return true;
	}
	
	public void encoder(GameMessage msg,ByteBuffer out,Socket socket) throws IOException
	{
		ByteBuffer content = msg.getContent();
		content.reset();
		short code=msg.getCode();
		byte[] body=content.getBytes();
		int lengthHead=2+body.length;//����ͷ=2�ֽ���Ϣ��+���ݳ���
		out.writeInt(lengthHead);
		out.writeShort(code);
		out.writeBytes(body);
	}
	
	public void writeStart()
	{
		writeMsgs.clear();
		new Thread(){
			public void run() {
				try {
					OutputStream out=socket.getOutputStream();
					for(;;)
					{
						if(!socket.isConnected())
						{
							break;
						}
						GameMessage msg=writeMsgs.poll();
						if(msg!=null)
						{
							ByteBuffer outBuffer=new ByteBuffer();
							encoder(msg, outBuffer, socket);
							byte[] sendData=outBuffer.getBytes();
							out.write(sendData);
							out.flush();
						}
					}
				} catch (Exception e) {
				}
			};
		}.start();
	}
}
