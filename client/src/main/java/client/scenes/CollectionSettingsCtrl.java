/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

import java.util.List;
import java.util.UUID;

public class CollectionSettingsCtrl {

    @FXML
    private ListView<Collection> collectionsListView;

    @FXML
    private TextField titleTextField;

    @FXML
    private TextField nameTextField;

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


    public boolean hasUnsavedChanges() {
        return isCollectionModified || createdCollection != null;
    }

    public void handleUnsavedChanges() {
        showConfirmSaveDialog(displayedCollection.title, this::saveModifiedChanges, this::discardModifiedChanges);
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
            handleUnsavedChanges();
        }

        String collectionName = serverUtils.getUniqueCollectionName();
        Collection newCollection = new Collection(
                collectionName,
                createCollectionTitle()
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
        // If user makes a newly create collection the default one, and it is not saved
        // it causes unexpected behaviour down the road
        if (createdCollection != null) {
            saveModifiedChanges();
        }

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
        nameTextField.setText(collection.name);

        boolean isDefault = collection.id.equals(config.getDefaultCollectionId());
        makeDefaultButton.setDisable(isDefault);
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

                displayedCollection = savedCollection;
                createdCollection = null;
            } else {
                // Selected collection would not work here because it is different to the one we want to save
                serverUtils.updateCollection(displayedCollection);
            }

            isCollectionModified = false;
            saveButton.setDisable(true);
        } catch(Exception e) {
            // TODO: should be display to the user (in sidebar)
            System.out.println("Server had an error while saving the collection.");
        }
    }

    private void discardModifiedChanges() {
        if (createdCollection == null) {
            Collection originalCollection = serverUtils.getCollectionById(displayedCollection.id);

            // Reset all attributes to the original ones
            displayedCollection.title = originalCollection.title;
            displayedCollection.name = originalCollection.name;
        } else {
            collectionsListView.getItems().remove(createdCollection);
            createdCollection = null;
        }

        isCollectionModified = false;
        collectionsListView.refresh();
    }

    private String createCollectionTitle() {
        List<Collection> collections = collectionsListView.getItems();

        String prefix = "New collection #";
        // FIXME: duplicate code in sidebar>create default note title
        int maxNoteNumber = collections.stream()
                .filter(collection -> collection.title.startsWith(prefix))
                .mapToInt(collection -> {
                    try {
                        return Integer.parseInt(collection.title.replace(prefix, ""));
                    } catch (Exception e) {
                        return -1;
                    }
                })
                .max()
                .orElse(0);

        return prefix + (maxNoteNumber + 1);
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
                "Collection \"" + collectionName + "\" has been modified. Do you want to save changes? \n" +
                        "Otherwise the changes will be lost.",
                (confirmed) -> {
                    if (confirmed) yes.run();
                    else no.run();
                }
        );

        dialog.showAndWait();
    }
}

