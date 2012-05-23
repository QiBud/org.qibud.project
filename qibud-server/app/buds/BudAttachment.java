package buds;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import com.mongodb.gridfs.GridFSDBFile;

public class BudAttachment
{

    public static final String IDENTITY = "bud_identity";

    public static final String ORIGINAL_FILENAME = "bud_original_filename";

    private final GridFSDBFile gridFSFile;

    /* package */ BudAttachment( GridFSDBFile gridFSFile )
    {
        this.gridFSFile = gridFSFile;
    }

    public String id()
    {
        return gridFSFile.getId().toString();
    }

    public String filename()
    {
        if ( gridFSFile.containsField( ORIGINAL_FILENAME ) ) {
            return ( String ) gridFSFile.get( ORIGINAL_FILENAME );
        }
        return gridFSFile.getFilename();
    }

    public String md5()
    {
        return gridFSFile.getMD5();
    }

    public String contentType()
    {
        return gridFSFile.getContentType();
    }

    public long contentLength()
    {
        return gridFSFile.getLength();
    }

    public Map<String, ?> metadata()
    {
        return gridFSFile.getMetaData().toMap();
    }

    public void writeTo( OutputStream outputStream )
            throws IOException
    {
        gridFSFile.writeTo( outputStream );
    }

    @Override
    public String toString()
    {
        return "BudAttachment[" + gridFSFile.getFilename() + "]";
    }

}
