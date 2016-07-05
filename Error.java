public class Error
{

    private Translator sygCommands = new Translator();
    private static String curline = "";

    public static void curLine( String line )
    {

        curline = line;

    }

    public Error()
    {

        //Do nothing, important

    }

    public Error( String msg, int code )
    {

        System.out.println( msg );
        
        if( !curline.isEmpty() )
        {

            System.out.println( curline+"\n" );

        }

        sygCommands.ExitProg( code );

    }

}
