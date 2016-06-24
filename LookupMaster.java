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

    public String checkExistence( String name, String header, String str )
    {

        boolean result = false;

        for( Lookup i : lookups )
        {

            if( i.getFilename().equals( name ) )
            {

                result = i.checkExistence( header, str );

            }

        }
        
        return (result)?("true"):("false");

    }

}
