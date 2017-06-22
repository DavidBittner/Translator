package translator.main;
import java.util.ArrayList;

public class VariableFactory
{
    private static ArrayList<Variable> vars;

    public VariableFactory()
    {
        if( vars == null )
        {
            vars = new ArrayList<>();
        }
    }

    public static void addVar( String name )
    {
        vars.add( new Variable(name) );
    }

    public static void setVar( String name, String dat )
    {
        for( Variable var : vars )
        {
            if(var.getName().equals(name))
            {
                var.setDat(dat);
                return;
            }
        }
        new Error("Cannot find variable "+name, 0);
    }

    public static String getVar( String name )
    {
        for( Variable var : vars )
        {
            if(var.getName().equals(name))
            {
                return var.getDat();
            }
        }
        new Error("Cannot find variable "+name, 0);
        return "";
    }
}
