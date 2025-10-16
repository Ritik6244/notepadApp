package in.theritik.notepadApp.repositories;

import in.theritik.notepadApp.entities.Notepad;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotepadRepository extends MongoRepository<Notepad, ObjectId> {

}
