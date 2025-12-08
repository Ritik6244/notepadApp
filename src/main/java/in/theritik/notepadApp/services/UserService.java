package in.theritik.notepadApp.services;

import in.theritik.notepadApp.entities.User;
import in.theritik.notepadApp.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public Optional<User> getUserById(ObjectId id){
        return userRepository.findById(id);
    }

    public void saveNewUser(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Arrays.asList("USER"));
        userRepository.save(user);
    }

    public void saveAdmin(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Arrays.asList("USER","ADMIN"));
        userRepository.save(user);
    }

    public void saveUser(User user){
        userRepository.save(user);
    }

    public boolean deleteUserById(ObjectId id){
        User existingUser = userRepository.findById(id).orElse(null);
        if(existingUser == null)
            return false;
        else {
            userRepository.deleteById(id);
            return true;
        }
    }

    public User findByUserName(String userName){
        return userRepository.findByUserName(userName);
    }

    public void deleteUserByName(String username) {
        userRepository.deleteByUserName(username);
    }

//    public Notepad updateNote(ObjectId id, Notepad note){
//        Optional<Notepad> existingNote = userRepository.findById(id);
//        if(existingNote.isPresent()) {
//            Notepad toUpdate = existingNote.get();
//            toUpdate.setTitle(note.getTitle());
//            toUpdate.setContent(note.getContent());
//            return userRepository.save(toUpdate);
//        }else
//            return null;
//    }


}
