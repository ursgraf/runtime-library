package ch.ntb.inf.sts.util;



/**
 * First in first out <code>Integer</code> queue.
 * The size of the queue should be a multiple of 2 minus one (size = 2^x - 1).
 * 
 * @author NTB/SP 8.12.2008
 */
/*
 * Changes:
 * 
 */
public class IntFifo{
	
	public static final int NO_DATA = -1; 
	
	int[] data;
	int head, tail;
	int size;

	/**
	 * @param size The size of the queue (size = 2^x - 1).
	 */
	public IntFifo(int size){
		data = new int[size + 1];
		this.size = size;
	}
	
	/**
	 * Inserts one <code>Integer</code> into the queue.
	 * @param data <code>Integer</code> which will be inserted into the queue
	 */
	public void enqueue(int data){
		if(availToWrite() > 0){
			this.data[tail] = data;
			tail = (tail + 1) % this.data.length;
		}
	}
	
	/**
	 * Removes one <code>Integer</code> from the queue.
	 * @return The removed Integer or @see {@link #NO_DATA} if no data is present
	 */
	public int dequeue(){
		if(head != tail){
			int c= data[head];
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
	 * @return The available <code>Integers</code> to read.
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