package gt.app.modules.note;

import gt.app.domain.Note;
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
    NoteReadDto mapForRead(Note note);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "attachedFiles", ignore = true)
    void createToEntity(NoteEditDto dto, @MappingTarget Note note);

    Note createToEntity(NoteCreateDto dto);

    @Mapping(source = "originalFileName", target = "name")
    NoteReadDto.FileInfo map(ReceivedFile receivedFile);
}
