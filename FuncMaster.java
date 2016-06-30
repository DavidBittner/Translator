import java.util.*;

//Bringin' the func!
public class FuncMaster
{

    static LookupMaster keyMaster = new LookupMaster();
    static int seq = 0;

    private String []GrabParams( ArrayList<String> params, int count )
    {

        String ret[] = new String[count];

        for( int i = 0; i < count; i++ )
        {

            ret[(count-i)-1] = params.get( params.size()-1 );
            params.remove( params.size()-1 );

        }

        return ret;

    }

    private String Substr( String str, String sta, String sto )
    {

        int start = Integer.parseInt( sta );
        int stop = Integer.parseInt( sto );

        return str.substring( start, stop ); 

    }

    public void SeqInc()
    {

        seq++;

    }

    String CallFunc( ArrayList<String> rowData, ArrayList<String> paramstack, String funcName )
    {

       switch( funcName )
       {

            case "C":
            {
            
               String params[] = GrabParams( paramstack, 1 ); 
               return rowData.get( (Integer.parseInt( params[0] )-1)%rowData.size() );

            }
            case "CONCAT":
            {

                String params[] = GrabParams( paramstack, 2 );
                return params[0]+params[1];

            }
            case "HEADER":
            {

                Translator statTrans = new Translator();

                String params[] = GrabParams( paramstack, 1 );
                String heads[] = params[0].split(",");

                statTrans.GiveHeaders( heads );
                break;

            }
            case "LOAD":
            {

                String params[] = GrabParams( paramstack, 1 );
                keyMaster.LoadFile( params[0] );
                break;

            }
            case "SUBSTR":
            {

                String params[] = GrabParams( paramstack, 3 );
                return Substr( params[0], params[1], params[2] );

            }
            case "LENGTH":
            {

                String params[] = GrabParams( paramstack, 1 );
                return Integer.toString(params[0].length());

            }
            case "BLANK":
            {

                return "";

            }
            case "IGNORE":
            {

                Translator tempTrans = new Translator();
                tempTrans.IgnoreRecord();

                break;

            }
            case "ISNUMERIC":
            {

                break;

            }
            case "SEQ":
            {

                String params[] = GrabParams( paramstack, 1 );
                return Integer.toString( Integer.parseInt( params[0] )+seq );

            }
            case "TRUE":
            {

                return "true";

            }
            case "FALSE":
            {

                return "false";

            }
            case "LOWER":
            {

                String params[] = GrabParams( paramstack, 1 );
                return params[0].toLowerCase();

            }
            case "UPPER":
            {

                String params[] = GrabParams( paramstack, 1 );
                return params[0].toUpperCase();

            }
            case "EXISTS":
            {

                String params[] = GrabParams( paramstack, 3 );
                return keyMaster.checkExistence( params[0], params[1], params[2] );

            }
            case "LOOKUP":
            {

                String params[] = GrabParams( paramstack, 4 );
                return keyMaster.Lookup( params[0], params[1], params[2], params[3] );

            }
            default:
            {

                break;

            }

       }

       return "";

    }

    boolean solveLogic( ArrayList<String> logicstack, ArrayList<String> paramstack )
    {
    
        while( logicstack.size() > 0 )
        {
    
            String params[] = GrabParams( paramstack, 2 );
            String operator = logicstack.get( logicstack.size()-1 );
            logicstack.remove( logicstack.size()-1 );

            switch( operator )
            {

                case "=":
                {

                    paramstack.add( (params[0].equals(params[1]))?("true"):("false") );
                    break;

                }
                case ">":
                {
        
                    int a = Integer.parseInt( params[0] );
                    int b = Integer.parseInt( params[1] );

                    paramstack.add( (a<b)?("true"):("false") );
                    break;

                }
                case "<":
                {

                    int a = Integer.parseInt( params[0] );
                    int b = Integer.parseInt( params[1] );

                    paramstack.add( (a<b)?("true"):("false") );
                    break;

                }
                case ">=":
                {

                    int a = Integer.parseInt( params[0] );
                    int b = Integer.parseInt( params[1] );

                    paramstack.add( (a>=b)?("true"):("false") );
                    break;

                }
                case "<=":
                {

                    int a = Integer.parseInt( params[0] );
                    int b = Integer.parseInt( params[1] );

                    paramstack.add( (a<=b)?("true"):("false") );
                    break;

                }
                case "!=":
                {

                    paramstack.add(!(params[0].equals(params[1]))?("true"):("false"));
                    break;
                }

            }
        
        }
    
        if( paramstack.get( paramstack.size()-1 ).equals("true") )
        {

            return true;

        }else if( paramstack.get( paramstack.size()-1 ).equals("false") )
        {

            return false;

        }

        return false;
    
    } 

}
