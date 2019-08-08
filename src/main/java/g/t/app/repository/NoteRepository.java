package g.t.app.repository;

import g.t.app.domain.Authority;
import g.t.app.domain.Note;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {
}
