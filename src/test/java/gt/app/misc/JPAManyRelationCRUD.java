package gt.app.misc;

import gt.app.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.test.annotation.DirtiesContext;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
class JPAManyRelationCRUD {

    /*
     * Reference: http://ganeshtiwaridotcomdotnp.blogspot.com/2016/09/hibernate-create-update-delete-child.html
     */


    @Test
    void createTestWithOutChildren(@Autowired ParentRepo repo, @Autowired ChildRepo childRepo, @Autowired ParentService parentService) {

        ParentUpdateDto pDto = new ParentUpdateDto(0, "hari", "tiwari", 20, List.of());

        parentService.createOrUpdate(pDto);

        assertNotNull(repo.findByFirstName("hari"));
        assertEquals(0, childRepo.count());
    }


    @Test
    void createTestWithChildren(@Autowired ParentRepo repo, @Autowired ChildRepo childRepo, @Autowired ParentService parentService) {

        ParentUpdateDto pDto = new ParentUpdateDto(0, "hari", "tiwari", 20, List.of(new ChildUpdateDto(0, "hari jr 1", 1), new ChildUpdateDto(0, "hari jr 2", 2)));

        parentService.createOrUpdate(pDto);

        assertNotNull(repo.findByFirstName("hari"));
        assertEquals(2, childRepo.count());
    }


    @Test
    void createTestWithChildrenInvalidChildIdsWhenCreatingParent(@Autowired ParentRepo repo, @Autowired ChildRepo childRepo, @Autowired ParentService parentService) {

        ParentUpdateDto pDto = new ParentUpdateDto(0, "hari", "tiwari", 20, List.of(new ChildUpdateDto(111, "hari jr 1", 1), new ChildUpdateDto(123, "hari jr 2", 2)));

        parentService.createOrUpdate(pDto);

        assertNotNull(repo.findByFirstName("hari"));
        assertEquals(2, childRepo.count());
    }


    @Test
    void createUpdateTestWithChildren(@Autowired ParentRepo repo, @Autowired ChildRepo childRepo, @Autowired ParentService parentService) {

        ParentUpdateDto pDto = new ParentUpdateDto(0, "hari", "tiwari", 20, List.of(new ChildUpdateDto(0, "hari jr 1", 1), new ChildUpdateDto(0, "hari jr 2", 2)));

        parentService.createOrUpdate(pDto);

        Parent hari = repo.findByFirstName("hari");

        assertNotNull(hari);
        assertEquals(2, hari.children.size());

        //update hari's age, add a new children, remove one and update one
        pDto = new ParentUpdateDto(hari.getId(), "hari", "tiwari", 25, List.of(new ChildUpdateDto(childRepo.findByFirstName("hari jr 1").getId(), "hari jr (updated)", 5), new ChildUpdateDto(0, "hari jr new child", 2)));

        parentService.createOrUpdate(pDto);

        hari = repo.findByFirstName("hari");
        assertEquals(25, hari.age);
        assertEquals(2, hari.children.size());
        assertNotNull(childRepo.findByFirstName("hari jr (updated)"));
        assertNotNull(childRepo.findByFirstName("hari jr new child"));
        assertNull(childRepo.findByFirstName("hari jr 2")); //orphan removal takes care of deletion
    }


}

@Service
@RequiredArgsConstructor
class ParentService {

    final ParentRepo parentRepo;
    final ChildRepo childRepo;

    void createOrUpdate(ParentUpdateDto pDto) {

        Optional<Parent> parentOpt = parentRepo.findById(pDto.id);

        boolean isCreate = false;

        //0: find parent
        Parent parentEntity;

        if (parentOpt.isEmpty()) {
            parentEntity = new Parent();
            isCreate = true;
        } else {
            parentEntity = parentOpt.get();
        }

        //1: update parent entity
        parentEntity.age = pDto.age;
        parentEntity.lastName = pDto.lName;
        parentEntity.firstName = pDto.fName;

        List<Child> newChildList = new ArrayList<>();

        //update child relations..
        for (ChildUpdateDto cdto : pDto.children) {

            Child childEntity;
            if (isCreate || cdto.childId == 0) { //check isCreate at first
                //create
                childEntity = new Child();
            } else {
                Optional<Child> childEntityOpt = childRepo.findById(cdto.childId);
                childEntity = childEntityOpt.orElseGet(Child::new);
                childEntity.setId(cdto.childId);
            }

            //update child fields
            childEntity.age = cdto.age;
            childEntity.firstName = cdto.fName;

            newChildList.add(childEntity);
        }

        //clear and add all
        parentEntity.children.clear();
        parentEntity.children.addAll(newChildList);

        //save
        parentRepo.save(parentEntity);
    }


}


//the repo, entity and dtos

interface ParentRepo extends JpaRepository<Parent, Long> {
    @EntityGraph(attributePaths = "children")
    Parent findByFirstName(String fn);

    @EntityGraph(attributePaths = "children")
    Optional<Parent> findById(Long id);
}

interface ChildRepo extends JpaRepository<Child, Long> {
    Child findByFirstName(String fn);
}


@Entity
@NoArgsConstructor
@AllArgsConstructor
class Parent extends BaseEntity {
    String firstName;
    String lastName;
    int age;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    List<Child> children = new ArrayList<>();
}


@Entity
@NoArgsConstructor
@AllArgsConstructor
class Child extends BaseEntity {
    String firstName;
    int age;

    @ManyToOne
    Parent parent;

}


@AllArgsConstructor
class ParentUpdateDto {
    long id;
    String fName;
    String lName;
    int age;
    List<ChildUpdateDto> children;

}


@AllArgsConstructor
class ChildUpdateDto {
    long childId;
    String fName;
    int age;
}
