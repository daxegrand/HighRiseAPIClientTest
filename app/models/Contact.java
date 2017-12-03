package models;

import java.util.*;
import javax.persistence.*;
import play.db.jpa.*;
import play.data.validation.*;

@Entity
public class Contact extends Model{

    @Column(unique=true)
    public Long contactId;

    @Lob
    public String background;

    @Email
    public String email;

    public String phone;

    public String firstName;

    public String lastName;

    public String company;

    public String title;

    @ManyToMany(cascade=CascadeType.PERSIST)
    public Set<Tag> tags;

    public Contact(Long contactId, String background, String email, String firstName, String lastName, String company, String title, String phone){
        this.tags = new TreeSet<Tag>();
        this.contactId = contactId;
        this.background = background;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.company = company;
        this.title = title;
        this.phone = phone;
    }

    public Contact tagItWith(String name){
        tags.add(Tag.findOrCreatedByName(name));
        return this;
    }

    public static List<Contact> findTaggedWith(String... tags){
        return Contact.find("select distinct p from Contact p join p.tags as t where t.name in (:tags) group by p.id, p.background, p.email, p.firstName, p.lastName, p.company, p.title having count(t.id) = :size").bind("tags", tags).bind("size", tags.length).fetch();
    }


}