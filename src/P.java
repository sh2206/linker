import java.io.*;

class P // class for P table
{
	private String symbol[];
	private short address[];
	private int index;
	private static final int maxSize = 5;

	// Constructor initializes symbol and address tables to 5 and index to 0
	public P()
	{
		symbol = new String[maxSize];
		address = new short[maxSize];
		index = 0;
	}

	// Looks for duplicate symbols.
	public void dupSearch(String s)
	{
		int nummatches = 0;
		for (int i = 0; i < index; i++)
		{
			if (s.equals(symbol[i]))
			{
				nummatches++;
			}
		}
		if (nummatches >= 1)
		{
			System.out.println("ERROR: Duplicate PUBLIC symbol " + s + ".");
			System.exit(1);
		}
	}

	// finds symbol in table and returns its index
	public int search(String s)
	{
		// Unresolved external reference error.
		int result = -1;
		for (int i = 0; i < index; i++)
		{
			if (s.equals(symbol[i]))
			{
				result = i;
			}
		}
		if (result == -1)
		{
			System.out.println("ERROR: Unresolved external symbol " + s + ".");
			System.exit(1);
		}
		return result;
	}

	// return address at index i
	public short getAddress(int i)
	{
		return address[i];
	}

	// return symbol at index i
	public String getSymbol(int i)
	{
		return symbol[i];
	}

	// return size of table so far
	public int size()
	{
		return index;
	}

	public void enter(short add, String sym)
	{
		symbol[index] = sym;
		address[index] = add;
		index++;
	}

	// Writes out P entries: P, address, null-terminated string.
	public void write(DataOutputStream s) throws IOException
	{
		for (int i = 0; i < index; i++)
		{
			s.writeByte(80);
			s.writeShort(linv1.reverseOrder(address[i]));
			byte[] array = symbol[i].getBytes();
			for (int k = 0; k < array.length; k++)
			{
				s.writeByte(array[k]);
			}
			s.writeByte(0);
		}
	}
}
