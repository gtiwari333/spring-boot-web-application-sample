package g.t.app.web.mvc;

import g.t.app.config.security.UserDetails;
import g.t.app.domain.Note;
import g.t.app.dto.note.NoteCreateDto;
import g.t.app.dto.note.NoteEditDto;
import g.t.app.service.NoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/note")
@RequiredArgsConstructor
@Slf4j
public class NoteController {

    final NoteService noteService;

    @GetMapping("/")
    public String notes(Model model, Pageable pageable, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("notes", noteService.readAllByUser(pageable, userDetails.getId()));
        model.addAttribute("note", new Note());
        return "note/view-notes";
    }

    @GetMapping("/add")
    public String startAddNote(Model model) {
        model.addAttribute("msg", "Add a new note");
        model.addAttribute("note", new Note());
        return "note/add-note";
    }

    @PostMapping("/add")
    public String finishAddNote(Model model, NoteCreateDto noteDto, BindingResult result, RedirectAttributes redirectAttrs) {

        //TODO:validate and return to GET:/add on errors

        Note note = noteService.createNote(noteDto);

        redirectAttrs.addFlashAttribute("success", "Note with id " + note.getId() + " is created");

        return "redirect:/note/";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("@permEvaluator.hasAccess(#id, 'Note' )")
    public String deleteNote(@PathVariable Long id, RedirectAttributes redirectAttrs) {

        noteService.delete(id);

        redirectAttrs.addFlashAttribute("success", "Note with id " + id + " is deleted");

        return "redirect:/note/";
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
    public String finishEditNote(Model model, NoteEditDto noteDto, BindingResult result, RedirectAttributes redirectAttrs) {
        model.addAttribute("msg", "Add a new note");


        //TODO:validate and return to GET:/edit/{id} on errors


        noteService.update(noteDto);

        redirectAttrs.addFlashAttribute("success", "Note with id " + noteDto.getId() + " is updated");

        return "redirect:/note/";
    }
}
