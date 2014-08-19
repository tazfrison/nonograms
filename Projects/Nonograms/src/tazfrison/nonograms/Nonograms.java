package tazfrison.nonograms;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class Nonograms extends JPanel
{
	private boolean DEBUG = false;

	public Nonograms ()
	{
		super( new GridLayout( 1, 0 ) );

		String[] columnNames = {
				"First Name", "Last Name", "Sport", "# of Years", "Vegetarian"
		};

		Object[][] data = {
				{
						"Kathy", "Smith", "Snowboarding", new Integer( 5 ),
						new Boolean( false )
				},
				{
						"John", "Doe", "Rowing", new Integer( 3 ), new Boolean( true )
				},
				{
						"Sue", "Black", "Knitting", new Integer( 2 ), new Boolean( false )
				},
				{
						"Jane", "White", "Speed reading", new Integer( 20 ),
						new Boolean( true )
				}, {
						"Joe", "Brown", "Pool", new Integer( 10 ), new Boolean( false )
				}
		};

		final JTable table = new JTable( data, columnNames );
		table.setPreferredScrollableViewportSize( new Dimension( 500, 70 ) );
		table.setFillsViewportHeight( true );

		if ( DEBUG )
		{
			table.addMouseListener( new MouseAdapter()
			{
				public void mouseClicked ( MouseEvent e )
				{
					printDebugData( table );
				}
			} );
		}

		// Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane( table );

		// Add the scroll pane to this panel.
		add( scrollPane );
	}

	private void printDebugData ( JTable table )
	{
		int numRows = table.getRowCount();
		int numCols = table.getColumnCount();
		javax.swing.table.TableModel model = table.getModel();

		System.out.println( "Value of data: " );
		for ( int i = 0; i < numRows; i++ )
		{
			System.out.print( "    row " + i + ":" );
			for ( int j = 0; j < numCols; j++ )
			{
				System.out.print( "  " + model.getValueAt( i, j ) );
			}
			System.out.println();
		}
		System.out.println( "--------------------------" );
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

	private static void createAndShowGUI ()
	{
		// Create and set up the window.
		JFrame frame = new JFrame( "Nonograms" );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

		// Create and set up the content pane.
		Nonograms newContentPane = new Nonograms();
		newContentPane.setOpaque( true ); // content panes must be opaque
		frame.setContentPane( newContentPane );

		// Display the window.
		frame.pack();
		frame.setVisible( true );
	}

	public static void main ( String[] args )
	{
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater( new Runnable()
		{
			public void run ()
			{
				createAndShowGUI();
			}
		} );
	}
	/*
	 * public static void main ( String[] args ) { Scanner input = null,
	 * lineScanner = null; int rowLength, columnLength; String line;
	 * ArrayList<Integer> runTemp = new ArrayList<Integer>();
	 * 
	 * Integer rowRuns[][]; Integer columnRuns[][];
	 * 
	 * try { input = new Scanner( new BufferedReader( new FileReader(
	 * "src/tazfrison/nonograms/data/game1.txt" ) ) ); columnLength =
	 * input.nextInt(); rowLength = input.nextInt();
	 * 
	 * rowRuns = new Integer[rowLength][]; columnRuns = new
	 * Integer[columnLength][]; input.nextLine(); for ( int i = 0; i <
	 * columnLength; ++i ) { runTemp.clear(); line = input.nextLine(); lineScanner
	 * = new Scanner( line ); while ( lineScanner.hasNextInt() ) { runTemp.add(
	 * lineScanner.nextInt() ); } rowRuns[i] = new Integer[runTemp.size()];
	 * runTemp.toArray( rowRuns[i] ); } for ( int i = 0; i < rowLength; ++i ) {
	 * runTemp.clear(); line = input.nextLine(); lineScanner = new Scanner( line
	 * ); while ( lineScanner.hasNextInt() ) { runTemp.add( lineScanner.nextInt()
	 * ); } columnRuns[i] = new Integer[runTemp.size()]; runTemp.toArray(
	 * columnRuns[i] ); }
	 * 
	 * input.close(); } catch ( Exception e ) { return; }
	 * 
	 * Integer board[][] = new Integer[rowLength][columnLength]; Strip rows[] =
	 * new Strip[columnLength]; Strip columns[] = new Strip[rowLength];
	 * LinkedList<Strip> processQueue = new LinkedList<Strip>();
	 * 
	 * for ( int i = 0; i < rowLength; ++i ) { Integer tempColumn[] = new
	 * Integer[columnLength]; for ( int j = 0; j < columnLength; ++j ) {
	 * board[i][j] = Strip.EMPTY; tempColumn[j] = board[i][j]; } rows[i] = new
	 * Strip( board[i], columns, rowRuns[i], processQueue, i ); columns[i] = new
	 * Strip( tempColumn, rows, columnRuns[i], processQueue, i ); }
	 * 
	 * while ( !processQueue.isEmpty() ) { processQueue.poll().process(); } }
	 */

}
