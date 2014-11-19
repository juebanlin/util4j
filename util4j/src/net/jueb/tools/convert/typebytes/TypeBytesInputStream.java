package net.jueb.tools.convert.typebytes;
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.io.UTFDataFormatException;

/**
 * 默认的字节都以执行& 0xff
 * @author Administrator
 */
public final class TypeBytesInputStream extends FilterInputStream implements DataInput {

	/**
	 * 是否为小端模式
	 * 默认false是大端模式
	 */
	private boolean isLittleEndian=false;
	
	
	/**
	 * 以ByteArrayInputStream初始化一个数组为输入流
	 * @param buf
	 */
	public TypeBytesInputStream(byte[] buf) {
        super(new ByteArrayInputStream(buf));
    }
	
	/**
	 * 以ByteArrayInputStream初始化一个数组为输入流
	 * @param buf
	 * @param mode
	 */
	public TypeBytesInputStream(byte[] buf,boolean isLittleEndian) {
        super(new ByteArrayInputStream(buf));
        this.isLittleEndian=isLittleEndian;
    }
	
	/**
	 * 创建一个新的大端模式的数据输入流
	 * @param in
	 */
	public TypeBytesInputStream(InputStream in) {
        super(in);
    }
	
    public TypeBytesInputStream(InputStream in,boolean isLittleEndian) {
        super(in);
        this.isLittleEndian=isLittleEndian;
    }
    
    public boolean isLittleEndian()
    {
    	return this.isLittleEndian;
    }

    /**
     * working arrays initialized on demand by readUTF
     */
    private byte bytearr[] = new byte[80];
    private char chararr[] = new char[80];

    /**
     *从包含的输入流中读取一定数量的字节，并将它们存储到缓冲区数组 b 中。
     *以整数形式返回实际读取的字节数。在输入数据可用、检测到文件末尾 (end of file) 或抛出异常之前，此方法将一直阻塞。 
     *如果 b 为 null，则抛出 NullPointerException。
     *如果 b 的长度为 0，则不读取字节并返回 0；否则，尝试读取至少一个字节。
     *如果因为流位于文件末尾而没有字节可用，则返回值 -1；否则至少读取一个字节并将其存储到 b 中。 
     *将读取的第一个字节存储到元素 b[0] 中，将下一个字节存储到 b[1] 中，依此类推。
     *读取的字节数至多等于 b 的长度。设 k 为实际读取的字节数；
     *这些字节将存储在从 b[0] 到 b[k-1] 的元素中，b[k] 到 b[b.length-1] 的元素不受影响。 
     *read(b) 方法与以下方法的效果相同： 
     *read(b, 0, b.length) 
     */
    public final int read(byte b[]) throws IOException {
        return in.read(b, 0, b.length);
    }

    /**
     *从包含的输入流中将最多 len 个字节读入一个 byte 数组中。尽量读取 len 个字节，但读取的字节数可能少于 len 个，也可能为零。
     *以整数形式返回实际读取的字节数。 在输入数据可用、检测到文件末尾或抛出异常之前，此方法将阻塞。 
	 *如果 len 为零，则不读取任何字节并返回 0；否则，尝试读取至少一个字节。
	 *如果因为流位于文件未尾而没有字节可用，则返回值 -1；否则，至少读取一个字节并将其存储到 b 中。 
	 *将读取的第一个字节存储到元素 b[off] 中，将下一个字节存储到 b[off+1] 中，依此类推。
	 *读取的字节数至多等于 len。设 k 为实际读取的字节数；
	 *这些字节将存储在 b[off] 到 b[off+k-1] 的元素中，b[off+k] 到 b[off+len-1] 的元素不受影响。 
	 *在所有情况下，b[0] 到 b[off] 的元素和 b[off+len] 到 b[b.length-1] 的元素都不受影响。 
	 *参数：b - 存储读取数据的缓冲区。;off - 目标数组 b 中的起始偏移量 ;len - 读取的最大字节数。
     */
    public final int read(byte b[], int off, int len) throws IOException {
        return in.read(b, off, len);
    }

