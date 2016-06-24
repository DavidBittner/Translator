import java.io.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/*
 * Created by David Bittner on 6/18/2015.
 *
 * REVISION 2:
 *      Started: 7/7/15
 *
 * CSV Translation Program
 *
 */

 /*
 If you are reading through this code for whatever reason,
 good luck! It's in need of a re-formatting which should be done soon.
 */

public class Translator
{

    //Create and initialize the ArrayList (An array with dynamic memory allocation).
    static ArrayList<String> data = new ArrayList<>();
    static ArrayList< ArrayList<String> > output = new ArrayList<>();
    static String []ColumnHeaders;
    static ArrayList<String> Headers = new ArrayList<>();

    //The ArrayList that tells the program with lines need to be executed.
    static ArrayList<ArrayList<String>> code = new ArrayList<>();

    //Things for the grouptotal function
    static ArrayList<Unique>  countedIDs = new ArrayList<>();
    static ArrayList<Integer> IDrows = new ArrayList<>();

    static LookupMaster keyMaster = new LookupMaster(); //Like in the Matrix heh

    //The outputstream to write to the log file.
    static FileOutputStream log;

    //Declaring variables that hold the info about the file.
    static int rows = 0;
    static int columns = 0;
    static int currow = 0;
    static boolean first = true;

    static boolean ignorerec = false;

    static String transfile = "";

    //Simple function to check if a string contains numeric characters.
    private static boolean isNumeric( String a )
    {

        try
        {

            //If it is not numeric, then it will throw an exception which is caught below.
            double b = Double.parseDouble( a );

        }
        catch( NumberFormatException nfe )
        {

            return false;

        }

        return true;

    }

    private static String removeChar( String a, char b )
    {

        a = a.replace( Character.toString(b), "" );
        return a;

    }

    private static String COL( int col )
    {

        //Get the value from the column.
        col = col-1;
        if( col < 0 ) col = 0;
        if( col > columns ) col = columns-1;

        return data.get( col );

    }

    private static String CONCAT( String a, String b )
    {

        //It's as easy as A plus B (plus c)! (just concatenating)
        return a+b;

    }

    private static String TODAY( String format )
    {

        if( isNumeric( format ) )
        {

            if( first ) ERROR_INVALID_PARAM( "TODAY", 1 );
            return "null";

        }

        Date curdat = new Date();
        SimpleDateFormat form = new SimpleDateFormat( format );

        try
        {

            return form.format( curdat );

        }
        catch( IllegalArgumentException a )
        {

            String text = "Invalid format for TODAY function. "+format+" \r\n";
            System.out.println( text );

            try
            {

                log.write( text.getBytes() );

            }catch( IOException b )
            {

                System.out.println("");

            }

        }

        return "null";

    }

    private static String SEQ( String startpoint )
    {

        try
        {

            int start = Integer.parseInt( startpoint );
            return Integer.toString(rows + start);

        }catch( NumberFormatException a )
        {

            if( first )ERROR_INVALID_PARAM( "SEQ", 1 );
            return "null";

        }

    }

    private static String UPPER( String text )
    {

        return text.toUpperCase();

    }

    private static String LOWER( String text )
    {

        return text.toLowerCase();

    }

    private static String LENGTH( String text )
    {

        text = removeChar( text, '"' );
        return Integer.toString(text.length());

    }

    private static void PRINT( String str )
    {

        String temp = str + "\r\n";
        try
        {
            log.write(temp.getBytes());
            System.out.println( str );
        }catch( IOException a )
        {

            PRINT( "IOExcept error!" );

        }

    }

    private static String SUBSTR( String str, String start, String end )
    {

        if( str.isEmpty() )
        {

            return "";

        }

        if( getCharCount( str, ',' ) >=1 || getCharCount( str, '"' ) >= 1 )
        {

            str = removeChar( str, '"' );

        }

        //The declaration of the end integer and start integer of the substring.
        int endint = Integer.parseInt( end );
        int startint = Integer.parseInt( start )-1;

        //Making sure not to overflow the substring function.
        if(endint > str.length()) endint = str.length();
        if(startint >= str.length()) startint = str.length();
        if( endint < 0 )endint = 0;
        if( startint < 0 )startint = 0;

        //A string builder is delcared, and then the value taken from the substring is returned.
        StringBuilder temp = new StringBuilder(str);
        str = temp.substring( startint, endint );


        if( getCharCount( str, ',' ) >= 1 )
        {

            str = '"'+str+'"';

        }

        return str;

    }

    private static String REPLACE( String rep, String charac, String with )
    {

        return rep.replace( charac, with );

    }

