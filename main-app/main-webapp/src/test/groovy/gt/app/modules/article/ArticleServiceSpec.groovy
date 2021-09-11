package gt.app.modules.article


import gt.app.domain.Article
import gt.app.modules.file.FileService
import gt.app.modules.review.JmsContentCheckService
import org.springframework.jms.core.JmsTemplate
import spock.lang.Specification

class ArticleServiceSpec extends Specification {

    ArticleRepository articleRepository;
    FileService fileService;
    JmsTemplate jmsTemplate;
    CommentRepository commentRepo;
    ArticleService articleService
    JmsContentCheckService contentCheckRequestService

    def setup() {
        articleRepository = Mock()
        fileService = Mock()
        jmsTemplate = Mock()
        commentRepo = Mock()
        commentRepo = Mock()
        contentCheckRequestService = Mock()
        articleService = new ArticleService(articleRepository, fileService, jmsTemplate, commentRepo, contentCheckRequestService)
    }

    def 'save article'() {
        given:
        def article = new Article()

        when:
        Article created = articleRepository.save(article)

        then:
        1 * articleRepository.save(article)
        0 * _
    }

}
