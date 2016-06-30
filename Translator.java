import java.io.*;
import java.util.*;

public class Translator
{

    static ArrayList<Code> execLines = new ArrayList<>();
    static String[] headers = new String[1];

    static boolean ignoreFlag = false;

    private static int GetCharCount( String str, char ch )
    {

        int count = 0;

        for( int i = 0; i < str.length(); i++ )
        {

            if( str.charAt( i ) == ch )
            {

                count++;

            }

        }

        return count;

    }

    public static void IgnoreRecord()
    {

        ignoreFlag = true;

    }

    public static void GiveHeaders( String heads[] )
    {

        headers = heads;

    }

    public static void main( String args[] )
    {


        String templateFile = "";
        String dataFile = "";
        String outputFile = "";

        for( int i = 0; i < args.length; i+=2 )
        {

            switch( args[i] )
            {

                case "-template":
                {

                    templateFile = args[i+1];
                    break;

                }
                case "-import":
                {

                    dataFile = args[i+1];
                    break;

                }
                case "-export":
                {

                    outputFile = args[i+1];
                    break;

                }
                default:
                {

                    System.out.println( "Unkown argument: "+args[i]+"." );
                    break;

                }

            }

        }

        if( templateFile.isEmpty() )
        {
           Error er = new Error( "No filename entered for the template file ." ); 
        }
        if( dataFile.isEmpty() )
        {
           Error er = new Error( "No filename entered for the input file." ); 
        }
        if( outputFile.isEmpty() )
        {
           Error er = new Error( "No filename entered for the output file." ); 
        }

        ArrayList<String[]> output = new ArrayList<>();

        long stime = System.nanoTime();

        try
        {

            BufferedReader read = new BufferedReader( new FileReader( templateFile ) );
            String buff = "";

            execLines.add( new Code() );
            while( ( buff = read.readLine() ) != null )
            {

                if( buff.equals( "END" ) )
                {

                    execLines.add( new Code() );

                }else
                {

                    execLines.get( execLines.size()-1 ).AddLine( buff );

                }

            }

            for( int i = 0; i < execLines.size(); i++ )
            {

                if( execLines.get( i ).GetSize() <= 0 )
                {
                    
                    execLines.remove( i );

                }

            }

            read.close();
        
        }
        catch( IOException e )
        {

            System.out.println( e.getMessage() );

        }

        try
        {

            BufferedReader read = new BufferedReader( new FileReader( dataFile ) ); 
            String buff = "";

            String headers = read.readLine();

            //Execute the header once, then remove it as it's not needed anymore. 
            //(Is zero a magic number in this case?)
            execLines.get(0).Execute( null );
            execLines.remove( 0 );

            while( (buff = read.readLine()) != null )
            {

                String tempArray[] = buff.split(","); 
                ArrayList<String> usedList = new ArrayList<>();

                for( String i : tempArray )
                {

                   usedList.add( i );

                }

                for( int i = 0; i < usedList.size(); i++ )
                {

                    while( GetCharCount( usedList.get(i), '"' ) == 1 )
                    {

                        usedList.set( i, usedList.get(i)+',' );
                        usedList.set( i, usedList.get(i)+usedList.get(i+1) );
                        usedList.remove( i+1 );

                    }
                    usedList.set( i, usedList.get(i).replace( Character.toString('"'), "" ) );

                }

                output.add( new String[ execLines.size() ] );

                int adder = 0;
                for( Code i : execLines )
                {

                    output.get( output.size()-1 )[adder] = i.Execute( usedList );
                    adder++;

                }
                FuncMaster seqAdder = new FuncMaster();
                seqAdder.SeqInc();

                if( ignoreFlag )
                {
                    
                    ignoreFlag = false;
                    output.remove( output.size()-1 );

                }

            }

        }
        catch( IOException e )
        {

            System.out.println( e.getMessage() );

        }

        try
        {

            FileOutputStream writer = new FileOutputStream( outputFile );

            boolean first = true;
            for( String i : headers )
            {

                String str = i;
                if( !first )
                {
                    
                    str = ","+i;

                }else
                {

                    str = "*"+i;

                }
                first = false;

                writer.write( str.getBytes() );

            }
            String lnbrk = "\r\n";
            writer.write( lnbrk.getBytes() );

            for( String i[] : output )
            {

                first = true;
                for( String str : i )
                {

                    if( GetCharCount( str, ',' ) > 0 )
                    {

                        str = '"'+str+'"';

                    }

                    if( !first )
                    {

                        str = ","+str;

                    }

                    writer.write( str.getBytes() );

                    first = false;

                }
                writer.write( lnbrk.getBytes() );

            }

        }
        catch( FileNotFoundException e )
        {

            System.out.println( e.getMessage() );
    
        }
        catch( IOException e )
        {

            System.out.println( e.getMessage() );

        }

        long etime = System.nanoTime();

        double calckedTime = (etime-stime)/(Math.pow(10,9));
        calckedTime = Math.round( calckedTime*1000.0 )/1000.0;

        System.out.println( "Process took a total of "+Double.toString( calckedTime )+" seconds." );

    }

}
