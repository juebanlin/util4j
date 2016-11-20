package net.jueb.util4j.test.bytebuffTest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import net.jueb.util4j.bytesStream.bytes.HexUtil;
import net.jueb.util4j.test.bytebuffTest.bean.GameErrCode;
import net.jueb.util4j.test.bytebuffTest.bean.GateLoginRsp;

public class TestClient extends AbstractClient{

	public TestClient(String host, int port) {
		super(host, port);
	}
	
	@Override
	protected boolean isHeartReq(GameMessage msg) {
		return msg.getCode()==GameMsgCode.Heart_Req;
	}

	@Override
	protected GameMessage buildHeartRsp() {
		return new GameMessage(GameMsgCode.Heart_Rsp,new ByteBuffer());
	}

	@Override
	protected void onConnected() {
		
	}

	@Override
	protected void onMessage(GameMessage msg) {
		short code=msg.getCode();
		ByteBuffer content=msg.getContent();
		switch (code) {
		case GameMsgCode.Gate_UserLogin:
		{
			/**
			 * ���ػظ���Ϣ�Ĵ��룺
			 *  ByteBuffer buffer=new ByteBuffer();
				buffer.writeInt(code.value());
				if(code==GameErrCode.Succeed)
				{
					GateLoginRsp rsp=ra.getLastRspInfo();
					rsp.writeTo(buffer);
				}
				conn.sendMessage(new GameMessage(getMessageCode(),buffer));
			 */
			System.out.println("�յ���¼�ظ���Ϣ:\n"+HexUtil.prettyHexDump(msg.getContent().getBytes()));
			GateLoginRsp rsp=null;
			int errorCode=content.readInt();//��Ϣ�������һ���Ǵ�����
			if(errorCode==GameErrCode.Succeed.value())
			{//��������Ϊ�ɹ�ʱ������
				rsp=new GateLoginRsp();
				rsp.readFrom(content);
			}
			System.out.println("������:"+errorCode+",rsp="+rsp);
		}
			break;
		case GameMsgCode.RoleHallDataRefresh://����ˢ�°�
		{
			System.out.println("�յ��������ݻظ���Ϣ:\n"+HexUtil.prettyHexDump(msg.getContent().getBytes()));
			String name=content.readUTF();
			long money=content.readLong();
			String faceIcon=content.readUTF();
			int size=content.readInt();
			Map<Integer,Integer> bag=new HashMap<>();
			for(int i=0;i<size;i++)
			{
				bag.put(content.readInt(), content.readInt());
			}
			System.out.println("������Ϣ����:name="+name+",money="+money+",faceIcon="+faceIcon+",bag="+bag);
		}
			break;
		default:
			System.out.println("δ������Ϣ,code="+code);
			break;
		}
	}
	
	public void login()
	{
		String token="123456";
		String retoken=null;
		ByteBuffer buffer=new ByteBuffer();
		buffer.writeUTF(token);
		buffer.writeUTF(retoken);
		GameMessage msg=new GameMessage(GameMsgCode.Gate_UserLogin,buffer);
		sendMsg(msg);
		System.out.println("��¼��Ϣ�ѷ���");
	}

	public void refreshHallData()
	{
		GameMessage msg=new GameMessage(GameMsgCode.RoleHallDataRefresh,new ByteBuffer());
		sendMsg(msg);//�ظ�����
		System.out.println("��������ˢ����Ϣ�ѷ���");
	}

	public static void main(String[] args) throws IOException {
		TestClient test=new TestClient("127.0.0.1", 4001);
		test.start();
		if(test.isConnect())
		{
			Scanner sc=new Scanner(System.in);
			for(;;)
			{
				System.out.println("����������:");
				String s=sc.nextLine();
				switch (s) {
				case "login":
					test.login();
					break;
				case "hallData":
					test.refreshHallData();
					break;
				case "exit":
					sc.close();
					System.exit(0);
					return ;
				default:
					break;
				}
			}
		}
	}
}
