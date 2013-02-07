import java.io.*;

public class linv1
{
	// ReverseOrder function.
	public static short reverseOrder(short x)
	{
		int y = ((int) x) & 0xffff;
		return (short) (256 * (y % 256) + y / 256);
	}

	public static void main(String[] args) throws IOException
	{
		System.out.println("linv1 program.");

		// Must have at least one command line argument.
		if (args.length < 1)
		{
			System.out.println("ERROR: Incorrect number of command line arguments.");
			System.exit(1);
		}

		// Create new tables for P, E, R, S, T.
		P ptable = new P();
		E etable = new E();
		R rtable = new R();
		S stable = new S();
		T ttable = new T();

		// Master module address initialized
		short module_address = 0;

		// For each file on the command line:
		for (int i = 0; i < args.length; i++)
		{
			// Using lastIndexOf, check for period. if period, check if next entry
			// is a backslash. if it is, has no extension. if not, has an extension.
			String filename = args[i];
			String newfilename = "";

			int lastindex = filename.lastIndexOf('.');
			// No extension found.
			if (lastindex == -1)
			{
				newfilename = filename + ".mob";
			}
			else
			{
				String next = filename.substring(lastindex + 1, lastindex + 2);
				// If next character after lastindex is a \:
				if (next.equals("\\"))
					newfilename = filename + ".mob";
				else
					newfilename = filename;
			}
			// Open .mob file as binary file.
			try
			{
				DataInputStream inStream = new DataInputStream(new FileInputStream(newfilename));
				byte b = 0;

				// Read until end of file, place entries in appropriate tables.
				try
				{
					while (true)
					{
						// Read a single byte.
						b = inStream.readByte();

						// If b is a T value:
						if (b == 84)
						{
							try
							{
								while (true)
								{
									short x = reverseOrder(inStream.readShort());
									try
									{
										ttable.add(x);
									}
									catch (ArrayIndexOutOfBoundsException e)
									{
										System.out.println("ERROR: Linked program too big.");
										System.exit(1);
									}
								}
							}
							catch (EOFException e)
							{
							}
							module_address = (short) ttable.size();
						}
						// If b is a P value:
						else if (b == 80)
						{
							short addy = reverseOrder(inStream.readShort());
							String symbol = "";
							while (true)
							{
								b = inStream.readByte();
								if (b == 0)
									break;
								else
									symbol += (char) b;
							}

							// Call ptable.dupSearch to terminate if duplicate
							// public symbols.
							ptable.dupSearch(symbol);
							try
							{
								ptable.enter((short) (addy + module_address), symbol);
							}
							catch (ArrayIndexOutOfBoundsException e)
							{
								System.out.println("ERROR: P Table Overflow.");
								System.exit(1);
							}
						}
						// If b is an E value:
						else if (b == 69)
						{
							short addy = reverseOrder(inStream.readShort());
							String symbol = "";
							while (true)
							{
								b = inStream.readByte();
								if (b == 0)
									break;
								else
									symbol += (char) b;
							}
							try
							{
								etable.enter((short) (addy + module_address), symbol);
							}
							catch (ArrayIndexOutOfBoundsException e)
							{
								System.out.println("ERROR: E Table Overflow.");
								System.exit(1);
							}
						}
						// If b is an R value:
						else if (b == 82)
						{
							short addy = reverseOrder(inStream.readShort());
							try
							{
								rtable.enter((short) (addy + module_address), module_address);
							}
							catch (ArrayIndexOutOfBoundsException e)
							{
								System.out.println("ERROR: R Table Overflow.");
								System.exit(1);
							}
						}
						// If b is a small-S:
						else if (b == 115)
						{
							try
							{
								char s = (char) b;
								short addy = reverseOrder(inStream.readShort());
								stable.enter((short) (addy + module_address), s);
							}
							catch (ArrayIndexOutOfBoundsException e)
							{
								System.out.println("ERROR: More than one starting address.");
								System.exit(1);
							}
						}
						// If b is a big-S:
						else if (b == 83)
						{
							try
							{
								char S = (char) b;
								short addy = reverseOrder(inStream.readShort());
								stable.enter(addy, S);
							}
							catch (ArrayIndexOutOfBoundsException e)
							{
								System.out.println("ERROR: More than one starting address.");
								System.exit(1);
							}
						}

						else
						{
							System.out.println("ERROR: Input file "
									+ newfilename + " is not linkable.");
							System.exit(1);
						}
					}
				}
				catch (EOFException e)
				{
				}
			}
			catch (IOException e)
			{
				System.out.println("ERROR: Cannot open input file " + filename);
				System.exit(1);
			}
		}

		// E Table Processing
		for (int i = 0; i < etable.size(); i++)
		{
			// Get address of this E entry (location of word in T table to be
			// adjusted)
			short eaddress = etable.getAddress(i);

			// Get the entry in the T table at that address.
			short textcontents = ttable.getWord(eaddress);

			// Find index in P table where that symbol is stored.
			int pindex = ptable.search(etable.getSymbol(i));

			// Retrieve the address from the P table at that index.
			short paddress = ptable.getAddress(pindex);

			// Zero right 12 bits of text buffer contents, save for later.
			short savedTextContents = (short) (textcontents & 0xF000);

			// Zero leftmost 4 bits of text buffer contents.
			textcontents = (short) (textcontents & 0x0FFF);

			// Adjust text contents with address from P Table.
			short finalResult = (short) (paddress + textcontents);

			// Discard any overflow in leftmost 4 bits.
			finalResult = (short) (finalResult & 0x0FFF);

			// Bitwise OR with saved text buffer contents to restore leftmost 4
			// bits.
			finalResult = (short) (finalResult | savedTextContents);

			// Store finalResult back into text buffer.
			ttable.relocate(eaddress, finalResult);
		}

		// R Table Processing
		for (int i = 0; i < rtable.size(); i++)
		{
			// Get Module Address of this entry
			short modAddress = rtable.getModAddress(i);

			// Get address of this entry (which is a location in T table)
			short location = rtable.getAddress(i);

			// Get word stored in the T table at that location
			short addrToAdjust = ttable.getWord(location);

			// Zero rightmost 12 bits of word to be adjusted, save for later.
			short savedOr = (short) (addrToAdjust & 0xF000);

			// Zero the leftmost 4 bits of word to be adjusted.
			short result = (short) (addrToAdjust & 0x0FFF);

			// Add this to the module address.
			short adjustedResult = (short) (result + modAddress);

			// Clear out any overflow in leftmost 4 bits.
			adjustedResult = (short) (adjustedResult & 0x0FFF);

			// Bitwise OR adjusted result with saved value to restore opcode.
			short finalResult = (short) (adjustedResult | savedOr);

			// Store finalResult back into text buffer.
			ttable.relocate(location, finalResult);
		}

		// Open final output file as a binary file
		// Get first command line argument to make output file name.
		String outfilename = args[0];
		if (outfilename.contains(".mob"))
		{
			outfilename = outfilename.replace(".mob", ".mac");
		}
		else
		{
			outfilename = outfilename + ".mac";
		}

		try
		{
			DataOutputStream outStream = new DataOutputStream(new FileOutputStream(outfilename));

			ptable.write(outStream);
			rtable.write(outStream);
			etable.write(outStream);
			stable.write(outStream);
			ttable.write(outStream);

			outStream.close();
		}
		catch (IOException e)
		{
			System.out.println("ERROR: cannot open output file.");
			System.exit(1);
		}
	}
}