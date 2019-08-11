package g.t.app.service;

import g.t.app.domain.Note;
import g.t.app.domain.ReceivedFile;
import g.t.app.dto.note.NoteReadDto;
import g.t.app.dto.note.NoteEditDto;
import g.t.app.mapper.NoteMapper;
import g.t.app.repository.NoteRepository;
import g.t.app.service.file.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class NoteService {

    private static final ReceivedFile.FileGroup FILE_GROUP = ReceivedFile.FileGroup.NOTE_ATTACHMENT;
    final NoteRepository noteRepository;
    final FileService fileService;

    public Note createNote(NoteEditDto dto) {

        List<ReceivedFile> files = new ArrayList<>();
        for (MultipartFile mpf : dto.getFiles()) {

            if (mpf.isEmpty()) {
                continue;
            }

            String fileId = fileService.store(FILE_GROUP, mpf);
            files.add(new ReceivedFile(FILE_GROUP, mpf.getOriginalFilename(), fileId));
        }

        Note note = NoteMapper.INSTANCE.userToUserDto(dto);
        note.getAttachedFiles().addAll(files);

        return noteRepository.save(note);
    }

    public Note update(Long id, NoteEditDto dto) {
        Note note = NoteMapper.INSTANCE.userToUserDto(dto);
        note.setId(id);

        return noteRepository.save(note);
    }

    public NoteReadDto read(Long id) {
        return noteRepository.findById(id)
            .map(NoteMapper.INSTANCE::mapForRead)
            .orElseThrow();
    }

    public Page<NoteReadDto> readAll(Pageable pageable) {
        return noteRepository.findAll(pageable)
            .map(NoteMapper.INSTANCE::mapForRead);
    }

    public void delete(Long id) {
        noteRepository.deleteById(id);
    }

}
