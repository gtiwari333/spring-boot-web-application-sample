package gt.app.web.mvc;

import gt.app.domain.Article;
import gt.app.modules.article.ArticleCreateDto;
import gt.app.modules.article.ArticleEditDto;
import gt.app.modules.article.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/article")
@RequiredArgsConstructor
@Slf4j
public class ArticleController {

    final ArticleService articleService;

    @GetMapping("/add")
    public String startAddArticle(Model model) {
        model.addAttribute("msg", "Add a new article");
        model.addAttribute("article", new Article());
        return "article/add-article";
    }

    @PostMapping("/add")
    public String finishAddArticle(ArticleCreateDto articleDto, RedirectAttributes redirectAttrs) {

        //TODO:validate and return to GET:/add on errors

        Article article = articleService.createArticle(articleDto);

        redirectAttrs.addFlashAttribute("success", "Article with id " + article.getId() + " is created");

        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("@permEvaluator.hasAccess(#id, 'Article' )")
    public String deleteArticle(@PathVariable Long id, RedirectAttributes redirectAttrs) {

        articleService.delete(id);

        redirectAttrs.addFlashAttribute("success", "Article with id " + id + " is deleted");

        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("@permEvaluator.hasAccess(#id, 'Article' )")
    public String startEditArticle(Model model, @PathVariable Long id) {
        model.addAttribute("msg", "Add a new article");
        model.addAttribute("article", articleService.read(id));
        return "article/edit-article";
    }

    @PostMapping("/edit")
    @PreAuthorize("@permEvaluator.hasAccess(#articleDto.id, 'Article' )")
    public String finishEditArticle(Model model, ArticleEditDto articleDto, RedirectAttributes redirectAttrs) {
        model.addAttribute("msg", "Add a new article");


        //TODO:validate and return to GET:/edit/{id} on errors


        articleService.update(articleDto);

        redirectAttrs.addFlashAttribute("success", "Article with id " + articleDto.getId() + " is updated");

        return "redirect:/article/";
    }
}
