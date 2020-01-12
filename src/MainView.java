import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class MainView extends Application implements ModelListener {

    public TextField minText;
    public TextField secText;
    public Button timerButton;
    public Button metroButton;
    public TextField intervalText;
    public TextField bpmText;

    private TimerController timerController;
    private Model model;
    private Audio audio;
    private Controller controller;

    public void modelChanged() {
        // update all view elements on UI thread
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                // timer text
                minText.setText(Integer.toString(model.getTimerMin()));
                minText.setEditable(!timerController.timerRunning());
                String sec = Integer.toString(model.getTimerSec());
                if (model.getTimerSec() < 10)    // pad seconds with extra 0 if necessary
                    sec  = "0" + sec;
                secText.setText(sec);
                secText.setEditable(!timerController.timerRunning());
                // metronome text
                intervalText.setText(Integer.toString(model.getAccentInterval()));
                intervalText.setEditable(!audio.audioPlaying());
                bpmText.setText(Integer.toString(model.getBpm()));
                bpmText.setEditable(!audio.audioPlaying());
                // buttons
                timerButton.setText(timerController.timerRunning() ? "Stop" : "Start");
                metroButton.setText(audio.audioPlaying() ? "Stop" : "Start");
            }
        });
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        // setup MVC
        timerController = new TimerController();
        model = new Model();
        audio = new Audio();
        controller = new Controller();

        timerController.setAudio(audio);
        timerController.setModel(model);
        controller.setAudio(audio);
        controller.setModel(model);
        controller.setTimerController(timerController);
        model.addSubscriber(this);

        // setup the stage
        Parent root = FXMLLoader.load(getClass().getResource("MainView.fxml"));
        primaryStage.setTitle("MetroTime");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        // find elements
        minText = (TextField) root.lookup("#minText");
        secText = (TextField) root.lookup("#secText");
        timerButton = (Button) root.lookup("#timerButton");
        metroButton = (Button) root.lookup("#metroButton");
        intervalText = (TextField) root.lookup("#intervalText");
        bpmText = (TextField) root.lookup("#bpmText");

        // add button listeners
        metroButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                controller.handleMetroClick();
            }
        });
        timerButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                controller.handleTimerClick();
            }
        });
        // add text input listeners
        minText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue,
                                String oldValue, String newValue) {
                // do nothing if box is empty
                if (newValue.length() == 0) return;
                // delete the new character if the controller couldn't parse the input
                if (! controller.handleMinTextChanged(newValue)) {
                    minText.setText(oldValue);
                }
            }
        });
        secText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue,
                                String oldValue, String newValue) {
                // do nothing if box is empty
                if (newValue.length() == 0) return;
                // delete the new character if the controller couldn't parse the input
                if (! controller.handleSecTextChanged(newValue)) {
                    secText.setText(oldValue);
                }
            }
        });
        intervalText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue,
                                String oldValue, String newValue) {
                // do nothing if box is empty
                if (newValue.length() == 0) return;
                // delete the new character if the controller couldn't parse the input
                if (! controller.handleIntervalTextChanged(newValue)) {
                    intervalText.setText(oldValue);
                }
            }
        });
        bpmText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue,
                                String oldValue, String newValue) {
                // do nothing if box is empty
                if (newValue.length() == 0) return;
                // delete the new character if the controller couldn't parse the input
                if (! controller.handleBpmTextChanged(newValue)) {
                    bpmText.setText(oldValue);
                }
            }
        });
    }

    public static void main(String[] args) {

        launch(args);
    }

}
