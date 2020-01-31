package gt.app.modules.note;

import gt.app.domain.Article;
import gt.app.domain.ReceivedFile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface NoteMapper {

    NoteMapper INSTANCE = Mappers.getMapper(NoteMapper.class);

    @Mapping(source = "createdByUser.id", target = "userId")
    @Mapping(source = "createdByUser.uniqueId", target = "username")
    @Mapping(source = "attachedFiles", target = "files")
    NoteReadDto mapForRead(Article article);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "attachedFiles", ignore = true)
    void createToEntity(NoteEditDto dto, @MappingTarget Article article);

    Article createToEntity(NoteCreateDto dto);

    @Mapping(source = "originalFileName", target = "name")
    NoteReadDto.FileInfo map(ReceivedFile receivedFile);
}
