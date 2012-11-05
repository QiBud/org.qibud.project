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
package domain.roles;

import domain.buds.Bud;
import org.codehaus.jackson.JsonNode;
import org.qi4j.api.entity.EntityComposite;
import org.qi4j.api.injection.scope.This;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.property.Property;
import play.libs.Json;

@Mixins( Role.Mixin.class )
public interface Role
    extends EntityComposite
{

    Property<String> budPackName();

    Property<String> roleName();

    Property<String> roleState();

    boolean is( String pack, String role );

    /**
     * @return budPackName() + "/" + roleName()
     */
    String codename();

    /**
     * @return budPackName() + "-" + roleName()
     */
    String idname();

    JsonNode jsonRoleState();

    /**
     * Called on first Role creation.
     */
    void onCreate( Bud bud );

    /**
     * Called on Role enablement.
     */
    void onEnable( Bud bud );

    /**
     * Called on Role disablement.
     */
    void onDisable( Bud bud );

    /**
     * Called on Bud change.
     */
    void onBudChange( Bud bud );

    abstract class Mixin
        implements Role
    {

        @This
        private Role roleValue;

        @Override
        public boolean is( String pack, String role )
        {
            return roleValue.budPackName().get().equals( pack ) && roleValue.roleName().get().equals( role );
        }

        @Override
        public String codename()
        {
            return roleValue.budPackName().get() + "/" + roleValue.roleName().get();
        }

        @Override
        public String idname()
        {
            return roleValue.budPackName().get() + "-" + roleValue.roleName().get();
        }

        @Override
        public JsonNode jsonRoleState()
        {
            return Json.parse( roleValue.roleState().get() );
        }

        @Override
        public void onCreate( Bud bud )
        {
            // NOOP Override in role Mixins
        }

        @Override
        public void onEnable( Bud bud )
        {
            // NOOP Override in role Mixins
        }

        @Override
        public void onDisable( Bud bud )
        {
            // NOOP Override in role Mixins
        }

        @Override
        public void onBudChange( Bud bud )
        {
            // NOOP Override in role Mixins
        }

    }

}
