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
package client.utils;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import java.net.ConnectException;
import java.util.List;

import commons.EmbeddedFile;
import commons.Note;
import commons.NoteTitle;
import jakarta.ws.rs.client.Entity;
import org.glassfish.jersey.client.ClientConfig;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.GenericType;

public class ServerUtils {

	private static final String SERVER = "http://localhost:8080/";

	/**
	 * method for requesting titles in a List of NoteTitles from the server.
	 * GET request on endpoint /api/titles
	 * @return List of NoteTitle object that link the noteID to the title
	 */
	public List<NoteTitle> getNoteTitles() {
		return ClientBuilder.newClient(new ClientConfig()) //
				.target(SERVER).path("api/titles") //
				.request(APPLICATION_JSON) //
				.get(new GenericType<List<NoteTitle>>() {});
	}

	public Note MOCK_getDefaultNote() {
		return ClientBuilder.newClient(new ClientConfig())
				.target(SERVER).path("api/notes/mock")
				.request(APPLICATION_JSON)
				.get(new GenericType<Note>() {});
	}

	public Note updateNote(Note note) {
		return ClientBuilder.newClient(new ClientConfig())
				.target(SERVER).path("api/notes")
				.request(APPLICATION_JSON)
				.put(Entity.entity(note, APPLICATION_JSON), Note.class);
	}

	/**
	 * method for getting a note based on the giving id by which it is stored in the server
	 * GET request on endpoint /api/notes/{id}
	 * @param id Server id of the giving note
	 * @return The note with specified ID, including title and contents
	 */
	public Note getNoteById(long id) {
		return ClientBuilder.newClient(new ClientConfig())
				.target(SERVER).path("api/notes/" + id)
				.request(APPLICATION_JSON)
				.get(new GenericType<Note>() {});
	}

	public boolean isServerAvailable() {
		try {
			ClientBuilder.newClient(new ClientConfig()) //
					.target(SERVER) //
					.request(APPLICATION_JSON) //
					.get();
		} catch (ProcessingException e) {
			if (e.getCause() instanceof ConnectException) {
				return false;
			}
		}
		return true;
	}


	public List<Note> getAllNotes() {
		return ClientBuilder.newClient(new ClientConfig())
				.target(SERVER).path("api/notes")
				.request(APPLICATION_JSON)
				.get(new GenericType<List<Note>>() {});
	}

	/**
	 * Returns a note corresponding to the provided id
	 * @param id the id of a valid id of a note in the database
	 * @return a note which is provided from the database.
	 */
	public Note getNoteById(Long id) {
		return ClientBuilder.newClient(new ClientConfig())
				.target(SERVER).path("api/notes/" + id)
				.request(APPLICATION_JSON)
				.get(new GenericType<Note>() {});
	}

	/**
	 * Returns a boolean based on if the note exists in the noteRepository
	 * @param id the id of a note (in the database or not)
	 * @return a boolean which covers the existence of the note in the database
	 */
	public boolean existsNoteById(long id) {
		return ClientBuilder.newClient(new ClientConfig())
				.target(SERVER).path("api/notes/exists/" + id)
				.request(APPLICATION_JSON)
				.get(new GenericType<Boolean>() {});
	}

	/**
	 * Stores the provided note in the database
	 * @param note a valid note that needs to be stored in the database
	 */
	public void addNote(Note note) {
		ClientBuilder.newClient(new ClientConfig())
				.target(SERVER).path("api/notes")
				.request(APPLICATION_JSON)
				.post(Entity.entity(note, APPLICATION_JSON), Note.class);
	}

	/**
	 * Deletes the provided note through the deleteById()
	 * @param note a valid note that is currently in the database and can be removed
	 */
	public void deleteNote(Note note) {
		ClientBuilder.newClient(new ClientConfig())
				.target(SERVER).path("api/notes/delete/" + note.id)
				.request(APPLICATION_JSON)
				.delete();
	}

	/**
	 * Adds the file to the database
	 * @param file the file that needs to be stored
	 */
	public void addFileToNote(EmbeddedFile file) {
		ClientBuilder.newClient(new ClientConfig())
				.target(SERVER).path("api/notes/" + file.note.id + "/embedded/" + file.id)
				.request(APPLICATION_JSON)
				.post(Entity.entity(file, APPLICATION_JSON), EmbeddedFile.class);
	}

	/**
	 * Deletes the specified file
	 * @param noteId the id of the note the file is located in
	 * @param fileId the id of the file that needs to be removed
	 */
	public void deleteFileToNote(long noteId, long fileId) {
		ClientBuilder.newClient(new ClientConfig())
				.target(SERVER).path("api/notes/" + noteId + "/embedded/" + fileId)
				.request(APPLICATION_JSON)
				.delete();
	}

	/**
	 * Deletes all files from a specified note
	 * @param note The note that needs its files cleared
	 */
	public void deleteAllFilesToNote(Note note) {
		ClientBuilder.newClient(new ClientConfig())
				.target(SERVER).path("api/notes/" + note.id + "/embedded")
				.request(APPLICATION_JSON)
				.delete();
	}

	public List<EmbeddedFile> getAllFilesFromNote(Note note) {
		return ClientBuilder.newClient(new ClientConfig())
				.target(SERVER).path("api/notes/" + note.id + "/embedded")
				.request(APPLICATION_JSON)
				.get(new GenericType<List<EmbeddedFile>>() {});
	}

	public EmbeddedFile getFileFromNote(long noteId, long fileId) {
		return ClientBuilder.newClient(new ClientConfig())
				.target(SERVER).path("api/notes/" + noteId + "/embedded/" + fileId)
				.request(APPLICATION_JSON)
				.get(new GenericType<EmbeddedFile>() {});
	}

	public void editFileTitle(EmbeddedFile file) {
		ClientBuilder.newClient(new ClientConfig())
				.target(SERVER).path("api/notes/" + file.note.id + "/embedded/" + file.id)
				.request(APPLICATION_JSON)
				.put(Entity.entity(file, APPLICATION_JSON), EmbeddedFile.class);
	}
}