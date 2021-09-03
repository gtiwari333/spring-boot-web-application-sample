package gt.app.modules.article;

import gt.app.domain.Article;
import gt.app.domain.Comment;
import gt.app.domain.ReceivedFile;
import gt.common.dtos.ArticleSummaryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ArticleMapper {

    ArticleMapper INSTANCE = Mappers.getMapper(ArticleMapper.class);

    @Mapping(source = "createdByUser.id", target = "userId")
    @Mapping(source = "createdByUser.username", target = "username")
    @Mapping(source = "attachedFiles", target = "files")
    ArticleReadDto mapForRead(Article article);

    @Mapping(source = "createdByUser.id", target = "userId")
    @Mapping(source = "createdByUser.username", target = "username")
    @Mapping(source = "attachedFiles", target = "files")
    @Mapping(target = "content", qualifiedByName = {"substringArticleContent"})
    ArticlePreviewDto mapForPreviewListing(Article article);

    @Mapping(source = "createdByUser.id", target = "userId")
    @Mapping(source = "createdByUser.username", target = "username")
    @Mapping(source = "attachedFiles", target = "files")
    ArticlePreviewDto mapForReview(Article article);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "attachedFiles", ignore = true)
    void createToEntity(ArticleEditDto dto, @MappingTarget Article article);

    Article createToEntity(ArticleCreateDto dto);

    @Mapping(source = "createdByUser.username", target = "username")
    @Mapping(source = "createdByUser.id", target = "userId")
    ArticleReadDto.CommentDto map(Comment comment);

    @Mapping(source = "originalFileName", target = "name")
    ArticleReadDto.FileInfo mapRead(ReceivedFile receivedFile);

    @Mapping(source = "originalFileName", target = "name")
    ArticlePreviewDto.FileInfo map(ReceivedFile receivedFile);

    @Mapping(source = "createdByUser.username", target = "username")
    ArticleSummaryDto mapForPublishedEvent(Article article);

    ArticleSummaryDto mapForPublishedEvent(ArticleReadDto article);

    @Named("substringArticleContent")//will do custom transformation once we move to  Markdown format
    default String substringArticleContent(String content) {
        if (content.length() < 200) {
            return content;
        }

        return content.substring(0, 200) + " ...";
    }
}
