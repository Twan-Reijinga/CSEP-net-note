# Backlog Checklist

## TO BE COMPLETED

- [ ] Update: To be prevented from using a duplicate note title, so note content is clear.
	*(Note titles should be unique per collection, like a filename has to be unique in a folder.)*

- [ ] To refer to embedded images in my Markdown, so can see images in my preview.
	*(The links to embedded files need to be processed in the generated HTML to point to the right server location)*

- [ ] Should be implemented via ✮JavaFX Internationalization. This involves creating localized property files in the
resources folder and calling ✮FXMLoader.setResources.

## CLARIFY 

- [?] The WebView should use a local .css file, if available, that allows to adjust the format of the rendered page
- [?] To use a user-friendly name for my collections, so I can use short and descriptive names.
- [?] To use a user-friendly name for my collections, so I can use short and descriptive names.
- [?] To see the server status while I create/edit a collection, so I can spot typos.
	*(At least the following states are supported: server not reachable, collection will be created, collection already exists.)*
- [?] The configured collections are stored in a local config file, so they are persisted across restarts.

- [?] To delete all embedded files when I delete notes, so the server does not accumulate irrelevant files.

## BE AWARE 

- [!] The client only received relevant updates, i.e., no changes for unconnected collections or no content updates for
notes that are not currently being viewed.

- [!] All labels in the application should match the selected language. It is not required to translate notes.

## 4.1 Basic Requirements

As a user, I want ...

- [x] To host my notes on a server, so I can use my notes from multiple clients.
- [x] To see all existing notes on the server, so I can browser the available information.
- [x] To create new notes, so I can persist information for later use.
- [x] To add titles to note, so I can organize my information.
- [x] To change note titles, so I can keep title and content in sync.
- [x] To delete notes, so I can remove unneeded information.
- [x] To sync every change in my the notes automatically with the server, so I do not need to manually save.
- [x] To write note contents as free text, so I am not hindered by any structural requirements.
- [x] To manually refresh my client view, so I see new information on the server.
- [x] To search for keywords, so I can find notes with matching titles or content.
- [x] To be able to use Markdown formatting in my notes, so I can write structured notes.
- [x] To see rendered version of the Markdown note, so I can see a formatted version of my note.
- [x] To see an automatic update of the rendered view, so I see my changes reflected in real time.
- [x] Update: To see a short summary of any problem during the Markdown rendering, so I can fix the problem.
	*(Removed as a requirement. We meant to catch Exceptions in the processing, which could be caused by the Markdown library
or also by your own code. In case of an error, the ‘WebView‘ should have shown the error message of the Exception .)*
- [ ] Update: To be prevented from using a duplicate note title, so note content is clear.
	*(Note titles should be unique per collection, like a filename has to be unique in a folder.)*

### Non-Functional Requirements

- [x] The basic version of the application only need to work with a single collection, which can be hard-coded in a
configuration file that is read during application start-up.
- [x] The client should fully support ✮basic Markdown syntax. Use ✮commonmark-java or ✮flexmark-java to parse
Markdown and generate a (temporary) HTML file.
- [x] The visualization in the client should be achieved via an JavaFX WebView pane
- [?] The WebView should use a local .css file, if available, that allows to adjust the format of the rendered page

## 4.2 Multi-Collection

As a user, I want ...
- [x] To distribute my notes across several collections, so I can organize my notes better.
- [x] To set a collections filter, so I only see notes that exist in that collection.
- [x] To select all collections at once, so I see all notes that I have access to.
- [x] To edit the list of collections (add/delete/change), so I can control which note collections I see in my client.
- [?] To use a user-friendly name for my collections, so I can use short and descriptive names.
- [?] To see the server status while I create/edit a collection, so I can spot typos.
	*(At least the following states are supported: server not reachable, collection will be created, collection already exists.)*
- [x] To define a default collection, so new notes will always be added to this collection.
- [x] To create new notes in a specific collection, when it is the only one shown, so I do not have to move notes.
- [x] To change the collection for a note, so I can move notes from one collection to another.

### Non-Functional Requirements
- [x] The configured collections are stored in a local config file, so they are persisted across restarts.

### Additional Information
- [x] Do not write/parse the structure of the local config file yourself.
- [x] Create a Config data structure and use the Jackson mapper to convert the object to/from a JSON string.
- [x] Look into the FileUtils of the ✮Apache Commons IO library to understand how to read/write string to a File .

