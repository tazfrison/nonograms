package tazfrison.nonograms;

import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;

public class Nonograms extends JPanel
{
	private int height;
	private int width;

	private final int CELL_SIZE = 20;

	public Nonograms ()
	{
		super();
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
		g.drawRect( 0, 0, 15 * this.CELL_SIZE, 15 * this.CELL_SIZE );
		for ( int i = 0; i < 15; ++i )
		{
			for ( int j = 0; j < 15; ++j )
			{
				this.drawCell( g, i, j, Strip.EMPTY );
			}
		}
	}

	public void drawCell ( Graphics g, int row, int column, int value )
	{
		int x = column * this.CELL_SIZE,
				y = row * this.CELL_SIZE;
		g.drawRect( x, y, this.CELL_SIZE,
				this.CELL_SIZE );
		
		if ( value == Strip.MARKED )
		{
			g.drawLine( x + 2, y + 2, x + this.CELL_SIZE - 4,
					y + this.CELL_SIZE - 4 );
			g.drawLine( x + this.CELL_SIZE - 4, y + 2, x + 2,
					y + this.CELL_SIZE - 4 );
		}
		else if ( value == Strip.FILLED )
		{
			g.fillRect( x + 2, y + 2, this.CELL_SIZE - 4,
					this.CELL_SIZE - 4 );
		}
	}

	public static void printBoard ( Integer[][] board, Integer rowRuns[][],
			Integer columnRuns[][] )
	{
		int rowMax = 0, columnMax = 0;
		for ( Integer run[] : rowRuns )
		{
			if ( run.length > rowMax )
				rowMax = run.length;
		}
		for ( Integer run[] : columnRuns )
		{
			if ( run.length > columnMax )
				columnMax = run.length;
		}
		int width = board.length + rowMax, height = board[0].length + columnMax;
		Object tableData[][] = new Integer[width][height];
		for ( int i = rowMax; i < width; ++i )
		{
			for ( int j = 0; j < columnMax; ++j )
			{
				if ( columnRuns[i - rowMax].length > j )
					tableData[i][j] = columnRuns[i - rowMax][j];
			}
		}
		for ( int j = columnMax; j < height; ++j )
		{
			for ( int i = 0; i < rowMax; ++i )
			{
				if ( rowRuns[j - columnMax].length > i )
					tableData[i][j] = rowRuns[j - columnMax][i];
			}
		}
		for ( int i = 0; i < width - rowMax; ++i )
		{
			for ( int j = 0; j < height - columnMax; ++j )
			{
				tableData[i + rowMax][j + columnMax] = board[i][j];
			}
		}
		JTable table = new JTable( tableData, null );
	}

	private void readFile ()
	{
		Scanner input = null, lineScanner = null;
		String line;
		ArrayList<Integer> runTemp = new ArrayList<Integer>();

		Integer rowRuns[][];
		Integer columnRuns[][];

		try
		{
			input = new Scanner( new BufferedReader( new FileReader(
					"src/tazfrison/nonograms/data/game1.txt" ) ) );
			this.height = input.nextInt();
			this.width = input.nextInt();

			rowRuns = new Integer[this.width][];
			columnRuns = new Integer[this.height][];
			input.nextLine();
			for ( int i = 0; i < this.height; ++i )
			{
				runTemp.clear();
				line = input.nextLine();
				lineScanner = new Scanner( line );
				while ( lineScanner.hasNextInt() )
				{
					runTemp.add( lineScanner.nextInt() );
				}
				rowRuns[i] = new Integer[runTemp.size()];
				runTemp.toArray( rowRuns[i] );
			}
			for ( int i = 0; i < this.width; ++i )
			{
				runTemp.clear();
				line = input.nextLine();
				lineScanner = new Scanner( line );
				while ( lineScanner.hasNextInt() )
				{
					runTemp.add( lineScanner.nextInt() );
				}
				columnRuns[i] = new Integer[runTemp.size()];
				runTemp.toArray( columnRuns[i] );
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

/*
		nono.readFile();

		Integer board[][] = new Integer[nono.width][nono.height];
		Strip rows[] = new Strip[nono.height];
		Strip columns[] = new Strip[nono.width];
		LinkedList<Strip> processQueue = new LinkedList<Strip>();

		for ( int i = 0; i < nono.width; ++i )
		{
			Integer tempColumn[] = new Integer[nono.height];
			for ( int j = 0; j < nono.height; ++j )
			{
				board[i][j] = Strip.EMPTY;
				tempColumn[j] = board[i][j];
			}
			rows[i] = new Strip( board[i], columns, rowRuns[i], processQueue, i );
			columns[i] = new Strip( tempColumn, rows, columnRuns[i], processQueue, i );
		}

		while ( !processQueue.isEmpty() )
		{
			processQueue.poll().process();
		}*/
	}
}