    /**
     *从输入流中读取一些字节，并将它们存储在缓冲区数组 b 中。读取的字节数等于 b 的长度。 
     *在出现以下条件之一以前，此方法将一直阻塞：
     *输入数据的 b.length 个字节是可用的，在这种情况下，正常返回。 
     *检测到文件末尾，在这种情况下，抛出 EOFException。 
     *发生 I/O 错误，在这种情况下，将抛出 IOException，而不是 EOFException。 
     *如果 b 为 null，则抛出 NullPointerException。
     *如果 b.length 为零，则不读取字节。否则，将读取的第一个字节存储到元素 b[0] 中，
     *下一个字节存储到 b[1] 中，依此类推。如果此方法抛出异常，则可能是因为已经用输入流中的数据更新了 b 的某些（但非全部）字节。
     * @param      b - 存储读取数据的缓冲区。 
     * @exception  如果此输入流在读取所有字节之前到达末尾。 
     * @exception  该流已关闭并且包含的输入流在关闭后不支持读取操作，或者发生其他 I/O 错误。
     * @see        java.io.FilterInputStream#in
     */
    public final void readFully(byte b[]) throws IOException {
        readFully(b, 0, b.length);
    }

    /**
     *从输入流中读取 len 个字节。 
     *在出现以下条件之一以前，此方法将一直阻塞：
     *输入数据的 len 个字节是可用的，在这种情况下，正常返回。 
     *检测到文件末尾，在这种情况下，抛出 EOFException。 
     *如果发生 I/O 错误，在这种情况下，将抛出 IOException，而不是 EOFException。 
     *如果 b 为 null，则抛出 NullPointerException。
     *如果 off 为负，或 len 为负，或者 off+len 大于数组 b 的长度，
     *则抛出 IndexOutOfBoundsException。如果 len 为零，则不读取字节。
     *否则，将读取的第一个字节存储到元素 b[off] 中，下一个字节存储到 b[off+1] 中，依此类推。读取的字节数至多等于 b[0]。 
     * @param      b - 存储读取数据的缓冲区。
     * @param      off - 数据的起始偏移量。
     * @param      len - 要读取的字节数。 
     * @exception  EOFException  if this input stream reaches the end before
     *               reading all the bytes.
     * @exception  IOException   the stream has been closed and the contained
     *             input stream does not support reading after close, or
     *             another I/O error occurs.
     * @see        java.io.FilterInputStream#in
     */
    public final void readFully(byte b[], int off, int len) throws IOException {
        if (len < 0)
            throw new IndexOutOfBoundsException();
        int n = 0;
        while (n < len) {
            int count = in.read(b, off + n, len - n);
            if (count < 0)
                throw new EOFException();
            n += count;
        }
    }

    /**
     * 试图在输入流中跳过数据的 n 个字节，并丢弃跳过的字节。
     * 不过，可以跳过更少的字节数，该字节数甚至可以为零。
     * 这可能由很多情况引起；在已经跳过 n 个字节前到达文件末尾只是其中的一种可能。
     * 此方法从不抛出 EOFException。返回实际跳过的字节数。
     * @param      n - 要跳过的字节数。 
     * @return     实际跳过的字节数。
     * @exception  IOException  if the contained input stream does not support
     *             seek, or the stream has been closed and
     *             the contained input stream does not support
     *             reading after close, or another I/O error occurs.
     */
    public final int skipBytes(int n) throws IOException {
        int total = 0;
        int cur = 0;

        while ((total<n) && ((cur = (int) in.skip(n-total)) > 0)) {
            total += cur;
        }

        return total;
    }

    /**
     * 读取一个输入字节，如果该字节不是零，则返回 true，如果是零，则返回 false。
     * 此方法适用于读取用接口 DataOutput 的 writeBoolean 方法写入的字节。 
     * @return     读取的 boolean 值。 
     * @exception  EOFException  if this input stream has reached the end.
     * @exception  IOException   the stream has been closed and the contained
     *             input stream does not support reading after close, or
     *             another I/O error occurs.
     * @see        java.io.FilterInputStream#in
     */
    public final boolean readBoolean() throws IOException {
        int ch = in.read();
        if (ch < 0)
            throw new EOFException();
        return !(ch == 0);
    }

