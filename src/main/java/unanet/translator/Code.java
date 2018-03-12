package unanet.translator;
import java.util.*;

public class Code
{

    //The various lists of accepted inputs.
    static String logiclist[] = { "IF","ELSE","SWITCH","ELSEIF" };
    static char wantedchars[] = { '(', ')', '{', '}' };
    static String oplist[] = { "=", "!=", "<", ">", "<=", ">=", "&&", "||" };
    static String funclist[] = { "SEQ","SEARCH","TODAY","CONCAT","SUBSTR","LENGTH","MATH","RIGHT","REPLACE","C","TRUE","FALSE","PRINT","UPPER","LOWER","LEFT","ISNUMERIC","BLANK", "IGNORE", "FC", "UNIQUE", "GROUPTOTAL","HEADER","LOAD","EXISTS","LOOKUP","NEWVAR","GETVAR","SETVAR","LOGICONLY","RTRIM","LTRIM","TRIM","LOADMIN","LOADRELATIVEMIN","ASCII" };

    FuncMaster funcs = new FuncMaster();

    private ArrayList<String> lines;
    private boolean execlines[];

    public Code() {
        lines = new ArrayList<>();
    }

    //Function used to add lines to this block of code.
    public void AddLine( String line ) {
        lines.add( line );
    }

    //Function to check whether or not a character is on the list of wanted characters.
    private boolean OnList( char list[], char check ) {
        for( char i : list )
        {
            if( check == i )
            {
                return true;
            }
        }
        return false;
    }

    //For checking if other things are on the other lists.
    private boolean OnList( String list[], String check ) {
        for( String i : list )
        {
            if( i.equals( check ) )
            {
                return true;
            }
        }
        return false;
    }

    //For checking all of them at once.
    private boolean CheckAllLists( String check ) {
        boolean found = false;
        found = (found || OnList( logiclist, check ) );
        found = (found || OnList( oplist, check ) );
        found = (found || OnList( funclist, check ) );

        return found;
    }

    private boolean IsNumeric( String str ) {
        for( char i : str.toCharArray() ) {
        	if( !Character.isDigit(i) ) {
        		return false;
        	}
        }

        return true;
    }

    //This is for replacing a character in a string, but only outside of quotes.
    String ReplaceOutsideQuotes( String str, char charac ) {
        StringBuilder strbuild = new StringBuilder( str );

        for( int i = 0; i < strbuild.length(); i++ )
        {
            if( strbuild.charAt(i) == '"' )
            {
                i++;
                while( strbuild.charAt(i)!='"' && i < strbuild.length() )
                {
                    i++;
                }
            }

            if( i >= strbuild.length() ) i = 0;
            if( strbuild.charAt(i) == charac )
            {
                strbuild.deleteCharAt( i );
                i--;
            }
        }

        return strbuild.toString();
    }

    int GetCharCount( String str, char ch ) {
        int count = 0;

        for( int i = 0; i < str.length(); i++ )
        {
            if( str.charAt(i) == ch )
            {
                count++;
            }
        }

        return count;
    }

    //This function is for tokenizing the line of code its given.
    public ArrayList<String> TokenizeLine( String line ) {

        if(line.replace(" ","").replace("\t","").startsWith( "#" )) {
            return new ArrayList<>();
        }

        line = line.replace( Character.toString( '\t' ), "" );
        line = ReplaceOutsideQuotes( line, ' ' );

        String holder = "";
        ArrayList<String> output = new ArrayList<>();

        //This turns the line into a bunch of tokens, iterating through each character and checking if it exists in one of the list given at the beginning of the file.
        for( int i = 0; i < line.length(); i++ ) {
            if( line.charAt(i) == ',' ) {
                if( !holder.isEmpty() ) {
                    output.add( holder );
                    holder = "";
                }
                continue;
            }

            if( IsNumeric( holder ) && !IsNumeric( holder+line.charAt(i) ) ) {
                output.add( holder );
                holder = "";
            }

            if( line.charAt(i) == '"' ) {
                String temp = "";
                temp+='"';
                i++;

                while( line.charAt(i) != '"' ) {
                    temp+=line.charAt(i);
                    i++;
                }

                temp += '"';
                output.add( temp );
                holder = "";
                continue;

            }else if( OnList( wantedchars, line.charAt(i) ) ) {
                output.add( Character.toString( line.charAt(i) ) );
            }else {
                holder+=line.charAt(i);
            }

            if( OnList( oplist, holder ) && OnList( oplist, holder+line.charAt(i+1) ) ) {
                continue;
            }

            if( CheckAllLists( holder ) ) {
                if( OnList( funclist, holder ) && line.charAt(i+1) == '(' )
                {
                    output.add( holder );
                    holder = "";
                }else if( !OnList( funclist, holder ) )
                {
                    output.add( holder );
                    holder = "";
                }
            }
        }

        return output;

    }

