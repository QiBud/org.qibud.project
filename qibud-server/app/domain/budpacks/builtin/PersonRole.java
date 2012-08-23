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
package domain.budpacks.builtin;

import java.util.Collections;
import java.util.List;
import domain.roles.Role;
import domain.roles.RoleAction;
import domain.roles.RoleActionNotFound;

public class PersonRole
        implements Role<PersonEntity>
{

    private static final String ROLENAME = "Person";

    private final PersonEntity entity;

    public PersonRole( PersonEntity entity )
    {
        this.entity = entity;
    }

    @Override
    public String roleName()
    {
        return ROLENAME;
    }

    @Override
    public boolean hasActionNamed( String name )
    {
        return false;
    }

    @Override
    public RoleAction actionNamed( String name )
    {
        throw new RoleActionNotFound( name + " does not exist" );
    }

    @Override
    public List<RoleAction> actions()
    {
        return Collections.emptyList();
    }

    @Override
    public PersonEntity roleEntity()
    {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

}
