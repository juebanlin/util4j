package net.jueb.util4j.buffer;

import static io.netty.util.internal.MathUtil.isOutOfBounds;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import io.netty.buffer.ByteBufAllocator;
import net.jueb.util4j.bytesStream.bytes.BytesUtil;

public class ArrayBytesBuff implements BytesBuff{

	public final static int LEN_1=1;
	public final static int LEN_2=2;
	public final static int LEN_3=3;
	public final static int LEN_4=4;
	public final static int LEN_8=8;
	byte[] array;
    int readerIndex;
    int writerIndex;
    private int markedReaderIndex;
    private int markedWriterIndex;
    
    public ArrayBytesBuff(int readIndex,int writeIndex,byte[] initialArray) {
    	this(initialArray, readIndex, writeIndex);
	}
    
    private ArrayBytesBuff(byte[] initialArray, int readerIndex, int writerIndex) {
        if (initialArray == null) {
            throw new NullPointerException("initialArray");
        }
        this.array=initialArray;
        setIndex(readerIndex, writerIndex);
    }
    
    final void setIndexUnsafe(int readerIndex, int writerIndex) {
        this.readerIndex = readerIndex;
        this.writerIndex = writerIndex;
    }

    final void discardMarks() {
        markedReaderIndex = markedWriterIndex = 0;
    }
	private void checkIndex(int index, int fieldLength) {
	        if (isOutOfBounds(index, fieldLength, capacity())) {
	            throw new IndexOutOfBoundsException(String.format(
	                    "index: %d, length: %d (expected: range(0, %d))", index, fieldLength, capacity()));
	        }
	}

	private boolean isOutOfBounds(int index, int length, int capacity) {
	        return (index | length | (index + length) | (capacity - (index + length))) < 0;
	}

	/**
	 * 检查是否可读字节数
	 * @param minimumReadableBytes
	 */
	private void checkReadableBytes0(int minimumReadableBytes) {
	    if (readerIndex > writerIndex - minimumReadableBytes) {
	        throw new IndexOutOfBoundsException(String.format(
	                "readerIndex(%d) + length(%d) exceeds writerIndex(%d): %s",
	                readerIndex, minimumReadableBytes, writerIndex, this));
	    }
	}

	private final void checkReadableBytes(int minimumReadableBytes) {
	    if (minimumReadableBytes < 0) {
	        throw new IllegalArgumentException("minimumReadableBytes: " + minimumReadableBytes + " (expected: >= 0)");
	    }
	    checkReadableBytes0(minimumReadableBytes);
	}
	
	/**
	 * 如果容量不够则扩容
	 * @param minWritableBytes
	 */
	private void ensureWritable0(int minWritableBytes) {
		int size=writableBytes();
		if (minWritableBytes <= size) {
            return;
        }
		int addSize=minWritableBytes-size;
		array=Arrays.copyOf(array,array.length+addSize);
    }

	@Override
	public BytesBuff markReaderIndex() {
		markedWriterIndex=readerIndex;
		return this;
	}
	@Override
	public BytesBuff resetReaderIndex() {
		readerIndex(markedReaderIndex);
        return this;
	}
	@Override
	public BytesBuff markWriterIndex() {
		markedWriterIndex=writerIndex;
		return this;
	}
	@Override
	public BytesBuff resetWriterIndex() {
		 writerIndex = markedWriterIndex;
	     return this;
	}
	@Override
	public int readerIndex() {
		return readerIndex;
	}
	@Override
	public BytesBuff readerIndex(int readerIndex) {
		if (readerIndex < 0 || readerIndex > writerIndex) {
            throw new IndexOutOfBoundsException(String.format(
                    "readerIndex: %d (expected: 0 <= readerIndex <= writerIndex(%d))", readerIndex, writerIndex));
        }
        this.readerIndex = readerIndex;
        return this;
	}
	@Override
	public int writerIndex() {
		return writerIndex;
	}
	@Override
	public BytesBuff writerIndex(int writerIndex) {
		if (writerIndex < readerIndex || writerIndex > capacity()) {
            throw new IndexOutOfBoundsException(String.format(
                    "writerIndex: %d (expected: readerIndex(%d) <= writerIndex <= capacity(%d))",
                    writerIndex, readerIndex, capacity()));
        }
        this.writerIndex = writerIndex;
        return this;
	}
	@Override
	public BytesBuff setIndex(int readerIndex, int writerIndex) {
		 if (readerIndex < 0 || readerIndex > writerIndex || writerIndex > capacity()) {
	            throw new IndexOutOfBoundsException(String.format(
	                    "readerIndex: %d, writerIndex: %d (expected: 0 <= readerIndex <= writerIndex <= capacity(%d))",
	                    readerIndex, writerIndex, capacity()));
	        }
		 setIndexUnsafe(readerIndex, writerIndex);
	  return this;
	}
	
