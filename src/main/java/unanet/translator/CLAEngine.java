package translator.main;

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

    public boolean checkArg( String name ) {
    	return !(getArg(name, false).isEmpty());
    }
    
    public String getArg( String name, boolean req )
    {
        for( CLA arg : clas )
        {
            if( arg.name.equals(name) )
            {
                if( arg.data == null || arg.data.isEmpty() && req )
                {
                	if( !arg.name.startsWith("--") )
                	{
                		System.out.println( name + " argument left blank." );
                        System.exit(2);
                	}else{
                		return " ";
                	}
                }
                return arg.data;
            }
        }

        if( req ) {
        	System.out.println( "Missing required argument " + name );
        	System.exit(2);
        }
        return "";
    }
}
