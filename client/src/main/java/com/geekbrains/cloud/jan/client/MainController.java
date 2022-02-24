package com.geekbrains.cloud.jan.client;

import com.geekbrains.cloud.jan.common.*;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.w3c.dom.css.CSSUnknownRule;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class MainController implements Initializable {

    @FXML
    public VBox clientSide, serverSide;

    @FXML
    Button deleteFromClient, deleteFromServer;  // кнопка удалить из клиента, из сервера

    @FXML
    ListView<String> clientFileList;  // список файлов клиента

    @FXML
    ListView<String> serverFileList;  // список файлов сервера

    @FXML
    HBox cloudPanel;  // облачная панель

    @FXML
    VBox authPanel;   // панель авторизации

    @FXML
    TextField loginField;  // поле для входа в систему

    @FXML
    PasswordField passwordField;  // поле пароля

    @FXML
    Label authLabel;  // авторизация




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAuthorized(false);
        Network.start();

        Thread thread = new Thread(() -> {
            try {
                while (true) {
                    AbstractMessage abstractMessage = Network.readObject();
                    if (abstractMessage instanceof AuthMessage) {
                        AuthMessage authMessage = (AuthMessage) abstractMessage;
                        if ("/authOk".equals(authMessage.message)) {
                            setAuthorized(true);
                            break;
                        }
                        if ("/null_userId".equals(authMessage.message)) {
                            Platform.runLater(() -> authLabel.setText("Неверный логин или пароль"));
                        }
                    }
                }

                Network.sendMsg(new RefreshServerMessage());



                while (true) {
                    AbstractMessage abstractMessage = Network.readObject();
                    if (abstractMessage instanceof FileMessage) {
                        FileMessage fileMessage = (FileMessage) abstractMessage;
                        Files.write(Paths.get("client_storage/" + fileMessage.getFilename()), fileMessage.getData(), StandardOpenOption.CREATE);

                    }
                    if (abstractMessage instanceof RefreshServerMessage) {
                        RefreshServerMessage refreshServerMsg = (RefreshServerMessage) abstractMessage;
                        refreshServerFilesList(refreshServerMsg.getServerFileList());
                    }
                }

            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            } finally {
                Network.stop();
            }
        });
        thread.setDaemon(true);
        thread.start();
        refreshLocalFilesList();
        System.out.println("New client connected...");
    }


    private void setAuthorized(boolean isAuthorized) {
        if (!isAuthorized) {
            authPanel.setVisible(true);
            authPanel.setManaged(true);
            cloudPanel.setVisible(false);
            cloudPanel.setVisible(false);
        } else {
            authPanel.setVisible(false);
            authPanel.setManaged(false);
            cloudPanel.setVisible(true);
            cloudPanel.setVisible(true);
            clientSide.setVisible(true);
            serverSide.setVisible(true);

        }
    }

    public void tryToAuth() {   // авторизация
        Network.sendMsg(new AuthMessage(loginField.getText(), passwordField.getText()));
        loginField.clear();
        passwordField.clear();
    }

    public void pressOnDownloadButton(ActionEvent actionEvent) {  // нажатие на кнопку загрузки
        Network.sendMsg(new DownloadRequest(serverFileList.getSelectionModel().getSelectedItem()));
    }

    public void pressOnSendToCloudButton(ActionEvent actionEvent) {  // нажатие на кнопку отправить в облако
        try {
            Network.sendMsg(new FileMessage(Paths.get("client_storage/" + clientFileList.getSelectionModel().getSelectedItem())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pressOnDeleteButton(ActionEvent actionEvent) { // нажатие на кнопку удалить
        Button sourceButton = (Button) actionEvent.getSource();

        if (deleteFromClient.equals(sourceButton)) {
            try {
                Files.delete(Paths.get("client_storage/" + clientFileList.getSelectionModel().getSelectedItem()));
                refreshLocalFilesList();  // обновляем список локальных файлов
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (deleteFromServer.equals(sourceButton)) {
            Network.sendMsg(new DeleteRequest(serverFileList.getSelectionModel().getSelectedItem()));
        }
    }

    private void refreshLocalFilesList() {  // обновить список локальных файлов
        updateUI(() -> {
            try {
                clientFileList.getItems().clear();
                Files.list(Paths.get("client_storage/"))
                        .map(p -> p.getFileName().toString())
                        .forEach(o -> clientFileList.getItems().add(o));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    private void refreshServerFilesList(ArrayList<String> filesList) { // обновить список файлов сервера
        updateUI(() -> {
            serverFileList.getItems().clear();
            serverFileList.getItems().addAll(filesList);
        });
    }

    private static void updateUI(Runnable runnable) {   // обновление
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            Platform.runLater(runnable);
        }
    }

//    private void initMouseListenersClient() {    //
//        clientFileList.setOnMouseClicked(e -> {
//            if (e.getClickCount() == 2) {
//                Path current = client_Storage.resolve(getItem());
//                if (Files.isDirectory(current)) {
//                    client_Storage = current;
//                    Platform.runLater(this::refreshLocalFilesList);
//                }
//            }
//        });
//    }

//    private String getItem() {
//        return clientFileList.getSelectionModel().getSelectedItem();
//    }

}
