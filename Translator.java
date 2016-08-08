import java.io.*;
import java.util.*;

public class Translator
{

    static ArrayList<Code> execLines = new ArrayList<>();
    static String[] headers = new String[1];

    static ArrayList<Integer> uniqueCols = new ArrayList<>();;
    static int curColumn = 0;

    static boolean ignoreFlag = false;
    static boolean uniqueFlag = false;
    static boolean exitFlag = false;

    static int lineTracker = 0;

    public static void ExitProg( int num )
    {

        //Determines what to do based on the number they pass
        switch( num )
        {

            case 0:
            {

                break;

            }
            case 1:
            {

                exitFlag = true;
                break;

            }
            default:
            {

                System.out.println( "Program forcefully closed with error code "+num+"." );
                System.exit( num );

            }
        }
    }

    public static void CountLine()
    {

        lineTracker++;

    }

    public static int GetLine()
    {

        return lineTracker;

    }

    private static String ConcatRow( String row[] )
    {

        String ret = "";

        boolean first = true;
        for( String i : row )
        {

            if( i.contains( Character.toString('"') ) )
            {

                i = '"'+i+'"';

            }

            ret = (!first)?(ret+','+i):(ret+i);

            first = false;

        }

        return ret;

    }

    private static ArrayList<String[]> GetUniques( ArrayList<String[]> in )
    {

        String ary[] = new String[ in.size() ];

        int tracker = 0;
        for( String i[] : in )
        {

            ary[tracker] = ConcatRow( i );
            tracker++;

        }

        Arrays.sort( ary );

        in.clear();
        String last = "";
        for( int i = 0; i < ary.length; i++ )
        {

            if( !last.equals( ary[i] ) )
            {

                last = ary[i];

                ArrayList<String> tempList = new ArrayList<>();

                for( String iter : last.split(",") )
                {

                    tempList.add( iter );

                }

                for( int iter = 0; iter < tempList.size()-1; iter++ )
                {

                    while( GetCharCount( tempList.get(iter), '"' ) == 1 )
                    {

                        tempList.set( iter, tempList.get(iter)+','+tempList.get( iter+1 ) );
                        tempList.remove( iter+1 );
                        iter--;

                    }

                }
                in.add( tempList.toArray( new String[tempList.size()] ) );

            }

        }

        return in;

    }

    //Finds the amount of times a character occurs in a string
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

    //Finds the columns in which the GroupTotal function is used on
    public static void UniqueColNum()
    {

        boolean exists = false;
        for( int i : uniqueCols )
        {

            if( i == curColumn )
            {

                exists = true;

            }

        }

        if( !exists )
        {

            uniqueCols.add( curColumn );

        }

    }

    //Called when IGNORE() is called
    public static void IgnoreRecord()
    {

        ignoreFlag = true;

    }

    //Called when UNIQUE() is called
    public static void UniqueRecords()
    {

        uniqueFlag = true;

    }

    //Called when the HEADER() function is called. Tells the program what to actually output header-wise.
    //You could say it.. gives it a heads up to what the headers are! 
    public static void GiveHeaders( String heads[] )
    {
        headers = heads;
    }

    public static void main( String args[] )
    {

        String templateFile = "";
        String dataFile = "";
        String outputFile = "";

        //Done horribly. I'll try and think of a cleaner way, it will always work but when a user makes a mistake its very unclear what mistake they made.
        try
        {
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
        }

        catch( ArrayIndexOutOfBoundsException e )
        {
            Error er = new Error( "Invalid parameters entered.", 2 );
        }

        if( templateFile.isEmpty() )
        {
           Error er = new Error( "No filename entered for the template file .", -1 ); 
        }
        if( dataFile.isEmpty() )
        {
           Error er = new Error( "No filename entered for the input file.", -1 ); 
        }
        if( outputFile.isEmpty() )
        {
           Error er = new Error( "No filename entered for the output file.", -1 ); 
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

            int inTracker = 1;
            while( (buff = read.readLine()) != null && !buff.isEmpty() )
            {

                inTracker++;
                if( buff.split(",").length != headers.split(",").length && !buff.contains(Character.toString( '"' ) ) )
                {

                    Error er = new Error( "Input file column count does not match output header count at line "+inTracker, -2 );

                }

                String tempArray[] = buff.split(","); 
                ArrayList<String> usedList = new ArrayList<>();

                for( String i : tempArray )
                {

                   usedList.add( i );

                }

                for( int i = 0; i < usedList.size(); i++ )
                {

                    //This basically combines entries until they have matching quotes on each end.
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
                curColumn = 0;

                for( Code i : execLines )
                {
                    output.get( output.size()-1 )[adder] = i.Execute( usedList );
                    adder++;
                    curColumn++;

                    CountLine();
                }

                lineTracker = 0;

                FuncMaster seqAdder = new FuncMaster();
                seqAdder.SeqInc();

                if( exitFlag )
                {
                    System.out.println( "Program exited cleanly." );
                    System.exit( 0 );
                }

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
            UniqueMaster tempUniq = new UniqueMaster();
            for( int j = 0; j < output.size(); j++ )
            {
                for( int i : uniqueCols )
                {
                    output.get(j)[i] = Integer.toString(tempUniq.getOccurences( output.get(j)[i] ));
                }
            }

            if( uniqueFlag )
            {

                output = GetUniques( output );

            }

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

                if( ConcatRow( i ).isEmpty() )
                {

                    continue;

                }

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

        } catch( FileNotFoundException e )
        {

            System.out.println( e.getMessage() );
    
        } catch( IOException e ) 
        {

            System.out.println( e.getMessage() );

        }

        //Just calculating how long the whole operation took
        long etime = System.nanoTime();

        double calckedTime = (etime-stime)/(Math.pow(10,9));
        calckedTime = Math.round( calckedTime*1000.0 )/1000.0;

        System.out.println( "Process took a total of "+Double.toString( calckedTime )+" seconds." );

    }
}
