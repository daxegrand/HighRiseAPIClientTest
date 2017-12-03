import play.*;
import play.jobs.*;
import play.test.*;

import models.*;

@OnApplicationStart
public class Bootstrap extends Job{
    
    public void doJob(){
        //Check if database is empty
        System.out.println("Contact Count: " + Contact.count());
        if(Contact.count() > 0){
            Fixtures.deleteDatabase();
        }
    }
}
