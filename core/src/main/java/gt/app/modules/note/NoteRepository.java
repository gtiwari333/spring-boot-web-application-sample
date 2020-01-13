package gt.app.modules.note;

import gt.app.domain.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

interface NoteRepository extends JpaRepository<Note, Long> {

    @EntityGraph(attributePaths = {"attachedFiles", "createdByUser"})
    Optional<Note> findById(Long id);

    @EntityGraph(attributePaths = {"createdByUser", "attachedFiles"})
    Page<Note> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"createdByUser", "attachedFiles"})
    Page<Note> findByCreatedByUser_IdOrderByCreatedDateDesc(Pageable pageable, Long userId);

    @Query("select n.createdByUser.id from Note n where n.id=:id ")
    Long findCreatedByUserIdById(@Param("id") Long id);
}
