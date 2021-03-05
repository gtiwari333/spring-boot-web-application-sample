package gt.app.modules.article

import gt.app.domain.Article
import gt.app.modules.article.search.ArticleElasticSearchService
import gt.app.modules.file.FileService
import org.springframework.jms.core.JmsTemplate
import spock.lang.Specification

class ArticleServiceSpec extends Specification {

    ArticleRepository articleRepository;
    FileService fileService;
    ArticleElasticSearchService articleSearchService;
    JmsTemplate jmsTemplate;
    CommentRepository commentRepo;
    ArticleService articleService

    def setup() {
        articleRepository = Mock();
        fileService = Mock()
        articleSearchService = Mock()
        jmsTemplate = Mock()
        commentRepo = Mock()
        commentRepo = Mock()
        articleService = new ArticleService(articleRepository, fileService, articleSearchService, jmsTemplate, commentRepo)
    }

    def 'save article'() {
        given:
        def article = new Article()

        when:
        Article created = articleService.save(article)

        then:
        1 * articleRepository.save(article)
        1 * articleSearchService.save(article)
        0 * _
    }

}
