package net.jueb.util4j.test.bytebuffTest;
import java.io.IOException;

import net.jueb.util4j.bytesStream.bytes.HexUtil;

public class Test extends Client{

	public Test(String host, int port) {
		super(host, port);
	}
	
	@Override
	protected void onConnected() {
		
	}
	
	public void login()
	{
		String token="123345345";
		String retoken="qweqweqwe";
		ByteBuffer buffer=new ByteBuffer();
		buffer.writeUTF(token);
		buffer.writeUTF(retoken);
		GameMessage msg=new GameMessage(GameMsgCode.Gate_UserLogin,buffer);
		sendMsg(msg);
		System.out.println("��¼��Ϣ�ѷ���");
	}
	
	@Override
	protected void onMessage(GameMessage msg) {
		short code=msg.getCode();
		switch (code) {
		case GameMsgCode.Heart_Req:
			System.out.println("�յ�����������Ϣ"+msg.hashCode()+":\n"+HexUtil.prettyHexDump(msg.getContent().getBytes()));
			GameMessage heartRsp=new GameMessage(GameMsgCode.Heart_Rsp,new ByteBuffer());
			sendMsgNow(heartRsp);//�ظ�����
			break;
		case GameMsgCode.Gate_UserLogin:
			System.out.println("�յ���¼��Ϣ:\n"+HexUtil.prettyHexDump(msg.getContent().getBytes()));
			ByteBuffer content=msg.getContent();
			int errorCode=content.readInt();
			System.out.println("������:"+errorCode);
			break;
		default:
			System.out.println("δ������Ϣ,code="+code);
			break;
		}
	}
	
	public static void main(String[] args) throws IOException {
		Test test=new Test("127.0.0.1", 4001);
		test.start();
		if(test.isConnect())
		{
			test.login();
		}
	}
}
