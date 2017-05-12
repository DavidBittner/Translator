import java.util.ArrayList;
import java.util.Scanner;

public class RPN
{

    static ArrayList<Answer> cache = new ArrayList<>();

    private static double mag( int am )
    {
        int ret = 1;

        for( int i = 0; i < am; i++ )
        {

            ret*=10;

        }

        return ret;
    }

    public static String Calc( String equ, int prec )
    {

        equ = equ.replace(" ", "");

        for( Answer i : cache )
        {
            if( i.ops.equals( equ ) )
            {
                return Double.toString( Math.round( i.ans*mag(prec) )/mag(prec) );
            }
        }

        double val = 0.0;
        val = EvalRPN( shuntingYard( tokenize( equ ) ) );

        cache.add( new Answer( equ, val ) );

        return Double.toString( Math.round(val*mag(prec))/mag(prec) );

    }

    public static boolean CheckFunc( String in )
    {

        String funcs[] = {"pi","e","sin","cos","tan","asin","acos","atan","abs","x","log","int","floor","ceil"};

        for( String i : funcs )
        {

            if( in.equals(i) )
            {

                return true;

            }

        }

        return false;

    }

    public static int getPrecedence( String op )
    {

        if( CheckFunc( op ) )
        {

            return 5;

        }

        switch( op )
        {

            case "(":
            {
                return 0;
            }
            case ")":
            {
                return 0;
            }
            case "^":
            {
                return 4;
            }
            case "*":
            {
                return 3;
            }
            case "/":
            {
                return 3;
            }
            case "+":
            {
                return 2;
            }
            case "-":
            {
                return 2;
            }

        }

        return 5;

    }

    public static boolean getAssoc( String op )
    {

        //false = right associativity
        //true  = left  associativity

        if( CheckFunc( op ) )
        {

            return true;

        }

        switch( op )
        {

            case "^":
            {
                return false;
            }
            case "*":
            {
                return true;
            }
            case "/":
            {
                return true;
            }
            case "+":
            {
                return true;
            }
            case "-":
            {
                return true;
            }

        }

        return false;

    }


    public static ArrayList<String> shuntingYard( ArrayList<String> input )
    {

        ArrayList<String> output = new ArrayList<>(0);
        ArrayList<String> operator = new ArrayList<>(0);

        boolean OpAlready = true;
        boolean AddSign = false;
        String sign = "";

        for( String i : input )
        {

            if( isNumeric( i ) || i.equals("x") )
            {

                if( AddSign )
                {

                    AddSign = false;
                    OpAlready = false;
                    output.add( sign + i );
                    sign = "";

                }
                else
                {
                    output.add( i );
                }

                OpAlready = false;

            }else if( !i.equals( "(" ) && !i.equals( ")" ) )
            {

                if( OpAlready && (i.equals("+") || i.equals("-")) )
                {

                    OpAlready = false;
                    AddSign = true;
                    sign = i;
                    continue;

                }else
                {

                    if( !CheckFunc( i ) )
                    {

                        OpAlready = true;

                    }

                }

                OpAlready = true;

                if( operator.size() > 0 )
                {

                    //left
                    if( getAssoc( i ) )
                    {

                        while( operator.size() > 0 )
                        {

                            if( getPrecedence( i ) <= getPrecedence( operator.get(operator.size()-1) ) )
                            {

                                output.add( operator.get( operator.size()-1 ) );
                                operator.remove( operator.size()-1 );

                            }else
                            {

                                break;

                            }

                        }
                        operator.add(i);

                    }else
                    {

                        while( operator.size() > 0 )
                        {

                            if( getPrecedence( i ) < getPrecedence( operator.get( operator.size()-1 ) ) )
                            {

                                output.add( operator.get( operator.size()-1 ) );
                                operator.remove( operator.size()-1 );

                            }else
                            {

                                break;

                            }

                        }

                        operator.add(i);

                    }

                }else
                {

                    operator.add( i );

                }

            }else
            {

                if( i.equals("(") )
                {

                    operator.add( "(" );

                }else if( i.equals(")") )
                {


                    while( operator.size() > 0 && !operator.get(operator.size()-1).equals("(") )
                    {

                        output.add( operator.get(operator.size()-1) );
                        operator.remove( operator.size()-1 );

                    }
                    if( operator.size() > 0 )
                    {
                        operator.remove( operator.size()-1 );

                    }

                }

            }

        }

        for( int i = operator.size()-1; i >= 0; i-- )
        {
            output.add( operator.get(i) );
        }

        return output;

    }

    public static ArrayList<Double> GetVals( int count, ArrayList<Double> in )
    {

        ArrayList<Double> out = new ArrayList<>();

        if( count > in.size() )
        {

            System.out.println( "Not enough values!" );
            NumberFormatException e = new NumberFormatException();
            throw e;

        }

        for( int i = 0; i < count; i++ )
        {

            out.add( in.get( in.size()-1 ) );
            in.remove( in.size()-1 );

        }

        return out;

    }