	@Override
	public int readableBytes() {
		return writerIndex - readerIndex;
	}
	
	@Override
    public int writableBytes() {
        return capacity() - writerIndex;
    }
	
	@Override
	public int capacity() {
		return array.length;
	}
	
	@Override
	public BytesBuff copy() {
		return copy(readerIndex, readableBytes());
	}
	@Override
	public BytesBuff copy(int index, int length) {
		checkIndex(readerIndex, readableBytes());
        byte[] copiedArray = new byte[length];
        System.arraycopy(array, index, copiedArray, 0, length);
        return new ArrayBytesBuff(0,0,copiedArray);
	}
	
	@Override
	public boolean readBoolean() {
		return readByte() != 0;
	}
	@Override
	public byte readByte() {
		checkReadableBytes0(LEN_1);
	    return BytesUtil.readByte(array, readerIndex++);
	}
	
	@Override
	public short readUnsignedByte() {
		return (short) (readByte() & 0xFF);
	}
	
	@Override
	public short readShort() {
		checkReadableBytes0(LEN_2);
		short b=BytesUtil.readShort(array,readerIndex);
		readerIndex+=LEN_2;
	    return b;
	}
	@Override
	public short readShortLE() {
		checkReadableBytes0(LEN_2);
		short b=BytesUtil.readShortLE(array,readerIndex);
		readerIndex+=LEN_2;
	    return b;
	}
	
	@Override
    public int readUnsignedShort() {
        return readShort() & 0xFFFF;
    }

    @Override
    public int readUnsignedShortLE() {
        return readShortLE() & 0xFFFF;
    }
    
    @Override
    public int readMedium() {
        int value = readUnsignedMedium();
        if ((value & 0x800000) != 0) {
            value |= 0xff000000;
        }
        return value;
    }

    @Override
    public int readMediumLE() {
        int value = readUnsignedMediumLE();
        if ((value & 0x800000) != 0) {
            value |= 0xff000000;
        }
        return value;
    }
    
	@Override
	public int readUnsignedMedium() {
		checkReadableBytes0(LEN_3);
		int b=BytesUtil.readUnsignedMedium(array,readerIndex);
		readerIndex+=LEN_3;
	    return b;
	}
	
	@Override
	public int readUnsignedMediumLE() {
		checkReadableBytes0(LEN_3);
		int b=BytesUtil.readUnsignedMediumLE(array,readerIndex);
		readerIndex+=LEN_3;
	    return b;
	}
	
	@Override
	public int readInt() {
		checkReadableBytes0(LEN_4);
		int b=BytesUtil.readInt(array,readerIndex);
		readerIndex+=LEN_4;
	    return b;
	}
	@Override
	public int readIntLE() {
		checkReadableBytes0(LEN_4);
		int b=BytesUtil.readIntLE(array,readerIndex);
		readerIndex+=LEN_4;
	    return b;
	}
	
	@Override
    public long readUnsignedInt() {
        return readInt() & 0xFFFFFFFFL;
    }

    @Override
    public long readUnsignedIntLE() {
        return readIntLE() & 0xFFFFFFFFL;
    }
    
	@Override
	public long readLong() {
		checkReadableBytes0(LEN_8);
		long b=BytesUtil.readLong(array,readerIndex);
		readerIndex+=LEN_8;
	    return b;
	}
	@Override
	public long readLongLE() {
		checkReadableBytes0(LEN_8);
		long b=BytesUtil.readLongLE(array,readerIndex);
		readerIndex+=LEN_8;
	    return b;
	}

	@Override
    public char readChar() {
        return (char) readShort();
    }

    @Override
    public float readFloat() {
        return Float.intBitsToFloat(readInt());
    }