    /**
     * 读取并返回一个输入字节。该字节被看作是 -128 到 127（包含）范围内的一个有符号值。
     * 此方法适用于读取用接口 DataOutput 的 writeByte 方法写入的字节。
     * @return     读取的 8 位值。 
     *             <code>byte</code>.
     * @exception  EOFException  if this input stream has reached the end.
     * @exception  IOException   the stream has been closed and the contained
     *             input stream does not support reading after close, or
     *             another I/O error occurs.
     * @see        java.io.FilterInputStream#in
     */
    public final byte readByte() throws IOException {
        int ch = in.read();
        if (ch < 0)
            throw new EOFException();
        return (byte)(ch);
    }

    /**
     * 读取一个输入字节，将它左侧补零 (zero-extend) 转变为 int 类型，并返回结果，所以结果的范围是 0 到 255。
     * 如果接口 DataOutput 的 writeByte 方法的参数是 0 到 255 之间的值，则此方法适用于读取用 writeByte 写入的字节。 
     * @return     读取的无符号 8 位值。 
     * @exception  EOFException  if this input stream has reached the end.
     * @exception  IOException   the stream has been closed and the contained
     *             input stream does not support reading after close, or
     *             another I/O error occurs.
     * @see         java.io.FilterInputStream#in
     */
    public final int readUnsignedByte() throws IOException {
        int ch = in.read();
        if (ch < 0)
            throw new EOFException();
        return ch;
    }

    /**
     *读取两个输入字节并返回一个 short 值。设 a 为第一个读取字节，b 为第二个读取字节。返回的值是： 
     *(short)((a << 8) | (b & 0xff))
     *此方法适用于读取用接口 DataOutput 的 writeShort 方法写入的字节。 
     * @return     读取的 16 位值。 
     * @exception  EOFException  if this input stream reaches the end before
     *               reading two bytes.
     * @exception  IOException   the stream has been closed and the contained
     *             input stream does not support reading after close, or
     *             another I/O error occurs.
     * @see        java.io.FilterInputStream#in
     */
    public final short readShort() throws IOException {
        int ch1 = in.read();
        int ch2 = in.read();
        if ((ch1 | ch2) < 0)
        	throw new EOFException();
        if(isLittleEndian)
        {//小端模式，先取到低位
        	return (short)((ch1 << 0) + (ch2 << 8));
        }else
        {
        	return (short)((ch1 << 8) + (ch2 << 0));
        }
    }
        
        

    /**
     * 读取两个输入字节，并返回 0 到 65535 范围内的一个 int 值。设 a 为第一个读取字节，b 为第二个读取字节。返回的值是： 
     * (((a & 0xff) << 8) | (b & 0xff))
     * 如果接口 DataOutput 的 writeShort 方法的参数是 0 到 65535 范围内的值，则此方法适用于读取用 writeShort 写入的字节。
     * @return     读取的无符号 16 位值。
     * @exception  EOFException  if this input stream reaches the end before
     *             reading two bytes.
     * @exception  IOException   the stream has been closed and the contained
     *             input stream does not support reading after close, or
     *             another I/O error occurs.
     * @see        java.io.FilterInputStream#in
     */
    public final int readUnsignedShort() throws IOException {
        int ch1 = in.read();
        int ch2 = in.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        if(isLittleEndian)
        {//小端模式，先取到低位
        	return ((ch1 << 0) + (ch2 << 8));
        }else
        {
        	return ((ch1 << 8) + (ch2 << 0));
        }
    }

    /**
     * 读取两个输入字节并返回一个 char 值。设 a 为第一个读取字节，b 为第二个读取字节。返回的值是： 
     * (char)((a << 8) | (b & 0xff))
     * 此方法适用于读取用接口 DataOutput 的 writeChar 方法写入的字节。 
     * @return     读取的 char 值。 
     * @exception  EOFException  if this input stream reaches the end before
     *               reading two bytes.
     * @exception  IOException   the stream has been closed and the contained
     *             input stream does not support reading after close, or
     *             another I/O error occurs.
     * @see        java.io.FilterInputStream#in
     */
    public final char readChar() throws IOException {
        int ch1 = in.read();
        int ch2 = in.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        if(isLittleEndian)
        {//小端模式，先取到低位
        	return (char)((ch1 << 0) + (ch2 << 8));
        }else
        {
        	return (char)((ch1 << 8) + (ch2 << 0));
        }
    }

