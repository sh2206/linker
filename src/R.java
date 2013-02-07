import java.io.*;

class R // class for R table
{
	private short address[];
	private short modaddress[];
	private int index;
	private static final int maxSize = 5;

	public R()
	{
		address = new short[maxSize];
		modaddress = new short[maxSize];
		index = 0;
	}

	// returns address of entry at index i
	public short getAddress(int i)
	{
		return address[i];
	}

	// returns module address of entry at index i
	public short getModAddress(int i)
	{
		return modaddress[i];
	}

	// returns current size of table
	public int size()
	{
		return index;
	}

	// enters new relocatable address and corresponding module address
	public void enter(short add, short modadd)
	{
		modaddress[index] = modadd;
		address[index] = add;
		index++;
	}

	// Writes out R entries.
	public void write(DataOutputStream s) throws IOException
	{
		for (int i = 0; i < index; i++)
		{
			s.writeByte(82);
			s.writeShort(linv1.reverseOrder(address[i]));
		}
	}
}