    private static String FC( String start, String end )
    {

        String a = "";
        for( int i = 0; i < data.size(); i++ )
        {

            a = CONCAT( a, data.get(i) );

        }

        return SUBSTR( a, start, end );

    }

    //Just a function to return the amount of times a character occurs in a string.
    private static int getCharCount( String list, char charac )
    {

        int charcount = 0;
        for( int i = 0; i < list.length(); i++ )
        {

            if( list.charAt(i) == charac )charcount+=1;

        }

        return charcount;

    }

    private static void ERROR_INVALID_PARAM( String func, int loc )
    {

        String a = "Invalid parameter received by "+func+" function at row "+currow+"."+"\r\n";

        try
        {

            System.out.print(a);
            log.write( a.getBytes() );

        }catch( IOException b )
        {

            System.out.println( "" );

        }

    }
    private static void ERROR_INVALID_FUNCNAME( String funcname )
    {

        String a = "Unknown function " + funcname + " on row "+currow+"." + "\r\n";

        try
        {

            System.out.print(a);
            log.write( a.getBytes() );

        }catch( IOException b )
        {

            System.out.println( "" );

        }

    }
    private static void ERROR_INVALID_PARAMNUM( String func, int loc )
    {

        String a = "Invalid number of parameters received by " + func + " function at character:" + loc + "." + "\r\n";

        try
        {

            System.out.print( a );
            log.write( a.getBytes() );

        }catch( IOException b )
        {

            System.out.println( "" );

        }

    }

    private static void ERROR_ODDCHAR( String charac )
    {

        String a = "";
        //Checking which error was received.
        if( charac.equals("paren") )
        {

            a = "Odd number of parentheses in row "+currow+". Forget to end a function? Skipping.";

        }else if( charac.equals("quote") )
        {

            a = "Odd number of quotes in row "+currow+". Forgot to end a string maybe? Skipping.";

        }

        a = a+"\r\n";

        System.out.print(a);
        try
        {

            //Write the error to the log.
            log.write( a.getBytes() );

        }catch( IOException b )
        {

            //do nothing, just needed here.
            System.out.println( "" );

        }

    }

    private static void ERROR_NON_NUMERIC_COMPARISON( String text )
    {

        String a = "Invalid comparison between non-numeric types on line " + currow + ".";

        a = a+"\r\n";

        System.out.println(a);
        try
        {

            log.write( a.getBytes() );

        }
        catch( IOException b )
        {

            System.out.println("");

        }

    }

    //This function is used to grab parameters from a list.
    //It returns an array of aforementioned parameters.
    private static String []GETPARAMS( ArrayList<String> list, int count )
    {

        String out[] = new String[count];
        for( int i = 0; i < count; i++ )
        {

            out[count-(i+1)] = list.get( list.size()-1 );
            list.remove( list.size()-1 );

        }

        return out;

    }

    private static String GenUniques( String ID, int temprow )
    {

        boolean exists = false;

        for( int i : IDrows )
        {

            if( i == temprow )
            {

                exists = true;

            }

        }

        if( !exists )
        {

            IDrows.add( temprow );

        }
        exists = false;

        int i = 0;
        for( Unique tempID : countedIDs )
        {

            if( tempID.ID.equals( ID ) )
            {

                exists = true;
                break;

            }

            i++;

        }

        if( exists )
        {

            countedIDs.get(i).ocs++;
            return ID;

        }else
        {

            countedIDs.add( new Unique( ID ) );
            countedIDs.get(countedIDs.size()-1).ocs=1;
            return ID;

        }

    }

    //A function used to determine whether or not an item is on a list of strings.
    private static boolean ISONLIST( String a, String list[] )
    {

        for( String i:list )
        {

            if( a.equals( i ) )
                return true;

        }

        return false;

    }

    private static boolean ISONLIST( char a, char list[] )
    {

        for( char i:list )
        {

            if( a == i )
                return true;

        }

        return false;

    }

    private static String removeCharQuotes( String a, char charac )
    {

        //Creating a StringBuilder using the string that was passed through.
        StringBuilder strbuild = new StringBuilder( a );

        for( int i = 0; i < strbuild.length(); i++ )
        {

            //If a quote is found, skip until the next quote.
            if( strbuild.charAt(i) == '"' )
            {

                //It adds 1 to i, and the runs a loop that continues until it has found another quote.
                i++;
                while( strbuild.charAt(i)!='"' && i < strbuild.length() )
                {

                    i++;

                }

            }

            if( i >= strbuild.length() ) i = 0;

            //Everytime it deletes a character, i needs to move backwards to avoid any overflow possibilities.
            if( strbuild.charAt(i) == charac )
            {

                strbuild.deleteCharAt( i );
                i--;

            }

        }
        //Return the string!
        return strbuild.toString();

    }

