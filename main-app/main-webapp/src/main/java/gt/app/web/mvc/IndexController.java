package gt.app.web.mvc;

import gt.app.domain.Article;
import gt.app.modules.article.ArticleService;
import gt.app.utl.PaginationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
class IndexController {

    private final ArticleService articleService;

    @GetMapping({"/", ""})
    public String index(Model model, Pageable pageable) {
        model.addAttribute("greeting", "Hello Spring");
        var page = articleService.previewForPublicHomePage(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createdDate").descending()));
        model.addAttribute("article", new Article());
        model.addAttribute("articles", page);
        PaginationUtil.decorateModel(model, page);
        return "landing";
    }

    @GetMapping("/admin")
    public String adminHome(Model model, Pageable pageable) {
        var page = articleService.getAllToReview(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createdDate").descending()));
        model.addAttribute("articlesToReview", page);
        PaginationUtil.decorateModel(model, page);
        return "admin/admin-area";
    }

}
