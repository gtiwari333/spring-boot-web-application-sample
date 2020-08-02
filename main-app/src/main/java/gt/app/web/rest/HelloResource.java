package gt.app.web.rest;

import gt.app.modules.article.search.ArticleElasticSearchService;
import gt.app.modules.article.search.ArticleSearch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/public")
@Slf4j
public class HelloResource {

    final ArticleElasticSearchService articleSearchService;

    @GetMapping("/hello")
    public Map<String, String> sayHello() {
        return Map.of("hello", "world");
    }


    @GetMapping("/search/{str}")
    public Page<ArticleSearch> search(@PathVariable String str, Pageable page) {
        return articleSearchService.search(str, page);
    }
}
