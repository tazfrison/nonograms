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

	public boolean process ()
	{
		int sum = this.runs.length - 1, index = 0;
		
		for( int i : this.runs )
		{
			sum += i;
		}
		
		int diff = this.cells.length - sum;

		System.out.println( "Processing: " + this.index + ", diff: " + diff );

		for ( int i : this.runs )
		{
			if ( i > diff )
			{
				for ( int j = index + diff; j < index + i; ++j )
				{
					if ( this.cells[j].get() != FILLED )
					{
						this.cells[j].set( FILLED );
						this.crosses[j].queue( this.index );
						System.out.println( "Filled: " + this.index + " " + j );
					}
				}
				if ( sum == cells.length && this.cells.length > i + index )
				{
					if ( this.cells[i + index].get() != MARKED)
					{
						this.cells[i + index].set( MARKED );
						this.crosses[i + index].queue( this.index );
						System.out.println( "Marked: " + this.index + " " + ( i + index ) );
					}
				}
				index += i + 1;
			}
		}
		this.bIsQueued = false;
		return true;
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
