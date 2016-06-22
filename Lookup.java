import java.util.*;
import java.io.*;

public class Lookup
{

    private String filename;
    private ArrayList<String[]> data;
    private String[] headers;

    public void giveName( String filename )
    {

        this.filename = filename;
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

                data.add( read.split( "," ) );

            }
        
        }
        catch( IOException err )
        {

            System.out.println( "Unable to open file "+filename+"." );

        }

    }

    //This function goes through to see if a string exists under that header
    public boolean checkExistence( String header, String str )
    {

        int col = 0;
        while( col < headers.length-1 && !headers[col].equals( header ) )
        {
        
            col++;

        }

        if( col > headers.length-1 )
        {

            return false;

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

}
