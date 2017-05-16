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

    public void runInit()
    {
        try
        {
            BufferedReader dataReader = new BufferedReader( new FileReader( filename ) );
            data = new ArrayList<>();

            String read = "";
            headers = dataReader.readLine().split(",");

            while( ( read = dataReader.readLine() ) != null )
            {
                data.add( read.split( ",", -1 ) );
            }

        }
        catch( IOException err )
        {
            System.out.println( err.getMessage() + "\n" );
            Error er = new Error( "Error when opening file:"+filename+" for Lookup funcs.", 2 );
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
            Error er = new Error( "Cannot find column "+header+".", 2 );
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

    public String Lookup( String cola, String str, String colb )
    {
        int loca = FindCol( cola );
        int locb = FindCol( colb );

        if( loca == -1 )
        {
            Error er = new Error("Unable to find column " + cola + ".", 1 );
            return "";
        }
        if( locb == -1 )
        {
            Error er = new Error("Unable to find column " + colb + ".", 1 );
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
