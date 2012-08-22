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
package domain.budpacks;

import java.util.List;
import org.qi4j.api.entity.EntityComposite;
import org.qi4j.api.property.Property;
import roles.RoleDescriptor;

public interface BudPack
        extends EntityComposite
{

    Property<String> name();

    Property<String> description();

    Property<String> version();

    Property<List<RoleDescriptor>> roles();

    // TODO Add Dependencies
}
