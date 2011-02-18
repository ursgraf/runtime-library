package ch.ntb.inf.deep.runtime.util;

/**
 * First in first out <code>byte</code> queue.
 * The size of the queue should be a multiple of 2 minus one (size = 2^x - 1).
 * 
 * @author NTB/SP 28.9.2007
 */
/*
 * Changes:
 * 1.4.2008 NTB/SP error states added
 */
public class ByteFifo{
	
	public static final int NO_DATA = -1; 
	
	byte[] data;
	int head, tail;
	int size;

	/**
	 * 
	 * @param size The size of the queue (size = 2^x - 1)
	 */
	public ByteFifo(int size){
		data = new byte[size + 1];
		this.size = size;
	}
	
	/**
	 * Inserts one <code>byte</code> into the queue.
	 * @param data <code>Byte</code> which will be inserted into the queue
	 */
	public void enqueue(byte data){
		if(availToWrite() > 0){
			this.data[tail] = data;
			tail = (tail + 1) % this.data.length;
		}
	}
	
	/**
	 * Removes one <code>byte</code> from the queue.
	 * @return The removed byte or @see {@link #NO_DATA} if no data is present
	 */
	public byte dequeue(){
		if(head != tail){
			byte c= data[head];
			head = (head + 1) % data.length;
			return c;
		}
		return NO_DATA;
	}
	
	
	/**
	 * Clears the queue.
	 */
	public void clear(){
		head = tail;
	}
	
	/**
	 * @return The available <code>bytes</code> to read.
	 */
	public int availToRead(){
		int len = tail - head;
		if(len < 0) return data.length + len;
		return len;
	}
	
	/**
	 * @return The available queue space.
	 */
	public int availToWrite(){
		int len = tail - head;
		if(len < 0) len = data.length + len;
		return size - len;
	}
	
	/**
	 * @return The size of the queue.
	 */
	public int getSize(){
		return size;
	}
}