package gt.app.modules.note;

import gt.app.domain.Article;
import gt.app.domain.NoteStatus;
import gt.app.domain.ReceivedFile;
import gt.app.modules.file.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class NoteService {

    private static final ReceivedFile.FileGroup FILE_GROUP = ReceivedFile.FileGroup.NOTE_ATTACHMENT;
    private final NoteRepository noteRepository;
    private final FileService fileService;

    public Article save(Article article) {
        return noteRepository.save(article);
    }

    public Article createNote(NoteCreateDto dto) {

        List<ReceivedFile> files = new ArrayList<>();
        for (MultipartFile mpf : dto.getFiles()) {

            if (mpf.isEmpty()) {
                continue;
            }

            String fileId = fileService.store(FILE_GROUP, mpf);
            files.add(new ReceivedFile(FILE_GROUP, mpf.getOriginalFilename(), fileId));
        }

        Article article = NoteMapper.INSTANCE.createToEntity(dto);
        article.getAttachedFiles().addAll(files);

        return save(article);
    }

    public Article update(NoteEditDto dto) {

        Optional<Article> noteOpt = noteRepository.findWithFilesAndUserById(dto.getId());
        return noteOpt.map(note -> {
                NoteMapper.INSTANCE.createToEntity(dto, note);
                return save(note);
            }
        ).orElseThrow();
    }

    public NoteReadDto read(Long id) {
        return noteRepository.findWithFilesAndUserByIdAndStatus(id, NoteStatus.PUBLISHED)
            .map(NoteMapper.INSTANCE::mapForRead)
            .orElseThrow();
    }

    public Page<NoteReadDto> readAll(Pageable pageable) {
        return noteRepository.findWithFilesAndUserAllByStatus(pageable, NoteStatus.PUBLISHED)
            .map(NoteMapper.INSTANCE::mapForRead);
    }

    public Page<NoteReadDto> readAllByUser(Pageable pageable, Long userId) {
        return noteRepository.findWithFilesAndUserByCreatedByUser_IdAndStatusOrderByCreatedDateDesc(pageable, userId, NoteStatus.PUBLISHED)
            .map(NoteMapper.INSTANCE::mapForRead);
    }

    public NoteReadDto readForReview(Long id) {
        return noteRepository.findWithFilesAndUserByIdAndStatus(id, NoteStatus.FLAGGED)
            .map(NoteMapper.INSTANCE::mapForRead)
            .orElseThrow();
    }

    public Page<NoteReadDto> getAllToReview(Pageable pageable) {
        return noteRepository.findWithFilesAndUserAllByStatus(pageable, NoteStatus.FLAGGED)
            .map(NoteMapper.INSTANCE::mapForRead);
    }

    public void delete(Long id) {
        noteRepository.deleteById(id);
    }

    public Long findCreatedByUserIdById(Long id) {
        return noteRepository.findCreatedByUserIdById(id);
    }


    public Optional<Article> handleReview(NoteReviewDto dto) {
        return noteRepository.findWithFilesAndUserByIdAndStatus(dto.getId(), NoteStatus.FLAGGED)
            .map(n -> {
                n.setStatus(dto.getVerdict());
                return save(n);
            });
    }
}