    private static boolean AND( String a, String b )
    {

        return (a.equals("true") && b.equals("true"));

    }

    private static boolean OR( String a, String b )
    {

        return (a.equals("true") || b.equals("true"));

    }

    private static boolean GREATER( String a, String b )
    {

        double tempa = Double.parseDouble( a );
        double tempb = Double.parseDouble( b );

        return (tempa > tempb);

    }

    private static boolean LESS( String a, String b )
    {

        double tempa = Double.parseDouble( a );
        double tempb = Double.parseDouble( b );

        return (tempa < tempb);

    }

    private static boolean GREATEREQ( String a, String b )
    {

        double tempa = Double.parseDouble( a );
        double tempb = Double.parseDouble( b );

        return (tempa >= tempb);

    }

    private static boolean LESSEQ( String a, String b )
    {

        double tempa = Double.parseDouble( a );
        double tempb = Double.parseDouble( b );

        return (tempa <= tempb);

    }
    private static int GETPREC( String operator )
    {

        switch( operator )
        {

            case "=": return 1;
            case "!=": return 1;
            case ">=": return 1;
            case "<=": return 1;
            case ">": return 1;
            case "<": return 1;

            case "||": return 2;
            case "&&": return 2;

            default: return 1;

        }

    }

    private static void translate()throws IOException
    {

        //Loading in the translator file.
        BufferedReader in = new BufferedReader(new FileReader(transfile));
        String read = "";

        //Get rid of the next lines, do it like this:
        //If its the first time, process the first x amount of lines. Else, skip them and continually
        //process the following data. Inits can be run in here also.

        //These are the lists that contain accepted functions, operators, and unwanted characters for the parser.
        String logiclist[] = { "IF","ELSE","SWITCH","ELSEIF" };
        char unwantedchars[] = { '\t', '"', '(', ')', '{', '}', ',' };
        String oplist[] = { "=", "!=", "<", ">", "<=", ">=", "&&", "||" };
        String funclist[] = { "SEQ","TODAY","CONCAT","SUBSTR","LENGTH","REPLACE","C","TRUE","FALSE","PRINT","UPPER","LOWER", "ISNUMERIC","BLANK", "IGNORE", "FC", "GROUPTOTAL","HEADER","LOAD","EXISTS","LOOKUP" };

        if(first)
        {
            code.add( new ArrayList<String>() );
        }

        int levelcount = 0;

        //First order of business is to divvy up the code into sections.
        while( first )
        {

            //Read until it returns null.
            read = in.readLine();
            if(read == null )break;

            //At each end of a line, it adds a new entry to the code ArrayList.
            if( read.equals( "END" ) )
            {

                levelcount++;
                code.add( new ArrayList<String>() );
                continue;

            }

            //This function removes all spaces, besides the ones that are in quotes.
            read = removeCharQuotes( read, ' ' );
            //Adds the line of code to the code array.
            code.get(levelcount).add( read );

        }

        currow = 1;

        //Increment through each block of code.
        for( int i = 0; i < code.size(); i++ )
        {

            if( !first && i == 0 )
            {

                continue;
             
            }

            //Create an array of booleans for each line of code, and then set it all to false.
            //This array tells the program whether or not to execute a line of code.
            boolean execlines[] = new boolean[code.get(i).size()];
            for( int f = 0; f < execlines.length; f++)
            {

                if( first )
                {
                    execlines[f] = true;
                }
                else
                {
                    execlines[f] = false;
                }

            }
            //The first line should always be executed.
            if( execlines.length > 0 )
            {
                execlines[0] = true;
            }

            //This determines whether or not a value has been added to the output ArrayList. If so, there is nothing more to add.
            boolean hasadded = false;

            //Find the amount of lines of code in this block.
            for( int f = 0; f < code.get(i).size(); f++ )
            {
    
                currow++;

                if( removeChar( code.get(i).get(f),'\t').isEmpty() )
                {
                    continue;
                }

                if( removeChar(code.get(i).get(f),'\t').charAt(0) == '#' || removeChar( code.get(i).get(f), ' ' ).charAt(0) == '#' )
                {
                    continue;
                }

                if( (getCharCount( code.get(i).get(f), ')' ) + getCharCount( code.get(i).get(f), '(' ))%2 != 0 )
                {
                    if( first )ERROR_ODDCHAR( "paren" );
                    continue;
                }

                if( !execlines[f] )continue;

                //Initalizing the various stacks for the program.
                ArrayList<String> funcstack = new ArrayList<>();
                ArrayList<String> opstack = new ArrayList<>();
                ArrayList<String> paramstack = new ArrayList<>();
                ArrayList<String> logicstack = new ArrayList<>();
                ArrayList<String> logicansstack = new ArrayList<>();

                String holder = "";

                //Start to read through the individual characters.
                for (int j = 0; j < code.get(i).get(f).length(); j++)
                {

                    //If it finds a quote, then it moves until it finds the other quote and adds it to the parameter stack.
                    if (code.get(i).get(f).charAt(j) == '"')
                    {

                        j++;
                        String temp = "";
                        while (code.get(i).get(f).charAt(j) != '"') {

                            temp += code.get(i).get(f).charAt(j);
                            j++;

                        }

                        if( getCharCount( temp, ',' ) > 0 )
                            temp = '"'+temp+'"';

                        paramstack.add(temp);

                    }

                    //If it finds a closed paren, then it knows that it needs to solve the last function.
                    if (code.get(i).get(f).charAt(j) == ')')
                    {

                        //If there is logic that must be solved, and there are not any more funcions in the function stack.
                        if (logicstack.size() > 0 && funcstack.size() <= 0) 
                        {

                            //Do a switch on the piece of logic in the back of the stack. Why there isn't an ArrayList.end function I will never know.
                            switch (logicstack.get(logicstack.size() - 1))
                            {

                                case "IF":
                                {

                                    while (opstack.size() > 0) {

                                        switch (opstack.get(opstack.size() - 1)) {

                                            //Checks for the various different operators.
                                            case "=":
                                            {

                                                //Grab two parameters, check if they are equal, return true or false and add it to the stack.
                                                String params[] = GETPARAMS(paramstack, 2);
                                                opstack.remove(opstack.size() - 1);
                                                logicansstack.add(Boolean.toString((params[0].equals(params[1]))));
                                                break;

                                            }
                                            case "!=":
                                            {

                                                String params[] = GETPARAMS(paramstack, 2);
                                                opstack.remove(opstack.size() - 1);
                                                logicansstack.add(Boolean.toString(!(params[0].equals(params[1]))));
                                                break;

                                            }
                                            case ">":
                                            {

                                                String params[] = GETPARAMS(paramstack, 2);

                                                if( !isNumeric(params[0]) || !isNumeric(params[1] ) )
                                                {

                                                    logicansstack.add( "null" );
                                                    ERROR_NON_NUMERIC_COMPARISON("");
                                                    opstack.remove( opstack.size()-1 );

                                                }
                                                else
                                                {

                                                    opstack.remove(opstack.size() - 1);
                                                    logicansstack.add( Boolean.toString(GREATER( params[0], params[1] ) ) );

                                                }
                                                break;

                                            }
                                            case ">=":
                                            {

                                                String params[] = GETPARAMS(paramstack, 2);

                                                if( !isNumeric(params[0]) || !isNumeric(params[1] ) )
                                                {

                                                    logicansstack.add( "null" );
                                                    ERROR_NON_NUMERIC_COMPARISON( "" );
                                                    opstack.remove( opstack.size()-1 );

                                                }
                                                else
                                                {

                                                    opstack.remove(opstack.size() - 1);
                                                    logicansstack.add( Boolean.toString(GREATEREQ(params[0], params[1]) ) );

                                                }
                                                break;

                                            }
                                            case "<":
                                            {

                                                String params[] = GETPARAMS(paramstack, 2);

                                                if( !isNumeric(params[0]) || !isNumeric(params[1] ) )
                                                {

                                                    logicansstack.add( "null" );
                                                    ERROR_NON_NUMERIC_COMPARISON( "" );
                                                    opstack.remove( opstack.size()-1 );

                                                }
                                                else
                                                {

                                                    opstack.remove(opstack.size() - 1);
                                                    logicansstack.add( Boolean.toString(LESS(params[0], params[1]) ) );

                                                }
                                                break;

                                            }
                                            case "<=":
                                            {

                                                String params[] = GETPARAMS(paramstack, 2);

                                                if( !isNumeric(params[0]) || !isNumeric(params[1] ) )
                                                {

                                                    logicansstack.add( "null" );
                                                    ERROR_NON_NUMERIC_COMPARISON( "" );
                                                    opstack.remove( opstack.size()-1 );

                                                }
                                                else
                                                {

                                                    opstack.remove(opstack.size() - 1);
                                                    logicansstack.add( Boolean.toString(LESSEQ( params[0], params[1] ) ) );

                                                }
                                                break;

                                            }
                                            case "&&":
                                            {

                                                String params[] = GETPARAMS(logicansstack, 2);
                                                opstack.remove(opstack.size() - 1);
                                                logicansstack.add(Boolean.toString(AND(params[0], params[1])));
                                                break;

                                            }
                                            case "||":
                                            {

                                                String params[] = GETPARAMS(logicansstack, 2);
                                                opstack.remove(opstack.size() - 1);
                                                logicansstack.add(Boolean.toString(OR(params[0], params[1])));
                                                break;

                                            }

                                        }

                                    }
                                    logicstack.remove(logicstack.size() - 1);

                                    //If the operators all result in a true statement.
                                    if ( logicansstack.size() > 0 && logicansstack.get(logicansstack.size() - 1).equals( "true" ) )
                                    {

                                        //The level of nesting starts at zero. It's all relative.
                                        int nstlvl = 0;

                                        //Set every line of this block of code to false, so it doesn't get executed.
                                        for( int tmp = 0; tmp < execlines.length; tmp++ )
                                        {

                                            execlines[tmp] = false;

                                        }

                                        //It then moves down the line in an effort to find the end of the if-statement.
                                        for( int tmp = f;; tmp++ )
                                        {

                                            //The current nest level increases with every open bracket it finds.
                                            nstlvl+=getCharCount( code.get(i).get(tmp), '{' );
                                            //And it decreases with every closed bracket it finds.
                                            nstlvl-=getCharCount( code.get(i).get(tmp), '}' );

                                            execlines[tmp] = true;

                                            if( (getCharCount( code.get(i).get(tmp), '}' ) == 1) && nstlvl == 0 )
                                                break;

                                        }


                                    }else if( logicansstack.size() > 0 && logicansstack.get( logicansstack.size()-1 ).equals("false") )
                                    {

                                        //This basically identical to the previous area. It just needs to find the else statement first.
                                        int tmp;
                                        int nstlvl = 0;
                                        boolean brkflag = false;

                                        //This is the loop that works to try and find the ELSE flag. If it doesn't find one, it quits the loop.
                                        for( tmp = f;; tmp++ )
                                        {
                                            //This breaks and calls the break flag to cancel out of the next for loop before it starts.
                                            if( tmp >= execlines.length-1 )
                                            {
                                                brkflag = true;
                                                break;
                                            }

                                            nstlvl+=getCharCount( code.get(i).get(tmp), '{' );
                                            nstlvl-=getCharCount( code.get(i).get(tmp), '}' );
                                            execlines[tmp] = false;

                                            if( nstlvl < 0 )
                                            {
                                                brkflag = true;
                                                break;
                                            }

                                            if( removeChar(code.get(i).get(tmp),'\t').equals("ELSE") && nstlvl == 0 )
                                                break;

                                        }

                                        nstlvl = 0;
                                        for (; !removeChar(code.get(i).get(tmp),'\t').equals("}") || nstlvl == 0; tmp++ )
                                        {

                                            if( brkflag )break;
                                            nstlvl+=getCharCount( code.get(i).get(tmp), '{' );
                                            nstlvl-=getCharCount(code.get(i).get(tmp), '}' );

                                            execlines[tmp] = true;
                                            if( getCharCount( code.get(i).get(tmp), '}' ) >= 1 && nstlvl == 0 )
                                                break;

                                        }

                                    }

                                    break;

                                }

                                case "SWITCH" :
                                {


                                    String comp[] = GETPARAMS( paramstack, 1 );
                                    int nstlvl = 0;

                                    boolean foundans = false;

                                    for( int tmp = 0; tmp < execlines.length; tmp++ )
                                        execlines[tmp] = false;

                                    for( int tmp = f;; tmp++ )
                                    {

                                        nstlvl += getCharCount( code.get(i).get(tmp), '{' );
                                        nstlvl -= getCharCount( code.get(i).get(tmp), '}' );

                                        if( getCharCount( code.get(i).get(tmp), '}' ) >= 1 && nstlvl == 0 )
                                            break;

                                        String lin = removeCharQuotes( code.get(i).get(tmp), '\t' );
                                        lin = removeCharQuotes( lin, ' ' );
                                        String ans = null;

                                        if( SUBSTR( lin, "1", "4" ).equals( "CASE" ) )
                                        {

                                            ans = SUBSTR( lin, "5", Integer.toString( lin.length()-1 ) );
                                            ans = removeCharQuotes( ans, '(' );
                                            ans = removeCharQuotes( ans, ')' );

                                            if( ans.equals( comp[0] ) )
                                            {

                                                foundans = true;

                                                int newnst = 0;
                                                for( int tmp2 = tmp; !(getCharCount( code.get(i).get(tmp2),'}')>=1 && newnst == 0); tmp2++ )
                                                {

                                                    newnst += getCharCount( code.get(i).get(tmp), '{' );
                                                    newnst -= getCharCount( code.get(i).get(tmp), '}' );
                                                    execlines[tmp2] = true;

                                                }
                                                execlines[tmp] = false;

                                            }

                                        }else if( SUBSTR( lin, "1", "4" ).equals("DEF") && !foundans )
                                        {

                                            int newnst = 0;
                                            for( int tmp2 = tmp; !(getCharCount( code.get(i).get(tmp2),'}')>=1 && newnst == 0); tmp2++ )
                                            {

                                                newnst += getCharCount( code.get(i).get(tmp), '{' );
                                                newnst -= getCharCount( code.get(i).get(tmp), '}' );
                                                execlines[tmp2] = true;

                                            }
                                            execlines[tmp] = false;

                                        }

                                    }

                                    break;

                                }

                            }

                        }

                        if (funcstack.size() > 0)
                            try {
                                switch (funcstack.get(funcstack.size() - 1)) {

                                    case "CONCAT": {

                                        //Get the two paramteres required for the function from the parameter stack.
                                        String params[] = GETPARAMS(paramstack, 2);
                                        //Remove the function from the funcstack.
                                        funcstack.remove(funcstack.size() - 1);
                                        //Add the answer to the function back into the parameter stack.
                                        paramstack.add(CONCAT(params[0], params[1]));
                                        break;

                                        //////////////////////////////////
                                        //All cases are exactly the same//
                                        //////////////////////////////////

                                    }
                                    case "SEQ": {

                                        String params[] = GETPARAMS(paramstack, 1);
                                        funcstack.remove(funcstack.size() - 1);
                                        paramstack.add(SEQ(params[0]));
                                        break;

                                    }
                                    case "FALSE": {

                                        logicansstack.add("false");
                                        funcstack.remove(funcstack.size() - 1);
                                        break;

                                    }
                                    case "TRUE": {

                                        logicansstack.add("true");
                                        funcstack.remove(funcstack.size() - 1);
                                        break;

                                    }
                                    case "ISNUMERIC": {

                                        String params[] = GETPARAMS(paramstack, 1);
                                        logicansstack.add(Boolean.toString(isNumeric(params[0])));
                                        funcstack.remove(funcstack.size() - 1);
                                        break;

                                    }
                                    case "LENGTH": {

                                        String params[] = GETPARAMS(paramstack, 1);
                                        paramstack.add(LENGTH(params[0]));
                                        funcstack.remove(funcstack.size() - 1);
                                        break;

                                    }
                                    case "SUBSTR": {

                                        String params[] = GETPARAMS(paramstack, 3);
                                        paramstack.add(SUBSTR(params[0], params[1], params[2]));
                                        funcstack.remove(funcstack.size() - 1);
                                        break;

                                    }
                                    case "PRINT": {

                                        String params[] = GETPARAMS(paramstack, 1);
                                        PRINT(params[0]);
                                        funcstack.remove(funcstack.size() - 1);
                                        break;

                                    }
                                    case "TODAY": {

                                        String params[] = GETPARAMS(paramstack, 1);
                                        funcstack.remove(funcstack.size() - 1);
                                        paramstack.add(TODAY(params[0]));
                                        break;

                                    }
                                    case "UPPER": {

                                        String params[] = GETPARAMS(paramstack, 1);
                                        funcstack.remove(funcstack.size() - 1);
                                        paramstack.add(UPPER(params[0]));
                                        break;

                                    }
                                    case "LOWER":
                                    {

                                        String params[] = GETPARAMS(paramstack, 1);
                                        funcstack.remove(funcstack.size() - 1);
                                        paramstack.add(LOWER(params[0]));
                                        break;

                                    }
                                    case "C":
                                    {

                                        String params[] = GETPARAMS(paramstack, 1);
                                        funcstack.remove(funcstack.size() - 1);
                                        paramstack.add(COL(Integer.parseInt(params[0])));
                                        break;
                                    }
                                    case "REPLACE":
                                    {

                                        String params[] = GETPARAMS(paramstack, 3);
                                        funcstack.remove(funcstack.size() - 1);
                                        paramstack.add(REPLACE(params[0], params[1], params[2]));
                                        break;

                                    }
                                    case "BLANK":
                                    {

                                        funcstack.remove(funcstack.size() - 1);
                                        paramstack.add("");
                                        break;

                                    }
                                    case "IGNORE":
                                    {

                                        funcstack.remove(funcstack.size() - 1);
                                        ignorerec = true;
                                        break;

                                    }
                                    case "FC":
                                    {

                                        String params[] = GETPARAMS(paramstack, 2);
                                        funcstack.remove( funcstack.size()-1 );
                                        paramstack.add( FC( params[0], params[1] ) );
                                        break;

                                    }
                                    case "GROUPTOTAL":
                                    {

                                        String params[] = GETPARAMS(paramstack, 1);
                                        funcstack.remove( funcstack.size()-1 );
                                        paramstack.add( GenUniques( params[0], i ) );
                                        break;

                                    }
                                    case "HEADER":
                                    {

                                        String params[] = GETPARAMS(paramstack, 1);
                                        funcstack.remove( funcstack.size()-1 );

                                        params = params[0].split(",");
                                        
                                        for( String head:params )
                                        {
                                        
                                            Headers.add( head );

                                        }

                                        break;

                                    }
                                    case "LOAD":
                                    {

                                        String params[] = GETPARAMS( paramstack, 1 );
                                        funcstack.remove( funcstack.size()-1 );

                                        keyMaster.LoadFile( params[0] );
                                        break;

                                    }
                                    case "EXISTS":
                                    {

                                        String params[] = GETPARAMS( paramstack, 3 );
                                        funcstack.remove( funcstack.size()-1 );

                                        logicansstack.add( keyMaster.checkExistence( params[0], params[1], params[2] ) );
                                        break;

                                    }
                                    case "LOOKUP":
                                    {

                                        String params[] = GETPARAMS( paramstack, 4 );
                                        funcstack.remove( funcstack.size()-1 );

                                        paramstack.add( keyMaster.Lookup( params[0], params[1], params[2], params[3] ) );

                                        break;

                                    }

                                }
                            }
                            catch( ArrayIndexOutOfBoundsException a )
                            {

                                if( first )ERROR_INVALID_PARAMNUM( funcstack.get(funcstack.size()-1 ), f );

                            }
                            catch( NumberFormatException a )
                            {

                                if( first )ERROR_INVALID_PARAM( funcstack.get( funcstack.size()-1 ), f );

                            }

                    }

                    //If the character is an accepted character, then add it to the holder string.
                    if( !ISONLIST( code.get(i).get(f).charAt(j), unwantedchars ) )
                    {
                        holder += code.get(i).get(f).charAt(j);
                    }

                    //If it is numeric and the character head of it is not numeric, it's a number.
                    //It's then added to the parameter stack.
                    if( isNumeric(holder) && !isNumeric( holder + code.get(i).get(f).charAt(j+1) ) )
                    {
                        if( !holder.isEmpty() )
                        {
                            paramstack.add(holder);
                        }

                        holder = "";
                    }

                    //Checking if the current holder 'buffer' matches entries in various accepted values.
                    //  For example, function names, operators, logic expression, etc.
                    if (ISONLIST(holder, oplist) && !ISONLIST( holder+code.get(i).get(f).charAt(j+1), oplist ) )
                    {

                        if (opstack.size() > 0 && GETPREC(holder ) > GETPREC(opstack.get(opstack.size() - 1))) {

                            String temp = opstack.get(opstack.size() - 1);
                            opstack.set(opstack.size() - 1, holder);
                            opstack.add(temp);

                        } else
                            opstack.add(removeChar(holder, ')'));

                        holder = "";
                    } else if (ISONLIST(holder, funclist) && code.get(i).get(f).charAt(j) == '(') {
                        funcstack.add( holder );
                        holder = "";
                    } else if (ISONLIST(holder, logiclist)) {
                        logicstack.add(holder);
                        holder = "";

                    }

                }

                if( paramstack.size() > 0 )
                {
                    output.get(output.size()-1).add(paramstack.get( paramstack.size()-1 ));
                    hasadded = true;
                }

            }

            if (!hasadded && !first )
            {
                output.get( output.size()-1 ).add( "" );
            }

        }

    } 

