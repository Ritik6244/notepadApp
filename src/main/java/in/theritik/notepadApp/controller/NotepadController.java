package in.theritik.notepadApp.controller;

import in.theritik.notepadApp.entities.Notepad;
import in.theritik.notepadApp.entities.User;
import in.theritik.notepadApp.services.NotepadService;
import in.theritik.notepadApp.services.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/notepad")
public class NotepadController {

    @Autowired
    private NotepadService notepadService;
    @Autowired
    private UserService userService;

    @GetMapping()
    public ResponseEntity<?> getAllNotesOfUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);
        List<Notepad> all = user.getNotepadEntries();
        if(all != null && !all.isEmpty()){
            return new ResponseEntity<>(all, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping()
    public ResponseEntity<Notepad> createEntry(@RequestBody Notepad note){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            notepadService.saveNotepadEntry(note, userName);
            return new ResponseEntity<>(note, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(note, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/id/{takeId}")
    public ResponseEntity<Optional<Notepad>> searchById(@PathVariable ObjectId takeId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);
        List<Notepad> allNotes = user.getNotepadEntries().stream().filter(x -> x.getId().equals(takeId)).collect(Collectors.toList());
        if (!allNotes.isEmpty()) {
            Optional<Notepad> foundNote = notepadService.getNoteById(takeId);
            if (foundNote.isPresent()) {
                return new ResponseEntity<>(foundNote, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/id/{takeId}")
    public ResponseEntity<?> deleteNoteById(@PathVariable ObjectId takeId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        boolean removed = notepadService.deleteNoteById(takeId, userName);
        if(removed){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
     }

    @PutMapping("/id/{id}")
    public ResponseEntity<?> updateNote(@PathVariable ObjectId id, @RequestBody Notepad newNote){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);
        List<Notepad> allNotes = user.getNotepadEntries().stream().filter(x -> x.getId().equals(id)).collect(Collectors.toList());
        if (!allNotes.isEmpty()) {
            Optional<Notepad> foundNote = notepadService.getNoteById(id);
            if (foundNote.isPresent()) {
                Notepad oldNote = foundNote.get();
                oldNote.setTitle(newNote.getTitle() != null && !newNote.getTitle().equals("") ? newNote.getTitle() : oldNote.getTitle());
                oldNote.setContent(newNote.getContent() != null && !newNote.getContent().equals("") ? newNote.getContent() : oldNote.getContent());
                notepadService.saveNotepadEntry(oldNote);
                return new ResponseEntity<>(foundNote, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
