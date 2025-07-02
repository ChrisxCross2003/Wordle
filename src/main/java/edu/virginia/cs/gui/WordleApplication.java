package edu.virginia.cs.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
//import javax.swing.JTextField;
//https://www.youtube.com/watch?v=EE40IvWP5YM&ab_channel=JohnGizdich
// ^ for disabling the text field after user types in a letter, maybe?
// putting this here bc i'll need this later
    // https://docs.oracle.com/javafx/2/get_started/fxml_tutorial.htm
import java.io.IOException;

public class WordleApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(WordleApplication.class.getResource("wordle-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 625);
        stage.setTitle("Wordle");
        stage.setScene(scene);
        stage.show();
    }



    public static void main(String[] args) {
        launch();
    }
}