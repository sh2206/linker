import java.io.*;

class E // class for E table
{
	private String symbol[];
	private short address[];
	private int index;
	private static final int maxSize = 5;

	public E()
	{
		symbol = new String[maxSize];
		address = new short[maxSize];
		index = 0;
	}

	// finds symbol in table and returns its index
	public int search(String s)
	{
		int result = 0;
		for (int i = 0; i < index; i++)
		{
			if (s.equals(symbol[i]))
			{
				result = i;
			}
		}
		return result;
	}

	// returns address of entry at index i
	public short getAddress(int i)
	{
		return address[i];
	}

	// returns symbol of entry at index i
	public String getSymbol(int i)
	{
		return symbol[i];
	}

	// returns current size of table
	public int size()
	{
		return index;
	}

	// enters new symbol and address
	public void enter(short add, String sym)
	{
		symbol[index] = sym;
		address[index] = add;
		index++;
	}

	// Write E entries out as R entries.
	public void write(DataOutputStream s) throws IOException
	{
		for (int i = 0; i < index; i++)
		{
			s.writeByte(82);
			s.writeShort(linv1.reverseOrder(address[i]));
		}
	}
}