package translator.main;

import java.io.FileWriter;
import java.io.IOException;

public class Error
{

    private static String curline = "";
    private static FileWriter logFile;

    public static void curLine( String line )
    {
        curline = line;
    }
    
    public static void closeLog()
    {
    	try {
			logFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    public static void openLog( String path ) {
    	if( logFile == null ) {
    		try {
				logFile = new FileWriter(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }
    public static void flushLog() {
    	try {
			logFile.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public Error()
    {
        //Needs to be declared to do nothing
    }

    private void Print( String msg ) {
    	System.err.println(msg);
    	try {
			logFile.write(msg + System.lineSeparator());
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public Error( String msg, boolean warn ) {
    	if( !warn ) {
    		new Error(msg);
    	}else {
    		Print("WARNING: Received during execution of " + Translator.curFunc+"() at line "+(Translator.GetLine()+1)+":");
    		Print("\t"+msg);
    	}
    }
    
    public Error( String msg )
    {
    	
    	boolean linePrinted = false;
    	
        if( !msg.isEmpty() )
        {
            if(!Translator.curFunc.isEmpty())
            {
                Print("ERROR: Failed during execution of "+Translator.curFunc+"() at line "+(Translator.GetLine()+1)+":");
                linePrinted = true;
            }
            if(!curline.isEmpty())
            {
            	if( !linePrinted ) {
                	Print("Error at line "+(Translator.GetLine()+1)+":\n");
            	}
                Print( "\t"+curline );
            }
            Print("\t"+msg);
        }
        closeLog();
        System.exit(1);
    }
}
