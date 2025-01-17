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

    private Collection getSelectedCollection() {
        return collectionsListView.getSelectionModel().getSelectedItem();
    }

    private void selectDefaultCollection() {
        UUID defaultCollectionId = config.getDefaultCollectionId();

        for (Collection collection : collectionsListView.getItems()) {
            if (collection.id.equals(defaultCollectionId)) {
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
                    boolean isDefault = item.id.equals(config.getDefaultCollectionId());
                    setGraphic(new Label(item.title + (isDefault ? " (Default)" : "")));
                }
            }
        };
    }

    @FXML
    private void onListViewClick() {
        Collection selectedCollection = getSelectedCollection();

        if (selectedCollection == null) return;

        if (isCollectionModified && selectedCollection != displayedCollection) {
            showConfirmSaveDialog(
                    displayedCollection.title,
                    this::saveModifiedChanges,
                    this::clearModifiedChanges
            );
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
        config.setDefaultCollectionId(displayedCollection.id);

        // To ensure that the display collection is labeled as default
        collectionsListView.refresh();
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

        boolean isDefault = collection.id.equals(config.getDefaultCollectionId());
        makeDefaultButton.setDisable(isDefault);
    }

    private void clearModifiedChanges() {
        if (createdCollection != null) {
            collectionsListView.getItems().remove(createdCollection);
        }

        try {
            // FIXME: may be not the cleanest solution to fetch it from the server
            //  rather a hacky fix than a final solution (consider preserving server field)
            //  maybe drop the idea of restoring collection/discarding changes
            Collection originalCollection = serverUtils.getCollectionById(displayedCollection.id);

            // It won't work to reassign the object itself because it will be different to the one in list view
            displayedCollection.name = originalCollection.name;
            displayedCollection.title = originalCollection.title;

            collectionsListView.refresh();
        } catch (Exception e) {
            // ignored; it may fail because collection was deleted
            // WILL CHANGE in subsequent MRs
        }

        isCollectionModified = false;
        createdCollection = null;

        saveButton.setDisable(true);
    }

    private void deleteSelectedCollection() {
        Collection selectedCollection = getSelectedCollection();

        // If deleting an existing collection then it must be removed from the server
        if (createdCollection == null) {
            serverUtils.deleteCollection(selectedCollection);
        }

        collectionsListView.getItems().remove(selectedCollection);
        collectionsListView.refresh();

        clearModifiedChanges();
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

    private void showConfirmSaveDialog(String collectionName, Runnable yes, Runnable no) {
        var dialog = DialogBoxUtils.createYesNoDialog(
                "Save changes to the collection?",
                "Collection \"" + collectionName + "\" has been modified. Do you want to save changes?",
                (confirmed) -> {
                    if (confirmed) yes.run();
                    else no.run();
                }
        );

        dialog.showAndWait();
    }
}

