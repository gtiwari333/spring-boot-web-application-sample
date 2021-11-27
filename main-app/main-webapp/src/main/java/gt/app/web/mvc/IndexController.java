package gt.app.web.mvc;

import gt.app.domain.Article;
import gt.app.modules.article.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@Slf4j
@RequiredArgsConstructor
public class IndexController {

    private final ArticleService articleService;

    @GetMapping({"/", ""})
    public String index(Model model, Pageable pageable) {
        model.addAttribute("articles", articleService.previewForPublicHomePage(
            PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createdDate").descending())
        ));
        model.addAttribute("article", new Article());

        return "landing";
    }

    @GetMapping("/tag/{name}")
    public String byTag(Model model, Pageable pageable, @PathVariable String name) {
        model.addAttribute("tag", name);

        model.addAttribute("articles", articleService.previewForPublicHomePageByTag(
            PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createdDate").descending()), name
        ));
        model.addAttribute("article", new Article());

        return "landing";
    }

    @GetMapping("/admin")
    public String adminHome(Model model, Pageable pageable) {
        model.addAttribute("articlesToReview", articleService.getAllToReview(
            PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createdDate").descending())
        ));
        return "admin/admin-area";
    }

}
