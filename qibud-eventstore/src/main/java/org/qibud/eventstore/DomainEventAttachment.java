package org.qibud.eventstore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class DomainEventAttachment
{

    public static interface DataProvider
    {

        InputStream data()
                throws IOException;

    }

    public static class FileDataProvider
            implements DataProvider
    {

        private final File file;

        public FileDataProvider( File file )
        {
            this.file = file;
        }

        @Override
        public InputStream data()
                throws FileNotFoundException
        {
            return new FileInputStream( file );
        }

    }

    public static interface DataLocator
    {

        InputStream data( String attachmentLocalIdentity )
                throws IOException;

    }

    public static class DataProviderLocator
            implements DataProvider
    {

        private final String attachmentLocalIdentity;

        private final DataLocator locator;

        public DataProviderLocator( String attachmentLocalIdentity, DataLocator locator )
        {
            this.attachmentLocalIdentity = attachmentLocalIdentity;
            this.locator = locator;
        }

        @Override
        public InputStream data()
                throws IOException
        {
            return locator.data( attachmentLocalIdentity );
        }

    }

    private final String localIdentity;

    private final DataProvider dataProvider;

    public DomainEventAttachment( String localIdentity, DataProvider dataProvider )
    {
        this.localIdentity = localIdentity;
        this.dataProvider = dataProvider;
    }

    /**
     * Identity of the DomainEventAttachment, local to its DomainEventSequence.
     */
    public String localIdentity()
    {
        return localIdentity;
    }

    public InputStream data()
            throws IOException
    {
        return dataProvider.data();
    }

}
