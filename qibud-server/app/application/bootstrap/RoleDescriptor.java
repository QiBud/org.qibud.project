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

import domain.roles.Role;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonProperty;

public class RoleDescriptor
{

    private final String budPackName;

    private final String name;

    private final String description;

    private final Class<? extends Role> roleType;

    private final Map<String, RoleActionDescriptor> actions = new HashMap<String, RoleActionDescriptor>();

    public RoleDescriptor( String budPackName, String name, String description, Class<? extends Role> roleType )
    {
        this.budPackName = budPackName;
        this.name = name;
        this.description = description;
        this.roleType = roleType;
    }

    /* package */ Map<String, RoleActionDescriptor> mutableActions()
    {
        return actions;
    }

    @JsonProperty( "budpack-name" )
    public String budPackName()
    {
        return budPackName;
    }

    @JsonProperty( "role-name" )
    public String name()
    {
        return name;
    }

    @JsonProperty( "description" )
    public String description()
    {
        return description;
    }

    public Class<? extends Role> roleType()
    {
        return roleType;
    }

    @JsonProperty( "actions" )
    public Map<String, RoleActionDescriptor> actions()
    {
        return Collections.unmodifiableMap( actions );
    }

    @Override
    public String toString()
    {
        return "RoleDescriptor{" + "name=" + name + ", roleType=" + roleType + ", actions=" + actions + '}';
    }

}