    @Override
    public double readDouble() {
        return Double.longBitsToDouble(readLong());
    }

	@Override
	public BytesBuff readBytes(int length) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff readSlice(int length) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff readRetainedSlice(int length) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff readBytes(BytesBuff dst) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff readBytes(BytesBuff dst, int length) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff readBytes(BytesBuff dst, int dstIndex, int length) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff readBytes(byte[] dst) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff readBytes(byte[] dst, int dstIndex, int length) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff readBytes(OutputStream out, int length) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff skipBytes(int length) {
		 checkReadableBytes(length);
	     readerIndex += length;
	     return this;
	}
	@Override
	public BytesBuff writeBoolean(boolean value) {
		writeByte(value ? 1 : 0);
        return this;
	}
	
	@Override
	public BytesBuff writeByte(int value) {
		 ensureWritable0(LEN_1);
	     BytesUtil.writeByte(array,writerIndex,value);
	     writerIndex+=LEN_1;
	     return this;
	}
	@Override
	public BytesBuff writeShort(int value) {
		 ensureWritable0(LEN_2);
	     BytesUtil.writeShort(array,writerIndex,value);
	     writerIndex+=LEN_2;
	     return this;
	}
	@Override
	public BytesBuff writeShortLE(int value) {
		ensureWritable0(LEN_2);
	     BytesUtil.writeShortLE(array,writerIndex,value);
	     writerIndex+=LEN_2;
	     return this;
	}
	@Override
	public BytesBuff writeMedium(int value) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff writeMediumLE(int value) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff writeInt(int value) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff writeIntLE(int value) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff writeLong(long value) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff writeLongLE(long value) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff writeChar(int value) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff writeFloat(float value) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff writeDouble(double value) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff writeBytes(BytesBuff src) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff writeBytes(BytesBuff src, int length) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff writeBytes(BytesBuff src, int srcIndex, int length) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff writeBytes(byte[] src) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff writeBytes(byte[] src, int srcIndex, int length) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean getBoolean(int index) {
		return getByte(index) != 0;
	}
	@Override
	public byte getByte(int index) {
		checkIndex(index,LEN_1);
	    return BytesUtil.readByte(array, index);
	}
	@Override
	public short getUnsignedByte(int index) {
		return (short) (getByte(index) & 0xFF);
	}
	
	@Override
	public short getShort(int index) {
		checkIndex(index,LEN_2);
		return BytesUtil.readShort(array, index);
	}
	
	@Override
	public short getShortLE(int index) {
		checkIndex(index,LEN_2);
		return BytesUtil.readShortLE(array, index);
	}
	
	@Override
    public int getUnsignedShort(int index) {
        return getShort(index) & 0xFFFF;
    }

    @Override
    public int getUnsignedShortLE(int index) {
        return getShortLE(index) & 0xFFFF;
    }
    
    @Override
    public int getMedium(int index) {
        int value = getUnsignedMedium(index);
        if ((value & 0x800000) != 0) {
            value |= 0xff000000;
        }
        return value;
    }

    @Override
    public int getMediumLE(int index) {
        int value = getUnsignedMediumLE(index);
        if ((value & 0x800000) != 0) {
            value |= 0xff000000;
        }
        return value;
    }
    
	@Override
	public int getUnsignedMedium(int index) {
		checkIndex(index,LEN_3);
		return BytesUtil.readUnsignedMedium(array, index);
	}
	
	@Override
	public int getUnsignedMediumLE(int index) {
		checkIndex(index,LEN_3);
		return BytesUtil.readUnsignedMediumLE(array, index);
	}
	
	@Override
	public int getInt(int index) {
		checkIndex(index,LEN_4);
		return BytesUtil.readInt(array, index);
	}
	@Override
	public int getIntLE(int index) {
		checkIndex(index,LEN_4);
		return BytesUtil.readIntLE(array, index);
	}
	
	@Override
    public long getUnsignedInt(int index) {
        return getInt(index) & 0xFFFFFFFFL;
    }

    @Override
    public long getUnsignedIntLE(int index) {
        return getIntLE(index) & 0xFFFFFFFFL;
    }
    
