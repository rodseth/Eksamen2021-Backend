package entities;
import facades.UserFacade;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.NotFoundException;

public class Tester {
    public static void main(String[] args) throws NotFoundException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("pu");
        EntityManager em = emf.createEntityManager();
       
        UserFacade UF = UserFacade.getUserFacade(emf);
        
      
        
       // PersonFacade PF = PersonFacade.getPersonFacade(emf);
        
       //City c1 = new City(3730, "Nexø");
       //Address a1 = new Address("Gl. Rønnevej 23");
       //Person p1 = new Person("Katte", "Pus");
       
       //PF.addPerson(p1, a1, c1);
       
      // List<PersonDTO> personList = PF.getPersonsInCity(3770);
      // for(PersonDTO p : personList){
       //    System.out.println(p.getFirstName());
             
       }
       
      // PersonDTO p = PF.getPersonById(3L);
       
       //City c = new City(3700, "Rønne");
      // Address a = new Address("Ullasvej 4");
      // Person p1 = new Person("John", "Madsen");
       
     //  a.setCity(c);
     //  p1.setAddress(a);
       
     //  System.out.println(p.getFirstName());
       
     //  List<PersonDTO> personDTOList = PF.getAllPersons();
     //  for (PersonDTO person : personDTOList){
      //     System.out.println(person.getFirstName());
      // }     
        
  //  }
    
    
    
}
