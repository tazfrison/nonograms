package tazfrison.nonograms;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.JTable;

public class Nonograms
{
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
		int width = board.length + rowMax,
				height = board[0].length + columnMax;
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

	public static void main ( String[] args )
	{
		Scanner input = null, lineScanner = null;
		int rowLength, columnLength;
		String line;
		ArrayList<Integer> runTemp = new ArrayList<Integer>();

		Integer rowRuns[][];
		Integer columnRuns[][];

		try
		{
			input = new Scanner( new BufferedReader( new FileReader(
					"src/tazfrison/nonograms/data/game1.txt" ) ) );
			columnLength = input.nextInt();
			rowLength = input.nextInt();

			rowRuns = new Integer[rowLength][];
			columnRuns = new Integer[columnLength][];
			input.nextLine();
			for ( int i = 0; i < columnLength; ++i )
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
			for ( int i = 0; i < rowLength; ++i )
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

		Integer board[][] = new Integer[rowLength][columnLength];
		Strip rows[] = new Strip[columnLength];
		Strip columns[] = new Strip[rowLength];
		LinkedList<Strip> processQueue = new LinkedList<Strip>();

		for ( int i = 0; i < rowLength; ++i )
		{
			Integer tempColumn[] = new Integer[columnLength];
			for ( int j = 0; j < columnLength; ++j )
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
		}
	}

}
