
import play.Application;
import play.GlobalSettings;

public class Global
        extends GlobalSettings
{

    @Override
    public void onStart( Application aplctn )
    {
        super.onStart( aplctn );
        // TODO Start Neo4J
    }

    @Override
    public void onStop( Application aplctn )
    {
        // TODO Stop Neo4J
        super.onStop( aplctn );
    }

}
