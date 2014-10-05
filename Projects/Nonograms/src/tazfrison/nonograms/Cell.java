package tazfrison.nonograms;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Data about a cell and what it belongs to. There will be two cell objects for
 * each cell location, one for column and one for row, that share the same
 * AtomicInteger so they stay in sync.
 * 
 * @author Thomas
 * 
 */
public class Cell
{
	final public static int EMPTY = 0;
	final public static int FILLED = 1;
	final public static int MARKED = 2;

	private AtomicInteger value;
	public Substrip substrip;
	public Sequence sequence;

	/**
	 * Constructor.
	 * 
	 * @param value
	 *          Reference to the original item in the array.
	 * 
	 * @param parent
	 *          The Substrip this cell is currently in.
	 */
	public Cell ( AtomicInteger value, Substrip substrip )
	{
		this.value = value;
		this.substrip = substrip;
	}

	/**
	 * Basic AtomicInteger.get wrapper.
	 * 
	 * @return Int representing current cell state.
	 */
	public int get ()
	{
		return this.value.get();
	}

	/**
	 * Basic AtomicInteger.set wrapper.
	 * 
	 * @param value
	 *          New value for the cell.
	 */
	public void set ( int value )
	{
		this.value.set( value );
	}
}
