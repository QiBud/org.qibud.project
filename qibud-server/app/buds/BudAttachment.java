/*
 * Copyright (c) 2012, Paul Merlin. All Rights Reserved.
 * Copyright (c) 2012, Samuel Loup. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package buds;

import com.mongodb.gridfs.GridFSDBFile;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

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
