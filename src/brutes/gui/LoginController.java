package brutes.gui;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import brutes.ScenesContext;
import brutes.ScenesContext;
import brutes.net.Protocol;
import brutes.net.client.ErrorResponseException;
import brutes.net.client.InvalidResponseException;
import brutes.net.client.NetworkClient;
import brutes.user.Session;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author Karl
 */
public class LoginController implements Initializable {
    @FXML
    private Button connexion;
    @FXML
    private TextField login;
    @FXML
    private PasswordField password;
    @FXML
    private TextField server;
    @FXML
    private ProgressIndicator loading;
    @FXML
    private Text logError;
    
    @FXML
    private void handleConnexionAction(ActionEvent e){
        logError.setVisible(false);
        this.login();
    }
    
    private synchronized void login(){
        Logger.getLogger(LoginController.class.getName()).log(Level.INFO, "Login thread");
        
        this.loading.setVisible(true);
        this.login.setDisable(true);
        this.password.setDisable(true);
        this.server.setDisable(true);
        this.connexion.setDisable(true);
        
        final LoginTask loginTask = new LoginTask(this.server.getText(), this.login.getText(), this.password.getText());
        this.logError.textProperty().bind(loginTask.statusMessageProperty());

        loginTask.stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends State> observable, State oldValue, State newState) {
                if(newState == Worker.State.SUCCEEDED){
                    loginTask.cancel();
                    ScenesContext.getInstance().showFight();
                    this.reactiveLogin();
                }
                else if(newState == Worker.State.FAILED){
                    loginTask.cancel();
                    logError.setVisible(true);
                    this.reactiveLogin();
                }
            }   

            private void reactiveLogin() {
                login.setDisable(false);
                password.setDisable(false);
                server.setDisable(false);
                connexion.setDisable(false);
                loading.setVisible(false);
            }
        });
        new Thread(loginTask).start();
    }
   
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
}