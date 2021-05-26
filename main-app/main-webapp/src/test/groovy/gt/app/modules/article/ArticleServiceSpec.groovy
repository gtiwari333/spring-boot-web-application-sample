package gt.app.modules.article

import gt.app.domain.Article
import gt.app.modules.file.FileService
import org.springframework.jms.core.JmsTemplate
import spock.lang.Specification

class ArticleServiceSpec extends Specification {

    ArticleRepository articleRepository;
    FileService fileService;
    JmsTemplate jmsTemplate;
    CommentRepository commentRepo;
    ArticleService articleService

    def setup() {
        articleRepository = Mock();
        fileService = Mock()
        jmsTemplate = Mock()
        commentRepo = Mock()
        commentRepo = Mock()
        articleService = new ArticleService(articleRepository, fileService, jmsTemplate, commentRepo)
    }

    def 'save article'() {
        given:
        def article = new Article()

        when:
        Article created = articleService.save(article)

        then:
        1 * articleRepository.save(article)
        0 * _
    }

}
