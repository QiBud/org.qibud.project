/*
 * Copyright (c) 2012, Paul Merlin. All Rights Reserved.
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
package utils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

public class Threads
{

    private Threads()
    {
    }

    @SuppressWarnings( "unchecked" )
    public static boolean isThreadRegisteredAsShutdownHook( String threadName )
    {
        try
        {
            Class clazz = Class.forName( "java.lang.ApplicationShutdownHooks" );
            Field field = clazz.getDeclaredField( "hooks" );
            field.setAccessible( true );
            Object rawHooks = field.get( null );
            Map<Thread, Thread> hooks = (Map<Thread, Thread>) rawHooks;
            Set<Thread> threads = hooks.keySet();
            for( Thread eachThread : threads )
            {
                if( threadName.equals( eachThread.getName() ) )
                {
                    return true;
                }
            }
            return false;
        }
        catch( IllegalArgumentException ex )
        {
            throw new QiBudException( ex.getMessage(), ex );
        }
        catch( IllegalAccessException ex )
        {
            throw new QiBudException( ex.getMessage(), ex );
        }
        catch( NoSuchFieldException ex )
        {
            throw new QiBudException( ex.getMessage(), ex );
        }
        catch( SecurityException ex )
        {
            throw new QiBudException( ex.getMessage(), ex );
        }
        catch( ClassNotFoundException ex )
        {
            throw new QiBudException( ex.getMessage(), ex );
        }
        catch( ClassCastException ex )
        {
            throw new QiBudException( ex.getMessage(), ex );
        }
    }

}
