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
package controllers;

import domain.buds.Bud;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

public class RootBudContextAction
    extends Action<WithRootBudContext>
{

    private static final String ROOT_BUD_IDENTITY_KEY = "bud:root:identity";

    @Override
    public Result call( Http.Context ctx )
        throws Throwable
    {
        ctx.args.put( ROOT_BUD_IDENTITY_KEY, Bud.ROOT_BUD_IDENTITY );
        Result result = delegate.call( ctx );
        return result;
    }

    public static String rootBudIdentity()
    {
        return (String) Http.Context.current().args.get( ROOT_BUD_IDENTITY_KEY );
    }

}
