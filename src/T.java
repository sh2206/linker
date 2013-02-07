import java.io.*;

class T // class for text buffer
{
	private final int mainMemorySize = 4096;
	private short buffer[];
	private int index;

	public T()
	{
		buffer = new short[mainMemorySize];
		index = 0;
	}

	// add word to buffer
	public void add(short x)
	{
		buffer[index] = x;
		index++;
	}

	// get word
	public short getWord(int i)
	{
		return buffer[i];
	}

	// Return size so far.
	public int size()
	{
		return index;
	}

	// change to word at index
	public void relocate(int index, short change)
	{
		buffer[index] = change;
	}

	// Writes out text buffer.
	public void write(DataOutputStream s) throws IOException
	{
		s.writeByte(84);
		for (int i = 0; i < index; i++)
		{
			s.writeShort(linv1.reverseOrder(buffer[i]));
		}
	}
}
