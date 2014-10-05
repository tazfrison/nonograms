package tazfrison.nonograms;

import java.util.Arrays;

/**
 * A group of cells delineated by marked cells. Used to narrow down the strip
 * into sections.
 * 
 * @author Thomas
 * 
 */
public class Substrip
{
	private Cell cells[];
	private Strip parent;

	// Strip indices this Substrip covers
	public int start;
	public int end;

	// Range of runs contained in this Substrip
	public int upperRun;
	public int lowerRun;

	public Substrip next;
	public Substrip previous;

	/**
	 * Constructor.
	 * 
	 * @param cells
	 *          The Cells belonging to this Substrip.
	 * @param parent
	 *          The row or column this Substrip is a part of.
	 * @param start
	 *          The Strip index this Substrip starts at.
	 */
	public Substrip ( Cell cells[], Strip parent, int start )
	{
		this.cells = cells;
		for ( Cell cell : this.cells )
		{
			cell.substrip = this;
		}
		this.parent = parent;
		this.start = start;
		this.end = start + cells.length - 1;
	}

	/**
	 * Called when a cell in this Substrip is MARKed. Either shrinks the Substrip
	 * or splits it.
	 * 
	 * @param index
	 *          The Strip index that was MARKed.
	 */
	public void reduce ( int index )
	{
		if ( this.start == this.end && this.start == index )
		{
			// Remove Substrip
			this.cells = null;
			this.next.previous = this.previous;
			this.previous.next = this.next;
		}
		else if ( index == this.start )
		{
			// Trim beginning
			this.cells = Arrays.copyOfRange( this.cells, 1, this.cells.length );
			++this.start;
		}
		else if ( index == this.end )
		{
			// Trim end
			this.cells = Arrays.copyOfRange( this.cells, 0, this.cells.length - 1 );
			--this.end;
		}
		else
		{
			// Create new Substrip after split point
			Substrip child = new Substrip( Arrays.copyOfRange( this.cells, index
					- this.start, this.cells.length ), parent, this.start );
			child.previous = this;
			child.next = this.next;
			if ( this.next != null )
				this.next.previous = child;

			this.cells = Arrays.copyOfRange( this.cells, 0, index - this.start );
			this.next = child;
			this.end = index - 1;
		}
	}

	/**
	 * MARK all empty Cells.
	 */
	public void finish ()
	{
		try
		{
			for ( int i = 0; i < this.cells.length; ++i )
			{
				if ( this.cells[i].get() == Cell.EMPTY )
				{
					this.parent.mark( i + this.start );
				}
			}
		} catch ( Exception ex )
		{
			System.out.println( ex.getMessage() + " in finish" );
			// Should not be possible
		}
	}
}
