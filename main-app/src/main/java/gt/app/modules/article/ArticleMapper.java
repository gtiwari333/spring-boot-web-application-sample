package gt.app.modules.article;

import gt.app.domain.Article;
import gt.app.domain.Comment;
import gt.app.domain.ReceivedFile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
interface ArticleMapper {

    ArticleMapper INSTANCE = Mappers.getMapper(ArticleMapper.class);

    @Mapping(source = "createdByUser.id", target = "userId")
    @Mapping(source = "createdByUser.username", target = "username")
    @Mapping(source = "attachedFiles", target = "files")
    ArticleReadDto mapForRead(Article article);

    @Mapping(source = "createdByUser.id", target = "userId")
    @Mapping(source = "createdByUser.username", target = "username")
    @Mapping(source = "attachedFiles", target = "files")
    ArticleListDto mapForListing(Article article);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "attachedFiles", ignore = true)
    void createToEntity(ArticleEditDto dto, @MappingTarget Article article);

    Article createToEntity(ArticleCreateDto dto);

    @Mapping(source = "createdByUser.username", target = "username")
    @Mapping(source = "createdByUser.id", target = "userId")
    ArticleReadDto.CommentDto map(Comment comment);

    @Mapping(source = "originalFileName", target = "name")
    ArticleReadDto.FileInfo map(ReceivedFile receivedFile);
}
