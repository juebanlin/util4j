package net.jueb.tools.convert.typebytes;
import java.io.DataOutput;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UTFDataFormatException;

/**
 * 采用无符号左移方式写入位数值
 * @author Administrator
 *
 */
public final class TypeBytesOutputStream extends FilterOutputStream implements DataOutput {
    
	/**
	 * 是否为小端模式
	 * 默认false是大端模式
	 */
	private boolean isLittleEndian=false;
	
	/**
     * The number of bytes written to the data output stream so far.
     * If this counter overflows, it will be wrapped to Integer.MAX_VALUE.
     */
    protected int written;

    /**
     * bytearr is initialized on demand by writeUTF
     */
    private byte[] bytearr = null;

    /**
     * 创建一个新的大端模式的数据输出流，将数据写入指定基础输出流。计数器 written 被设置为零。
     * @param out
     */
    public TypeBytesOutputStream(OutputStream out) {
        super(out);
    }
    /**
     * 创建一个新的指定模式的数据输出流，将数据写入指定基础输出流。计数器 written 被设置为零。
     * @param out
     * @param isLittleEndian 是否是小端模式
     */
    public TypeBytesOutputStream(OutputStream out,boolean isLittleEndian) {
        super(out);
        this.isLittleEndian=isLittleEndian;
    }
    
    public boolean isLittleEndian()
    {
    	return this.isLittleEndian;
    }
    
    /**
     * Increases the written counter by the specified value
     * until it reaches Integer.MAX_VALUE.
     */
    private void incCount(int value) {
        int temp = written + value;
        if (temp < 0) {
            temp = Integer.MAX_VALUE;
        }
        written = temp;
    }

    /**
     *将指定字节（参数 b 的八个低位）写入基础输出流。如果没有抛出异常，则计数器 written 增加 1。 
	 *实现 OutputStream 的 write 方法。 
     */
    public synchronized void write(int b) throws IOException {
        out.write(b);
        incCount(1);
    }

    /**
     *将指定 byte 数组中从偏移量 off 开始的 len 个字节写入基础输出流。
     *如果没有抛出异常，则计数器 written 增加 len。 
     */
    public synchronized void write(byte b[], int off, int len)
        throws IOException
    {
        out.write(b, off, len);
        incCount(len);
    }

    /**
     *清空此数据输出流。这迫使所有缓冲的输出字节被写出到流中。 
     */
    public void flush() throws IOException {
        out.flush();
    }

    /**
     *将一个 boolean 值以 1-byte 值形式写入基础输出流。
     *值 true 以值 (byte)1 的形式被写出；值 false 以值 (byte)0 的形式被写出。
     *如果没有抛出异常，则计数器 written 增加 1。 
     */
    public final void writeBoolean(boolean v) throws IOException {
        out.write(v ? 1 : 0);
        incCount(1);
    }

    /**
     * 将一个 byte 值以 1-byte 值形式写出到基础输出流中。如果没有抛出异常，则计数器 written 增加 1。
     */
    public final void writeByte(int v) throws IOException {
        out.write(v);
        incCount(1);
    }

    /**
     * 将一个 short 值以 2-byte 值形式写入基础输出流中。
     * 如果没有抛出异常，则计数器 written 增加 2。
     */
    public final void writeShort(int v) throws IOException {
       if(isLittleEndian)
       {
    	   out.write((v >>> 0) & 0xFF);
	       out.write((v >>> 8) & 0xFF);
       }else 
       {
    	   out.write((v >>> 8) & 0xFF);
	       out.write((v >>> 0) & 0xFF);  
       }
       incCount(2);
    }

    /**
     * 将一个 char 值以 2-byte 值形式写入基础输出流中。
     * 如果没有抛出异常，则计数器 written 增加 2。 
     */
    public final void writeChar(int v) throws IOException {
    	if(isLittleEndian)
        {
    	   out.write((v >>> 0) & 0xFF);
   	       out.write((v >>> 8) & 0xFF);
        }else 
        {
           out.write((v >>> 8) & 0xFF);
   	       out.write((v >>> 0) & 0xFF);
        }
        incCount(2);
    }

    /**
    * 将一个 int 值以 4-byte 值形式写入基础输出流中。如果没有抛出异常，则计数器 written 增加 4。 
    */
    public final void writeInt(int v) throws IOException {
    	if(isLittleEndian)
        {
    	   out.write((v >>> 0) & 0xFF);
   	       out.write((v >>> 8) & 0xFF);
   	       out.write((v >>> 16) & 0xFF);
   	       out.write((v >>> 24) & 0xFF);
        }else 
        {
           out.write((v >>> 24) & 0xFF);
           out.write((v >>> 16) & 0xFF);
       	   out.write((v >>> 8) & 0xFF);
   	       out.write((v >>> 0) & 0xFF);
        }
        incCount(4);
    }

    private byte writeBuffer[] = new byte[8];

