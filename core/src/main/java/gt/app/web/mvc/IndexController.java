package gt.app.web.mvc;

import gt.app.config.security.AppUserDetails;
import gt.app.domain.Article;
import gt.app.modules.article.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
public class IndexController {

    private final ArticleService articleService;

    @GetMapping({"/", ""})
    public String index(Model model, Pageable pageable) {
        model.addAttribute("greeting", "Hello Spring");

        model.addAttribute("articles", articleService.previewAll(PageRequest.of(0, 20, Sort.by("createdDate").descending())));
        model.addAttribute("article", new Article());

        return "landing";
    }

    @GetMapping("/admin")
    public String adminHome(Model model) {
        model.addAttribute("articlesToReview", articleService.getAllToReview(PageRequest.of(0, 20, Sort.by("createdDate").descending())));
        return "admin/admin-area";
    }

    @GetMapping("/article")
    public String userHome(Model model, @AuthenticationPrincipal AppUserDetails principal) {
        model.addAttribute("message", getWelcomeMessage(principal));
        model.addAttribute("articles", articleService.previewAllByUser(PageRequest.of(0, 20, Sort.by("createdDate").descending()), principal.getId()));
        model.addAttribute("article", new Article());
        return "article";
    }

    private String getWelcomeMessage(AppUserDetails principal) {
        return "Hello " + principal.getUsername() + "!";
    }

}
