/*
 * Copyright 2012 TypeSafe.
 * Copyright 2012 Paul Merlin.
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package utils;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Originnaly borrowed from the inject plugin: https://github.com/typesafehub/play-plugins/tree/master/inject
 * Added functionnality for this very project.
 */
public class ClassFinder
{

    public static Class[] getClasses( Package pack, ClassLoader classLoader )
    {
        return getClasses( pack.getName(), classLoader );
    }

    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     * Adapted from http://snippets.dzone.com/posts/show/4831 and extended to support use of JAR files
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public static Class[] getClasses( String packageName, ClassLoader classLoader )
    {
        try
        {
            assert classLoader != null;
            String path = packageName.replace( '.', '/' );
            Enumeration<URL> resources = classLoader.getResources( path );
            List<String> dirs = new ArrayList<String>();
            while( resources.hasMoreElements() )
            {
                URL resource = resources.nextElement();
                dirs.add( resource.getFile() );
            }
            TreeSet<String> classes = new TreeSet<String>();
            for( String directory : dirs )
            {
                classes.addAll( findClasses( directory, packageName ) );
            }
            ArrayList<Class> classList = new ArrayList<Class>();
            for( String clazz : classes )
            {
                classList.add( classLoader.loadClass( clazz ) );
            }
            return classList.toArray( new Class[ classes.size() ] );
        }
        catch( Exception ex )
        {
            play.Logger.warn( "Unable to find classes in " + packageName, ex );
            return null;
        }
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     * Adapted from http://snippets.dzone.com/posts/show/4831 and extended to support use of JAR files
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static TreeSet<String> findClasses( String directory, String packageName )
        throws Exception
    {
        TreeSet<String> classes = new TreeSet<String>();
        if( directory.startsWith( "file:" ) && directory.contains( "!" ) )
        {
            String[] split = directory.split( "!" );
            URL jar = new URL( split[0] );
            ZipInputStream zip = new ZipInputStream( jar.openStream() );
            ZipEntry entry;
            while( ( entry = zip.getNextEntry() ) != null )
            {
                if( entry.getName().endsWith( ".class" ) )
                {
                    String className = entry.getName().replaceAll( "[$].*", "" ).replaceAll( "[.]class", "" ).replace( '/', '.' );
                    if( className.startsWith( packageName ) )
                    {
                        classes.add( className );
                    }
                }
            }
        }
        File dir = new File( directory );
        if( !dir.exists() )
        {
            return classes;
        }
        File[] files = dir.listFiles();
        for( File file : files )
        {
            if( file.isDirectory() )
            {
                assert !file.getName().contains( "." );
                classes.addAll( findClasses( file.getAbsolutePath(), packageName + "." + file.getName() ) );
            }
            else
            {
                if( file.getName().endsWith( ".class" ) )
                {
                    classes.add( packageName + '.' + file.getName().substring( 0, file.getName().length() - 6 ) );
                }
            }
        }
        return classes;
    }

}