    /**
     * 将一个 long 值以 8-byte 值形式写入基础输出流中。如果没有抛出异常，则计数器 written 增加 8。 
     */
    public final void writeLong(long v) throws IOException {
    	if(isLittleEndian)
        {
    		writeBuffer[0] = (byte)(v >>> 0);
            writeBuffer[1] = (byte)(v >>> 8);
            writeBuffer[2] = (byte)(v >>> 16);
            writeBuffer[3] = (byte)(v >>> 24);
            writeBuffer[4] = (byte)(v >>> 32);
            writeBuffer[5] = (byte)(v >>> 40);
            writeBuffer[6] = (byte)(v >>> 48);
            writeBuffer[7] = (byte)(v >>> 56);
        }else
        {
        	writeBuffer[0] = (byte)(v >>> 56);
            writeBuffer[1] = (byte)(v >>> 48);
            writeBuffer[2] = (byte)(v >>> 40);
            writeBuffer[3] = (byte)(v >>> 32);
            writeBuffer[4] = (byte)(v >>> 24);
            writeBuffer[5] = (byte)(v >>> 16);
            writeBuffer[6] = (byte)(v >>>  8);
            writeBuffer[7] = (byte)(v >>>  0);
        }
        out.write(writeBuffer, 0, 8);
        incCount(8);
    }

    /**
     * 使用 Float 类中的 floatToIntBits 方法将 float 参数转换为一个 int 值，
     * 然后将该 int 值以 4-byte 值形式写入基础输出流中。
     * 如果没有抛出异常，则计数器 written 增加 4。 
     */
    public final void writeFloat(float v) throws IOException {
        writeInt(Float.floatToIntBits(v));
    }

    /**
     * 使用 Double 类中的 doubleToLongBits 方法将 double 参数转换为一个 long 值，
     * 然后将该 long 值以 8-byte 值形式写入基础输出流中，
     * 如果没有抛出异常，则计数器 written 增加 8。
     */
    public final void writeDouble(double v) throws IOException {
        writeLong(Double.doubleToLongBits(v));
    }

    /**
     * 将字符串按字节顺序写出到基础输出流中。
     */
    public final void writeBytes(String s) throws IOException {
        int len = s.length();
        for (int i = 0 ; i < len ; i++) {
            out.write((byte)s.charAt(i));
        }
        incCount(len);
    }

    /**
     * 将字符串按字符顺序写入基础输出流。
     */
    public final void writeChars(String s) throws IOException {
        int len = s.length();
        for (int i = 0 ; i < len ; i++) {
            int v = s.charAt(i);
            out.write((v >>> 8) & 0xFF);
            out.write((v >>> 0) & 0xFF);
        }
        incCount(len * 2);
    }

    /**
     * 以与机器无关方式使用 UTF-8 修改版编码将一个字符串写入基础输出流。 
	 * 首先，通过 writeShort 方法将两个字节写入输出流，表示后跟的字节数。
	 * 该值是实际写出的字节数，不是字符串的长度。
	 * 根据此长度，使用字符的 UTF-8 修改版编码按顺序输出字符串的每个字符。
	 * 如果没有抛出异常，则计数器 written 增加写入输出流的字节总数。
	 * 该值至少是 2 加 str 的长度，最多是 2 加 str 的三倍长度。 
     */
    public final void writeUTF(String str) throws IOException {
        writeUTF(str, this);
    }

    
    static int writeUTF(String str, DataOutput out) throws IOException {
        int strlen = str.length();
        int utflen = 0;
        int c, count = 0;

        /* use charAt instead of copying String to char array */
        for (int i = 0; i < strlen; i++) {
            c = str.charAt(i);
            if ((c >= 0x0001) && (c <= 0x007F)) {
                utflen++;
            } else if (c > 0x07FF) {
                utflen += 3;
            } else {
                utflen += 2;
            }
        }

        if (utflen > 65535)
            throw new UTFDataFormatException(
                "encoded string too long: " + utflen + " bytes");

        byte[] bytearr = null;
        if (out instanceof TypeBytesOutputStream) {
            TypeBytesOutputStream dos = (TypeBytesOutputStream)out;
            if(dos.bytearr == null || (dos.bytearr.length < (utflen+2)))
                dos.bytearr = new byte[(utflen*2) + 2];
            bytearr = dos.bytearr;
        } else {
            bytearr = new byte[utflen+2];
        }

        bytearr[count++] = (byte) ((utflen >>> 8) & 0xFF);
        bytearr[count++] = (byte) ((utflen >>> 0) & 0xFF);

        int i=0;
        for (i=0; i<strlen; i++) {
           c = str.charAt(i);
           if (!((c >= 0x0001) && (c <= 0x007F))) break;
           bytearr[count++] = (byte) c;
        }

        for (;i < strlen; i++){
            c = str.charAt(i);
            if ((c >= 0x0001) && (c <= 0x007F)) {
                bytearr[count++] = (byte) c;

            } else if (c > 0x07FF) {
                bytearr[count++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
                bytearr[count++] = (byte) (0x80 | ((c >>  6) & 0x3F));
                bytearr[count++] = (byte) (0x80 | ((c >>  0) & 0x3F));
            } else {
                bytearr[count++] = (byte) (0xC0 | ((c >>  6) & 0x1F));
                bytearr[count++] = (byte) (0x80 | ((c >>  0) & 0x3F));
            }
        }
        out.write(bytearr, 0, utflen+2);
        return utflen + 2;
    }

    /**
     *返回计数器 written 的当前值，即到目前为止写入此数据输出流的字节数。
     *如果计数器溢出，则将它包装到 Integer.MAX_VALUE。
     * @return
     */
    public final int size() {
        return written;
    }
    
}
