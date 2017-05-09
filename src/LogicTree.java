import java.util.ArrayList;

public class LogicTree
{

    private Code code;
    private ArrayList<LogicTree> branches;

    public LogicTree()
    {
        branches = new ArrayList<>();
    }

    public LogicTree( ArrayList<String> lines )
    {
        branches = new ArrayList<>();
        code.AddLines(lines);

        for( String line : lines )
        {
            if( line.contains("IF") )
            {
                
            }
        }
    }
}
