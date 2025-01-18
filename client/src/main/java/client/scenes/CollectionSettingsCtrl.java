package client.scenes;

import client.config.Config;
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
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

import java.util.List;
import java.util.UUID;

public class CollectionSettingsCtrl {

    @FXML
    private ListView<Collection> collectionsListView;

    @FXML
    private TextField titleTextField;

    @FXML
    private TextField serverTextField;

    @FXML
    private TextField nameTextField;

    @FXML
    private Label statusLabel;

    @FXML
    private CheckBox isRemoteCheckBox;

    @FXML
    private Button deleteButton;

    @FXML
    private Button makeDefaultButton;

    @FXML
    private Button saveButton;

    // TODO: maybe there is a cleaner approach without: selected/displayed/created collections

    // Flag if the displayed collection was modified or new collection created
    private boolean isCollectionModified = false;
    // The collection that is loaded in the settings (text fields)
    private Collection displayedCollection;

    // The collection that is created by pressing the button
    // This is necessary because if a user creates a collection it doesn't guarantee that they'll save it
    private Collection createdCollection;

    // Set in the dialog when user is prompted to save the changes
    private boolean isSaveCancelled = false;

    // Injectable
    private final ServerUtils serverUtils;
    private final Config config;

    @Inject
    public CollectionSettingsCtrl(ServerUtils serverUtils, Config config) {
        this.serverUtils = serverUtils;
        this.config = config;
    }

    @FXML
    public void initialize() {
        List<Collection> collections = serverUtils.getAllCollections();

        collectionsListView.getItems().addAll(collections);

        collectionsListView.setCellFactory(_ -> createCollectionListViewItem());

        selectDefaultCollection();

        Collection defaultCollection = getSelectedCollection();
        loadCollectionInfo(defaultCollection);
    }

    public boolean hasUnsavedChanges() {
        return isCollectionModified || createdCollection != null;
    }

    public boolean handleUnsavedChanges() {
        showConfirmSaveDialog(displayedCollection.title, this::saveModifiedChanges);

        return isSaveCancelled;
    }

    private Collection getSelectedCollection() {
        return collectionsListView.getSelectionModel().getSelectedItem();
    }

    private void selectDefaultCollection() {
        UUID defaultCollectionId = config.getDefaultCollectionId();

        for (Collection collection : collectionsListView.getItems()) {
            if (collection.id == defaultCollectionId) {
                collectionsListView.getSelectionModel().select(collection);
                return;
            }
        }

        // This is a safety fallback if some reason default collection is not found
        collectionsListView.getSelectionModel().select(0);
    }

