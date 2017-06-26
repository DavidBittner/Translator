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

    	boolean linePrinted = false;
    	
        if( !msg.isEmpty() )
        {
            if(!Translator.curFunc.isEmpty())
            {
                System.err.println("Failed during execution of "+Translator.curFunc+"() at line "+(Translator.GetLine()+1)+":\n");
                linePrinted = true;
            }
            if(!curline.isEmpty())
            {
            	if( !linePrinted ) {
                	System.err.println("Error at line "+(Translator.GetLine()+1)+":\n");
            	}
                System.err.println( curline );
            }
            System.err.println("ERROR: "+msg);
        }
        System.exit(1);
    }
}
