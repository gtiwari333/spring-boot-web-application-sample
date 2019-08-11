package g.t.app.mapper;

import g.t.app.domain.Note;
import g.t.app.domain.ReceivedFile;
import g.t.app.dto.note.NoteEditDto;
import g.t.app.dto.note.NoteReadDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface NoteMapper {

    NoteMapper INSTANCE = Mappers.getMapper(NoteMapper.class);

    @Mapping(source = "createdByUser.id", target = "userId")
    @Mapping(source = "attachedFiles", target = "files")
    NoteReadDto mapForRead(Note note);

    Note userToUserDto(NoteEditDto dto);

    @Mapping(source = "originalFileName", target = "name")
    NoteReadDto.FileInfo map(ReceivedFile receivedFile);
}
