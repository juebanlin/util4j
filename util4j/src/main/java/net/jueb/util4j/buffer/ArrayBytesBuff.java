package net.jueb.util4j.buffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.netty.buffer.ByteBufAllocator;

public class ArrayBytesBuff implements BytesBuff{

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
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public byte readByte() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public short readUnsignedByte() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public short readShort() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public short readShortLE() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int readUnsignedShort() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int readUnsignedShortLE() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int readMedium() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int readMediumLE() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int readUnsignedMedium() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int readUnsignedMediumLE() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int readInt() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int readIntLE() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public long readUnsignedInt() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public long readUnsignedIntLE() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public long readLong() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public long readLongLE() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public char readChar() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public float readFloat() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public double readDouble() {
		// TODO Auto-generated method stub
		return 0;
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
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff writeBoolean(boolean value) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff writeByte(int value) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff writeShort(int value) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff writeShortLE(int value) {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public byte getByte(int index) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public short getUnsignedByte(int index) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public short getShort(int index) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public short getShortLE(int index) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int getUnsignedShort(int index) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int getUnsignedShortLE(int index) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int getMedium(int index) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int getMediumLE(int index) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int getUnsignedMedium(int index) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int getUnsignedMediumLE(int index) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int getInt(int index) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int getIntLE(int index) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public long getUnsignedInt(int index) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public long getUnsignedIntLE(int index) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public long getLong(int index) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public long getLongLE(int index) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public char getChar(int index) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public float getFloat(int index) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public double getDouble(int index) {
		// TODO Auto-generated method stub
		return 0;
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
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff setByte(int index, int value) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff setShort(int index, int value) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff setShortLE(int index, int value) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff setMedium(int index, int value) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff setMediumLE(int index, int value) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff setInt(int index, int value) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff setIntLE(int index, int value) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff setLong(int index, long value) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff setLongLE(int index, long value) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff setChar(int index, int value) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff setFloat(int index, float value) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BytesBuff setDouble(int index, double value) {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}
}
