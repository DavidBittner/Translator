import java.util.*;

public class UniqueFactory
{

    private static ArrayList<Unique> uniques = new ArrayList<>();

    public static void giveKey( String key )
    {

        boolean exists = false;
        Unique found = null;
        for( Unique i : uniques )
        {
            if( i.ID.equals( key ) )
            {
                exists = true;
                found = i;
            }
        }

        if( !exists )
        {

            uniques.add( new Unique( key ) );

        }else
        {

            found.ocs++;

        }

    }

    public static int getOccurences( String key )
    {

        for( Unique i : uniques )
        {

            if( i.ID.equals( key ) )
            {

                return i.ocs;

            }

        }

        return 0;

    }

}
