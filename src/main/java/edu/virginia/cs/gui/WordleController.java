package edu.virginia.cs.gui;

import edu.virginia.cs.wordle.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;

import java.util.*;

public class WordleController {
    public Label errorLabel;
    static Paint green = Paint.valueOf("00FF00");
    static Paint yellow = Paint.valueOf("FFFF00");
    static Paint gray = Paint.valueOf("808080");
    static Paint white = Paint.valueOf("FFFFFF");
    @FXML VBox table;
    @FXML HBox row1, row2, row3, row4, row5, row6;
    HBox currentRow = row1;
    public int currentRowIndex = 1;
    @FXML TextField box1a, box1b, box1c, box1d, box1e;
    @FXML TextField box2a, box2b, box2c, box2d, box2e;
    @FXML TextField box3a, box3b, box3c, box3d, box3e;
    @FXML TextField box4a, box4b, box4c, box4d, box4e;
    @FXML TextField box5a, box5b, box5c, box5d, box5e;
    @FXML TextField box6a, box6b, box6c, box6d, box6e;
    DefaultDictionaryFactory dictionaryFactory = new DefaultDictionaryFactory();
    WordleDictionary answer_dictionary = dictionaryFactory.getDefaultAnswersDictionary();
    WordleDictionary guess_dictionary = dictionaryFactory.getDefaultGuessesDictionary();
    WordleImplementation implementation = new WordleImplementation(answer_dictionary.getRandomWord(),guess_dictionary);
    String answer = implementation.getAnswer();

    @FXML
    private Label welcomeText;
    private final StringBuilder guess = new StringBuilder();
    private boolean invalidWord = false;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    public void initialize() {
        System.out.println("🤫 Psst... The answer is: " + answer);
    }

    public void onTextEntry(KeyEvent event) {
        //System.out.println("Psst... the answer is: "+answer);
        TextField source = (TextField) event.getSource();
        int length = source.getText().length();
        // If the length of the text in the current TextField is greater than 0 and the user pressed a key from the alphabet...
        if (currentRow == null) {
            //initialize row
            currentRow = row1;
            row1.getChildren().get(0).setStyle("-fx-control-inner-background: #"+white.toString().substring(2));
            unlockRow(currentRow);
        }
        if (event.getCode() == KeyCode.BACK_SPACE && length == 0) {
            invalidWord = false;
            errorLabel.setVisible(false);
            if (!guess.isEmpty()) {
                guess.deleteCharAt(guess.length()-1);
                System.out.println(guess);
            }
            goToPreviousBox(source);
        }


        if (length >= 1 && !invalidWord) {
            if (length > 1) {
                // Debug for Capital Letters using Shift, or player presses Enter.
                source.setText(source.getText().substring(0, 1));
            }
            // Get the index of the current TextField in the row
            int index = currentRow.getChildren().indexOf(source);
            if (index < 4) {
                moveToNextBoxInRow(index);


            } else if (index == 4) {
                // If the current TextField is the last box in the row, add the current character to the box and move to the next row
                TextField lastBox = (TextField) currentRow.getChildren().get(index);
                if (lastBox != null) {
                    String currentText = lastBox.getText();
                    if (currentText.isEmpty()) {
                        lastBox.setText(source.getText());
                    }
                }
                String finalGuess = createFinalGuess();
                finalGuess = finalGuess.toUpperCase();
                //DEBUG: Keep track of final guess for row.
                System.out.println(finalGuess);
                if (!(guess_dictionary.isLegalWordleWord(finalGuess))) {
                    //if word is not valid...
                    invalidWord = true;
                    errorLabel.setText("Invalid word");
                    errorLabel.setVisible(true);
                } else if (answer_dictionary.containsWord(finalGuess)){
                    lockRow(currentRow);
                    guess.setLength(0);
                    System.out.println("word is in dictionary!");
                    if (answer.equals(finalGuess)) {
                        for (Node node : currentRow.getChildren()) {
                            TextField box = (TextField) node;
                            box.setStyle("-fx-control-inner-background: #" + green.toString().substring(2));
                        }
                        try {
                            implementation.submitGuess(finalGuess);
                        } catch (GameAlreadyOverException e) {
                            // Game is already over — no action needed
                        }
                        System.out.println("You win! \n Do you want to play again?");
                        endGame("Game over. You win! \n Do you want to play again?");
                    }
                    if (!answer.equals(finalGuess)){
                        int boxIndex = 0;
                        for (int j=0;j<currentRow.getChildren().size();j++) {
                            TextField box = (TextField) currentRow.getChildren().get(j);
                            String character = String.valueOf(answer.charAt(j));
                            String potentialCharacter = box.getText().toUpperCase();
                                if (answer.contains(potentialCharacter)) {
                                    if (character.equals(potentialCharacter)) {
                                        box.setStyle("-fx-control-inner-background: #"+green.toString().substring(2));
                                    } else if(!potentialCharacter.isEmpty()) {
                                        box.setStyle("-fx-control-inner-background: #"+yellow.toString().substring(2));
                                    }
                                } else {
                                    box.setStyle("-fx-control-inner-background: #"+gray.toString().substring(2));
                                }
                        }
                        if (currentRowIndex < 6) {
                            //if we still have rows left...
                            implementation.submitGuess(finalGuess);
                            moveToNextRow();
                        } else {
                            System.out.println("You lose! Do you want to play again?");
                            endGame("You lose! The word was: "+answer+"\n Do you want to play again?");
                        }
                    }
                } else {
                    invalidWord = true;
                    errorLabel.setText("Word not in dictionary.");
                    errorLabel.setVisible(true);
                }
            }
        }
    }

