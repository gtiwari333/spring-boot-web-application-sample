package gt.app.misc;

import gt.app.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
class SpringTransactionTest {
    private static final String TEST_PERSON = "ram";

    @Test
    void jpaUnManagedEntityDoesntDoDirtySave(@Autowired PersonRepo repo, @Autowired PersonService personService) {

        Person ramBefore = repo.findByFirstName(TEST_PERSON);

        personService.makeAgeDirtyWithoutTransaction(TEST_PERSON); //same result on public or protected method

        assertEquals(ramBefore.age, repo.findByFirstName(TEST_PERSON).age); //no change
    }

    @Test
    void jpaManagedEntityDirtySave(@Autowired PersonRepo repo, @Autowired PersonService personService) {
        Person ramBefore = repo.findByFirstName(TEST_PERSON);

        personService.makeAgeDirtyWithTransaction(TEST_PERSON);

        assertEquals((ramBefore.age + 1), repo.findByFirstName(TEST_PERSON).age); //saves change
    }

    @Test
    void jpaManagedEntityDirtySaveNonPublicMethod(@Autowired PersonRepo repo, @Autowired PersonService personService) {

        Person ramBefore = repo.findByFirstName(TEST_PERSON);

        personService.makeAgeDirtyWithTransactionNonPublicMethod(TEST_PERSON); //non-public @Transactional has no effect

        assertEquals(ramBefore.age, repo.findByFirstName(TEST_PERSON).age); //no change
    }

    //propagation / rollback

    @Test
    void trnPropagation1(@Autowired PersonRepo repo, @Autowired PersonService personService) {

        Person ramBefore = repo.findByFirstName(TEST_PERSON);

        assertThrows(RuntimeException.class, () -> personService.makeAgeDirtyWithoutTransactionThrowRuntimeException(TEST_PERSON));

        assertEquals(ramBefore.age, repo.findByFirstName(TEST_PERSON).age); //no change -- no transaction --> not managed by JPA --- dirty is not saved
    }

    @Test
    void trnPropagation2(@Autowired PersonRepo repo, @Autowired PersonService personService) {

        Person ramBefore = repo.findByFirstName(TEST_PERSON);

        assertThrows(RuntimeException.class, () -> personService.makeAgeDirtyWithTransactionThrowRuntimeException(TEST_PERSON));

        assertEquals(ramBefore.age, repo.findByFirstName(TEST_PERSON).age); //no change, got auto rollback
    }

    @Test
    void trnPropagation3(@Autowired PersonRepo repo, @Autowired PersonService personService) {

        Person ramBefore = repo.findByFirstName(TEST_PERSON);

        assertThrows(RuntimeException.class, () -> personService.saveWithoutTransactionThrowRuntimeException(TEST_PERSON));

        assertEquals((ramBefore.age + 1), repo.findByFirstName(TEST_PERSON).age); //does the update - no transaction --executes line by line -- is not managed by JPA
    }

    @Test
    void trnPropagation3a(@Autowired PersonRepo repo, @Autowired PersonService personService) {

        Person ramBefore = repo.findByFirstName(TEST_PERSON);

        assertThrows(RuntimeException.class, () -> personService.saveTransactionRuntimeException_SameMethod(TEST_PERSON));

        assertEquals(ramBefore.age, repo.findByFirstName(TEST_PERSON).age); //no change, got rollback for unchecked exception
    }

    @Test
    void trnPropagation3b(@Autowired PersonRepo repo, @Autowired PersonService personService) {

        Person ramBefore = repo.findByFirstName(TEST_PERSON);

        assertThrows(MyException.class, () -> personService.saveTransactionThrowMyException_SameMethod(TEST_PERSON));

        assertEquals((ramBefore.age + 1), repo.findByFirstName(TEST_PERSON).age); //does the update --> checked exception
    }

    @Test
    void trnPropagation4(@Autowired PersonRepo repo, @Autowired PersonService personService) {

        Person ramBefore = repo.findByFirstName(TEST_PERSON);

        assertThrows(MyException.class, () -> personService.saveWithoutTransactionThrowMyException(TEST_PERSON));

        assertEquals((ramBefore.age + 1), repo.findByFirstName(TEST_PERSON).age); //does the update-- no transaction
    }

    @Test
    void trnPropagation5(@Autowired PersonRepo repo, @Autowired PersonService personService) {

        Person ramBefore = repo.findByFirstName(TEST_PERSON);

        assertThrows(MyException.class, () -> personService.saveWithTransactionThrowMyException_Rollback(TEST_PERSON));

        assertEquals(ramBefore.age, repo.findByFirstName(TEST_PERSON).age); //rollback
    }


}

@Service
@RequiredArgsConstructor
class PersonService implements InitializingBean {

    final PersonRepo repo;

    void makeAgeDirtyWithoutTransaction(String firstName) {
        makeAgeDirtyNonTransactional(firstName);
    }

    @Transactional
    public void makeAgeDirtyWithTransaction(String firstName) {
        makeAgeDirtyNonTransactional(firstName);
    }

    @Transactional
    protected void makeAgeDirtyWithTransactionNonPublicMethod(String firstName) {
        makeAgeDirtyNonTransactional(firstName);
    }

    //for propagation/rollback tests
    //RuntimeException


    public void makeAgeDirtyWithoutTransactionThrowRuntimeException(String firstName) {
        makeAgeDirtyNonTransactional(firstName);
        throw new RuntimeException();
    }

    @Transactional
    public void makeAgeDirtyWithTransactionThrowRuntimeException(String firstName) {
        makeDirtyFromPublicTransactional(firstName);
        throw new RuntimeException();
    }

    public void saveWithoutTransactionThrowRuntimeException(String firstName) {
        Person p = makeAgeDirtyNonTransactional(firstName);
        repo.save(p);
        throw new RuntimeException();
    }

    @Transactional
    public void saveTransactionRuntimeException_SameMethod(String firstName) {
        Person p = makeAgeDirtyNonTransactional(firstName);
        repo.save(p);
        throw new RuntimeException();
    }

    //MyException

    @Transactional
    public void saveTransactionThrowMyException_SameMethod(String firstName) throws MyException {
        Person p = makeAgeDirtyNonTransactional(firstName);
        repo.save(p);
        throw new MyException();
    }

    public void saveWithoutTransactionThrowMyException(String firstName) throws MyException {
        Person p = makeAgeDirtyNonTransactional(firstName);
        repo.save(p);
        throw new MyException();
    }

    @Transactional(rollbackFor = MyException.class)
    public void saveWithTransactionThrowMyException_Rollback(String firstName) throws MyException {
        Person p = makeAgeDirtyNonTransactional(firstName);
        repo.save(p);
        throw new MyException();
    }


    /////////////////////////////////

    @Transactional
    public void makeDirtyFromPublicTransactional(String firstName) {
        Person p = repo.findByFirstName(firstName);
        p.age = ++p.age;
    }

    private Person makeAgeDirtyNonTransactional(String firstName) {
        Person p = repo.findByFirstName(firstName);
        p.age = ++p.age;
        return p;
    }

    @Override
    public void afterPropertiesSet() {
        //setup data
        repo.save(new Person("ram", "tiwari", 20));
    }
}

class MyException extends Exception {

}

interface PersonRepo extends JpaRepository<Person, Long> {
    Person findByFirstName(String fn);
}

@Entity
@AllArgsConstructor
@NoArgsConstructor
class Person extends BaseEntity {
    String firstName;
    String lastName;
    int age;
}

