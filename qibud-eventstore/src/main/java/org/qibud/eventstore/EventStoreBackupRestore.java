package org.qibud.eventstore;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public interface EventStoreBackupRestore
{

    int backup( Writer writer )
            throws IOException;

    int restore( Reader reader )
            throws IOException;

}
