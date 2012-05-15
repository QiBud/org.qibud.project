package buds;

public class BudsFactory
{

    private static BudsFactory instance;

    public static synchronized BudsFactory getInstance()
    {
        if ( instance == null ) {
            instance = new BudsFactory();
        }
        return instance;
    }

    private BudsFactory()
    {
    }

}
