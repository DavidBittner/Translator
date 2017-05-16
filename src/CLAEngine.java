import java.util.ArrayList;

public class CLAEngine
{
    private ArrayList<CLA> clas;

    public CLAEngine( String []args )
    {
        clas = new ArrayList<>();

        CLA cachedArg = null;
        for( String arg : args )
        {
            if( arg.startsWith("-") )
            {
                clas.add(new CLA(arg));
                cachedArg = clas.get(clas.size()-1);
            }else
            {
                if( cachedArg == null )
                {
                    System.out.println( "ERROR No argument entered before string: " + arg );
                    return;
                }
                cachedArg.data = arg;
            }
        }
    }

    public String getArg( String name )
    {
        for( CLA arg : clas )
        {
            if( arg.name.equals(name) )
            {
                if( arg.data == null || arg.data.isEmpty() )
                {
                    System.out.println( name + " argument left blank." );
                    Translator.ExitProg(2);
                }
                return arg.data;
            }
        }

        System.out.println( "Missing required argument " + name );
        Translator.ExitProg(2);
        return "";
    }
}
