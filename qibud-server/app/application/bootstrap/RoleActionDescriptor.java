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

import domain.roles.RoleAction;
import org.codehaus.jackson.annotate.JsonProperty;

public class RoleActionDescriptor
{

    private final String name;
    private final String description;
    private final Class<? extends RoleAction> roleActionType;
    private final Class<?> returnType;
    private final Class<?> parameterType;


    /* package */ RoleActionDescriptor( String name, String description, Class<? extends RoleAction> roleActionType, Class<?> returnType, Class<?> parameterType )
    {
        this.name = name;
        this.description = description;
        this.roleActionType = roleActionType;
        this.returnType = returnType;
        this.parameterType = parameterType;
    }

    @JsonProperty
    public String name()
    {
        return name;
    }

    @JsonProperty
    public String description()
    {
        return description;
    }

    public Class<? extends RoleAction> roleActionType()
    {
        return roleActionType;
    }

    public Class<?> returnType()
    {
        return returnType;
    }

    @JsonProperty( "input" )
    public String returnTypeSimpleName()
    {
        return returnType.getSimpleName();
    }

    public Class<?> parameterType()
    {
        return parameterType;
    }

    @JsonProperty( "output" )
    public String parameterTypeSimpleName()
    {
        return parameterType.getSimpleName();
    }

    @Override
    public String toString()
    {
        return "RoleActionDescriptor{" + "name=" + name + ", roleActionType=" + roleActionType + '}';
    }

}
