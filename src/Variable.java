public class Variable
{
    private String dat;
    private String name;

    public Variable( String name )
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public String getDat()
    {
        return dat;
    }

    public void setDat( String dat )
    {
        this.dat = dat;
    }
}
