package client.scenes;

import client.utils.DialogBoxUtils;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Collection;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

public class CollectionSettingsCtrl {

    @FXML
    private TextField titleTextField;

    @FXML
    private TextField serverTextField;

    @FXML
    private TextField nameTextField;

    @FXML
    private Label statusLabel;

    @FXML
    private ListView<Collection> collectionsListView;

    // Injectable
    private ServerUtils serverUtils;

    @Inject
    public CollectionSettingsCtrl(ServerUtils serverUtils) {
        this.serverUtils = serverUtils;
    }

    @FXML
    public void initialize() {
        var collections = serverUtils.getAllCollections();

        collectionsListView.getItems().addAll(collections);

        collectionsListView.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(Collection item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setGraphic(new Label(item.title));
                }
            }
        });

        // select a collection (default one)
        // FIXME: incorrect (default is not necessarily first)
        collectionsListView.getSelectionModel().select(0);

        // fill in the text fields

        // show a status (in the future...)
    }

    public void onListViewClick() {
        Collection selectedCollection = getSelectedCollection();
        loadCollectionInfo(selectedCollection);
    }

    private Collection getSelectedCollection() {
        return collectionsListView.getSelectionModel().getSelectedItem();
    }

    @FXML
    private void onTitleChange() {
        Collection selectedCollection = getSelectedCollection();
        selectedCollection.title = titleTextField.getText();
        collectionsListView.refresh();
    }

    @FXML
    private void onServerChange() {
        // TODO: implement
    }

    @FXML
    private void onNameChange() {
        Collection selectedCollection = getSelectedCollection();
        selectedCollection.name = nameTextField.getText();
    }

    @FXML
    private void onMakeDefault() {
        // TODO: implement
    }

    @FXML
    private void onCreate() {
        String collectionName = serverUtils.getUniqueCollectionName();

        Collection newCollection = new Collection(
                collectionName,
                "New Collection"
        );

        // FIXME: another place to catch an error
        newCollection = serverUtils.addCollection(newCollection);

        collectionsListView.getItems().add(newCollection);
        collectionsListView.refresh();
    }

    @FXML
    private void onDelete() {
        // TODO: add a confirmation (collections AND its notes will be deleted!)

        Collection selectedCollection = getSelectedCollection();
        if (selectedCollection == null) {
            // Since default collection must always exist (cannot be deleted)
            // this edge case should never happen, just an extra safety measure
            return;
        }

        if (selectedCollection.isDefault) {
            var dialog = DialogBoxUtils.createSimpleDialog(
                    "Deletion not possible",
                    "You cannot delete the default collection.",
                    "Ok",
                    (EventHandler<ActionEvent>) _ -> {});

            dialog.show();
            return;
        }

        serverUtils.deleteCollection(selectedCollection);

        collectionsListView.getItems().remove(selectedCollection);
        collectionsListView.refresh();

        titleTextField.setText("");
        serverTextField.setText("");
        nameTextField.setText("");
    }

    @FXML
    private void onSave() {
        Collection selectedCollection = getSelectedCollection();

        System.out.println(selectedCollection);

        // TODO: handle possible error
        // TODO: should update all collections not only the selected one
        serverUtils.updateCollection(selectedCollection);

        // close window...
    }

    private void loadCollectionInfo(Collection collection) {
        titleTextField.setText(collection.title);

        // TODO: implement server
        serverTextField.setText("N/A");

        nameTextField.setText(collection.name);

        // TODO: implement status
        statusLabel.setText("[not implemented]");
    }
}

