package buds;

import com.mongodb.gridfs.GridFSDBFile;

public class BudAttachment
{

    private final GridFSDBFile gridFSFile;

    /* package */ BudAttachment( GridFSDBFile gridFSFile )
    {
        this.gridFSFile = gridFSFile;
    }

}
