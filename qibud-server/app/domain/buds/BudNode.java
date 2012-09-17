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
package domain.buds;

import org.neo4j.graphdb.Node;

public class BudNode
{

    public static final String IDENTITY = "identity";

    public static final String QI = "qi";

    private final Node underlyingNode;

    /* package */ BudNode( Node underlyingNode )
    {
        this.underlyingNode = underlyingNode;
    }

    protected Node getUnderlyingNode()
    {
        return underlyingNode;
    }

    public String getIdentity()
    {
        return ( String ) underlyingNode.getProperty( IDENTITY );
    }

    public Long getQi()
    {
        return ( Long ) underlyingNode.getProperty( QI );
    }

    @Override
    public int hashCode()
    {
        return underlyingNode.hashCode();
    }

    @Override
    public boolean equals( Object o )
    {
        return o instanceof BudNode && underlyingNode.equals( ( ( BudNode ) o ).getUnderlyingNode() );
    }

    @Override
    public String toString()
    {
        return "BudNode[" + getIdentity() + "]";
    }

}
