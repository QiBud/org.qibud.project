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
import java.util.Iterator;
import java.util.List;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.qi4j.api.injection.scope.This;
import org.qi4j.api.mixin.Mixins;
import play.libs.Json;

@BudRole(name = "project")
@Mixins(Project.ProjectMixin.class)
public interface Project
        extends Role {

    abstract class ProjectMixin implements Project {

        @This
        Role role;

        @Override
        public void onCreate(Bud bud) {

            ObjectNode state = JsonNodeFactory.instance.objectNode();



            //Initialise le status du cycle de vie
            state.put("projectStatus", "Waiting for actions");
            
            //Verification sur le père
            Bud parent = bud.parent().get();

            Role parentMissionRole = parent.role("project", "mission");

            if (parentMissionRole != null) 
            {
                //Ajoute un lien sur la mission (père)
                state.put("mission",parent.identity().get());
     
            }

            role.roleState().set(Json.stringify(state));


        }

        @Override
        public void onDisable(Bud bud) 
        {
            //Verification sur le père
            Bud parent = bud.parent().get();
            Role parentMissionRole = parent.role("project", "mission");
            if (parentMissionRole != null) 
            {
                //Récupération de l'état de la mission
                ObjectNode missionState = (ObjectNode) parentMissionRole.jsonRoleState();

                
                
                //Suppresion du projet dans la liste de la mission
                ArrayNode projects = (ArrayNode) missionState.get("projects");
                
                Iterator<JsonNode> it = projects.iterator();
                while(it.hasNext())
                {
                    if(it.next().get("id").asText().equals(bud.identity().get()))
                        it.remove(); 
                }
                
                
                missionState.put("projects", projects);
                
                //Changement de status de la mission si besoin
                if(projects.size()==0)
                {
                    missionState.put("missionStatus", "Waiting for projects");
                }
                
                
                
                //Sauvegarde des changements
                parentMissionRole.roleState().set(Json.stringify(missionState));
            }
        }

        @Override
        public void onEnable(Bud bud) 
        {
            //Verification sur le père
            Bud parent = bud.parent().get();
            Role parentMissionRole = parent.role("project", "mission");
            if (parentMissionRole != null) 
            {
                //Récupération de l'état de la mission
                ObjectNode missionState = (ObjectNode) parentMissionRole.jsonRoleState();

                //Changement de status de la mission
                missionState.put("missionStatus", "Started");
                
                //Ajout du projet dans la liste des ID projet de la mission
                ArrayNode prjs = (ArrayNode) missionState.get("projects");
                
                ObjectNode pnode = JsonNodeFactory.instance.objectNode();
                
                pnode.put("title",bud.title().get());
                pnode.put("id",bud.identity().get());
                
                prjs.add(pnode);
                
                missionState.put("projects", prjs);
                
                
                //Sauvegarde des changements
                parentMissionRole.roleState().set(Json.stringify(missionState));
            }
            
        }
        
        
        
    }
}
