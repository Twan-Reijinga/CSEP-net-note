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

import commons.Note;
import commons.NoteTitle;
import jakarta.ws.rs.client.Entity;
import org.glassfish.jersey.client.ClientConfig;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.GenericType;

import commons.Note;

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
}