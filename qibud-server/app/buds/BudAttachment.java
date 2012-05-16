package buds;

import java.io.IOException;
import java.io.OutputStream;

import com.mongodb.gridfs.GridFSDBFile;

public class BudAttachment
{

    public static final String IDENTITY = "bud_identity";

    private final GridFSDBFile gridFSFile;

    /* package */ BudAttachment( GridFSDBFile gridFSFile )
    {
        this.gridFSFile = gridFSFile;
    }

    public String getContentType()
    {
        return gridFSFile.getContentType();
    }

    public long getContentLength()
    {
        return gridFSFile.getLength();
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
