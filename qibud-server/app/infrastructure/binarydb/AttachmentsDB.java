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
package infrastructure.binarydb;

import com.mongodb.gridfs.GridFSDBFile;
import java.io.InputStream;
import java.util.List;
import org.qi4j.library.constraints.annotation.NotEmpty;

/**
 * Hold binary content attached to Buds.
 * 
 * Uploaded files are added as MongoDB GridFS files.
 * An async Akka task extract attachment metadata and store it as a Mongo
 * document alongside the GridFS file.
 * A Controller Action output attachment metadata as a json resource.
 */
public interface AttachmentsDB
{

    void storeAttachment( @NotEmpty String budIdentity, @NotEmpty String baseName, InputStream inputStream );

    /**
     * @param attachmentId This is not the Bud identity but the BudAttachemnt id!
     */
    GridFSDBFile getDBFile( String attachmentId );

    List<GridFSDBFile> getBudDBFiles( String identity );

    void deleteBudDBFiles( String identity );

}
