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

    public void addVar( String name )
    {
        vars.add( new Variable(name) );
    }

    public void setVar( String name, String dat )
    {
        for( Variable var : vars )
        {
            if(var.getName().equals(name))
            {
                var.setDat(dat);
                return;
            }
        }
    }

    public String getVar( String name )
    {
        for( Variable var : vars )
        {
            if(var.getName().equals(name))
            {
                return var.getDat();
            }
        }
        return "";
    }

}
