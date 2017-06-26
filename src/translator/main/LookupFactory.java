package translator.main;
import java.util.ArrayList;

class LookupFactory
{

    private ArrayList<Lookup> lookups;

    public LookupFactory()
    {
        lookups = new ArrayList<>();
    }

    //This checks if a file exists already, if it doesn't it loads it in.
    public void LoadFile( String filename, String id )
    {
        boolean exists = false;

        for( Lookup i : lookups )
        {
            if( filename.equals( i.getID() ) )
            {
                exists = true;
            }
        }

        if( !exists )
        {
            lookups.add( new Lookup( filename, id ) );
        }

    }

    private Lookup findLookup( String name )
    {

        //Search through the available lookups trying to find a matching ID. (Filename is deprecated)
        for( Lookup i : lookups )
        {
            if( i.getID().equals( name ) )
            {
                return i;
            }
        }

        new Error( "LOOKUP ID '"+name+"' not found." );
        return null;

    }

    public String checkExistence( String name, String header, String str )
    {

        //After it finds the matching lookup, it then searches whether or not the entry exists
        Lookup lookup = findLookup( name );
        return (lookup.checkExistence( header, str ))?("true"):("false");

    }

    //Checks actually executes the individual functions declared above.
    public String Lookup( String name, String cola, String str, String colb )
    {

        //This starts out the same as the previous function, but instead calls the lookup function
        Lookup lookup = findLookup( name );
        return ( lookup.LookupInstance( cola, str, colb ) );

    }

}
