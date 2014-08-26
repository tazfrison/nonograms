package tazfrison.nonograms;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class Strip
{
	final public static int EMPTY = 0;
	final public static int FILLED = 1;
	final public static int MARKED = 2;

	private boolean bIsQueued;
	private AtomicInteger cells[];
	private ArrayList<Integer> indices;
	private Integer[] runs;
	private Strip[] crosses;
	private LinkedList<Strip> queue;
	private int index;

	public Strip ( AtomicInteger[] cells, Strip[] crosses, Integer[] runs,
			LinkedList<Strip> queue, int index )
	{
		this.cells = cells;
		this.runs = runs;
		this.bIsQueued = false;
		this.crosses = crosses;
		this.queue = queue;
		this.index = index;
		this.indices = new ArrayList<Integer>();
	}

	public boolean initial () throws Exception
	{
		int sum = this.runs.length - 1, index = 0;

		for ( int i : this.runs )
		{
			sum += i;
		}

		int diff = this.cells.length - sum;

		System.out.println( "Initializing: " + this.index + ", diff: " + diff );

		for ( int i : this.runs )
		{
			if ( i > diff )
			{
				for ( int j = index + diff; j < index + i; ++j )
				{
					this.mark( j, FILLED );
				}
				if ( sum == cells.length && this.cells.length > i + index )
				{
					this.mark( i + index, MARKED );
				}
				index += i + 1;
			}
		}
		return true;
	}

	public boolean process () throws Exception
	{
		System.out.println( "Processing: " + this.index );
		int edge = 0;
		int nextRun = 0;
		for ( int i = 0; i < cells.length; ++i )
		{
			if ( cells[i].get() == FILLED )
			{
				if ( edge < this.runs[nextRun] )
				{
					for ( int j = edge; j < this.runs[nextRun] && i < this.cells.length; ++j )
					{
						this.mark( i, FILLED );
						++i;
					}
					if ( edge == 0 && i < this.cells.length  )
					{
						this.mark( i, MARKED );
					}
					++nextRun;
				}
			} else if ( cells[i].get() == EMPTY )
			{
				++edge;
			} else if ( cells[i].get() == MARKED )
			{
				if ( edge < this.runs[nextRun] )
				{
					while ( edge > 0 )
					{
						this.mark( i - edge--, MARKED );
					}
				}
				edge = 0;
			}
		}
		this.bIsQueued = false;
		return true;
	}

	public void finish ()
	{
		try
		{
			for ( int i = 0; i < this.cells.length; ++i )
			{
				if ( this.cells[i].get() == EMPTY )
				{
					this.mark( i, MARKED );
				}
			}
		} catch ( Exception ex )
		{
			System.out.println( ex.getMessage() + " in finish" );
			// Should not be possible
		}
	}

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

	public boolean queue ( int index )
	{
		indices.add( index );
		if ( !bIsQueued )
		{
			this.queue.add( this );
			bIsQueued = true;
		}
		return bIsQueued;
	}
}
