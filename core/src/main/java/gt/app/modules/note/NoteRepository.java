package gt.app.modules.note;

import gt.app.domain.Note;
import gt.app.domain.NoteStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

interface NoteRepository extends JpaRepository<Note, Long> {

    @EntityGraph(attributePaths = {"attachedFiles", "createdByUser"})
    Optional<Note> findWithFilesAndUserById(Long id);

    @EntityGraph(attributePaths = {"createdByUser", "attachedFiles"})
    Page<Note> findWithFilesAndUserAllByStatus(Pageable pageable, NoteStatus status);

    @EntityGraph(attributePaths = {"createdByUser", "attachedFiles"})
    Page<Note> findWithFilesAndUserByCreatedByUser_IdAndStatusOrderByCreatedDateDesc(Pageable pageable, Long userId, NoteStatus status);

    @Query("select n.createdByUser.id from Note n where n.id=:id ")
    Long findCreatedByUserIdById(@Param("id") Long id);

    @EntityGraph(attributePaths = {"attachedFiles", "createdByUser"})
    Optional<Note> findWithFilesAndUserByIdAndStatus(Long id, NoteStatus flagged);
}
