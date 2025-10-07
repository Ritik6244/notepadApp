package in.theritik.notepadApp.controller;

import in.theritik.notepadApp.entities.Notepad;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController()
@RequestMapping("/notepad")
public class NotepadController {

    private Map<Integer, Notepad> notepadEntry = new HashMap<>();

    @GetMapping
    public List<Notepad> getAll(){
        return new ArrayList<>(notepadEntry.values());
    }

    @PostMapping
    public boolean createEntry(@RequestBody Notepad note){
        notepadEntry.put(note.getId(), note);
        return true;
    }

    @GetMapping("/id/{myId}")
    public Notepad searchById(@PathVariable int myId){
        return notepadEntry.get(myId);
    }

    @DeleteMapping("/id/{myId}")
    public boolean deleteNoteById(@PathVariable int myId){
        notepadEntry.remove(myId);
        return true;
    }

    @PutMapping("/id/{id}")
    public Notepad updateNote(@PathVariable int id, @RequestBody Notepad note){
        notepadEntry.put(id, note);
        return note;
    }

}
