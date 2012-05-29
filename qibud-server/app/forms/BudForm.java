package forms;

import play.data.validation.Constraints;

public class BudForm
{

    @Constraints.Required
    public String title;

    public String content;

}
