package gt.app.modules.article.search;

import gt.app.domain.Article;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleElasticSearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final ArticleSearchRepository searchRepo;

    public void save(Article a) {
        searchRepo.save(ArticleSearch.from(a));
    }

    public Page<ArticleSearch> search(String keyword, Pageable pageable) {
        return searchRepo.findByTitle(keyword, pageable);
    }

    public void deleteById(Long id) {
        searchRepo.deleteById(id);
    }

}
