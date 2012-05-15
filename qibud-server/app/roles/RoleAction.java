package roles;

public interface RoleAction<ParamType extends Object, ResultType extends Object, ThrowableType extends Throwable>
{

    String actionName();

    ResultType invokeAction( ParamType params )
            throws ThrowableType;

}