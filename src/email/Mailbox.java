package email;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Fefe-Hern <https://github.com/Fefe-Hern>
 */
public class Mailbox implements Serializable {

    private final Folder inbox;
    private final Folder trash;
    private HashMap<String, Folder> folders;
    
    public static Mailbox mailbox;
    
    static boolean programFlag = true;
    static Scanner input = new Scanner(System.in);
    
    public static void main(String[] args) {
        try {
            // Later implement loading of mailbox.obj

            File file = new File("mailbox.obj");
            file.createNewFile();
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream fin = new ObjectInputStream(fis);
            mailbox = (Mailbox) fin.readObject();
            fis.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Mailbox.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("No previous mailbox found. Creating new mailbox.");
        }
        
        // Create new mailbox if null
        if(mailbox == null) {
            mailbox = new Mailbox();
        }
        
        while(programFlag) {
            char select = prompt();
            switch(select) {
                case 'A': // Add Folder
                    System.out.print("Enter folder name: ");
                    String folderToAdd = input.nextLine();
                    Folder newFolder = new Folder(folderToAdd, false);
                    mailbox.addFolder(newFolder);
                    break;
                case 'R': // Remove Folder
                    System.out.print("Enter folder name: ");
                    String folderToRemove = input.nextLine();
                    mailbox.deleteFolder(folderToRemove);
                    break;
                case 'C': // Compose Email
                    mailbox.composeEmail();
                    break;
                case 'F': // Open Custom Folder
                    System.out.print("Enter folder name: ");
                    String folderName = input.nextLine();
                    promptFolder(folderName);
                    break;
                case 'I': // Open Inbox
                    promptFolder("inbox");
                    break;
                case 'T': // Open Trash
                    promptFolder("trash");
                    break;
                case 'E': // Clear Trash
                    mailbox.trash.setEmails(new ArrayList<>());
                    break;
                case 'Q': // Save & Quit
                    saveMailbox();
                    programFlag = false;
                    break; 
            }
        }
    }

    public Mailbox() {
        inbox = new Folder("inbox", true);
        trash = new Folder("trash", true);
        folders = new HashMap<>();
    }
    
    public static void printFolders() {
        System.out.println(mailbox.inbox.getName());
        System.out.println(mailbox.trash.getName());
        Set names = mailbox.folders.keySet();
        if (!names.isEmpty()) {
            for (Object name : names) {
                System.out.println(name.toString());
            }
        }
        System.out.println();
    }
    
    public static char prompt() {
        System.out.println("Mailbox:\n--------");
        printFolders();
        System.out.print("\n"
                + "A - Add Folder\n"
                + "R - Remove Folder\n"
                + "C - Compose Email\n"
                + "F - Open Folder\n"
                + "I - Open Inbox\n"
                + "T - Open Trash\n"
                + "E - Clear Trash\n"
                + "Q - Save & Quit\n"
                + "Enter a user option: ");
        while(true) {
            char select = input.nextLine().toUpperCase().charAt(0);
            if("ARCFITEQ".indexOf(select) != -1) { //One of the specified selections
                return select;
            } else {
                System.out.print("The option " + select + " does not exist.\n"
                        + "Enter a user option: ");
            }
        }
    }
    