    private ListCell<Collection> createCollectionListViewItem() {
        return new ListCell<>() {
            @Override
            protected void updateItem(Collection item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setGraphic(new Label(item.title));
                }
            }
        };
    }

    @FXML
    private void onListViewClick() {
        Collection selectedCollection = getSelectedCollection();

        if (selectedCollection == null) return;

        if (hasUnsavedChanges() && selectedCollection != displayedCollection) {
            handleUnsavedChanges();
        }

        loadCollectionInfo(selectedCollection);
    }

    @FXML
    private void onTitleChange() {
        Collection selectedCollection = getSelectedCollection();
        selectedCollection.title = titleTextField.getText();

        isCollectionModified = true;
        saveButton.setDisable(false);

        // Reflect the change in the list view
        collectionsListView.refresh();
    }

    @FXML
    private void onServerChange() {
        isCollectionModified = true;
        saveButton.setDisable(false);

        // TODO: should be reflected in the server status
    }

    @FXML
    private void onNameChange() {
        Collection selectedCollection = getSelectedCollection();
        selectedCollection.name = nameTextField.getText();

        isCollectionModified = true;
        saveButton.setDisable(false);

        // TODO: server status should be updated accordingly
    }

    @FXML
    private void onCreate() {
        if (hasUnsavedChanges()) {
            boolean isCancelled = handleUnsavedChanges();
            if (isCancelled) return;
        }

        String collectionName = serverUtils.getUniqueCollectionName();
        Collection newCollection = new Collection(
                collectionName,
                "New Collection"
        );

        collectionsListView.getItems().add(newCollection);
        collectionsListView.refresh();

        isCollectionModified = true;
        saveButton.setDisable(false);

        createdCollection = newCollection;
        collectionsListView.getSelectionModel().select(createdCollection);
        loadCollectionInfo(createdCollection);
    }

    @FXML
    private void onDelete() {
        if (displayedCollection == null) {
            // Since default collection must always exist (cannot be deleted)
            // this edge case should never happen, just an extra safety measure
            return;
        }

        if (displayedCollection.id.equals(config.getDefaultCollectionId())) {
            showDeletionNotPossibleDialog();
            return;
        }

        showConfirmDeletionDialog(displayedCollection.title, this::deleteSelectedCollection);
    }

    @FXML
    private void onMakeDefault() {
        // TODO: implement
    }

    @FXML
    private void onSave() {
        saveModifiedChanges();
    }

    private void loadCollectionInfo(Collection collection) {
        // This is the only place where displayed collection can be changed
        // because it is exactly where it is loaded for the user
        displayedCollection = collection;

        // TODO: implement server/status
        titleTextField.setText(collection.title);
        serverTextField.setText("[not implemented]");
        nameTextField.setText(collection.name);
        statusLabel.setText("[not implemented]");
    }

    private void deleteSelectedCollection() {
        Collection selectedCollection = getSelectedCollection();

        // If deleting an existing collection then it must be removed from the server
        if (createdCollection == null) {
            serverUtils.deleteCollection(selectedCollection);
        } else {
            collectionsListView.getItems().remove(createdCollection);

            isCollectionModified = false;
            createdCollection = null;

            saveButton.setDisable(true);
        }

        collectionsListView.getItems().remove(selectedCollection);
        collectionsListView.refresh();

        selectDefaultCollection();
    }

    private void saveModifiedChanges() {
        if (!isCollectionModified) return;

        try {
            if (createdCollection != null) {
                Collection savedCollection = serverUtils.addCollection(createdCollection);

                // Server assigns new ID to the collection,
                // so we need to replace local reference to avoid inconsistency
                int idx = collectionsListView.getItems().indexOf(createdCollection);
                collectionsListView.getItems().set(idx, savedCollection);

                createdCollection = null;
            } else {
                // Selected collection would not work here because it is different to the one we want to save
                serverUtils.updateCollection(displayedCollection);
            }

            isCollectionModified = false;
            saveButton.setDisable(true);
        } catch(Exception e) {
            System.out.println("Server had an error while saving the collection.");
        }
    }

    // >>>> Dialogs >>>>

    private void showDeletionNotPossibleDialog() {
        var dialog = DialogBoxUtils.createSimpleDialog(
                "Deletion not possible",
                "You cannot delete the default collection.",
                "Ok",
                (EventHandler<ActionEvent>) _ -> {});

        dialog.showAndWait();
    }

    private void showConfirmDeletionDialog(String collectionName, Runnable action) {
        var dialog = DialogBoxUtils.createYesNoDialog(
                "Delete collection?",
                "Do you want to delete collection \"" + collectionName + "\"? " +
                "All notes inside the collection will be removed forever.",
                (confirmed) -> {
                    if (confirmed) action.run();
                });

        dialog.showAndWait();
    }


    private void showConfirmSaveDialog(String collectionName, Runnable yes) {
        var dialog = DialogBoxUtils.createYesNoDialog(
                "Save changes to the collection?",
                "Collection \"" + collectionName + "\" has been modified. Do you want to save changes?",
                (confirmed) -> {
                    isSaveCancelled = !confirmed;
                    if (confirmed) yes.run();
                }
        );

        dialog.showAndWait();
    }
}

