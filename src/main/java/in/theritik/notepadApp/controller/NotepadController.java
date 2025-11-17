package in.theritik.notepadApp.controller;

import in.theritik.notepadApp.entities.Notepad;
import in.theritik.notepadApp.entities.User;
import in.theritik.notepadApp.services.NotepadService;
import in.theritik.notepadApp.services.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController()
@RequestMapping("/notepad")
public class NotepadController {

    @Autowired
    private NotepadService notepadService;

    @Autowired
    private UserService userService;

    @GetMapping("{userName}")
    public ResponseEntity<?> getAllNotesOfUser(@PathVariable String userName){
        User user = userService.findByUserName(userName);
        List<Notepad> all = user.getNotepadEntries();
        if(all != null && !all.isEmpty()){
            return new ResponseEntity<>(all, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @PostMapping("{userName}")
    public ResponseEntity<Notepad> createEntry(@RequestBody Notepad note, @PathVariable String userName){
        try {
            notepadService.saveNotepadEntry(note, userName);
            return new ResponseEntity<>(note, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(note, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("id/{takeId}")
    public ResponseEntity<Optional<Notepad>> searchById(@PathVariable ObjectId takeId){
        Optional<Notepad> foundNote = notepadService.getNoteById(takeId);
        if (foundNote.isPresent())
             return new ResponseEntity<>(foundNote, HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{userName}/{takeId}")
    public ResponseEntity<?> deleteNoteById(@PathVariable String userName ,@PathVariable ObjectId takeId){
        notepadService.deleteNoteById(takeId, userName);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PutMapping("/{userName}/{id}")
    public ResponseEntity<Notepad> updateNote(@PathVariable ObjectId id, @RequestBody Notepad note, @PathVariable String userName){
        Notepad existingNote = notepadService.getNoteById(id).orElse(null);
        if(existingNote != null){
            existingNote.setTitle(note.getTitle());
            existingNote.setContent(note.getContent());
            notepadService.saveNotepadEntry(existingNote);
            return new ResponseEntity<>(existingNote, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
