import java.util.*;
import java.text.*;

//Bringin' the func!
public class FuncMaster
{

    //Definitely not a Matrix reference...
    static LookupFactory keyMaster = new LookupFactory();
    static int seq = 0;

    //This function grabs a certain amount of values from the given ArrayList
    //It also does error handling in case there aren't enough values available
    private String []GrabParams( ArrayList<String> params, int count )
    {

        if( count > params.size() )
        {

            Error er = new Error( "Not enough parameters. Need "+count+", have "+params.size()+".", 2 );

        }

        String ret[] = new String[count];

        for( int i = 0; i < count; i++ )
        {

            ret[(count-i)-1] = params.get( params.size()-1 );
            params.remove( params.size()-1 );

        }

        return ret;

    }

    //Checking whether or not a string is numeric, I.E. 1523 is true.
    private boolean isNumeric( String a )
    {
        for( int i = 0; i < a.length(); i++ )
        {
            if( !Character.isDigit( a.charAt(i) ) )
            {
                return false;
            }
        }
        return true;
    }

    //Modified substr that takes strings as parameters, nothing else.
    private String Substr( String str, String sta, String sto )
    {

        int start = Integer.parseInt( sta );
        int stop = Integer.parseInt( sto );

        if( start > str.length() )
        {
            start = str.length();
        }
        if( stop > str.length() )
        {
            stop = str.length();
        }

        if( start > stop )
        {
            return "";
        }

        return str.substring( start, stop );

    }

    //This function is used if the SEQ function is called, basically just tracks the line that the program is currently on.
    public void SeqInc()
    {
        seq++;
    }

    //A switch statement that handles every function call.
    String CallFunc( ArrayList<String> rowData, ArrayList<String> paramstack, String funcName )
    {

        Translator.curFunc = funcName;
        try
        {
            switch( funcName )
            {

                case "C":
                {

                    if( rowData.size() <= 0 )
                    {
                        return "";
                    }

                    //This function simply returns a column from the dataset.
                    String params[] = GrabParams( paramstack, 1 );
                    return rowData.get( (Integer.parseInt( params[0] )-1)%rowData.size() );

                }
                case "FC":
                {

                    //This is for comma-less files. It returns substrings of the entire dataset instead of columns.
                    String params[] = GrabParams( paramstack, 2 );
                    int st = Integer.parseInt( params[0] );
                    int en = Integer.parseInt( params[1] );
                    int tracker = 0;
                    String ret = "";

                    while( ret.length() < st+en && tracker < rowData.size() )
                    {

                        ret = ret+rowData.get(tracker);
                        tracker++;

                    }

                    return ret.substring( st, en );

                }
                case "SEARCH":
                {
                    String params[] = GrabParams( paramstack, 2 );

                    for( int i = 0; i < params[0].length()-params[1].length(); i++ )
                    {
                        if( params[0].substring( i, i+params[1].length() ).equals( params[1] ) )
                        {
                            return Integer.toString(i);
                        }
                    }
                    return "0";
                }
                case "PRINT":
                {

                    String params[] = GrabParams( paramstack, 1 );
                    System.out.println( params[0] );
                    return "";

                }
                case "UNIQUE":
                {

                    String params[] = GrabParams( paramstack, 1 );
                    if( params[0].equals( "true" ) )
                    {
                        Translator uniqueCall = new Translator();
                        uniqueCall.UniqueRecords();
                    }
                    break;


                }
                case "CONCAT":
                {

                    //For concatenating.
                    String params[] = GrabParams( paramstack, 2 );
                    return params[0]+params[1];

                }
                case "HEADER":
                {

                    //This function sets the headers of the output file.
                    Translator statTrans = new Translator();

                    String params[] = GrabParams( paramstack, 1 );
                    String heads[] = params[0].split(",");

                    statTrans.GiveHeaders( heads );
                    break;

                }
                case "LOAD":
                {

                    //This loads in another file for the EXISTS and LOOKUP functions.
                    String params[] = GrabParams( paramstack, 2 );
                    keyMaster.LoadFile( params[0], params[1] );
                    break;

                }
                case "SUBSTR":
                {

                    //A substring function.
                    String params[] = GrabParams( paramstack, 3 );
                    return Substr( params[0], params[1], params[2] );

                }
                case "LENGTH":
                {

                    //Returns the length of the given string.
                    String params[] = GrabParams( paramstack, 1 );
                    return Integer.toString(params[0].length());

                }
                case "MATH":
                {

                    String params[] = GrabParams( paramstack, 2 );
                    int prec = Integer.parseInt( params[1] );

                    RPN calc = new RPN();
                    return calc.Calc( params[0], prec );

                }
                case "RIGHT":
                {

                    String params[] = GrabParams( paramstack, 2 );
                    int val = Integer.parseInt( params[0] );

                    return params[1].substring( (params[1].length())-val, params[1].length() );

                }
                case "LEFT":
                {

                    String params[] = GrabParams( paramstack, 2 );
                    int val = Integer.parseInt( params[0] );

                    return params[1].substring( 0, val );

                }
                case "BLANK":
                {

                    //Returns a blank string.
                    return "";

                }
                case "IGNORE":
                {

                    //Ignores the entire row from the output file.
                    Translator tempTrans = new Translator();
                    tempTrans.IgnoreRecord();

                    break;

                }
                case "SEQ":
                {

                    //Returns a 'counter'. It starts at whatever value you supply as an argument.
                    String params[] = GrabParams( paramstack, 1 );
                    return Integer.toString( Integer.parseInt( params[0] )+seq );

                }
                case "TRUE":
                {

                    //Returns true for logic.
                    return "true";

                }
                case "FALSE":
                {

                    //Returns false for logic.
                    return "false";

                }
                case "LOWER":
                {

                    //Makes strings lowercase.
                    String params[] = GrabParams( paramstack, 1 );
                    return params[0].toLowerCase();

                }
                case "UPPER":
                {

                    //Makes strings uppercase.
                    String params[] = GrabParams( paramstack, 1 );
                    return params[0].toUpperCase();

                }
                case "EXISTS":
                {

                    //This function returns true or false if an entry already exists in the given file.
                    String params[] = GrabParams( paramstack, 3 );
                    return keyMaster.checkExistence( params[0], params[1], params[2] );

                }
                case "LOOKUP":
                {

                    //This looks into another file and returns an entry from that column, if it exists.
                    String params[] = GrabParams( paramstack, 4 );
                    return keyMaster.Lookup( params[0], params[1], params[2], params[3] );

                }
                case "GROUPTOTAL":
                {

                    //Used to count the amount of unique occurences of something such as a username.
                    Translator tempTrans = new Translator();

                    UniqueFactory tempUniq = new UniqueFactory();
                    String params[] = GrabParams( paramstack, 1 );

                    tempTrans.UniqueColNum();

                    tempUniq.giveKey( params[0] );
                    return params[0];

                }
                case "TODAY":
                {

                    //Returns the current date in the given format.
                    String params[] = GrabParams( paramstack, 1 );
                    Date curDat = new Date();
                    SimpleDateFormat form = new SimpleDateFormat( params[0] );

                    return form.format( curDat );

                }
                case "REPLACE":
                {

                    //For replacing things inside strings.
                    String params[] = GrabParams( paramstack, 3 );

                    return params[0].replace( params[1], params[2] );

                }
                case "ISNUMERIC":
                {

                    //Checks whether or not a string is a number.
                    String params[] = GrabParams( paramstack, 1 );
                    Integer.parseInt( params[0] );

                    for( int i = 0; i < params[0].length(); i++ )
                    {

                        if( !Character.isDigit( params[0].charAt(i) ) )
                        {
                            return "false";
                        }

                    }
                    return "true";
                }
                case "NEWVAR":
                {
                    //Creates a new variable.
                    String params[] = GrabParams( paramstack, 1 );
                    VariableFactory.addVar( params[0] );
                    return null;
                }
                case "GETVAR":
                {
                    //Retrieves data from a variable
                    String params[] = GrabParams( paramstack, 1 );
                    return VariableFactory.getVar( params[0] );
                }
                case "SETVAR":
                {
                    //Sets a variable
                    String params[] = GrabParams( paramstack, 2 );
                    VariableFactory.setVar( params[0], params[1] );
                    return null;

                }
                default:
                {
                    return null;
                }
           }
        }
        catch( NumberFormatException e )
        {

            Error er = new Error( "Invalid value received for "+funcName+"().", 1 );

        }

       return "";

    }

