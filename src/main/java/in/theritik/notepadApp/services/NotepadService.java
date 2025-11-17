package in.theritik.notepadApp.services;

import in.theritik.notepadApp.entities.Notepad;
import in.theritik.notepadApp.entities.User;
import in.theritik.notepadApp.repositories.NotepadRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class NotepadService  {

    @Autowired
    private NotepadRepository notepadRepository;

    @Autowired
    private UserService userService;

    public List<Notepad> getAllNotes(){
        return notepadRepository.findAll();
    }

    public Optional<Notepad> getNoteById(ObjectId id){
        return notepadRepository.findById(id);
    }

    @Transactional
    public void saveNotepadEntry(Notepad notepadEntry, String userName){
        try{
            User user = userService.findByUserName(userName);
            notepadEntry.setDate(LocalDateTime.now());
            Notepad saved = notepadRepository.save(notepadEntry);
            user.getNotepadEntries().add(saved);
            userService.saveUserEntry(user);
        } catch (Exception e) {
            log.error("Exception ", e);
            throw new RuntimeException("Entry is wrong :", e);
        }
    }

    public void saveNotepadEntry(Notepad notepadEntry){
            notepadRepository.save(notepadEntry);
    }

    public void deleteNoteById(ObjectId id, String userName){
        User user = userService.findByUserName(userName);
        user.getNotepadEntries().removeIf(x -> x.getId().equals(id));
        userService.saveUserEntry(user);
        notepadRepository.deleteById(id);
        }

//    public Notepad updateNote(ObjectId id, Notepad note){
//        Optional<Notepad> existingNote = notepadRepository.findById(id);
//        if(existingNote.isPresent()) {
//            Notepad toUpdate = existingNote.get();
//            toUpdate.setTitle(note.getTitle());
//            toUpdate.setContent(note.getContent());
//            return notepadRepository.save(toUpdate);
//        }else
//            return null;
//    }


}
