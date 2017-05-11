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

        //Needs to be declared to do nothing

    }

    public Error( String msg, int code )
    {

        if( code == -2 )
        {

            System.out.println( msg );
            sygCommands.ExitProg( code );

        }

        int line = sygCommands.GetLine()+1;

        if(code != 0)
        {
            System.out.println( "Error at line "+line+":" );
            System.out.println( curline+"\n" );
        }else
        {
            System.out.println( msg );
        }

        sygCommands.ExitProg( code );

    }

}
