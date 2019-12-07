package gt.app.web.mvc;

import gt.app.domain.Note;
import gt.app.modules.note.NoteCreateDto;
import gt.app.modules.note.NoteEditDto;
import gt.app.modules.note.NoteService;
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

    final NoteService noteService;

    @GetMapping("/add")
    public String startAddNote(Model model) {
        model.addAttribute("msg", "Add a new note");
        model.addAttribute("note", new Note());
        return "note/add-note";
    }

    @PostMapping("/add")
    public String finishAddNote(NoteCreateDto noteDto, RedirectAttributes redirectAttrs) {

        //TODO:validate and return to GET:/add on errors

        Note note = noteService.createNote(noteDto);

        redirectAttrs.addFlashAttribute("success", "Note with id " + note.getId() + " is created");

        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("@permEvaluator.hasAccess(#id, 'Note' )")
    public String deleteNote(@PathVariable Long id, RedirectAttributes redirectAttrs) {

        noteService.delete(id);

        redirectAttrs.addFlashAttribute("success", "Note with id " + id + " is deleted");

        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("@permEvaluator.hasAccess(#id, 'Note' )")
    public String startEditNote(Model model, @PathVariable Long id) {
        model.addAttribute("msg", "Add a new note");
        model.addAttribute("note", noteService.read(id));
        return "note/edit-note";
    }

    @PostMapping("/edit")
    @PreAuthorize("@permEvaluator.hasAccess(#noteDto.id, 'Note' )")
    public String finishEditNote(Model model, NoteEditDto noteDto, RedirectAttributes redirectAttrs) {
        model.addAttribute("msg", "Add a new note");


        //TODO:validate and return to GET:/edit/{id} on errors


        noteService.update(noteDto);

        redirectAttrs.addFlashAttribute("success", "Note with id " + noteDto.getId() + " is updated");

        return "redirect:/note/";
    }
}
