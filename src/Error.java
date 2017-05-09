public class Error
{

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
            Translator.ExitProg( code );

        }

        int line = Translator.GetLine()+1;

        System.out.println( "Error at line "+line+":" );
        System.out.println( msg );

        if( !curline.isEmpty() )
        {

            System.out.println( curline+"\n" );

        }

        Translator.ExitProg( code );

    }

}
