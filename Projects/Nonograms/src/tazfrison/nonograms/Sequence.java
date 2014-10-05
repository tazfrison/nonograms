package tazfrison.nonograms;

import java.util.ArrayList;

/**
 * A group of one or more cells that are filled.
 * 
 * @author Thomas
 * 
 */
public class Sequence
{
	private Cell cells[];
	public int start;
	public int end;
	public ArrayList<Run> runs;

	/**
	 * Constructor for Sequence.
	 * 
	 * @param start
	 *          Strip index the Sequence starts at.
	 * @param cells
	 *          Cells in this Sequence.
	 */
	public Sequence ( int start, Cell cells[] )
	{
		this.start = start;
		this.cells = cells;
		this.end = this.start + this.cells.length - 1;
	}

	static public Sequence sequenceForCell ( int index, Cell[] cells )
	{
		// Check edge conditions
		if ( index == 0 )
		{

		}
		else if ( index == cells.length - 1 )
		{

		}
		//Joins two Sequences
		else if ( cells[index - 1].sequence != null
				&& cells[index + 1].sequence != null )
		{

		}
		//Extends a Sequence
		else if ( cells[index - 1].sequence != null )
		{
			
		}
		else if ( cells[index + 1].sequence != null )
		{
			
		}
		//Starts new Sequence
		else
		{
			
		}
		return null;
	}
}