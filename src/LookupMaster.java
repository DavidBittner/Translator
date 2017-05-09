import java.util.ArrayList;

class LookupMaster
{

    private ArrayList<Lookup> lookups;

    public LookupMaster()
    {

        lookups = new ArrayList<>();

    }

    //This checks if a file exists already, if it doesn't it loads it in.
    public void LoadFile( String filename, String id )
    {

        boolean exists = false;

        for( Lookup i : lookups )
        {

            if( filename.equals( i.getFilename() ) )
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
 
            if( i.getFilename().equals( name ) )
            {

                return i;

            }

        }

        Error er = new Error( "LOAD(): File "+name+" not found.", 2 );
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
        return ( lookup.Lookup( cola, str, colb ) );

    }

}
