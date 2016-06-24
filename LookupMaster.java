import java.util.ArrayList;

class LookupMaster
{

    private ArrayList<Lookup> lookups;

    public LookupMaster()
    {

        lookups = new ArrayList<>();

    }

    public void LoadFile( String filename )
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

            lookups.add( new Lookup( filename ) );

        }

    }

    private Lookup findLookup( String name )
    {

        for( Lookup i : lookups )
        {

            if( i.getFilename().equals( name ) )
            {

                return i;

            }

        }

        System.out.println( "File "+name+" not loaded!" );
        return null;

    }

    public String checkExistence( String name, String header, String str )
    {

        Lookup lookup = findLookup( name );

        return (lookup.checkExistence( header, str ))?("true"):("false");

    }

    public String Lookup( String name, String cola, String str, String colb )
    {

        Lookup lookup = findLookup( name );

        return ( lookup.Lookup( cola, str, colb ) );

    }

}