    public static void main( String []args ) throws IOException
    {

        //Creating the output stream itself for the log.
        log = new FileOutputStream( "log.txt");

        String importfile = "",exportfile = "";

        //Responsible for actually retrieving the paths.
        for( int i = 0; i < args.length; i++ )
        {

            if( args[i].equals("-import") )importfile = args[i+1];
            else if( args[i].equals("-export") )exportfile = args[i+1];
            else if( args[i].equals("-template") )transfile = args[i+1];
            else if( args[i].charAt(0) == '-' )
            {
                System.out.println( "Argument not recognized:"+args[i] );
            }

        }

        //The kill boolean specifies whether or not the program should be stopped. I.E. if it does not receive a file path.
        boolean kill = false;
        if( importfile.equals("") ){ System.out.println( "File location not received for -import" ); kill = true; }
        if( exportfile.equals("") ){ System.out.println( "File location not received for -export" ); kill = true; }
        if( transfile.equals("") ){ System.out.println( "File location not received for -template" ); kill = true; }

        //It keeps the program open for nine seconds so the user can contemplate on what they've done.
        if( kill )
        {
            System.exit(0);
        }

        long stime = System.currentTimeMillis();

        //Create the object to load in the info.
        BufferedReader in = new BufferedReader(new FileReader(importfile));

        //Make sure to initialize the String! Also loads in the first line of the file.
        String temp = in.readLine();

        //Split the columns based on a comma delimiter.
        ColumnHeaders = temp.split(",");
        columns = ColumnHeaders.length;

        while( true )
        {

            //Beginning to read the actual data inside the file.
            temp = in.readLine();
            //Returns null if it reaches the end of the file.
            if( temp == null )break;

            output.add( new ArrayList<String>() );

            //Delimit based on commas.
            String []tempar = temp.split( "," );
            ArrayList<String> usedlist = new ArrayList<>();

            for( String i:tempar )usedlist.add(i);

            //In the case that it reads in a quote, it concatenates the fields so that the quote contains all data it should.
            for( int i = 0; i < usedlist.size(); i++ )
            {

                //While there is only one quote in a field, it knows it must concatenate with the next field until it finds the other.
                while( getCharCount(usedlist.get(i), '"') == 1 )
                {

                    usedlist.set( i, CONCAT( usedlist.get(i), "," ) );
                    usedlist.set( i, CONCAT( usedlist.get(i), usedlist.get(i+1) ) );
                    usedlist.remove(i+1);

                }

            }

            //Set data equal to the arraylist that was created.
            data = usedlist;
            //Run the translate function to actually execute the code on each line of data.
            translate();

            if( ignorerec )
            {

                output.remove( output.size()-1 );
                ignorerec = !ignorerec;

            }

            //The amount of rows ++'s.
            rows+=1;

            //The first time the translate function has passed, thus first is set to false.
            first = false;

        }

        //If there is nothing in the output array, no point in going through the next code!.. it'll crash anyway ;^)
        if( output.size() <= 0 )
        {

            temp = "No columns in array. Ending... \r\n";
            System.out.println( temp );
            log.write( temp.getBytes() );

            System.exit(0);

        }else
        {
            
            for( int temprow : IDrows )
            {

                for( int i = 0; i < output.size(); i++ )
                {

                    for( Unique tempuni : countedIDs )
                    {

                        if( output.get(i).get(temprow).equals( tempuni.ID ) )
                        {
        
                            output.get(i).set(temprow, String.valueOf( tempuni.ocs ) );

                        }

                    }

                }

            }


        }

        //Initializing the output stream to create the CSV file.
        FileOutputStream outputter = new FileOutputStream(exportfile);

        //Writing the headers and so on. The asterick is added for Unanet.
        outputter.write( '*' );
        for( int i = 0; i < Headers.size(); i++ )
        {

            //If it finds that there are zero commas in a field, then it removes any quotes that may be there.
            if( getCharCount(Headers.get(i),',') == 0 )
            {

                Headers.set(i, removeChar( Headers.get(i), '"' ) );

            }

            //The headout String is created so that it can be formatted it it needs to be.
            String headout = Headers.get(i);
            if( i < Headers.size()-1 )
                headout+=',';
            else
                headout+="\r\n";

            outputter.write(headout.getBytes());

        }

        for( int i = 0; i < output.size(); i++ )
        {

            //Starting to print based on the size of the first column, as the size of all columns should be the same.
            for( int j = 0; j < output.get(0).size(); j++ )
            {

                if( output.get(i).size() <= 0 ) continue;

                //Writing the elements to the file. They first must be converted to bytes.
                byte[] tempbytes = output.get(i).get(j).getBytes();
                if( !output.get(i).get(j).isEmpty() )
                {
                    outputter.write(tempbytes );
                }

                //After it has printed a column that isnt't the last column, it prints a comma.
                if( j != output.get(0).size()-1 )
                {
                    outputter.write(',');
                }

            }

            //Add a universally supported line break at the end of each line.
            String linebrk = "\r\n";
            outputter.write(linebrk.getBytes());

        }

        //Used to calculate how long the process took in it's entirety.
        long etime = System.currentTimeMillis();
        System.out.println( "Process took: "+(etime-stime)/1000.0+"s." );

    }
}
