package utils;

public class QiBudException
        extends RuntimeException
{

    public QiBudException( String string )
    {
        super( string );
    }

    public QiBudException( Throwable thrwbl )
    {
        super( thrwbl.getMessage(), thrwbl );
    }

    public QiBudException( String string, Throwable thrwbl )
    {
        super( string, thrwbl );
    }

}
