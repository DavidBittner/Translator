package translator.main;

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

    public Error( String msg )
    {

        if( !msg.isEmpty() )
        {
            if(!Translator.curFunc.isEmpty())
            {
                System.err.println("Failed during execution of "+Translator.curFunc+"() at line "+Translator.GetLine()+":");
            }
            if(!curline.isEmpty())
            {
                System.err.println( curline );
            }
            System.err.println(msg);
        }
        System.exit(1);
    }
}
