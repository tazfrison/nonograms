package tazfrison.nonograms;

import java.util.ArrayList;
import java.util.LinkedList;

public class Strip
{
	final public static int EMPTY = 0;
	final public static int FILLED = 1;
	final public static int MARKED = 2;

	private boolean bIsQueued;
	private Integer cells[];
	private ArrayList<Integer> indices;
	private Integer[] runs;
	private Strip[] crosses;
	private LinkedList<Strip> queue;
	private int index;

	public Strip ( Integer[] cells, Strip[] crosses, Integer[] runs,
			LinkedList<Strip> queue, int index )
	{
		this.cells = cells;
		this.runs = runs;
		bIsQueued = false;
		this.crosses = crosses;
		this.queue = queue;
		this.index = index;
		this.indices = new ArrayList<Integer>();
	}

	public boolean process ()
	{
		int sum = runs.length, index = 0;

		for ( int i : runs )
			sum += i;

		for ( int i : runs )
		{
			if ( i > (cells.length - sum) )
			{
				for ( int j = sum; j < i; ++j )
				{
					cells[j + index] = FILLED;
				}
				if ( sum == cells.length )
				{
					cells[i + index] = MARKED;
					this.crosses[i + index].queue( this.index );
				}
				index += i + 1;
			}
		}
		bIsQueued = false;
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
