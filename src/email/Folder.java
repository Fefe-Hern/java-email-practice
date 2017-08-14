package email;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

public class Folder implements Serializable {
    private ArrayList<Email> emails;
    private String name;
    
    private int currentSortingMethod;
    public final int DATE_DESC = 0, DATE_ASC = 1, SUBJECT_ASC = 2, SUBJECT_DESC = 3;
    
    private boolean specialFolder;

    public Folder(String name, boolean specialFolder) {
        this.name = name;
        this.specialFolder = specialFolder;
        this.emails = new ArrayList<>();
        this.currentSortingMethod = DATE_DESC;
    }
    
    public void addEmail(Email email) {
        if(currentSortingMethod == DATE_DESC) {
            emails.add(email);
            return;
        }
        switch(currentSortingMethod) {
            case DATE_DESC: emails.add(email); break;
            case DATE_ASC: emails.add(0, email); break;
            case SUBJECT_ASC:
            case SUBJECT_DESC:
                int indexToAdd = findSubjectIndex(email);
                if(indexToAdd == -1) emails.add(email); // Append to end
                else emails.add(indexToAdd, email); // Insert into spot
                break;
            default: break;
        }
    }
    
    public Email removeEmail(int index) {
        return emails.remove(index);
    }
    
    public void sortBy(int method) {
        if(this.currentSortingMethod == method) {
                return; // Do nothing. Same order as specified
        }
        
        if(method >= 0 && method <= 3) { // 0, 1, 2, or 3
            this.currentSortingMethod = method;
            sort();
        } else {
            throw new IllegalArgumentException("Sorting inputs are not valid numbers 0 or 1.");
        }
    }
    
    private void sort() {
        switch(currentSortingMethod) {
            case DATE_DESC: emails.sort(Comparator.comparing(Email::getTimestamp)); break;
            case DATE_ASC: emails.sort(Comparator.comparing(Email::getTimestamp).reversed()); break;
            case SUBJECT_ASC: emails.sort(Comparator.comparing(Email::getSubject)); break;
            case SUBJECT_DESC: emails.sort(Comparator.comparing(Email::getSubject).reversed()); break;
        }
    }
    
    private int findSubjectIndex(Email newEmail) {
        boolean AtoZ = currentSortingMethod == SUBJECT_ASC;
        String newSubject = newEmail.getSubject();
        
        if(emails.isEmpty()) return -1; // Empty folder. Just append
        
        int index = 0;
        for (Email email : emails) {
            if(AtoZ && newSubject.compareToIgnoreCase(email.getSubject()) < 0) { // Should come first
                return index;
            } else if (!AtoZ && newSubject.compareToIgnoreCase(email.getSubject()) > 0) {
                return index;
            }
            index++;
        }
        
        // Iterated through whole list. Append to end by giving -1
        return -1;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) throws IllegalAccessException {
        if(!this.specialFolder) {
            this.name = name;
        } else {
            throw new IllegalAccessException("Attempting to rename special folder");
        }
    }

    public ArrayList<Email> getEmails() {
        return emails;
    }

    public void setEmails(ArrayList<Email> emails) {
        this.emails = emails;
    }

    public int getCurrentSortingMethod() {
        return currentSortingMethod;
    }

    public void setCurrentSortingMethod(int currentSortingMethod) {
        this.currentSortingMethod = currentSortingMethod;
    }

    public boolean isSpecialFolder() {
        return specialFolder;
    }
    
}
