package in.theritik.notepadApp.services;

import in.theritik.notepadApp.entities.Notepad;
import in.theritik.notepadApp.repositories.NotepadRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class NotepadService  {

    @Autowired
    private NotepadRepository notepadRepository;

    public List<Notepad> getAllNotes(){
        return notepadRepository.findAll();
    }

    public Optional<Notepad> getNoteById(ObjectId id){
        return notepadRepository.findById(id);
    }

    public void saveNotepadEntry(Notepad notepadEntry){
        notepadRepository.save(notepadEntry);
    }

    public boolean deleteNoteById(ObjectId id){
        Notepad existingNote = notepadRepository.findById(id).orElse(null);
        if(existingNote == null)
            return false;
        else {
            notepadRepository.deleteById(id);
            return true;
        }
    }

    public Notepad updateNote(ObjectId id, Notepad note){
        Optional<Notepad> existingNote = notepadRepository.findById(id);
        if(existingNote.isPresent()) {
            Notepad toUpdate = existingNote.get();
            toUpdate.setTitle(note.getTitle());
            toUpdate.setContent(note.getContent());
            return notepadRepository.save(toUpdate);
        }else
            return null;
    }


}
