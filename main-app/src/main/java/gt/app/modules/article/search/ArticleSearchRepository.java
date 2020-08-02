package gt.app.modules.article.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

interface ArticleSearchRepository extends ElasticsearchRepository<ArticleSearch, Long> {
    Page<ArticleSearch> findByTitle(String title, Pageable pageable);
}
