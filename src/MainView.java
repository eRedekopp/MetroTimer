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
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Startup code for the application, and controls for the UI
 */
public class MainView extends Application implements ModelListener {

    public TextField minText;
    public TextField secText;
    public Button timerButton;
    public Button metroButton;
    public TextField intervalText;
    public TextField bpmText;
    public Text volumeText;
    public Slider volumeSlider;

    private TimerController timerController;
    private Model model;
    private Audio audio;
    private Controller controller;

    /**
     * Update UI after a change to the Model
     */
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
                // volume text
                volumeText.setText(String.format("%.0f%%", model.getVolume() * 100));
            }
        });
    }

    /**
     * Setup for the application
     */
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
        audio.setModel(model);
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
        volumeText = (Text) root.lookup("#volumeText");
        volumeSlider = (Slider) root.lookup("#volumeSlider");

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
        minText.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldVal, Boolean newVal) {
                if (!newVal) {
                    if (! controller.handleMinTextChanged(minText.textProperty().get())) {
                        // return to old value if couldn't parse input
                        model.notifySubscribers();
                    }
                }
            }
        });
        secText.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldVal, Boolean newVal) {
                if (!newVal) {
                    if (! controller.handleSecTextChanged(secText.textProperty().get())) {
                        // return to old value if couldn't parse input
                        model.notifySubscribers();
                    }
                }
            }
        });
        intervalText.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldVal, Boolean newVal) {
                if (!newVal) {
                    if (! controller.handleIntervalTextChanged(intervalText.textProperty().get())) {
                        // return to old value if couldn't parse input
                        model.notifySubscribers();
                    }
                }
            }
        });
        bpmText.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldVal, Boolean newVal) {
                if (!newVal) {
                    if (! controller.handleBpmTextChanged(bpmText.textProperty().get())) {
                        // return to old value if couldn't parse input
                        model.notifySubscribers();
                    }
                }
            }
        });
        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldVal, Number newVal) {
                controller.handleVolSliderChange((Double) newVal);
            }
        });

        // get slider label to display correct value on startup
        model.setVolume(volumeSlider.getValue());
        model.notifySubscribers();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