    //This is for resolving logic statements. It tells the class which lines to execute.
    //This is all a bit brute-forced, but I struggle to think of an elegant way to do this.
    private void LogicStatement( String statement, boolean res, String inp, int curline )
    {
        for( int i = 0; i < execlines.length; i++ )
        {
            execlines[i] = false;
        }

        //This switch goes through the logic possibilities
        switch( statement )
        {
            case "IF":
            {
                //The nest level variable determines how many layers of nesting the function has gone through, it's used to determine when it's back to the right level of curly brackets.
                int nstlvl = 0;

                if( !res )
                {
                    //If the resolution is false then it moves until it can find the else statement
                    do
                    {
                        curline++;
                        nstlvl+=GetCharCount( lines.get(curline), '{' );
                        nstlvl-=GetCharCount( lines.get(curline), '}' );

                        if( curline == lines.size()-1 )
                        {
                            nstlvl = -1;
                            break;
                        }

                    }while( nstlvl != 0 || !lines.get(curline).contains("ELSE") );
                }

                //If the nstlvl was set to -1, it knows it failed.
                if( nstlvl != -1 )
                {

                    nstlvl = 0;
                    do
                    {
                        curline++;
                        nstlvl+=GetCharCount( lines.get(curline), '{' );
                        nstlvl-=GetCharCount( lines.get(curline), '}' );

                        execlines[curline] = true;
                    }while( nstlvl != 0 );
                }
                break;
            }
            case "SWITCH":
            {
                //These two ArrayLists are to hold the options of the switch statement, as well as their associated lines.
                ArrayList<String> opts = new ArrayList<>();
                ArrayList<Integer> optslines = new ArrayList<>();

                //The templine is to iterate through finding the potential options.
                int templine = curline;

                int nstlvl = 0;
                boolean def = false;

                do
                {
                    templine++;
                    nstlvl+=GetCharCount( lines.get(templine), '{' );
                    nstlvl-=GetCharCount( lines.get(templine), '}' );

                    if( lines.get( templine ).contains( "CASE" ) )
                    {
                        int charnum = 0;
                        String holder = "";
                        boolean found = false;

                        //This is to determine whether or not there is a default statement
                        if( lines.get(templine).contains( "DEFAULT" ) )
                        {
                            opts.add( "DEFAULT" );
                            optslines.add( templine );
                            def = true;
                        }
                        else
                        {

                            for( int i = 0; i < 2; i++ )
                            {
                                while(lines.get(templine).charAt(charnum) != '"')
                                {
                                    if( found )
                                    {
                                        holder+=lines.get(templine).charAt(charnum);
                                    }
                                    charnum++;
                                }
                                charnum++;
                                found = true;
                            }

                            opts.add( holder );
                            optslines.add( templine );
                        }
                    }

                }while( nstlvl != 0 );

                if( opts.size() == 0 )
                {
                    new Error( "No cases for switch statement found. (Perhaps you did 'case' instead of 'CASE'?)");
                }

                int stline = -1;
                int defline = 0;
                for( int i = 0; i < opts.size(); i++ )
                {
                    if( opts.get(i).equals(inp) )
                    {
                       stline = optslines.get(i);
                       break;
                    }

                    if( opts.get(i).equals("DEFAULT") )
                    {
                        defline = optslines.get(i);
                    }
                }

                //If stline is set to -1, then it knows that the input did not match any of the cases in the switch statement.
                if( stline != -1 )
                {
                    curline = stline;
                    nstlvl = 0;
                    do
                    {

                        curline++;
                        nstlvl+=GetCharCount( lines.get(curline), '{' );
                        nstlvl-=GetCharCount( lines.get(curline), '}' );

                        execlines[curline] = true;

                    }while( nstlvl != 0 );

                }else if( def )
                {

                    //In the case of there being a default statement, it executes that code.
                    curline = defline;
                    nstlvl = 0;
                    do
                    {
                        curline++;
                        nstlvl+=GetCharCount( lines.get(curline), '{' );
                        nstlvl-=GetCharCount( lines.get(curline), '}' );

                        execlines[curline] = true;
                    }while( nstlvl != 0 );
                }
            }
        }
    }

