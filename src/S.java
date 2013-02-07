import java.io.*;

class S // class for S/s table
{
	private char symbol[];
	private short address[];
	private int index;
	private static final int maxSize = 1;

	// Constructor initializes symbol and address tables to 1 and index to 0
	public S()
	{
		symbol = new char[maxSize];
		address = new short[maxSize];
		index = 0;
	}

	// return address at index i
	public short getAddress(int i)
	{
		return address[i];
	}

	// return symbol at index i
	public char getSymbol(int i)
	{
		return symbol[i];
	}

	// return size of table so far
	public int size()
	{
		return index;
	}

	public void enter(short add, char sym)
	{
		symbol[index] = sym;
		address[index] = add;
		index++;
	}

	// Writes out the s or S entry.
	public void write(DataOutputStream s) throws IOException
	{
		if (symbol[0] == 115)
			s.writeByte(115);
		else if (symbol[0] == 83)
			s.writeByte(83);
		s.writeShort(linv1.reverseOrder(address[0]));
	}
}
