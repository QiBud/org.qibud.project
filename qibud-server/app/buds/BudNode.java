package buds;

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
