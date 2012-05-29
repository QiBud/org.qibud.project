package forms;

import buds.Bud;
import play.data.validation.Constraints;

public class BudForm
{

    @Constraints.Required
    public String title;

    public String content;
    
    
    public static BudForm filledWith(Bud bud)
    {
        BudForm budForm = new BudForm();
        budForm.title = bud.entity().title;
        budForm.content = bud.entity().content;
        return budForm;
    }

}