    private void endGame(String messageWin) {
        ButtonType playAgain = new ButtonType("Yes");
        ButtonType notPlayAgain = new ButtonType("No");
        Alert wonGame = new Alert(Alert.AlertType.NONE, messageWin, playAgain, notPlayAgain);
        Optional<ButtonType> clicked = wonGame.showAndWait();
        if (clicked.get() == playAgain) {
            resetGame();
        }
        if (clicked.get() == notPlayAgain) {
            System.exit(0);
        }
    }

    private String createFinalGuess() {
        guess.setLength(0);
        for (Node node : currentRow.getChildren()) {
            TextField field = (TextField) node;
            guess.append(field.getText());
        }
        return guess.toString();
    }

    private void moveToNextBoxInRow(int index) {
        TextField nextBox = (TextField) currentRow.getChildren().get(index + 1);
        nextBox.requestFocus();
        nextBox.positionCaret(nextBox.getText().length());
    }

    private void moveToNextRow() {
        currentRowIndex++;
        currentRow = (HBox) currentRow.getParent().getChildrenUnmodifiable().get(currentRowIndex);
        TextField nextBox = (TextField) currentRow.getChildren().get(0);
        unlockRow(currentRow);
        nextBox.requestFocus();
    }

    private void goToPreviousBox(TextField source) {
        int index = currentRow.getChildren().indexOf(source);
        if (index > 0) {
            TextField prevBox = (TextField) currentRow.getChildren().get(index - 1);
            prevBox.requestFocus();
        }
    }

    @FXML
    private void lockRow(HBox row) {
        for (Node node : row.getChildren()) {
            TextField field = (TextField) node;
            field.setEditable(false);
        }
    }
    @FXML
    private void unlockRow(HBox row) {
        for (Node node : row.getChildren()) {
            TextField field = (TextField) node;
            field.setEditable(true);
        }
    }
    protected void resetGame() {
        while (currentRowIndex > 0) {
            currentRow = (HBox) currentRow.getParent().getChildrenUnmodifiable().get(currentRowIndex);
            for (Node node : currentRow.getChildren()) {
                TextField field = (TextField) node;
                field.clear();
                field.setStyle("-fx-control-inner-background: #" + white.toString().substring(2));
            }
            currentRowIndex--;
        }
        implementation = new WordleImplementation(answer_dictionary.getRandomWord(), guess_dictionary);
        currentRow = row1;
        row1.getChildren().get(0).setStyle("-fx-control-inner-background: #" + white.toString().substring(2));
        row1.getChildren().get(0).requestFocus(); // Set focus on the first box of row1
        unlockRow(currentRow);
        answer = implementation.getAnswer();
        currentRowIndex = 0;
        System.out.println("🤫 Psst... The answer is: " + answer);
    }
}

