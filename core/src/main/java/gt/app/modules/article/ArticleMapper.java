package gt.app.modules.article;

import gt.app.domain.Article;
import gt.app.domain.ReceivedFile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ArticleMapper {

    ArticleMapper INSTANCE = Mappers.getMapper(ArticleMapper.class);

    @Mapping(source = "createdByUser.id", target = "userId")
    @Mapping(source = "createdByUser.uniqueId", target = "username")
    @Mapping(source = "attachedFiles", target = "files")
    ArticleReadDto mapForRead(Article article);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "attachedFiles", ignore = true)
    void createToEntity(ArticleEditDto dto, @MappingTarget Article article);

    Article createToEntity(ArticleCreateDto dto);

    @Mapping(source = "originalFileName", target = "name")
    ArticleReadDto.FileInfo map(ReceivedFile receivedFile);
}