    boolean solveLogic( ArrayList<String> logicstack, ArrayList<String> paramstack )
    {

        //This function is given a list of logic operators and determines the answer to them.
        while( logicstack.size() > 0 )
        {

            String params[] = GrabParams( paramstack, 2 );
            String operator = logicstack.get( logicstack.size()-1 );
            logicstack.remove( logicstack.size()-1 );

            switch( operator )
            {
                case "=":
                {

                    //Who needs if statements?
                    paramstack.add( (params[0].equals(params[1]))?("true"):("false") );
                    break;

                }
                case ">":
                {

                    if( isNumeric( params[0] ) && isNumeric( params[1] ) )
                    {
                        int a = Integer.parseInt( params[0] );
                        int b = Integer.parseInt( params[1] );

                        paramstack.add( (a>b)?("true"):("false") );

                     }else
                     {

                        paramstack.add( (params[0].compareTo(params[1]) > 0 )?("true"):("false") );

                     }
                     break;

                }
                case "<":
                {

                    if( isNumeric( params[0] ) && isNumeric( params[1] ) )
                    {
                        int a = Integer.parseInt( params[0] );
                        int b = Integer.parseInt( params[1] );

                        paramstack.add( (a<b)?("true"):("false") );

                     }else
                     {

                        int ret = params[0].compareTo( params[1] );
                        paramstack.add( (ret < 0)?("true"):("false") );

                     }
                     break;

                }
                case ">=":
                {

                    if( isNumeric( params[0] ) && isNumeric( params[1] ) )
                    {
                        int a = Integer.parseInt( params[0] );
                        int b = Integer.parseInt( params[1] );

                        paramstack.add( (a>=b)?("true"):("false") );

                     }else
                     {

                        paramstack.add( (params[0].compareTo(params[1]) >= 0 )?("true"):("false") );

                     }
                     break;

                }
                case "<=":
                {

                    if( isNumeric( params[0] ) && isNumeric( params[1] ) )
                    {
                        int a = Integer.parseInt( params[0] );
                        int b = Integer.parseInt( params[1] );

                        paramstack.add( (a<=b)?("true"):("false") );

                     }else
                     {

                        int ret = params[0].compareTo( params[1] );
                        paramstack.add( (ret <= 0)?("true"):("false") );

                     }
                     break;

                }
                case "!=":
                {

                    paramstack.add(!(params[0].equals(params[1]))?("true"):("false"));
                    break;
                }

            }

        }
        return (paramstack.get(paramstack.size()-1).equals("true"))?( true ):( false );
    }
}