## 4.3 Embedded Files

As a user, I want ...

- [?] To embed files into notes (e.g., images), so I can add relevant media to my descriptions.
- [?] To rename file names, so I can give them more descriptive names without re-upload.
- [?] To delete files, so I can remove irrelevant information.
- [?] To delete all embedded files when I delete notes, so the server does not accumulate irrelevant files.
- [ ] To refer to embedded images in my Markdown, so can see images in my preview.
	*(The links to embedded files need to be processed in the generated HTML to point to the right server location)*
- [x] To download files when I click on them, so I can use the files locally on my computer.

### Non-Functional Requirements

- [x] In principle, the client does not need to store any local data.
- [x] The notes should be requested on demand and only stored in memory.
- [x] Embedded files are available on dedicated URLs on the server that are connected to the note (e.g., a URL
like http://server/netnote/files/notes/My%20Note/foo.jpg represents a file foo.jpg in a note My Note of
collection notes . URL encoding is used for special characters.
- [x] Clients should only receive a simplified abstraction of the meta data, like file name, link, or file type.

## 4.4 Interconnected Content

As a user, I want ...

- [x] To use tags in the form of #foo in my notes, so I can organize information and make it easier to find.
	*(Make sure to identify/process/replace the tags first, or the Markdown parser will interpret them as titles)*
- [x] To use [[other note]] to refer to other notes in the same collection by title, so I can link related notes.
- [x] To automatically replace all [[..]] references when I rename a note, so the references stay consistent.
- [x] To have these tags/references be converted to links in the render view, so I can easily click on them.
- [x] To click on a tag, so I can filter all notes to those who have the tag.
- [x] To click on the link to another note, so it is easy to switch to the other note.
- [x] To see a visual difference in the preview if a referenced note does not exist, so I can spot typos.
- [x] To select multiple tags, so I can filter notes with multiple criteria.
- [x] To clear all selected tags, so I can easily go back to seeing all notes.

### Non-Functional Requirements

- [x] It should be possible to filter for an unlimited amount of tags, i.e., not just a fixed number of tag filters.
- [x] Adding subsequent tags should only list tags in the dropdown that are actually available in the remaining notes

## 4.5 Automated Change Synchronization

As a user, I want ...
- [?] To see that some changes (see later) are auto-propagated across all clients, so I do not have to manually refresh.
- [?] To see a change of a title to be propagated, so no manual refresh is required.
- [?] To see the addition/deletion of notes to be propagated, so no manual refresh is required.
- [?] To see any change in note content to be propagated when I view the same note, so no manual refresh is required.
- [?] To see any change in note content also to be propagated to the preview, so text and preview are consistent.

### Non-Functional Requirements

- [x] The client uses web sockets to subscribe for changes.
- [x] The client does not poll for changes, changes are pushed from the server.
- [x] The client only received relevant updates, i.e., no changes for unconnected collections or no content updates for
notes that are not currently being viewed.

### Additional Information
- [!] Synchronizing and handling conflicts in simultaneous edits is really difficult.
- [!] Your application should not crash when multiple users are simultaneously editing, but making this scenario
collision-safe and performant is out of scope for the CSE project. Our strong recommendation: do not even try
to do more than a basic implementation, we also will not consider any optimization in the assessment.


## 4.6 Live Language Switch

Add support for switching the language of your client during runtime. The language selection should be persisted in
the config file.

As a user, I want ...

- [x] To see a language indicator, so I know which language is currently configured in my client on a first glance
- [x] To see a flag icon as the language indicator, so I do not have to read additional text.
- [x] To see all available languages through clicking the indicator, so I can find my preferred one easily
- [x] To persist my language choice through restarts, so I do not have to pick a language each time

### Non-Functional Requirements

- [ ] Should be implemented via ✮JavaFX Internationalization. This involves creating localized property files in the
resources folder and calling ✮FXMLoader.setResources.
- [x] The application must fully support at least English and Dutch. A third language must be added as a proof of
concept, but can be made up.
- [x] Please note that languages that do not flow left-to-right are much harder to integrate. While we encourage you
to think about this use case, it is enough to limit your localization support to left-to-right languages.

### Additional Information

- [!] All labels in the application should match the selected language. It is not required to translate notes.
