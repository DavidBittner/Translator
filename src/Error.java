import java.util.Arrays;

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

        if( !msg.isEmpty() )
        {
            if(!Translator.curFunc.isEmpty())
            {
                System.out.println("Failed during execution of "+Translator.curFunc+"() at line "+Translator.GetLine()+":");
            }
            if(!curline.isEmpty())
            {
                System.out.println( curline );
            }
            System.out.println(msg + "\n");
            Translator.ExitProg(code);
            return;
        }
    }
}
