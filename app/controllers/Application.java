package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import java.io.*;
import play.data.validation.*;

import models.*;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.ClientResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class Application extends Controller {

    public static void index() {
        render();
    }

    private static String connectToHRApi(String target){
        //Create client instance
        //package: import com.sun.jersey.api.client.Client;
        Client client = Client.create();
        
        //add auth filter
        //param1 = username, param2 = passowrd
        //package: import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
        client.addFilter(new HTTPBasicAuthFilter(Play.configuration.getProperty("api.username"), Play.configuration.getProperty("api.password")));
        
        //create webResource
        //resource param = API endpoint URI
        //pacakage: import com.sun.jersey.api.client.WebResource;
        WebResource webResource = client
                .resource(Play.configuration.getProperty("api.endpoint") + target);

        
        //create response instance
        //package: import com.sun.jersey.api.client.ClientResponse;
        ClientResponse response = webResource.get(ClientResponse.class);

        //response code 200 = SUCCESS
        if (response.getStatus() != 200) {
            System.out.println("Failed : HTTP error code : "
                    + response.getStatus());
        }


        //get response from api
        String output = response.getEntity(String.class);

        return output;
    }

    public static void showAllTagsFromHIghrise() throws Exception{
        String output = connectToHRApi("tags.xml");

        NodeList parentNodeList = xmlParser(output, "tag");

        for (int i = 0; i < parentNodeList.getLength(); i++) {
            Node parentNode = parentNodeList.item(i);

            if (parentNode.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) parentNode;

                System.out.println("id: " + getTagValue("id", element));
                System.out.println("name: " + getTagValue("name", element));
            }
        }

        
        
        render(output);
    }

    private static NodeList xmlParser(String source, String tagName) throws Exception{
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(source));

        Document doc = db.parse(is);
        doc.getDocumentElement().normalize();

        NodeList parentNodeList = doc.getElementsByTagName(tagName);

        return parentNodeList;
    }

    private static int getTagId(String tag) throws Exception{
        //get response from api
        String output = connectToHRApi("tags.xml");

        NodeList parentNodeList = xmlParser(output, "tag");

        for (int i = 0; i < parentNodeList.getLength(); i++) {
            Node parentNode = parentNodeList.item(i);

            if (parentNode.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) parentNode;                
                if(tag.equalsIgnoreCase(getTagValue("name", element))){
                    return Integer.parseInt(getTagValue("id", element));
                }
            }
        }

        return 0;
    }

    private static String getTagValue(String sTag, Element eElement) {
        NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();

        Node nValue = (Node) nlList.item(0);

        return nValue.getNodeValue();
    }

    public static void getContact(@Required String tag) throws Exception{
        if (validation.hasErrors()) {
            render("Application/index.html");
        }
        String output = connectToHRApi("people.xml?tag_id=" + getTagId(tag));
        
        NodeList personList = xmlParser(output, "person");

        if(personList.getLength() == 0){
            output = "No records found!";
        }
        
        for (int i = 0; i < personList.getLength(); i++) {
            Node person = personList.item(i);

            if (person.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) person;

                
                Long contactId = Long.parseLong(getTagValue("id", element));
                String background = getTagValue("background", element);
                String email = getTagValue("address", element);
                String firstName = getTagValue("first-name", element);
                String lastName = getTagValue("last-name", element);
                String company = getTagValue("company-name", element);
                String title = getTagValue("title", element);
                String phone = getTagValue("number", element);

                Contact checkContact = Contact.find("byContactId", contactId).first();

                if(checkContact == null){
                    Contact contact = new Contact(contactId, background, email, firstName, lastName, company, title, phone).save();
                    contact.tagItWith(tag).save();
                    System.out.println("Contact info saved!");
                }else{
                    checkContact.tagItWith(tag).save();
                    System.out.println("Contact already exists!");
                }
                    
            }
        }

        list(tag);
        
    }
    public static void searchDB(String name){
        list(name);
    }
    public static void list(String tag){
        List<Contact> contacts = Contact.findTaggedWith(tag);
        List<Tag> tags = Tag.findAll();
        render(tag, contacts, tags);
    }

    public static void show(Long contactId){
        Contact contact = Contact.find("byContactId", contactId).first();
        render(contact);
    }

}