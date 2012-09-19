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

import config.bootstrap.QiBudConfigAssemblies;
import domain.bootstrap.QiBudDomainAssemblies;
import domain.roles.BudAction;
import domain.roles.BudActions;
import domain.roles.BudRole;
import domain.roles.Role;
import domain.roles.RoleAction;
import infrastructure.bootstrap.QiBudInfraAssemblies;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.codeartisans.java.toolbox.Strings;
import org.codehaus.jackson.node.ObjectNode;
import org.qi4j.bootstrap.ApplicationAssembler;
import org.qi4j.bootstrap.ApplicationAssembly;
import org.qi4j.bootstrap.ApplicationAssemblyFactory;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.LayerAssembly;
import org.qi4j.bootstrap.ModuleAssembly;
import play.Play;
import utils.ClassFinder;
import utils.QiBudException;

public class QiBudAssembler
        implements ApplicationAssembler
{

    public static final String LAYER_DOMAIN = "domain";

    public static final String MODULE_BUDS = "buds";

    public static final String LAYER_INFRASTRUCTURE = "infrastructure";

    public static final String MODULE_PERSISTENCE = "persistence";

    private final String mongoHostname;

    private final int mongoPort;

    private final String mongoUsername;

    private final String mongoPassword;

    private final String mongoDatabase;

    private final String mongoCollection;

    public QiBudAssembler( String mongoHostname, int mongoPort,
                           String mongoUsername, String mongoPassword,
                           String mongoDatabase, String mongoCollection )
    {
        this.mongoHostname = mongoHostname;
        this.mongoPort = mongoPort;
        this.mongoUsername = mongoUsername;
        this.mongoPassword = mongoPassword;
        this.mongoDatabase = mongoDatabase;
        this.mongoCollection = mongoCollection;
    }

    @Override
    public ApplicationAssembly assemble( ApplicationAssemblyFactory aaf )
            throws AssemblyException
    {
        ApplicationAssembly appAss = aaf.newApplicationAssembly();
        appAss.setName( "QiBud" );

        // Config
        LayerAssembly configLayer = appAss.layer( "config" );
        ModuleAssembly config = configLayer.module( "config" );
        {
            QiBudConfigAssemblies.config().assemble( config );
        }

        // Domain
        LayerAssembly domainLayer = appAss.layer( LAYER_DOMAIN );
        ModuleAssembly buds = domainLayer.module( MODULE_BUDS );
        {
            QiBudDomainAssemblies.buds( loadBudPacks() ).assemble( buds );
        }

        // Infrastructure
        LayerAssembly infraLayer = appAss.layer( LAYER_INFRASTRUCTURE );
        ModuleAssembly persistence = infraLayer.module( MODULE_PERSISTENCE );
        {
            QiBudInfraAssemblies.persistence( config,
                                              mongoHostname, mongoPort,
                                              mongoUsername, mongoPassword,
                                              mongoDatabase, mongoCollection ).
                    assemble( persistence );
        }

        domainLayer.uses( infraLayer, configLayer );
        infraLayer.uses( configLayer );

        return appAss;
    }

    private Map<String, BudPackDescriptor> loadBudPacks()
    {
        Map<String, BudPackDescriptor> packs = new HashMap<String, BudPackDescriptor>();
        String configPrefix = "qibud.budpack.";
        for ( String configKey : Play.application().configuration().keys() ) {
            if ( configKey.startsWith( configPrefix ) ) {
                String budPackName = configKey.substring( configPrefix.length(), configKey.lastIndexOf( '.' ) );
                BudPackDescriptor budPack = packs.get( budPackName );
                if ( budPack == null ) {
                    budPack = new BudPackDescriptor( budPackName );
                }
                if ( configKey.endsWith( ".package" ) ) {

                    String budPackPackage = Play.application().configuration().getString( configKey );
                    Class<?>[] candidates = ClassFinder.getClasses( budPackPackage, Play.application().classloader() );
                    for ( Class<?> candidate : candidates ) {

                        if ( Role.class.isAssignableFrom( candidate ) ) {

                            // Roles
                            Class<? extends Role> roleType = ( Class<? extends Role> ) candidate;
                            String roleName = roleType.getCanonicalName();
                            String roleDescription = roleName;
                            if ( roleType.isAnnotationPresent( BudRole.class ) ) {

                                BudRole roleAnnotation = roleType.getAnnotation( BudRole.class );
                                roleName = roleAnnotation.name();
                                roleDescription = Strings.EMPTY.equals( roleAnnotation.description() )
                                                  ? roleName
                                                  : roleAnnotation.description();

                            }
                            RoleDescriptor role = new RoleDescriptor( budPackName, roleName, roleDescription, roleType );
                            if ( roleType.isAnnotationPresent( BudActions.class ) ) {

                                // Actions
                                BudActions actionsAnnotation = roleType.getAnnotation( BudActions.class );
                                for ( Class<? extends RoleAction> actionType : actionsAnnotation.value() ) {
                                    String actionName = actionType.getCanonicalName();
                                    String actionDescription = actionName;
                                    if ( actionType.isAnnotationPresent( BudAction.class ) ) {

                                        BudAction actionAnnotation = actionType.getAnnotation( BudAction.class );
                                        actionName = actionAnnotation.name();
                                        actionDescription = Strings.EMPTY.equals( actionAnnotation.description() )
                                                            ? actionName
                                                            : actionAnnotation.description();

                                    }
                                    RoleActionDescriptor action = new RoleActionDescriptor( actionName, actionDescription, actionType,
                                                                                            ObjectNode.class, ObjectNode.class );
                                    role.mutableActions().put( actionName, action );
                                }

                            }
                            budPack.mutableRoles().put( roleName, role );
                        }

                    }

                } else if ( configKey.endsWith( ".description" ) ) {

                    String budPackDescription = Play.application().configuration().getString( configKey );
                    budPack.description( budPackDescription );

                } else {
                    throw new QiBudException( "Unknown BudPack configuration parameter: " + configKey );
                }

                packs.put( budPackName, budPack );
            }
        }
        return packs;
    }

    private Method findActionMethod( Class<? extends RoleAction> actionType )
    {
        for ( Method method : actionType.getDeclaredMethods() ) {
            if ( "invokeAction".equals( method.getName() )
                 && method.getParameterTypes().length == 3 ) {
                return method;
            }
        }
        throw new QiBudException( "BudAction has no single invokeAction(...) method, cannot register " + actionType );
    }

}
