package g.t.app.service;

import g.t.app.domain.Note;
import g.t.app.dto.note.NoteDto;
import g.t.app.dto.note.NoteEditDto;
import g.t.app.mapper.NoteMapper;
import g.t.app.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class NoteService {

    final NoteRepository noteRepository;

    public Note createNote(NoteEditDto dto) {
        return noteRepository.save(NoteMapper.INSTANCE.userToUserDto(dto));
    }

    public Note update(Long id, NoteEditDto dto) {
        Note note = NoteMapper.INSTANCE.userToUserDto(dto);
        note.setId(id);

        return noteRepository.save(note);
    }

    public NoteDto read(Long id) {
        return noteRepository.findById(id)
            .map(NoteMapper.INSTANCE::mapForRead)
            .orElseThrow();
    }

    public Page<NoteDto> readAll(Pageable pageable) {
        return noteRepository.findAll(pageable)
            .map(NoteMapper.INSTANCE::mapForRead);
    }

    public void delete(Long id) {
        noteRepository.deleteById(id);
    }

}
