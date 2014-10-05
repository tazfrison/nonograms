package tazfrison.nonograms;

import java.util.Comparator;

/**
 * A continuous group of filled cells that must appear in this strip. Stores
 * state of a particular run, and locations it may appear.
 * 
 * @author Thomas
 * 
 */
public class Run
{
	private Strip parent;
	public Substrip strips;
	public boolean bFinished;
	public int first;
	public int last;
	public Integer length;

	public static class RunComp implements Comparator<Run>
	{
		public int compare ( Run arg0, Run arg1 )
		{
			return arg0.length - arg1.length;
		}
	}

	/**
	 * Constructor for Run.
	 * 
	 * @param length
	 *          The length of this Run.
	 */
	public Run ( int length, Strip parent )
	{
		this.length = length;
		this.parent = parent;
	}

	/**
	 * Calculates Cells shared by all possible instances of this Run.
	 * 
	 * @throws Exception
	 *           Passes Exceptions from Strip.mark().
	 */
	public boolean overlap () throws Exception
	{
		int overlap = 2 * this.length + this.first - this.last;
		if ( overlap > 0 )
		{
			for ( int i = 0; i < overlap; ++i )
			{
				this.parent.fill( i + this.last - this.length );
			}
			if ( overlap == this.length )
			{
				this.bFinished = true;
				if ( this.first > 0 )
				{
					this.parent.mark( this.first - 1 );
				}
				if ( this.last < this.parent.length )
				{
					this.parent.mark( this.last );
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Find new first or last index for the run.
	 * 
	 * @param index
	 *          Index to start adjusting at.
	 * @param first
	 *          Whether to adjust first or last index.
	 */
	public void shift ( int index, boolean first )
	{
		int direction = 1;
		if ( !first )
		{
			direction = -1;
		}

		// Check to see if the run will fit at index.
		for ( int i = index; i < index + direction * this.length; i += direction )
		{
			if ( this.parent.cells[i].get() == Cell.MARKED )
			{
				do
				{// Shift the index to end of MARKED area.
					i += direction;
					index = i;
				} while ( this.parent.cells[i].get() == Cell.MARKED );
			}
		}

		while ( this.parent.cells[index - direction].get() != Cell.FILLED
				&& this.parent.cells[index + direction * (this.length + 1)].get() != Cell.FILLED )
		{
			index += direction;
		}

		if ( first )
		{
			this.first = index;
		}
		else
		{
			this.last = index;
		}
	}
}