import org.junit.*;
import java.util.*;
import play.test.*;
import models.*;

public class BasicTest extends UnitTest {

    @Before
    public void setup(){
        Fixtures.deleteDatabase();
    }

    @Test
    public void createAndRetrieveContact(){
        //create new contact
        new Contact(301433676L, "Donec vitae elit eget tortor sagittis dignissim et non risus. Fusce blandit in metus et consequat. Vivamus ut diam nunc.",
            "prudegrand@merlinai.com", "Prudencia", "Granados", "Merlin AI", "CEO", "5555555").save();
        
        //retrive contact by ID
        Contact contact = Contact.find("byEmail", "prudegrand@merlinai.com").first();

        //Test
        assertNotNull(contact);
        assertEquals("Prudencia", contact.firstName);
    }

    @Test
    public void testTags(){
        //create new contact
        new Contact(301433677L, "Donec vitae elit eget tortor sagittis dignissim et non risus. Fusce blandit in metus et consequat. Vivamus ut diam nunc.",
        "prudegrand@merlinai.com", "Prudencia", "Granados", "Merlin AI", "CEO", "5555555").save();

        new Contact(301433678L, "Donec vitae elit eget tortor sagittis dignissim et non risus. Fusce blandit in metus et consequat. Vivamus ut diam nunc.",
        "prudegrand@merlinai.com", "Prudencia", "Granados", "Merlin AI", "CEO", "5555555").save();
    
        //retrive contact by ID
        Contact contact = Contact.find("byEmail", "prudegrand@merlinai.com").first();

        //test before tagging
        assertEquals(0, Contact.findTaggedWith("CEO").size());

        //Tag the contact
        contact.tagItWith("CEO").tagItWith("AI").save();
        contact.tagItWith("FOUNDER").save();

        //Test
        assertEquals(1, Contact.findTaggedWith("CEO").size());
        assertEquals(1, Contact.findTaggedWith("AI").size());
        assertEquals(1, Contact.findTaggedWith("FOUNDER").size());
        assertEquals(1, Contact.findTaggedWith("CEO", "AI").size());
        assertEquals(1, Contact.findTaggedWith("AI", "FOUNDER").size());
        assertEquals(1, Contact.findTaggedWith("CEO", "FOUNDER").size());
        assertEquals(1, Contact.findTaggedWith("CEO", "AI", "FOUNDER").size());
        assertEquals(0, Contact.findTaggedWith("CTO", "FOUNDER").size());
        List<Map> cloud = Tag.getCloud();
        assertEquals("[{tag=AI, pound=1}, {tag=CEO, pound=1}, {tag=FOUNDER, pound=1}]",cloud.toString());

    }


}
