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
package domain.budpacks.project;

import domain.buds.Bud;
import domain.roles.BudRole;
import domain.roles.Role;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.qi4j.api.injection.scope.This;
import org.qi4j.api.mixin.Mixins;
import play.libs.Json;

@BudRole(name = "mission")
@Mixins(Mission.MissionMixin.class)
public interface Mission
        extends Role {

    String missionStatus();


    abstract class MissionMixin implements Mission {

        @This
        private Role role;

        @Override
        public String missionStatus() {
            JsonNode state = role.jsonRoleState();
            return state.has("missionStatus") ? state.get("missionStatus").getTextValue() : "N/D";
        }

        @Override
        public void onCreate(Bud bud) {

            ObjectNode state = JsonNodeFactory.instance.objectNode();
            //Initialise le statut de base du cycle de vie
            state.put("missionStatus", "Waiting for projects");

            //Prépare la stockage des projets fils
            state.putArray("projects");
            role.roleState().set(Json.stringify(state));
        }
    }
}