    public String Execute( ArrayList<String> data ) {

        //The stacks that hold functions, parameters and so on.
        ArrayList<String> paramstack = new ArrayList<String>();
        ArrayList<String> logicstack = new ArrayList<String>();
        ArrayList<String> funcstack = new ArrayList<String>();

        //This array tells whether or not a function should be executed.
        //It's purpose is just for the logic, determining where to execute what.
        execlines = new boolean[lines.size()];

        for( int i = 0; i < lines.size(); i++ )
        {
            execlines[i] = true;
        }

        //Iterating through every line of code in this section.
        int tracker = 0;

        for( String line : lines )
        {
            Translator.CountLine();
            
            //If this line isn't supposed to be executed or its empty, skip it.
            if( !execlines[tracker] || line.isEmpty() )
            {
                tracker++;
                continue;
            }

            Error.curLine( line );

            //This tokenizes the current line, returning functions and parameters seperated nicely.
            ArrayList<String> tokens = TokenizeLine( line );

            for( String iter : tokens )
            {

                if( IsNumeric( iter ) && !iter.isEmpty() )
                {
                    //If the value is a number, it gets added as a parameter.
                    paramstack.add( iter );
                }else if( GetCharCount( iter, '"' ) > 0 )
                {
                    //If it has quotes in it, then it's also a parameter, just a string.
                    paramstack.add( iter.replace( Character.toString('"'), "" ) );
                }else if( OnList( funclist, iter ) || OnList( logiclist, iter ) )
                {
                    //If its a function, it gets added to the function stack.
                    funcstack.add( iter );
                }else if( OnList( oplist, iter ) )
                {
                    logicstack.add( iter );
                }else if( iter.equals( ")" ) )
                {
                	if( funcstack.size() == 0 ) {
                		new Error("Invalid function, or too many ')'.");
                	}
                	
                    if( OnList( funclist, funcstack.get( funcstack.size()-1 ) ) )
                    {

                        //This executes the most recent function in the function stack.
                        String res = funcs.CallFunc( data, paramstack, funcstack.get(funcstack.size()-1) );
                        Translator.curFunc = "";

                        if( res != null )
                        {
                            paramstack.add( res );
                        }

                        funcstack.remove( funcstack.size()-1 );
                    }
                    else if( OnList( logiclist, funcstack.get( funcstack.size()-1 ) ) )
                    {
                        //This is determining what kind of logic function it is. If, or switch.
                        if( funcstack.get( funcstack.size()-1 ).equals( "IF" ) )
                        {
                            //Gets the results from the queued up logic functions
                            boolean res = funcs.solveLogic( logicstack, paramstack );
                            LogicStatement( funcstack.get( funcstack.size()-1 ), res, "", tracker );
                            
                            paramstack.remove(paramstack.size()-1);
                            funcstack.remove( funcstack.size()-1 );
                        }else if( funcstack.get( funcstack.size()-1 ).equals( "SWITCH" ) )
                        {
                            String param = paramstack.get( paramstack.size()-1 );
                            paramstack.remove( paramstack.size()-1 );

                            LogicStatement( funcstack.get( funcstack.size()-1 ), false, param, tracker );

                            funcstack.remove( funcstack.size()-1 );
                        }else
                        {
                            //Not sure how this could be called, but I'm handling it just in case
                            new Error( "Unrecognized logic type: "+funcstack.get(funcstack.size()-1)+".");
                        }
                    }
                }
            }
            tracker++;
        }

        if( funcstack.size() > 0 )
        {
            new Error( funcstack.size() + " unsolved function(s) left. (Did you forget a ')'?");
        }

        for( int i = 0; i < paramstack.size(); i++ )
        {
            if( paramstack.get(i) != null && paramstack.get(i).isEmpty())
            {
                paramstack.remove(i);
                i--;
            }
        }

        if( paramstack.size() > 0 )
        {
            return paramstack.get(paramstack.size()-1);
        }else
        {
            return "";
        }
    }

    public int GetSize()
    {
        return lines.size();
    }
}
