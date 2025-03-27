package gui;

import javafx.event.ActionEvent;
import javafx.stage.Stage;

public interface IControl {
	public void start(Stage primaryStage) throws Exception;

	public void getBackBtn(ActionEvent event) throws Exception;

}
