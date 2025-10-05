package in.theritik.notepadApp.entities;

import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan
public class Notepad {

    private int Id;
    private String title;
    private String note;

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