    public static double EvalRPN( ArrayList<String> input )
    {

        ArrayList<Double> output = new ArrayList<>();
        ArrayList<String> ops = new ArrayList<>();

        ArrayList<Double> vals = new ArrayList<>();

        for( String i : input )
        {

            if( isNumeric( i ) )
            {

                output.add( Double.parseDouble( i ) );

            }else
            {

                switch( i )
                {

                    case "+":
                    {

                        vals = GetVals( 2, output );
                        output.add( vals.get(0) + vals.get(1) );
                        break;

                    }
                    case "-":
                    {

                        vals = GetVals( 2, output );
                        output.add( vals.get(1) - vals.get(0) );
                        break;

                    }
                    case "*":
                    {

                        vals = GetVals( 2, output );
                        output.add( vals.get(1) * vals.get(0) );
                        break;

                    }
                    case "/":
                    {

                        vals = GetVals( 2, output );
                        output.add( vals.get(1) / vals.get(0) );
                        break;

                    }
                    case "^":
                    {

                        vals = GetVals( 2, output );
                        output.add( Math.pow( vals.get(1), vals.get(0) ) );
                        break;

                    }
                    case "sin":
                    {

                        vals = GetVals( 1, output );
                        output.add( Math.sin( vals.get(0) ) );
                        break;

                    }
                    case "cos":
                    {

                        vals = GetVals( 1, output );
                        output.add( Math.cos( vals.get(0) ) );
                        break;

                    }
                    case "tan":
                    {

                        vals = GetVals( 1, output );
                        output.add( Math.tan( vals.get(0) ) );
                        break;

                    }
                    case "log":
                    {

                        vals = GetVals( 1, output );
                        output.add( Math.log( vals.get(0) ) );
                        break;

                    }
                    case "e":
                    {

                        output.add(2.71828);
                        break;

                    }
                    case "pi":
                    {

                        output.add(3.14159);
                        break;

                    }
                    case "atan":
                    {

                        vals = GetVals( 1, output );
                        output.add( Math.atan( vals.get(0) ) );
                        break;

                    }
                    case "asin":
                    {

                        vals = GetVals( 1, output );
                        output.add( Math.asin( vals.get(0) ) );
                        break;

                    }
                    case "acos":
                    {

                        vals = GetVals( 1, output );
                        output.add( Math.acos( vals.get(0) ) );
                        break;

                    }
                    case "abs":
                    {

                        vals = GetVals( 1, output );
                        output.add( Math.abs( vals.get(0) ) );
                        break;

                    }
                    case "int":
                    {

                        vals = GetVals( 1, output );
                        output.add( (double)Math.round( vals.get(0) ) );
                        break;

                    }
                    case "ceil":
                    {

                        vals = GetVals( 1, output );
                        output.add( Math.ceil( vals.get(0) ));
                        break;

                    }
                    case "floor":
                    {

                        vals = GetVals( 1, output );
                        output.add( Math.floor( vals.get(0) ) );
                        break;

                    }
                    case "csc":
                    {

                        vals = GetVals( 1, output );
                        output.add( 1/Math.sin( vals.get(0) ) );
                        break;

                    }
                    case "sec":
                    {

                        vals = GetVals( 1, output );
                        output.add( 1/Math.cos( vals.get(0) ) );
                        break;

                    }
                    case "cot":
                    {

                        vals = GetVals( 1, output );
                        output.add( 1/Math.tan( vals.get(0) ) );
                        break;

                    }

                }

            }

        }

        if( output.size() > 1 )
        {

            NullPointerException e = new NullPointerException();

            throw e;

        }

        return output.get(0);

    }

    public static boolean isNumeric( String input )
    {

       try
       {

            Double.parseDouble( input );

       }
       catch( NumberFormatException e )
       {

            return false;

       }

       return true;

    }

    public static boolean isToken( String input )
    {

        String ops[] = {"+","-","/","*","^","(",")"};
        String funcs[] = {"pi","e","sin","cos","tan","asin","acos","atan","abs","x","log","int","floor","ceil","csc","sec","cot"};

        for( int i = 0; i < ops.length; i++ )
        {

            if( input.equals( ops[i] ) )
            {

                return true;

            }

        }

        for( int i = 0; i < funcs.length; i++ )
        {

            if( input.equals( funcs[i] ) )
            {

                return true;

            }

        }

        return false;

    }

    public static ArrayList<String> tokenize( String input )
    {

        input.replace( " ", "" );
        ArrayList<String> tokens = new ArrayList<>(0);

        String holder = "";

        for( int i = 0; i < input.length(); i++ )
        {

            if( isToken( holder ) && !holder.isEmpty() )
            {

                tokens.add( holder );
                holder = "";

                holder += input.charAt(i);

                continue;

            }

            if( ( isNumeric( holder ) != isNumeric( Character.toString(input.charAt(i)) ) ) && !holder.isEmpty() && input.charAt( i ) != '.' )
            {

                tokens.add(holder);
                holder = "";

                holder += input.charAt(i);

                continue;

            }

            holder += input.charAt(i);

        }

        tokens.add(holder);

        return tokens;

    }

}
