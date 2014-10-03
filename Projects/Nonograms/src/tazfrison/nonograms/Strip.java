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
	final public static int EMPTY = 0;
	final public static int FILLED = 1;
	final public static int MARKED = 2;

	// Whether this Strip is already in the process queue.
	private boolean bIsQueued;
	// The cells in this Strip.
	private Cell cells[];
	// Cells that have been updated in perpendicular Strips.
	private ArrayList<Integer> indices;
	// Groups of filled cells for this Strip.
	private Run[] runs;
	// Perpendicular Strips.
	private Strip[] crosses;
	// The global process queue.
	private LinkedList<Strip> queue;
	// Substrips in this Strip.
	private LinkedList<Substrip> substrips;
	// The global index of this Strip.
	private int index;
	// Whether initial() has been run for this Strip.
	private boolean bInitialized;
	// Length of Strip.
	public int length;

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
		this.bInitialized = false;
		this.crosses = crosses;
		this.queue = queue;
		this.index = index;
		this.length = cells.length;
		this.indices = new ArrayList<Integer>();
		this.substrips = new LinkedList<Substrip>();

		this.runs = new Run[runs.length];
		
		int counter = 0;
		for ( int i = 0; i < runs.length; ++i )
		{
			this.runs[i] = new Run( runs[i], this );
			this.runs[i].first = counter;
			counter += runs[i] + 1;
		}
		
		counter = this.length;
		for ( int i = runs.length - 1; i > 0; --i )
		{
			this.runs[i].last = counter;
			counter -= runs[i] - 1;
		}
		

		this.cells = new Cell[cells.length];
		for ( int i = 0; i < cells.length; ++i )
		{
			this.cells[i] = new Cell( cells[i], null );
		}

		Substrip initial = new Substrip( this.cells, this, 0 );

		initial.lowerRun = 0;
		initial.upperRun = this.runs.length - 1;

		this.substrips.add( initial );
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
		int sum = this.runs.length - 1, index = 0;

		for ( Run i : this.runs )
		{
			sum += i.length;
		}

		int diff = this.cells.length - sum;

		System.out.println( "Initializing: " + this.index + ", diff: " + diff );

		for ( Run i : this.runs )
		{
			if ( i.length > diff )
			{
				for ( int j = index + diff; j < index + i.length; ++j )
				{
					this.mark( j, FILLED );
				}
				if ( sum == cells.length && this.cells.length > i.length + index )
				{
					this.mark( i.length + index, MARKED );
				}
				index += i.length + 1;
			}
		}

		this.bInitialized = true;
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

		if ( !this.bInitialized )
		{
			this.initial();
		}

		// TODO: Streamline contiguous updates
		for ( int update : this.indices )
		{
			if ( this.cells[update].get() == Strip.MARKED )
			{
				this.cells[update].substrip.reduce( update );
			} else if ( this.cells[update].get() == Strip.FILLED )
			{

			}
		}

		int edge = 0;
		int nextRun = 0;

		for ( int i = 0; i < this.cells.length && nextRun < this.runs.length; ++i )
		{
			if ( this.cells[i].get() == FILLED )
			{
				if ( edge < this.runs[nextRun].length )
				{
					for ( int j = edge; j < this.runs[nextRun].length
							&& i < this.cells.length; ++j )
					{
						this.mark( i, FILLED );
						++i;
					}
					if ( edge == 0 && i < this.cells.length )
					{
						this.mark( i, MARKED );
					}
					++nextRun;
				}
			} else if ( this.cells[i].get() == EMPTY )
			{
				++edge;
			} else if ( this.cells[i].get() == MARKED )
			{
				if ( edge < this.runs[nextRun].length )
				{
					while ( edge > 0 )
					{
						this.mark( i - edge--, MARKED );
					}
				}
				edge = 0;
			}
		}

		edge = 0;
		nextRun = this.runs.length - 1;

		for ( int i = this.cells.length - 1; i > 0 && nextRun > 0; --i )
		{
			if ( this.cells[i].get() == FILLED )
			{
				if ( edge < this.runs[nextRun].length )
				{
					for ( int j = edge; j < this.runs[nextRun].length && i > 0; ++j )
					{
						this.mark( i, FILLED );
						--i;
					}
					if ( edge == 0 && i > 0 )
					{
						this.mark( i, MARKED );
					}
					--nextRun;
				}
			} else if ( this.cells[i].get() == EMPTY )
			{
				++edge;
			} else if ( this.cells[i].get() == MARKED )
			{
				if ( edge < this.runs[nextRun].length )
				{
					while ( edge > 0 )
					{
						this.mark( i + edge, MARKED );
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

	public void mark ( int index, int value ) throws Exception
	{
		if ( this.cells[index].get() == EMPTY )
		{
			this.cells[index].set( value );
			this.crosses[index].queue( this.index );
		} else if ( this.cells[index].get() != value )
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
