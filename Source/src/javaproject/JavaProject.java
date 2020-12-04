package javaproject;

import com.opencsv.CSVReaderHeaderAware;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class JavaProject extends Application {

    LinkedList<String> Songs = new LinkedList<String>();
    int Current = 0;
    ListView<String> list = new ListView<String>();
    Clip clip = null;
    boolean Playing = false;
    static HashMap<String, User> Users = new HashMap<String, User>();

    //start the GUI
    public void start(Stage stage) {
        Group root = new Group();
        
        //CREATE USER
        Users.put("admin", new User("admin", "pass"));

        Button help = new Button("Help");
        help.setLayoutX(213);
        help.setLayoutY(5);
        
        help.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText("How to use MP3 Player");
                alert.setContentText("Adding:\nTo add one or songs, click the add button. Navigate to the location of the song and select open.\n\nSaving:\nTo save a playlist, load all songs into the MP3 and click save. Then select a place to save your playlist.\n\nLoading:\nTo load a playlist, click the load button and find your previously saved CSV.\n\nSort:\nTo sort songs, click the sort button. This sorts alphabetically.\n\nSearching:\nTo search, type a song name into the text field and click search. If your song is found it will highlight in the list view.\n\nNext and Back:\nThese buttons will take your forward and backward in the playlist.\n\nPlay/Stop:\nThis will toggle playing your song.");
                alert.setTitle("Help");
                Optional<ButtonType> result = alert.showAndWait();
            }
        });

        Button addbutton = new Button("Add");
        addbutton.setLayoutX(10);
        addbutton.setLayoutY(5);
        
        Button backbutton = new Button("Back");
        backbutton.setLayoutX(50);
        backbutton.setLayoutY(5);
        
        Button nextbutton = new Button("Next");
        nextbutton.setLayoutX(166);
        nextbutton.setLayoutY(5);

        Button savebutton = new Button("Save");
        savebutton.setLayoutX(10);
        savebutton.setLayoutY(440);

        Button loadbutton = new Button("Load");
        loadbutton.setLayoutX(55);
        loadbutton.setLayoutY(440);

        Button sortbutton = new Button("Sort");
        sortbutton.setLayoutX(105);
        sortbutton.setLayoutY(440);

        Button searchbutton = new Button("Search");
        searchbutton.setLayoutX(160);
        searchbutton.setLayoutY(480);
        
        Button stopbutton = new Button("Play/Stop");
        stopbutton.setLayoutX(95);
        stopbutton.setLayoutY(5);

        TextField searchField = new TextField();
        searchField.setLayoutX(10);
        searchField.setLayoutY(480);
        
        TextField username = new TextField();
        username.setLayoutX(50);
        username.setLayoutY(170);
        
        TextField password = new TextField();
        password.setLayoutX(50);
        password.setLayoutY(215);
        
        Button loginbutton = new Button("Login");
        loginbutton.setLayoutX(50);
        loginbutton.setLayoutY(255);
        
        Text t = new Text(10, 50, "Incorrect Details");
        t.setLayoutX(108);
        t.setLayoutY(220);
        
        loginbutton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String user = username.getText();
                String pass = password.getText();
                User u = null;
                try {
                    u = Users.get(user);
                } catch (Exception ex) {
                    return;
                }
                if (u == null) {
                    t.setVisible(true);
                    return;
                }
                if (Hashing.ComparePasswords(u.getSalt(), u.getHashedPassword(), pass)) {
                    help.setVisible(true);
                    list.setVisible(true);
                    addbutton.setVisible(true);
                    nextbutton.setVisible(true);
                    backbutton.setVisible(true);
                    savebutton.setVisible(true);
                    loadbutton.setVisible(true);
                    searchbutton.setVisible(true);
                    sortbutton.setVisible(true);
                    searchField.setVisible(true);
                    stopbutton.setVisible(true);
                    loginbutton.setVisible(false);
                    username.setVisible(false);
                    password.setVisible(false);
                    t.setVisible(false);
                } else {
                    System.out.println("NO");
                    t.setVisible(true);
                }
            }
        });
        
        stopbutton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                StopSong();
            }
        });
        
        sortbutton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                SortSongsClicked();
            }
        });

        searchbutton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String song = searchField.getText();
                String value = SearchSong(song);
                System.out.println(value);
            }
        });

        savebutton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();

                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
                fileChooser.getExtensionFilters().add(extFilter);

                File file = fileChooser.showSaveDialog(stage);
                SaveSongs(file.getAbsolutePath());
            }
        });

        loadbutton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser chooser = new FileChooser();
                chooser.setTitle("Select CSV");
                File defaultDirectory = new File("c:/");
                chooser.setInitialDirectory(defaultDirectory);
                Window primaryStage = null;

                File file = chooser.showOpenDialog(primaryStage);
                LoadSongs(file);
            }
        });

        nextbutton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                NextSong();
            }
        });

        

        backbutton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                BackSong();
            }
        });

        addbutton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                SelectFiles();
            }
        });

        list.getSelectionModel().select(0);
        list.setLayoutX(10);
        list.setLayoutY(35);
        
        
        
        
        root.getChildren().add(help);
        root.getChildren().add(list);
        root.getChildren().add(addbutton);
        root.getChildren().add(nextbutton);
        root.getChildren().add(backbutton);
        root.getChildren().add(savebutton);
        root.getChildren().add(loadbutton);
        root.getChildren().add(searchbutton);
        root.getChildren().add(sortbutton);
        root.getChildren().add(searchField);
        root.getChildren().add(stopbutton);
        root.getChildren().add(username);
        root.getChildren().add(password);
        root.getChildren().add(loginbutton);
        root.getChildren().add(t);
        
        help.setVisible(false);
        list.setVisible(false);
        addbutton.setVisible(false);
        nextbutton.setVisible(false);
        backbutton.setVisible(false);
        savebutton.setVisible(false);
        loadbutton.setVisible(false);
        searchbutton.setVisible(false);
        sortbutton.setVisible(false);
        searchField.setVisible(false);
        stopbutton.setVisible(false);
        t.setVisible(false);
        

        Scene scene = new Scene(root, 266, 515);

        stage.setTitle("MP3 Player");
        stage.setScene(scene);
        stage.show();

    }
    
    //play the song from filepath
    private void playSong(String filePath) {
        AudioInputStream audioIn = null;
        try {
            File f = new File(filePath);
            System.out.println(f.toURI().toURL());
            audioIn = AudioSystem.getAudioInputStream(f.toURI().toURL());
            clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (Exception ex) {
            Logger.getLogger(JavaProject.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                audioIn.close();
            } catch (IOException ex) {
                Logger.getLogger(JavaProject.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    //merge the two song lists together
    private List<String> MergeSongs(List<String> left, List<String> right) {
        List<String> combined = new ArrayList<String>();

        while (left.size() > 0 || right.size() > 0) {
            if (left.size() > 0 && right.size() > 0) {

                if (left.get(0).compareTo(right.get(0)) <= 0) {
                    combined.add(left.get(0));
                    left.remove(left.get(0));
                } else {
                    combined.add(right.get(0));
                    right.remove(right.get(0));
                }
            } else if (left.size() > 0) {
                combined.add(left.get(0));
                left.remove(left.get(0));
            } else if (right.size() > 0) {
                combined.add(right.get(0));
                right.remove(right.get(0));
            }
        }

        return combined;
    }

    //Sorts the songs
    private List<String> SortSongs(List<String> unsorted) {
        if (unsorted.size() < 2) {
            return unsorted;
        }

        List<String> left = new ArrayList<String>();
        List<String> right = new ArrayList<String>();

        int mid = unsorted.size() / 2;

        for (int i = 0; i < mid; i++) {
            left.add(unsorted.get(i));
        }
        for (int i = mid; i < unsorted.size(); i++) {
            right.add(unsorted.get(i));
        }

        left = SortSongs(left);

        right = SortSongs(right);

        return MergeSongs(left, right);
    }
    
    //convert linkedlist to ArrayList and then Merge sort
    private void SortSongsClicked() {
        List<String> tempSongs = new ArrayList<String>();

        for (String song : Songs) {
            String[] split = song.split("\\\\");
            String item = split[split.length - 1];
            tempSongs.add(item);
        }

        List<String> SortedList = SortSongs(tempSongs);

        Songs.clear();

        for (String song : SortedList) {
            Songs.add(song);
        }

        updateSongs();
    }
    
    //Search for song using binary search
    private String SearchSong(String SongToFind) {
        SortSongsClicked(); //Sort the songs first
        String[] tempSongs = new String[Songs.size()];

        //convert songs to array
        int counter = 0;
        for (String song : Songs) {
            String[] split = song.split("\\\\");
            tempSongs[counter] = split[split.length - 1];
            counter++;
        }

        //perform the binary search
        int minNum = 0;
        int maxNum = Songs.size() - 1;
        
        while (minNum <= maxNum) {
            int mid = (minNum + maxNum) / 2;

            if (SongToFind.equals(tempSongs[mid])) {
                list.getSelectionModel().select(mid);
                Current = mid;
                return tempSongs[mid];
            } else if (SongToFind.compareTo(tempSongs[mid]) < 0) {
                maxNum = mid - 1;
            } else {
                minNum = mid + 1;
            }
        }

        return "";
    }
    
    //update the songs inside the listview
    private void updateSongs() {
        ObservableList<String> obList = FXCollections.observableArrayList();

        for (String song : Songs) {
            String[] split = song.split("\\\\");
            obList.add(split[split.length - 1]); 
        }

        list.setItems(obList);
    }
    
    //Select files to be added to the mp3 player
    private void SelectFiles() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select MP3");
        File defaultDirectory = new File("c:/");
        chooser.setInitialDirectory(defaultDirectory);
        Window primaryStage = null;

        List<File> files = chooser.showOpenMultipleDialog(primaryStage);

        for (File file : files) { //loop through selected files
            System.out.println(file);
            Songs.add(file.getAbsolutePath());
        }
        updateSongs(); //update the list box
    }

    //load songs from CSV using OpenCSV
    private void LoadSongs(File file) {
        try {
            CSVReaderHeaderAware CSVReader = new CSVReaderHeaderAware(new FileReader(file.getPath())); //create CSVReader instance
            List<String[]> csvData = CSVReader.readAll();
            for (String[] data : csvData) {
                Songs.add(data[0]); //add songs to linked list
            }

            updateSongs();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(JavaProject.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(JavaProject.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CsvException ex) {
            Logger.getLogger(JavaProject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Save the songs to a CSV file using OpenCSV
    private void SaveSongs(String Path) {
        try {
            Writer writer = Files.newBufferedWriter(Paths.get(Path)); //create instance of writer

            CSVWriter csvWriter = new CSVWriter(writer,
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);

            String[] headerRecord = {"Path", "Name"};
            csvWriter.writeNext(headerRecord);

            for (String song : Songs) {
                String[] split = song.split("\\\\"); //Split the file path to find the file name
                song = song.replace("\\", "\\\\");
                csvWriter.writeNext(new String[]{song, split[split.length - 1]});
            }

            writer.close();

        } catch (IOException ex) {
            Logger.getLogger(JavaProject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //toggle playing song
    private void StopSong() {
        if (Playing) { //check if song is playing
            Playing = false; //stop playing
            clip.stop();
        } else { //song is not playing, play current
            Playing = true;
            playSong(Songs.get(Current));
            list.getSelectionModel().select(Current);
        }
    }
    //goes forward in the playlist
    private void NextSong() {
        try {
            clip.stop();
        }
        catch (Exception ex) {
            
        }
        Playing = true;
        Current++;
        playSong(Songs.get(Current)); //play the song
        list.getSelectionModel().select(Current); //set the selected song
    }
    //goes back a song
    private void BackSong() {
        try {
            clip.stop();
        }
        catch (Exception ex) {
            
        }
        Playing = true;
        Current--;
        playSong(Songs.get(Current)); //play the song
        list.getSelectionModel().select(Current);//set the selected song
    }

    public static void main(String[] args) {
        launch(args);
    }
}
