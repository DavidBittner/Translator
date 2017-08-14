package translator.main;
import java.util.*;
import java.text.*;

//Bringin' the func!
public class FuncMaster
{

    static int seq = 0;
    
    //This function grabs a certain amount of values from the given ArrayList
    //It also does error handling in case there aren't enough values available
    private String []GrabParams( ArrayList<String> params, int count )
    {
        if( count > params.size() )
        {
            new Error( "Not enough parameters. Need "+count+", have "+params.size()+"." );
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
    	if( a.isEmpty() )
    	{
    		return false;
    	}
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
        	new Error("Substring start value exceeds length of string.");
        }
        if( stop > str.length() )
        {
        	new Error("Substring end value exceeds length of string.");
        }

        if( start > stop )
        {
            return "";
        }

        return str.substring( start, stop );
    }

    private String leftTrim( String str ) {
        for( int i = 0; i < str.length(); i++ ) {
            if(!Character.isWhitespace(str.charAt(i))) {
                return str.substring( i, str.length() );
            }
        }
        return "";
    }

    private String rightTrim( String str ) {
        for( int i = str.length()-1; i >= 0; i-- ) {
            if(!Character.isWhitespace(str.charAt(i))) {
                return str.substring( 0, i+1 );
            }
        }
        return "";
    }

    String strip( String strip ) {
    	return strip.replace("^\\\"|\\\"$", "");
    }
    
    String destrip( String destrip, String prev ) {
    	if( prev.matches("^\\\"|\\\"$") ) {
        	return '"'+destrip+'"';
    	}else {
    		return destrip;
    	}
    }
    
    //This function is used if the SEQ function is called, basically just tracks the line that the program is currently on.
    public void SeqInc()
    {
        seq++;
    }

    //A switch statement that handles every function call.
    public String CallFunc( ArrayList<String> rowData, ArrayList<String> paramstack, String funcName )
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
                    int num = Integer.parseInt( params[0] )-1;
                    if( num >= rowData.size() || num < 0 ) {
                    	new Error("Invalid column number: " + params[0] + ". There are " + rowData.size() + " columns total.");
                    }
                    
                    return rowData.get( (Integer.parseInt( params[0] )-1) );

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

                    if( ret.length() < en )
                    {
                    	new Error("FC value exceeds length of row.");
                    	return "";
                    }else
                    {
                        return ret.substring( st, Math.min(en,ret.length() ) );
                    }
                }
                case "LOADMIN":
                {
                	String params[] = GrabParams( paramstack, 2 );
                	
                	Lookup temp = LookupFactory.findLookup( params[0] );
                	if( temp.getRowCount() < Integer.parseInt(params[1]) )
                	{
                		new Error("Row count of file: " + temp.getID() + " is below minimum (it is " + temp.getRowCount()+").");
                	}
                	return null;
                }
                case "LOADRELATIVEMIN":
                {
                	String params[] = GrabParams( paramstack, 2 );
                	Lookup temp = LookupFactory.findLookup( params[0] );
                	
                	if( temp.getRowCount() < Translator.rowCount-Integer.parseInt(params[1]) )
                	{
                		new Error("Row count of file: " + temp.getID() + " is below mimimum (it is " + temp.getRowCount()+").");
                	}
                	
                	return null;
                }
                case "SEARCH":
                {
                    String params[] = GrabParams( paramstack, 2 );

                    String ret = Integer.toString(strip(params[1]).indexOf(params[0]));
                    return ret;
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
                        Translator.UniqueRecords();
                    }
                    break;
                }
                case "CONCAT":
                {

                    //For concatenating.
                    String params[] = GrabParams( paramstack, 2 );
                    String a = strip(params[0]), b = strip(params[1]);
                    if( a.equals(params[0]) && b.equals(params[1])) {
                        return params[0]+params[1];
                    }else {
                    	return '"'+params[0]+params[1]+'"';
                    }
                    
                }
                case "HEADER":
                {

                    //This function sets the headers of the output file.
                    String params[] = GrabParams( paramstack, 1 );
                    String heads[] = params[0].split(",");

                    Translator.GiveHeaders( heads );
                    break;

                }
                case "LOAD":
                {

                    //This loads in another file for the EXISTS and LOOKUP functions.
                    String params[] = GrabParams( paramstack, 2 );
                    
                    LookupFactory.LoadFile( params[0], params[1] );
                    return null;

                }
                case "SUBSTR":
                {

                    //A substring function.
                    String params[] = GrabParams( paramstack, 3 );
                    return destrip(Substr( strip(params[0]), params[1], params[2] ), params[0]);

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

                    return RPN.Calc( params[0], prec );

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
                    Translator.IgnoreRecord();

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
                    return LookupFactory.checkExistence( params[0], params[1], params[2] );

                }
                case "LOOKUP":
                {

                    //This looks into another file and returns an entry from that column, if it exists.
                    String params[] = GrabParams( paramstack, 4 );
                    return LookupFactory.Lookup( params[0], params[1], params[2], params[3] );

                }
                case "GROUPTOTAL":
                {

                    //Used to count the amount of unique occurences of something such as a username.
                    String params[] = GrabParams( paramstack, 1 );

                    Translator.UniqueColNum();

                    UniqueFactory.giveKey( params[0] );
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

                    return destrip(strip(params[0]).replace( params[1], params[2] ), params[0]);

                }
                case "ISNUMERIC":
                {

                    //Checks whether or not a string is a number.
                    String params[] = GrabParams( paramstack, 1 );

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
                case "LOGICONLY":
                {
                    return null;
                }
                case "LTRIM":
                {
                    String params[] = GrabParams( paramstack, 1 );
                    return leftTrim( params[0] );
                }
                case "RTRIM":
                {
                    String params[] = GrabParams( paramstack, 1 );
                    return rightTrim( params[0] );
                }
                case "TRIM":
                {
                    String params[] = GrabParams( paramstack, 1 );
                    return params[0].trim();
                }
                case "ASCII":
                {
                	String params[] = GrabParams( paramstack, 1 );
                	String temp = params[0].replaceAll( "[^\\x20-\\x7F]", "");
                	if( !temp.equals(params[0]) ) {
                    	new Error("Invalid characters found", true);
                	}
                	return temp;
                }
                default:
                {
                	new Error("Unrecognized function... you should never see this.");
                    return null;
                }
           }
        }
        catch( NumberFormatException e )
        {

            new Error( "Invalid value received for "+funcName+"().");

        }

       return null;

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
