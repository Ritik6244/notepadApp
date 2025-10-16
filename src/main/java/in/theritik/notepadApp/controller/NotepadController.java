package in.theritik.notepadApp.controller;

import in.theritik.notepadApp.entities.Notepad;
import in.theritik.notepadApp.services.NotepadService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController()
@RequestMapping("/notepad")
public class NotepadController {

    @Autowired
    private NotepadService notepadService;

    @GetMapping
    public List<Notepad> getAll(){
        return notepadService.getAllNotes();
    }

    @PostMapping
    public Notepad createEntry(@RequestBody Notepad note){
        note.setDate(LocalDateTime.now());
        notepadService.saveNotepadEntry(note);
        return note;
    }

    @GetMapping("id/{takeId}")
    public Notepad searchById(@PathVariable ObjectId takeId){
         return notepadService.getNoteById(takeId).orElse(null);
    }

    @DeleteMapping("id/{takeId}")
    public boolean deleteNoteById(@PathVariable ObjectId takeId){
        return notepadService.deleteNoteById(takeId);
    }

    @PutMapping("id/{id}")
    public Notepad updateNote(@PathVariable ObjectId id, @RequestBody Notepad note){
        notepadService.updateNote(id, note);
        return note;
    }

}
