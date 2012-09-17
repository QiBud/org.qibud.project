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
package application;

import application.bootstrap.QiBudAssembler;
import domain.buds.BudsFactory;
import domain.buds.BudsRepository;
import infrastructure.attachmentsdb.AttachmentsDB;
import infrastructure.graphdb.GraphDB;
import org.codeartisans.playqi.PlayQi;
import org.qi4j.api.structure.Module;

public final class QiBud
        extends PlayQi
{

    public static Module budsDomainModule()
    {
        return module( QiBudAssembler.LAYER_DOMAIN, QiBudAssembler.MODULE_BUDS );
    }

    public static BudsRepository budsRepository()
    {
        return service( QiBudAssembler.LAYER_DOMAIN, QiBudAssembler.MODULE_BUDS, BudsRepository.class );
    }

    public static BudsFactory budsFactory()
    {
        return service( QiBudAssembler.LAYER_DOMAIN, QiBudAssembler.MODULE_BUDS, BudsFactory.class );
    }

    public static GraphDB graphDB()
    {
        return service( QiBudAssembler.LAYER_DOMAIN, QiBudAssembler.MODULE_BUDS, GraphDB.class );
    }

    public static AttachmentsDB attachmentsDB()
    {
        return service( QiBudAssembler.LAYER_DOMAIN, QiBudAssembler.MODULE_BUDS, AttachmentsDB.class );
    }

}
