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
@RequestMapping("/note")
@RequiredArgsConstructor
@Slf4j
public class NoteController {

    final ArticleService articleService;

    @GetMapping("/add")
    public String startAddNote(Model model) {
        model.addAttribute("msg", "Add a new note");
        model.addAttribute("note", new Article());
        return "note/add-note";
    }

    @PostMapping("/add")
    public String finishAddNote(ArticleCreateDto noteDto, RedirectAttributes redirectAttrs) {

        //TODO:validate and return to GET:/add on errors

        Article article = articleService.createNote(noteDto);

        redirectAttrs.addFlashAttribute("success", "Note with id " + article.getId() + " is created");

        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("@permEvaluator.hasAccess(#id, 'Note' )")
    public String deleteNote(@PathVariable Long id, RedirectAttributes redirectAttrs) {

        articleService.delete(id);

        redirectAttrs.addFlashAttribute("success", "Note with id " + id + " is deleted");

        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("@permEvaluator.hasAccess(#id, 'Note' )")
    public String startEditNote(Model model, @PathVariable Long id) {
        model.addAttribute("msg", "Add a new note");
        model.addAttribute("note", articleService.read(id));
        return "note/edit-note";
    }

    @PostMapping("/edit")
    @PreAuthorize("@permEvaluator.hasAccess(#noteDto.id, 'Note' )")
    public String finishEditNote(Model model, ArticleEditDto noteDto, RedirectAttributes redirectAttrs) {
        model.addAttribute("msg", "Add a new note");


        //TODO:validate and return to GET:/edit/{id} on errors


        articleService.update(noteDto);

        redirectAttrs.addFlashAttribute("success", "Note with id " + noteDto.getId() + " is updated");

        return "redirect:/note/";
    }
}
