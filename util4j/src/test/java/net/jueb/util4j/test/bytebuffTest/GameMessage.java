package net.jueb.util4j.test.bytebuffTest;


import java.io.Serializable;

/**
 */
public class GameMessage implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final short code;
	
	private final ByteBuffer content;

	public GameMessage(short code) {
		this.code=code;
		this.content=new ByteBuffer();
	}
	
	public GameMessage(short code,ByteBuffer content) {
		this.code=code;
		this.content=content;
	}
	
	public GameMessage(int code) {
		this.code=(short) code;
		this.content=new ByteBuffer();
	}
	
	public GameMessage(int code,ByteBuffer content) {
		this.code=(short) code;
		this.content=content;
	}
	
	public GameMessage(short code,byte[] data) {
		this.code=code;
		this.content=new ByteBuffer(data);
	}
	public short getCode() {
		return code;
	}

	public ByteBuffer getContent() {
		return content;
	}

	@Override
	public String toString() {
		return "AppMessage [code=" + code + ", content=" + content + "]";
	}
}