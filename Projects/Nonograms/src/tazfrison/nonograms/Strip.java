package tazfrison.nonograms;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A row or column.
 * 
 * @author Thomas
 * 
 */
public class Strip
{

	// Whether this Strip is already in the process queue.
	private boolean bIsQueued;
	// Cells that have been updated in perpendicular Strips.
	private ArrayList<Integer> indices;
	// Groups of filled cells for this Strip.
	private Run[] runs;
	private Run[] runsSorted;

	// Perpendicular Strips.
	private Strip[] crosses;
	// The global process queue.
	private LinkedList<Strip> queue;
	// Substrips in this Strip.
	private LinkedList<Substrip> substrips;
	// The global index of this Strip.
	private int index;
	// Length of Strip.
	public int length;
	// The cells in this Strip.
	public Cell cells[];

	/**
	 * Constructor for Strip.
	 * 
	 * @param cells
	 *          The array representing this row or column in the Nonograms array.
	 * @param crosses
	 *          The Strips that intersect this Strip.
	 * @param runs
	 *          What runs this Strip must include.
	 * @param queue
	 *          Global queue for which Strips have been updated and need to be
	 *          rechecked.
	 * @param index
	 *          The row or column number for this Strip.
	 */
	public Strip ( AtomicInteger[] cells, Strip[] crosses, Integer[] runs,
			LinkedList<Strip> queue, int index )
	{
		this.bIsQueued = false;
		this.crosses = crosses;
		this.queue = queue;
		this.index = index;
		this.length = cells.length;
		this.indices = new ArrayList<Integer>();
		this.substrips = new LinkedList<Substrip>();

		this.runs = new Run[runs.length];
		this.runsSorted = new Run[runs.length];

		int counter = 0;
		for ( int i = 0; i < runs.length; ++i )
		{
			this.runs[i] = new Run( runs[i], this );
			this.runs[i].first = counter;
			counter += runs[i] + 1;
		}

		counter = this.length;
		for ( int i = runs.length - 1; i >= 0; --i )
		{
			this.runs[i].last = counter;
			counter -= (runs[i] + 1);
		}

		this.cells = new Cell[cells.length];
		for ( int i = 0; i < cells.length; ++i )
		{
			this.cells[i] = new Cell( cells[i], null );
		}

		Substrip initialStrip = new Substrip( this.cells, this, 0 );

		initialStrip.lowerRun = 0;
		initialStrip.upperRun = this.runs.length - 1;

		this.substrips.add( initialStrip );

		try
		{
			this.initial();
		} catch ( Exception e )
		{

		}
	}

	// TODO: Revisit this process. Change to calculating based on start/end for
	// each run.
	/**
	 * Checks for any initial run overlap.
	 * 
	 * @throws Exception
	 *           Passes exceptions from Strip.mark().
	 */
	public void initial () throws Exception
	{
		System.out.println( "Initializing: " + this.index );

		for ( Run i : this.runs )
		{
			i.overlap();
		}
	}

	/**
	 * Main logic function for Strip.
	 * 
	 * @throws Exception
	 *           Passes exception from Strip.mark().
	 */
	public void process () throws Exception
	{
		System.out.println( "Processing: " + this.index );

		// TODO: Streamline contiguous updates
		// TODO: Iterate in a way that allows me to add to list while iterating
		for ( int update : this.indices )
		{
			if ( this.cells[update].get() == Cell.MARKED )
			{
				if ( this.cells[update].substrip != null )
					this.cells[update].substrip.reduce( update );
			}
			else if ( this.cells[update].get() == Cell.FILLED )
			{

			}
		}

		int edge = 0;
		int nextRun = 0;

		for ( int i = 0; i < this.cells.length && nextRun < this.runs.length; ++i )
		{
			if ( this.cells[i].get() == Cell.FILLED )
			{
				if ( edge < this.runs[nextRun].length )
				{
					for ( int j = edge; j < this.runs[nextRun].length
							&& i < this.cells.length; ++j )
					{
						this.fill( i );
						++i;
					}
					if ( edge == 0 && i < this.cells.length )
					{
						this.mark( i );
					}
					++nextRun;
				}
			}
			else if ( this.cells[i].get() == Cell.EMPTY )
			{
				++edge;
			}
			else if ( this.cells[i].get() == Cell.MARKED )
			{
				if ( edge < this.runs[nextRun].length )
				{
					while ( edge > 0 )
					{
						this.mark( i - edge-- );
					}
				}
				edge = 0;
			}
		}

		edge = 0;
		nextRun = this.runs.length - 1;

		for ( int i = this.cells.length - 1; i > 0 && nextRun > 0; --i )
		{
			if ( this.cells[i].get() == Cell.FILLED )
			{
				if ( edge < this.runs[nextRun].length )
				{
					for ( int j = edge; j < this.runs[nextRun].length && i > 0; ++j )
					{
						this.fill( i );
						--i;
					}
					if ( edge == 0 && i > 0 )
					{
						this.mark( i );
					}
					--nextRun;
				}
			}
			else if ( this.cells[i].get() == Cell.EMPTY )
			{
				++edge;
			}
			else if ( this.cells[i].get() == Cell.MARKED )
			{
				if ( edge < this.runs[nextRun].length )
				{
					while ( edge > 0 )
					{
						this.mark( i + edge );
						--edge;
					}
				}
				edge = 0;
			}
		}

		this.bIsQueued = false;
	}

	/**
	 * Tells each Substrip to mark each empty cell.
	 */
	public void finish ()
	{
		for ( Substrip sub : this.substrips )
		{
			sub.finish();
		}
	}

	/**
	 * Sets the value of a cell.
	 * 
	 * @param index
	 *          The index of the cell to update.
	 * @param value
	 *          The value to update the cell to.
	 * @throws Exception
	 *           If trying to MARK a FILLed cell or vice versa.
	 */

	public void mark ( int index ) throws Exception
	{
		if ( this.cells[index].get() == Cell.EMPTY )
		{
			this.cells[index].set( Cell.MARKED );
			this.crosses[index].queue( this.index );
			if ( this.cells[index].substrip != null )
				this.cells[index].substrip.reduce( index );
		}
		else if ( this.cells[index].get() == Cell.FILLED )
		{
			throw new Exception( "Contradiction" );
		}
	}

	/**
	 * Sets the value of a cell.
	 * 
	 * @param index
	 *          The index of the cell to update.
	 * @param value
	 *          The value to update the cell to.
	 * @throws Exception
	 *           If trying to MARK a FILLed cell or vice versa.
	 */

	public void fill ( int index ) throws Exception
	{
		if ( this.cells[index].get() == Cell.EMPTY )
		{
			this.cells[index].set( Cell.FILLED );
			this.crosses[index].queue( this.index );
		}
		else if ( this.cells[index].get() == Cell.MARKED )
		{
			throw new Exception( "Contradiction" );
		}
	}

	/**
	 * Adds this Strip to the process queue if not queued. Marks what index was
	 * updated to necessitate queuing.
	 * 
	 * @param index
	 *          What index was updated.
	 */
	public void queue ( int index )
	{
		indices.add( index );
		if ( !bIsQueued )
		{
			this.queue.add( this );
			bIsQueued = true;
		}
	}
}
