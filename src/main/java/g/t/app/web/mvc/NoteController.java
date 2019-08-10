package g.t.app.web.mvc;

import g.t.app.domain.Note;
import g.t.app.dto.note.NoteEditDto;
import g.t.app.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/note")
@RequiredArgsConstructor
public class NoteController {

    final NoteService noteService;

    @GetMapping("/")
    public String notes(Model model, Pageable pageable) {
        model.addAttribute("notes", noteService.readAll(pageable));
        model.addAttribute("note", new Note());
        return "note/view-notes";
    }

    @GetMapping("/add")
    public String addNote(Model model) {
        model.addAttribute("msg", "Add a new note");
        model.addAttribute("note", new Note());
        return "note/add-note";
    }

    @PostMapping("/add")
    public String addNoteComplete(Model model, NoteEditDto noteDto, BindingResult result, RedirectAttributes redirectAttrs) {

        //TODO:validate and return to GET:/add on errors

        Note note = noteService.createNote(noteDto);

        redirectAttrs.addFlashAttribute("success", "Note with id " + note.getId() + " is created");

        return "redirect:/note/";
    }

    @GetMapping("/delete/{id}")
    public String deleteNote(@PathVariable Long id, RedirectAttributes redirectAttrs) {

        noteService.delete(id);

        redirectAttrs.addFlashAttribute("success", "Note with id " + id + " is deleted");
        return "redirect:/note/";
    }

    @GetMapping("/edit/{id}")
    public String getForEditNote(Model model, @PathVariable Long id) {
        model.addAttribute("msg", "Add a new note");
        model.addAttribute("note", noteService.read(id));
        return "note/edit-note";
    }

    @PostMapping("/edit/{id}")
    public String updateNote(Model model, NoteEditDto noteDto, @PathVariable Long id, BindingResult result, RedirectAttributes redirectAttrs) {
        model.addAttribute("msg", "Add a new note");


        //TODO:validate and return to GET:/edit/{id} on errors


        noteService.update(id, noteDto);

        redirectAttrs.addFlashAttribute("success", "Note with id " + id + " is updated");

        return "redirect:/note/";
    }
}
