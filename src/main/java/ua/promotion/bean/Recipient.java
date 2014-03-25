package ua.promotion.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple merchandiser class.
 *
 * @author aldanchenko
 */
public class Recipient {
    private String name;
    private String email;

    private List<String> files = new ArrayList<String>(); // Move to other place.

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public void addFile(String file) {
        this.files.add(file);
    }
}
