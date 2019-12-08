package gt.app.web.mvc;

import gt.app.config.security.AppUserDetails;
import gt.app.domain.Note;
import gt.app.modules.note.NoteService;
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

    private final NoteService noteService;

    @GetMapping({"/", ""})
    public String index(Model model, Pageable pageable) {
        model.addAttribute("greeting", "Hello Spring");

        model.addAttribute("notes", noteService.readAll(PageRequest.of(0, 20, Sort.by("createdDate").descending())));
        model.addAttribute("note", new Note());

        return "landing";
    }

    @GetMapping("/admin")
    public String adminHome(Model model, @AuthenticationPrincipal AppUserDetails principal) {
        model.addAttribute("message", getWelcomeMessage(principal));
        return "admin";
    }

    @GetMapping("/note")
    public String userHome(Model model, @AuthenticationPrincipal AppUserDetails principal) {
        model.addAttribute("message", getWelcomeMessage(principal));
        model.addAttribute("notes", noteService.readAllByUser(PageRequest.of(0, 20, Sort.by("createdDate").descending()), principal.getId()));
        model.addAttribute("note", new Note());
        return "note";
    }

    private String getWelcomeMessage(AppUserDetails principal) {
        return "Hello " + principal.getUsername() + "!";
    }

}
