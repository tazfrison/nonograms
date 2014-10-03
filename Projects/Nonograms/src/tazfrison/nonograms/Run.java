package tazfrison.nonograms;

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
	public void overlap () throws Exception
	{
		int overlap = this.last - this.first + 1 - this.length;
		if ( overlap > 0 )
		{
			for ( int i = 0; i < overlap; ++i )
			{
				this.parent.mark( i + this.last - this.length, Strip.FILLED );
			}
			if ( overlap == this.length )
			{
				if ( this.first > 0 )
				{
					this.parent.mark( this.first - 1, Strip.MARKED );
					// TODO: Split Substrip
				}
				if ( this.last < this.parent.length )
				{
					this.parent.mark( this.last, Strip.MARKED );
					// TODO: Split Substrip
				}
			}
		}
	}
}