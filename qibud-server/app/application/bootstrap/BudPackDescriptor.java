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
package application.bootstrap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BudPackDescriptor
{

    private final String name;

    private String description;

    private final Map<String, RoleDescriptor> roles = new HashMap<String, RoleDescriptor>();

    /* package */ BudPackDescriptor( String name )
    {
        this.name = name;
        this.description = name;
    }

    /* package */ void description( String description )
    {
        this.description = description;
    }

    /* package */ Map<String, RoleDescriptor> mutableRoles()
    {
        return roles;
    }

    public String name()
    {
        return name;
    }

    public String description()
    {
        return description;
    }

    public Map<String, RoleDescriptor> roles()
    {
        return Collections.unmodifiableMap( roles );
    }

    @Override
    public String toString()
    {
        return "BudPackDescriptor{" + "name=" + name + ", roles=" + roles + '}';
    }

}