    /**
     *读取四个输入字节并返回一个 int 值。
     * @return     读取的 int 值。
     * @exception  EOFException  if this input stream reaches the end before
     *               reading four bytes.
     * @exception  IOException   the stream has been closed and the contained
     *             input stream does not support reading after close, or
     *             another I/O error occurs.
     * @see        java.io.FilterInputStream#in
     */
    public final int readInt() throws IOException {
        int ch1 = in.read();
        int ch2 = in.read();
        int ch3 = in.read();
        int ch4 = in.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0)
            throw new EOFException();
        if(isLittleEndian)
        {//小端模式，先取到低位
        	return ((ch1 << 0) + (ch2 << 8) + (ch3 << 16) + (ch4 << 24));
        }else
        {
        	return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
        }
        
    }

    private byte readBuffer[] = new byte[8];

    /**
     * 读取八个输入字节并返回一个 long 值。
     * @return     读取的 long 值。 
     * @exception  EOFException  if this input stream reaches the end before
     *               reading eight bytes.
     * @exception  IOException   the stream has been closed and the contained
     *             input stream does not support reading after close, or
     *             another I/O error occurs.
     * @see        java.io.FilterInputStream#in
     */
    public final long readLong() throws IOException {
        readFully(readBuffer, 0, 8);
        if(isLittleEndian)
        {//小端模式，先取到低位
        	return (((long)readBuffer[7] << 56) +
                    ((long)(readBuffer[6] & 255) << 48) +
                    ((long)(readBuffer[5] & 255) << 40) +
                    ((long)(readBuffer[4] & 255) << 32) +
                    ((long)(readBuffer[3] & 255) << 24) +
                    ((readBuffer[2] & 255) << 16) +
                    ((readBuffer[1] & 255) <<  8) +
                    ((readBuffer[0] & 255) <<  0));
        }else
        {
        	return (((long)readBuffer[0] << 56) +
                    ((long)(readBuffer[1] & 255) << 48) +
                    ((long)(readBuffer[2] & 255) << 40) +
                    ((long)(readBuffer[3] & 255) << 32) +
                    ((long)(readBuffer[4] & 255) << 24) +
                    ((readBuffer[5] & 255) << 16) +
                    ((readBuffer[6] & 255) <<  8) +
                    ((readBuffer[7] & 255) <<  0));
        }
    }
    /**
     *读取四个输入字节并返回一个 float 值。
     *实现这一点的方法是：先使用与 readInt 方法完全相同的方式构造一个 int 值，
     *然后使用与 Float.intBitsToFloat 方法完全相同的方式将此 int 值转换成一个 float 值。
     *此方法适用于读取用接口 DataOutput 的 writeFloat 方法写入的字节。
     * @return     the next four bytes of this input stream, interpreted as a
     *             <code>float</code>.
     * @exception  EOFException  if this input stream reaches the end before
     *               reading four bytes.
     * @exception  IOException   the stream has been closed and the contained
     *             input stream does not support reading after close, or
     *             another I/O error occurs.
     * @see        java.io.DataInputStream#readInt()
     * @see        java.lang.Float#intBitsToFloat(int)
     */
    public final float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    /**
     * 读取八个输入字节并返回一个 double 值。
     * 实现这一点的方法是：先使用与 readlong 方法完全相同的方式构造一个 long 值，
     * 然后使用与 Double.longBitsToDouble 方法完全相同的方式将此 long 值转换成一个 double 值。
     * @return     the next eight bytes of this input stream, interpreted as a
     *             <code>double</code>.
     * @exception  EOFException  if this input stream reaches the end before
     *               reading eight bytes.
     * @exception  IOException   the stream has been closed and the contained
     *             input stream does not support reading after close, or
     *             another I/O error occurs.
     * @see        java.io.DataInputStream#readLong()
     * @see        java.lang.Double#longBitsToDouble(long)
     */
    public final double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    private char lineBuffer[];

    /**
     * 从输入流中读取下一文本行。该方法读取连续的字节，将每个字节分别转换成一个字符，直到遇到行结尾符或到达末尾；
     * 然后以 String 形式返回读取的字符。注意，因为此方法用于处理字符，所以它不支持整个 Unicode 字符集的输入。 
     * 如果在一个字节都没有读取的时候就到达文件末尾，则返回 null。否则，通过左侧补零将读取的每个字节转换成 char 类型的值。
     * 如果遇到字符 '\n'，则丢弃它并且停止读取。如果遇到字符 '\r' 则丢弃它，如果后续字节转变成字符 '\n'，则同样丢弃它并停止读取。
     * 如果在遇到字符 '\n' 和 '\r' 之一前到达文件末尾，则停止读取。一旦已停止读取，则返回一个 String，它按顺序包含所有已读取且未丢弃的字符。
     * 注意，此字符串中的每个字符的值都将小于 \u0100（即 (char)256）的值。 
     * @return     输入流中文本的下一行，如果还没有读取一个字节就到达文件末尾，则返回 null。
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.BufferedReader#readLine()
     * @see        java.io.FilterInputStream#in
     */
    @Deprecated
    public final String readLine() throws IOException {
        char buf[] = lineBuffer;

        if (buf == null) {
            buf = lineBuffer = new char[128];
        }

        int room = buf.length;
        int offset = 0;
        int c;

loop:   while (true) {
            switch (c = in.read()) {
              case -1:
              case '\n':
                break loop;

              case '\r':
                int c2 = in.read();
                if ((c2 != '\n') && (c2 != -1)) {
                    if (!(in instanceof PushbackInputStream)) {
                        this.in = new PushbackInputStream(in);
                    }
                    ((PushbackInputStream)in).unread(c2);
                }
                break loop;

              default:
                if (--room < 0) {
                    buf = new char[offset + 128];
                    room = buf.length - offset - 1;
                    System.arraycopy(lineBuffer, 0, buf, 0, offset);
                    lineBuffer = buf;
                }
                buf[offset++] = (char) c;
                break;
            }
        }
        if ((c == -1) && (offset == 0)) {
            return null;
        }
        return String.copyValueOf(buf, 0, offset);
    }

    /**
     * 读入一个已使用 UTF-8 修改版格式编码的字符串。readUTF 的常规协定是：
     * 该方法读取使用 UTF-8 修改版格式编码的 Unicode 字符串的表示形式；然后以 String 的形式返回此字符串。 
     * 首先读取两个字节，并使用它们构造一个无符号 16 位整数，构造方式与 readUnsignedShort 方法的方式完全相同。
     * 该整数值被称为 UTF 长度，它指定要读取的额外字节数。
     * 然后成组地将这些字节转换为字符。每组的长度根据该组第一个字节的值计算。
     * 紧跟在某个组后面的字节（如果有）是下一组的第一个字节。 
     * 如果组的第一个字节与位模式 0xxxxxxx（其中 x 表示“可能为 0 或 1”）匹配，
     * 则该组只有这一个字节。该字节被左侧补零，转换成一个字符。 
     * 如果组的第一个字节与位模式 110xxxxx 匹配，则该组只由字节 a 和另一个字节 b 组成。
     * 如果没有字节 b（因为字节 a 是要读取的最后一个字节），或者字节 b 与位模式 10xxxxxx 不匹配，
     * 则抛出 UTFDataFormatException。否则，将该组转换成字符：
     * (char)(((a& 0x1F) << 6) | (b & 0x3F))
     *  如果组的第一个字节与位模式 1110xxxx 匹配，则该组由字节 a 和另外两个字节 b 和 c 组成。
     *  如果没有字节 c（因为字节 a 是要读取的最后两个字节之一），
     *  或者字节 b 或字节 c 与位模式 10xxxxxx 不匹配，则抛出 UTFDataFormatException。否则，将该组转换成字符：
     *  (char)(((a & 0x0F) << 12) | ((b & 0x3F) << 6) | (c & 0x3F))
     *  如果组的第一个字节与模式 1111xxxx 或模式 10xxxxxx 匹配，则抛出 UTFDataFormatException。
     *  如果在执行整个过程中的任意时间到达文件末尾，则抛出 EOFException。 
     *  在通过此过程将每个组转换成字符后，按照从输入流中读取相应组的顺序，
     *  将这些字符收集在一起，形成一个 String，然后该字符串将被返回。 
     *  可以使用 DataOutput 接口的 writeUTF 方法写入适合此方法读取的数据。
     * @return     一个 Unicode 字符串。
     * @exception  EOFException  if this input stream reaches the end before
     *               reading all the bytes.
     * @exception  IOException   the stream has been closed and the contained
     *             input stream does not support reading after close, or
     *             another I/O error occurs.
     * @exception  UTFDataFormatException if the bytes do not represent a valid
     *             modified UTF-8 encoding of a string.
     * @see        java.io.DataInputStream#readUTF(java.io.DataInput)
     */
    public final String readUTF() throws IOException {
        return readUTF(this);
    }

    /**
     * Reads from the
     * stream <code>in</code> a representation
     * of a Unicode  character string encoded in
     * <a href="DataInput.html#modified-utf-8">modified UTF-8</a> format;
     * this string of characters is then returned as a <code>String</code>.
     * The details of the modified UTF-8 representation
     * are  exactly the same as for the <code>readUTF</code>
     * method of <code>DataInput</code>.
     *
     * @param      in   a data input stream.
     * @return     a Unicode string.
     * @exception  EOFException            if the input stream reaches the end
     *               before all the bytes.
     * @exception  IOException   the stream has been closed and the contained
     *             input stream does not support reading after close, or
     *             another I/O error occurs.
     * @exception  UTFDataFormatException  if the bytes do not represent a
     *               valid modified UTF-8 encoding of a Unicode string.
     * @see        java.io.DataInputStream#readUnsignedShort()
     */
    public final static String readUTF(DataInput in) throws IOException {
        int utflen = in.readUnsignedShort();
        byte[] bytearr = null;
        char[] chararr = null;
        if (in instanceof TypeBytesInputStream) {
            TypeBytesInputStream dis = (TypeBytesInputStream)in;
            if (dis.bytearr.length < utflen){
                dis.bytearr = new byte[utflen*2];
                dis.chararr = new char[utflen*2];
            }
            chararr = dis.chararr;
            bytearr = dis.bytearr;
        } else {
            bytearr = new byte[utflen];
            chararr = new char[utflen];
        }

        int c, char2, char3;
        int count = 0;
        int chararr_count=0;

        in.readFully(bytearr, 0, utflen);

        while (count < utflen) {
            c = (int) bytearr[count] & 0xff;
            if (c > 127) break;
            count++;
            chararr[chararr_count++]=(char)c;
        }

        while (count < utflen) {
            c = (int) bytearr[count] & 0xff;
            switch (c >> 4) {
                case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7:
                    /* 0xxxxxxx*/
                    count++;
                    chararr[chararr_count++]=(char)c;
                    break;
                case 12: case 13:
                    /* 110x xxxx   10xx xxxx*/
                    count += 2;
                    if (count > utflen)
                        throw new UTFDataFormatException(
                            "malformed input: partial character at end");
                    char2 = (int) bytearr[count-1];
                    if ((char2 & 0xC0) != 0x80)
                        throw new UTFDataFormatException(
                            "malformed input around byte " + count);
                    chararr[chararr_count++]=(char)(((c & 0x1F) << 6) |
                                                    (char2 & 0x3F));
                    break;
                case 14:
                    /* 1110 xxxx  10xx xxxx  10xx xxxx */
                    count += 3;
                    if (count > utflen)
                        throw new UTFDataFormatException(
                            "malformed input: partial character at end");
                    char2 = (int) bytearr[count-2];
                    char3 = (int) bytearr[count-1];
                    if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80))
                        throw new UTFDataFormatException(
                            "malformed input around byte " + (count-1));
                    chararr[chararr_count++]=(char)(((c     & 0x0F) << 12) |
                                                    ((char2 & 0x3F) << 6)  |
                                                    ((char3 & 0x3F) << 0));
                    break;
                default:
                    /* 10xx xxxx,  1111 xxxx */
                    throw new UTFDataFormatException(
                        "malformed input around byte " + count);
            }
        }
        // The number of chars produced may be less than utflen
        return new String(chararr, 0, chararr_count);
    }
}