	@Override
	public long getLong(int index) {
		checkIndex(index,LEN_8);
		return BytesUtil.readLong(array, index);
	}
	
	@Override
	public long getLongLE(int index) {
		checkIndex(index,LEN_8);
		return BytesUtil.readLongLE(array, index);
	}
	
	@Override
    public char getChar(int index) {
        return (char) getShort(index);
    }

    @Override
    public float getFloat(int index) {
        return Float.intBitsToFloat(getInt(index));
    }

    @Override
    public double getDouble(int index) {
        return Double.longBitsToDouble(getLong(index));
    }
    
	@Override
	public BytesBuff getBytes(int index, BytesBuff dst) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff getBytes(int index, BytesBuff dst, int length) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff getBytes(int index, BytesBuff dst, int dstIndex, int length) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff getBytes(int index, byte[] dst) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff getBytes(int index, byte[] dst, int dstIndex, int length) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff getBytes(int index, OutputStream out, int length) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public BytesBuff setBoolean(int index, boolean value) {
		setByte(index, value? 1 : 0);
        return this;
	}
	
	@Override
	public BytesBuff setByte(int index, int value) {
		checkIndex(index,LEN_1);
	    BytesUtil.writeByte(array, index, value);
	    return this;
	}
	
	@Override
	public BytesBuff setShort(int index, int value) {
		checkIndex(index,LEN_2);
	    BytesUtil.writeShort(array, index, value);
	    return this;
	}
	
	@Override
	public BytesBuff setShortLE(int index, int value) {
		checkIndex(index,LEN_2);
	    BytesUtil.writeShortLE(array, index, value);
	    return this;
	}
	
	@Override
	public BytesBuff setMedium(int index, int value) {
		checkIndex(index,LEN_3);
	    BytesUtil.writeMedium(array, index, value);
	    return this;
	}
	@Override
	public BytesBuff setMediumLE(int index, int value) {
		checkIndex(index,LEN_3);
	    BytesUtil.writeMediumLE(array, index, value);
	    return this;
	}
	@Override
	public BytesBuff setInt(int index, int value) {
		checkIndex(index,LEN_4);
	    BytesUtil.writeInt(array, index, value);
	    return this;
	}
	@Override
	public BytesBuff setIntLE(int index, int value) {
		checkIndex(index,LEN_4);
	    BytesUtil.writeIntLE(array, index, value);
	    return this;
	}
	@Override
	public BytesBuff setLong(int index, long value) {
		checkIndex(index,LEN_8);
	    BytesUtil.writeLong(array, index, value);
	    return this;
	}
	@Override
	public BytesBuff setLongLE(int index, long value) {
		checkIndex(index,LEN_8);
	    BytesUtil.writeLongLE(array, index, value);
	    return this;
	}
	@Override
	public BytesBuff setChar(int index, int value) {
		setShort(index, value);
	    return this;
	}
	@Override
	public BytesBuff setFloat(int index, float value) {
		setInt(index, Float.floatToRawIntBits(value));
        return this;
	}
	@Override
	public BytesBuff setDouble(int index, double value) {
		setLong(index, Double.doubleToRawLongBits(value));
        return this;
	}
	@Override
	public BytesBuff setBytes(int index, BytesBuff src) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff setBytes(int index, BytesBuff src, int length) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff setBytes(int index, BytesBuff src, int srcIndex, int length) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff setBytes(int index, byte[] src) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff setBytes(int index, byte[] src, int srcIndex, int length) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int setBytes(int index, InputStream in, int length) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public BytesBuff setZero(int index, int length) {
        if (length == 0) {
            return this;
        }
        checkIndex(index, length);
        int nLong = length >>> 3;
        int nBytes = length & 7;
        for (int i = nLong; i > 0; i --) {
            setLong(index, 0);
            index += 8;
        }
        if (nBytes == 4) {
        	setInt(index, 0);
            // Not need to update the index as we not will use it after this.
        } else if (nBytes < 4) {
            for (int i = nBytes; i > 0; i --) {
            	setByte(index, (byte) 0);
                index ++;
            }
        } else {
            setInt(index, 0);
            index += 4;
            for (int i = nBytes - 4; i > 0; i --) {
                setByte(index, (byte) 0);
                index ++;
            }
        }
        return this;
    }
}
