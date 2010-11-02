package ch.ntb.inf.sts.util;



/**
 * First in first out <code>byte</code> array queue.
 * The size of the queue should be a multiple of 2 minus one (size = 2^x - 1).
 * 
 * @author 08.01.2010 simon.pertschy@ntb.ch
 * 
 */
/*
 * Changes:
 * 	
 */
public class ByteArrayFifo{
	byte[][] arrays;
	int head, tail;
	int size;

	/**
	 * Creates a new ByteArrrayFifo.
	 * @param size The size of the queue (size = 2^x - 1)
	 */
	public ByteArrayFifo(int size){
		arrays = new byte[size + 1][];
		this.size = size;
	}
	
	/**
	 * Inserts one byte array into the queue.
	 * @param data array which will be inserted into the queue
	 */
	public void enqueue(byte[] data){
		if(availToWrite() > 0){
			arrays[tail] = data;
			tail = (tail + 1) % arrays.length;
		}
	}
	
	/**
	 * Removes one <code>byte</code> array form the queue.
	 * @return The removed <code>byte</code> array.
	 */
	public byte[] dequeue(){
		if(head != tail){
			 byte[] c= arrays[head];
			head = (head + 1) % arrays.length;
			return c;
		}
		return null;
	}
	
	/**
	 * Clears the queue.
	 */
	public void reset(){
		head = tail;
	}
	
	/**
	 * @return The available <code>byte<code> arrays to read.
	 */
	public int availToRead(){
		int len = tail - head;
		if(len < 0) return arrays.length + len;
		return len;
	}
	
	/**
	 * @return The available queue space.
	 */
	public int availToWrite(){
		int len = tail - head;
		if(len < 0) len = arrays.length + len;
		return size - len;
	}
	
	/**
	 * @return The size of the queue.
	 */
	public int getSize(){
		return size;
	}
}