    public static void promptFolder(String folderName) {
        Folder folder;
        if(folderName.equals("inbox")) folder = mailbox.inbox;
        else if(folderName.equals("trash")) folder = mailbox.trash;
        else {
            folder = mailbox.getFolder(folderName);
            if(folder == null) {
                System.out.println("Folder doesn't exist.");
                return;
            }
        }
        
        System.out.println(folderName);
        char folderInput = printEmails(folder);
        try {
            switch(folderInput) {
                case 'M': // Move Email
                    System.out.print("Enter Email index to move: ");
                    int emailIndexMove = input.nextInt(); input.nextLine();
                    Email email = folder.removeEmail(emailIndexMove);
                    printFolders();
                    System.out.print("Select a folder to move \"" + email.getSubject() + "\" to: ");
                    String folderTarget = input.nextLine();
                    if(mailbox.getFolder(folderTarget) == null) {
                        System.out.println("Invalid folder name.");
                        break;
                    }
                    mailbox.moveEmail(email, folderTarget);
                    System.out.println("\"" + email.getSubject() + "\" successfully moved to " + folderTarget);
                    break;
                    
                case 'D': // Delete Email
                    System.out.print("Enter email index to delete: ");
                    int emailIndexDelete = input.nextInt(); input.nextLine();
                    Email trashed = folder.removeEmail(emailIndexDelete);
                    mailbox.trash.addEmail(trashed);
                    System.out.println("\"" + trashed.getSubject() + "\" successfully moved to trash");
                    break;
                    
                case 'V': // View Email Contents
                    System.out.print("Enter email index to view: ");
                    int emailIndexView = input.nextInt(); input.nextLine();
                    Email viewed = folder.getEmails().get(emailIndexView);
                    System.out.println(viewed.toString());
                    System.out.println("Press any key to continue...");
                    input.nextLine();
                    break;
                    
                case 'W': // Sort
                    folder.sortBy(folder.SUBJECT_ASC);
                    promptFolder(folderName);
                    break;
                case 'X': // Sort
                    folder.sortBy(folder.SUBJECT_DESC);
                    promptFolder(folderName);
                    break;
                case 'Y': // Sort
                    folder.sortBy(folder.DATE_ASC);
                    promptFolder(folderName);
                    break;
                case 'Z': // Sort
                    folder.sortBy(folder.DATE_DESC);
                    promptFolder(folderName);
                    break;
                case 'R': break; // Return
            }
        } catch(IndexOutOfBoundsException e) {
            System.out.println("\n\nAn email Index went out of bounds. Restarting.");
        }
    }
    
    public static char printEmails(Folder folder) {
        System.out.println("Index |        Time       | Subject");
        System.out.println("-----------------------------------");
        ArrayList<Email> emails = folder.getEmails();
        
        if(emails.isEmpty()) {
            System.out.println(folder.getName() + " is empty.");
        } else {
            int index = 0;
            for (Email email : emails) {
                System.out.printf("%6d|%19s|%8s\n",
                        index,
                        email.getTimestamp().toZonedDateTime().format(DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss")),
                        email.getSubject());
                System.out.println("-----------------------------------");
                index++;
            }
        }
        
        
        System.out.print("\n"
                + "M - Move Email\n"
                + "D - Delete Email\n"
                + "V - View Email Contents\n"
                + "W - Sort by SUBJECT in ASCENDING order (A to Z)\n"
                + "X - Sort by SUBJECT in DESCENDING order (Z to A)\n"
                + "Y - Sort by DATE in ASCENDING order\n"
                + "Z - Sort by DATE in DESCENDING order\n"
                + "R - Return to mailbox\n"
                + "Enter a user option: ");
        while(true) {
            char select = input.nextLine().toUpperCase().charAt(0);
            if("MDVWXYZR".indexOf(select) != -1) { //One of the specified selections
                return select;
            } else {
                System.out.print("The option " + select + " does not exist.\n"
                        + "Enter a user option: ");
            }
        }
    }
    
    private static void saveMailbox() {
        try {
            FileOutputStream file = new FileOutputStream("mailbox.obj");
            ObjectOutputStream fout = new ObjectOutputStream(file);
            fout.writeObject(mailbox);
            fout.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Mailbox.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Mailbox.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void addFolder(Folder folder) {
        if(folders.containsKey(folder.getName())) {
            System.out.println("Folder already exists");
            return;
        } else {
            folders.put(folder.getName(), folder);
        }
    }
    
    public void deleteFolder(String name) {
        if(folders.remove(name) != null) {
            System.out.println("Folder removed.");
        } else {
            System.out.println("Folder not found.");
        }
    }
    
    public void composeEmail() {
        System.out.print("Enter Recipient (To): ");
        String to = input.nextLine();
        
        System.out.print("Enter CC recipients: ");
        String cc = input.nextLine();
        
        System.out.print("Enter BCC recipients: ");
        String bcc = input.nextLine();
        
        System.out.print("Enter Subject Line: ");
        String subject = input.nextLine();
        
        System.out.println("Enter Body:");
        String body = input.nextLine();
        
        mailbox.inbox.addEmail(new Email(to, cc, bcc, subject, body));
    }
    
    public void deleteEmail(Email email) {
        
    }
    
    public void clearTrash() {
        
    }
    
    public void moveEmail(Email email, String target) {
        Folder folderTarget = getFolder(target);
        if(folderTarget == null) {
            throw new IllegalArgumentException("Folder name is not specified.");
        } else {
            folderTarget.addEmail(email);
        }
    }
    
    public Folder getFolder(String name) {
        if(name.equals("inbox")) return mailbox.inbox;
        if(name.equals("trash")) return mailbox.trash;
        return folders.get(name);
    }
    
}