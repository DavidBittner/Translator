import java.util.Arrays;

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

        if( !msg.isEmpty() )
        {
            if(!Translator.curFunc.isEmpty())
            {
                System.out.println("Failed during execution of "+Translator.curFunc+"() at line "+Translator.GetLine()+":");
                System.out.println( curline );
            }
            System.out.println(msg + "\n");
            sygCommands.ExitProg(code);
            return;
        }

        //Old error stuff is deprecated.
        if( code == -2 )
        {

            System.out.println( msg );
            sygCommands.ExitProg( code );
            return;
        }

        int line = sygCommands.GetLine()+1;

        if(code == 2 )
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
