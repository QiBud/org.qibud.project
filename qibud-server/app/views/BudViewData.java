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
package views;

import application.bootstrap.RoleDescriptor;
import domain.buds.Bud;
import domain.roles.Role;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.qi4j.api.query.Query;
import org.qi4j.functional.Iterables;

/**
 * Template helper created by controllers and used to group Bud data frequently used by views.
 */
public class BudViewData
{

    public final Bud bud;
    public final Collection<Role> roles;
    public final Collection<RoleDescriptor> unused_roles;
    public final List<Bud> children;

    public BudViewData( Bud bud, Collection<RoleDescriptor> unused_roles, Query<Bud> children )
    {
        this.bud = bud;
        this.roles = Collections.unmodifiableCollection( Iterables.toList( bud.roles() ) );
        this.unused_roles = Collections.unmodifiableCollection( unused_roles );
        this.children = Iterables.toList( children );
    }

    public boolean hasChildren()
    {
        return !children.isEmpty();
    }

}
