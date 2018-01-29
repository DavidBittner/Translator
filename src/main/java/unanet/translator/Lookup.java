package translator.main;
import java.util.*;
import java.io.*;

public class Lookup
{

    private String filename;
    private String id;
    private ArrayList<String[]> data;
    private String[] headers;

    public String getID()
    {
        return id;
    }

    //Constructor, opens and loads the file.
    public Lookup( String filename, String id )
    {
        this.filename = filename;
        this.id = id;
        runInit();
    }
    
    public int getRowCount()
    {
    	return data.size();
    }

    public void runInit()
    {
        try
        {
            BufferedReader dataReader = new BufferedReader( new FileReader( filename ) );
            data = new ArrayList<>();

            String read = "";
            read = dataReader.readLine();
            if( read == null ) {
                new Error("Cannot load file: " + filename + ". File empty.");
            }
            headers = read.split(",");

            while( ( read = dataReader.readLine() ) != null )
            {
                data.add( read.split( ",", -1 ) );
            }
            dataReader.close();

        }
        catch( IOException err )
        {
            new Error( "Error when opening file: '"+filename+"'." );
        }
    }

    //Finds the index of a given column
    private int FindCol( String header )
    {
        int col = 0;
        while( col < headers.length-1 && !headers[col].equals( header ) )
        {
            col++;
        }

        if( col > headers.length-1 )
        {
            return -1;
        }

        return col;
    }

    //This function goes through to see if a string exists under that header
    public boolean checkExistence( String header, String str )
    {
        int col = FindCol( header );

        if( col == -1 )
        {
            new Error( "Cannot find column "+header+".");
        }

        for( int i = 0; i < data.size(); i++ )
        {
            if( str.equals( data.get(i)[col] ) )
            {
                return true;
            }
        }
        return false;
    }

    public String LookupInstance( String cola, String str, String colb )
    {
        int loca = FindCol( cola );
        int locb = FindCol( colb );

        if( loca == -1 )
        {
            new Error("Unable to find column " + cola + ".");
            return "";
        }
        if( locb == -1 )
        {
            new Error("Unable to find column " + colb + ".");
            return "";
        }

        for( int i = 0; i < data.size(); i++ )
        {
            if( str.equals( data.get(i)[loca] ) )
            {
                return data.get(i)[locb];
            }
        }

        return "";
    }
}
