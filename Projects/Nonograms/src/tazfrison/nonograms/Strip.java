package tazfrison.nonograms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class Strip
{
	private class Cell
	{
		private AtomicInteger value;
		public Substrip parent;

		public Cell ( AtomicInteger value, Substrip parent )
		{
			this.value = value;
			this.parent = parent;
		}

		public int get ()
		{
			return this.value.get();
		}

		public void set ( int value )
		{
			this.value.set( value );
		}
	}

	private class Substrip
	{
		private Cell cells[];
		private Strip parent;

		// Strip indices this Substrip covers;
		public int start;
		public int end;

		// Range of runs contained in this Substrip when shifted to low end
		private int firstfirst;
		private int lastfirst;

		// Range of runs contained in this Substrip when shifted to high end
		private int firstlast;
		private int lastlast;

		public Substrip next;
		public Substrip previous;

		public Substrip ( Cell cells[], Strip parent, int start )
		{
			this.cells = cells;
			for ( Cell cell : this.cells )
			{
				cell.parent = this;
			}
			this.parent = parent;
			this.start = start;
			this.end = start + cells.length - 1;
		}
		
		public void setFirstRange ( int start, int end )
		{
			this.firstfirst = start;
			this.lastfirst = end;
		}
		
		public void setLastRange ( int start, int end )
		{
			this.firstlast = start;
			this.lastlast = end;
		}

		public void reduce ( int index )
		{
			if ( this.start + 1 == this.end )
			{
				// Remove Substrip
				this.cells = null;
				this.next.previous = this.previous;
				this.previous.next = this.next;
			} else if ( index == this.start )
			{
				// Trim beginning
				this.cells = Arrays.copyOfRange( this.cells, 1, this.cells.length );
				++this.start;
			} else if ( index == this.end )
			{
				// Trim end
				this.cells = Arrays.copyOfRange( this.cells, 0, this.cells.length - 1 );
				--this.end;
			} else
			{
				// Create new Substrip after split point
				Substrip child = new Substrip( Arrays.copyOfRange( this.cells, 0, index
						- this.start ), parent, this.start );
				child.previous = this;
				child.next = this.next;
				if ( this.next != null )
					this.next.previous = child;

				this.cells = Arrays.copyOfRange( this.cells, index - this.start,
						this.cells.length );
				this.next = child;
				this.end = index - 1;
			}
		}
	}

	final public static int EMPTY = 0;
	final public static int FILLED = 1;
	final public static int MARKED = 2;

	// Whether this Strip is already in the process queue
	private boolean bIsQueued;
	// The cells in this Strip
	private Cell cells[];
	// Cells that have been updated in perpendicular Strips
	private ArrayList<Integer> indices;
	// Groups of filled cells for this Strip
	private Integer[] runs;
	// Perpendicular Strips
	private Strip[] crosses;
	// The global process queue
	private LinkedList<Strip> queue;
	// Substrips in this Strip
	private LinkedList<Substrip> substrips;
	// The global index of this Strip
	private int index;
	// Whether initial() has been run for this Strip
	private boolean bInitialized;

	public Strip ( AtomicInteger[] cells, Strip[] crosses, Integer[] runs,
			LinkedList<Strip> queue, int index )
	{
		this.runs = runs;
		this.bIsQueued = false;
		this.bInitialized = false;
		this.crosses = crosses;
		this.queue = queue;
		this.index = index;
		this.indices = new ArrayList<Integer>();
		this.substrips = new LinkedList<Substrip>();

		this.cells = new Cell[cells.length];
		for ( int i = 0; i < cells.length; ++i )
		{
			this.cells[i] = new Cell( cells[i], null );
		}

		Substrip initial = new Substrip( this.cells, this, 0 );
		
		initial.setFirstRange( 0, this.runs.length - 1 );
		initial.setLastRange( this.runs.length - 1, 0 );

		this.substrips.add( initial );
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

		this.bInitialized = true;
		return true;
	}

	public boolean process () throws Exception
	{
		System.out.println( "Processing: " + this.index );

		if ( !this.bInitialized )
		{
			this.initial();
		}
/*		
		//TODO: Streamline contiguous updates
		for( int update : this.indices )
		{
			if ( this.cells[ update ].get() == this.MARKED )
			{
				//this.cells[ update ].parent.reduce( update );
			}
		}*/

		int edge = 0;
		int nextRun = 0;

		for ( int i = 0; i < this.cells.length && nextRun < this.runs.length; ++i )
		{
			if ( this.cells[i].get() == FILLED )
			{
				if ( edge < this.runs[nextRun] )
				{
					for ( int j = edge; j < this.runs[nextRun] && i < this.cells.length; ++j )
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

		edge = 0;
		nextRun = this.runs.length - 1;

		for ( int i = this.cells.length - 1; i > 0 && nextRun > 0; --i )
		{
			if ( this.cells[i].get() == FILLED )
			{
				if ( edge < this.runs[nextRun] )
				{
					for ( int j = edge; j < this.runs[nextRun] && i > 0; ++j )
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
				if ( edge < this.runs[nextRun] )
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
