package tazfrison.nonograms;

import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Nonograms extends JPanel
{
	private int height;
	private int width;

	public AtomicInteger board[][];
	public Strip rows[];
	public Strip columns[];
	public LinkedList<Strip> processQueue;

	public Integer rowRuns[][];
	public Integer columnRuns[][];

	private final int CELL_SIZE = 20;

	public Nonograms ()
	{
		super();
		this.readFile();

		this.board = new AtomicInteger[this.width][this.height];
		this.rows = new Strip[this.height];
		this.columns = new Strip[this.width];
		this.processQueue = new LinkedList<Strip>();
		AtomicInteger tempColumn[][] = new AtomicInteger[this.width][this.height];

		for ( int i = 0; i < this.width; ++i )
		{
			for ( int j = 0; j < this.height; ++j )
			{
				board[j][i] = new AtomicInteger( Strip.EMPTY );
				tempColumn[i][j] = board[j][i];
			}
		}
		
		for ( int i = 0; i < this.height; ++i )
		{
			rows[i] = new Strip( board[i], columns, this.rowRuns[i], processQueue, i );
		}
		
		for ( int i = 0; i < this.width; ++i )
		{
			columns[i] = new Strip( tempColumn[i], rows, this.columnRuns[i],
					processQueue, i );
		}

		setBackground( Color.WHITE );
	}

	public void paintComponent ( Graphics g )
	{
		int width = getWidth();
		int height = getHeight();

		super.paintComponent( g );

		g.setColor( Color.BLACK );
		g.clearRect( 0, 0, width, height );
		this.drawBoard( g );
	}

	public void drawBoard ( Graphics g )
	{
		g.drawRect( 0, 0, this.width * this.CELL_SIZE, this.height * this.CELL_SIZE );
		for ( int i = 0; i < this.width; ++i )
		{
			for ( int j = 0; j < this.height; ++j )
			{
				this.drawCell( g, i, j, this.board[i][j].get() );
			}
		}
		for ( int i = 0; i < this.width; ++i )
		{
			this.drawRuns( g, false, i, this.columnRuns[i] );
		}
		for ( int i = 0; i < this.height; ++i )
		{
			this.drawRuns( g, true, i, this.rowRuns[i] );
		}
	}

	public void drawRuns ( Graphics g, boolean isRow, int index, Integer[] runs )
	{
		int x = this.CELL_SIZE, y = this.CELL_SIZE;
		if ( isRow )
		{
			x *= this.width;
			y *= index + 1;
		} else
		{
			y *= this.height + 1;
			x *= index;
		}
		for ( int i = 0; i < runs.length; ++i )
		{
			g.drawString( runs[i].toString(), x, y );
			if ( isRow )
				x += this.CELL_SIZE;
			else
				y += this.CELL_SIZE;
		}
	}

	public void drawCell ( Graphics g, int row, int column, int value )
	{
		int x = column * this.CELL_SIZE, y = row * this.CELL_SIZE;
		g.drawRect( x, y, this.CELL_SIZE, this.CELL_SIZE );

		if ( value == Strip.MARKED )
		{
			g.drawLine( x + 2, y + 2, x + this.CELL_SIZE - 3, y + this.CELL_SIZE - 3 );
			g.drawLine( x + this.CELL_SIZE - 3, y + 2, x + 2, y + this.CELL_SIZE - 3 );
		} else if ( value == Strip.FILLED )
		{
			g.fillRect( x + 2, y + 2, this.CELL_SIZE - 3, this.CELL_SIZE - 3 );
		}
	}

	private void readFile ()
	{
		Scanner input = null, lineScanner = null;
		String line;
		ArrayList<Integer> runTemp = new ArrayList<Integer>();

		try
		{
			input = new Scanner( new BufferedReader( new FileReader(
					"src/tazfrison/nonograms/data/game1.txt" ) ) );
			this.height = input.nextInt();
			this.width = input.nextInt();

			this.rowRuns = new Integer[this.width][];
			this.columnRuns = new Integer[this.height][];
			input.nextLine();
			for ( int i = 0; i < this.height; ++i )
			{
				runTemp.clear();
				line = input.nextLine();
				lineScanner = new Scanner( line );
				while ( lineScanner.hasNextInt() )
				{
					runTemp.add( new Integer( lineScanner.nextInt() ) );
				}
				this.rowRuns[i] = new Integer[runTemp.size()];
				runTemp.toArray( this.rowRuns[i] );
			}
			for ( int i = 0; i < this.width; ++i )
			{
				runTemp.clear();
				line = input.nextLine();
				lineScanner = new Scanner( line );
				while ( lineScanner.hasNextInt() )
				{
					runTemp.add( new Integer( lineScanner.nextInt() ) );
				}
				this.columnRuns[i] = new Integer[runTemp.size()];
				runTemp.toArray( this.columnRuns[i] );
			}

			input.close();
		} catch ( Exception e )
		{
			return;
		}
	}

	public static void main ( String[] args )
	{
		JFrame frame = new JFrame( "Nonograms" );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

		// Create and set up the content pane.
		Nonograms nono = new Nonograms();
		nono.setOpaque( true ); // content panes must be opaque
		frame.add( nono );
		frame.setSize( 500, 400 );
		frame.setVisible( true );

		try
		{
			for ( int i = 0; i < nono.width; ++i )
			{
				nono.columns[i].process();
			}
			for ( int i = 0; i < nono.height; ++i )
			{
				nono.rows[i].process();
			}
		} catch ( Exception ex )
		{
			System.out.println( "Failed to initialize: " + ex.getMessage() );
		}

		while ( !nono.processQueue.isEmpty() )
		{
			try
			{
				nono.processQueue.poll().process();
			} catch ( Exception ex )
			{
				System.out.println( "Failed to process: " + ex.getMessage() );
			}
			nono.repaint();
			try
			{
				Thread.sleep( 500 );
			} catch ( InterruptedException ex )
			{
				Thread.currentThread().interrupt();
				System.out.println( "Interrupted: " + ex.getMessage() );
			}
		}
	}
}
