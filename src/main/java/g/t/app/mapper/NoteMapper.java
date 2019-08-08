package g.t.app.mapper;

import g.t.app.domain.Note;
import g.t.app.dto.note.NoteDto;
import g.t.app.dto.note.NoteEditDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface NoteMapper {

    NoteMapper INSTANCE = Mappers.getMapper(NoteMapper.class);

    @Mapping(source = "createdByUser.id", target = "userId")
    NoteDto mapForRead(Note user);

    Note userToUserDto(NoteEditDto user);
}
