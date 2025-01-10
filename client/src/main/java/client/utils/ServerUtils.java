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
import java.util.UUID;

import client.config.Config;
import com.google.inject.Inject;
import commons.Note;
import commons.NoteTags;
import commons.NoteTitle;
import jakarta.ws.rs.client.Entity;
import org.glassfish.jersey.client.ClientConfig;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.GenericType;

import commons.Collection;

public class ServerUtils {
	private final Config config;
	private final String server;

	@Inject
	public ServerUtils(Config config) {
		this.config = config;
		this.server = config.getLocalServer();
	}

	private ServerUtils(Config config, String server) {
		this.config = config;
		this.server = server;
	}

	public ServerUtils withServer(String server) {
		return new ServerUtils(config, server);
	}

	// TODO: implement withTimeout

	public List<Collection> getAllCollections() {
		return ClientBuilder.newClient(new ClientConfig())
				.target(server).path("api/collections")
				.request(APPLICATION_JSON)
				.get(new GenericType<>() {});
	}

	public Collection getDefaultCollection() {
		return ClientBuilder.newClient(new ClientConfig())
				.target(server).path("api/collections/default")
				.request(APPLICATION_JSON)
				.get(new GenericType<>() {});
	}

	public Collection addCollection(Collection collection) {
		return ClientBuilder.newClient(new ClientConfig())
				.target(server).path("api/collections")
				.request(APPLICATION_JSON)
				.post(Entity.entity(collection, APPLICATION_JSON), Collection.class);
	}

	public Collection updateCollection(Collection collection) {
		return ClientBuilder.newClient(new ClientConfig())
				.target(server).path("api/collections")
				.request(APPLICATION_JSON)
				.put(Entity.entity(collection, APPLICATION_JSON), Collection.class);
	}

	public String getUniqueCollectionName() {
		return ClientBuilder.newClient(new ClientConfig())
				.target(server).path("api/collections/unique-name")
				.request().get(String.class);
	}

	public void deleteCollection(Collection collection) {
		ClientBuilder.newClient(new ClientConfig())
				.target(server).path("api/collections/delete/" + collection.id)
				.request(APPLICATION_JSON)
				.delete();
	}

	/**
	 * method for requesting titles in a List of NoteTitles from the server.
	 * GET request on endpoint /api/titles
	 * @return List of NoteTitle object that link the noteID to the title
	 */
	public List<NoteTitle> getNoteTitles() {
		return ClientBuilder.newClient(new ClientConfig()) //
				.target(server).path("api/titles") //
				.request(APPLICATION_JSON) //
				.get(new GenericType<>() {});
	}


	public List<NoteTitle> getNoteTitlesInCollection(UUID collectionId) {
		return ClientBuilder.newClient(new ClientConfig())
				.target(server).path("api/titles")
				.queryParam("collectionId", collectionId)
				.request(APPLICATION_JSON)
				.get(new GenericType<>() {});
	}

	public Note mockGetDefaultNote() {
		return ClientBuilder.newClient(new ClientConfig())
				.target(server).path("api/notes/mock")
				.request(APPLICATION_JSON)
				.get(new GenericType<>() {});
	}

	public Note updateNote(Note note) {
		return ClientBuilder.newClient(new ClientConfig())
				.target(server).path("api/notes")
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
				.target(server).path("api/notes/" + id)
				.request(APPLICATION_JSON)
				.get(new GenericType<>() {});
	}

	public boolean isServerAvailable() {
		try {
			ClientBuilder.newClient(new ClientConfig()) //
					.target(server) //
					.request(APPLICATION_JSON) //
					.get();
		} catch (ProcessingException e) {
			if (e.getCause() instanceof ConnectException) {
				return false;
			}
		}
		return true;
	}

	public Collection getCollectionInfo(String name) {
		return ClientBuilder.newClient(new ClientConfig())
				.target(server).path("/" +  name)
				.request(APPLICATION_JSON)
				.get(new GenericType<>() {});
	}

	public List<Note> getAllNotes() {
		return ClientBuilder.newClient(new ClientConfig())
				.target(server).path("api/notes")
				.request(APPLICATION_JSON)
				.get(new GenericType<>() {});
	}

	/**
	 * Returns a note corresponding to the provided id
	 * @param id the id of a valid id of a note in the database
	 * @return a note which is provided from the database.
	 */
	public Note getNoteById(Long id) {
		return ClientBuilder.newClient(new ClientConfig())
				.target(server).path("api/notes/" + id)
				.request(APPLICATION_JSON)
				.get(new GenericType<>() {});
	}

	/**
	 * Returns a boolean based on if the note exists in the noteRepository
	 * @param id the id of a note (in the database or not)
	 * @return a boolean which covers the existence of the note in the database
	 */
	public boolean existsNoteById(long id) {
		return ClientBuilder.newClient(new ClientConfig())
				.target(server).path("api/notes/exists/" + id)
				.request(APPLICATION_JSON)
				.get(new GenericType<Boolean>() {});
	}

	/**
	 * Stores the provided note in the database
	 * @param note a valid note that needs to be stored in the database
	 */
	public void addNote(Note note) {
		ClientBuilder.newClient(new ClientConfig())
				.target(server).path("api/notes")
				.request(APPLICATION_JSON)
				.post(Entity.entity(note, APPLICATION_JSON), Note.class);
	}

	/**
	 * Deletes the provided note through the deleteById()
	 * @param note a valid note that is currently in the database and can be removed
	 */
	public void deleteNote(Note note) {
		ClientBuilder.newClient(new ClientConfig())
				.target(server).path("api/notes/delete/" + note.id)
				.request(APPLICATION_JSON)
				.delete();
	}

	public Collection getCollectionById(UUID collectionId) {
		return ClientBuilder.newClient(new ClientConfig())
				.target(server).path("api/collections/" + collectionId)
				.request(APPLICATION_JSON)
				.get(new GenericType<>() {});
	}

	/** Sends a GET request to the server with the provided parameters.
	 * @param collectionId id of the collection where to search
	 * @param text search query
	 * @param matchAll if search should match all
	 * @param whereToSearch search in titles/contents/both
	 * @return List of NoteTitle objects that is used to fill in the sidebar.
	 */
	public List<NoteTitle> searchNotesInCollection(
			UUID collectionId,
			String text,
			boolean matchAll,
			String whereToSearch
	){
		String requestPath = "api/search";

		// TODO: SEARCH FILTERED ON COLLECTION NOT IMPLEMENTED

		return  ClientBuilder.newClient(new ClientConfig())
				.target(server).path(requestPath  +
										"/" + text +
										"/" + matchAll +
										"/" + whereToSearch)
				.request(APPLICATION_JSON)
				.get(new GenericType<>() {});
	}

	/**
	 * Returns a list of NoteTags each containing an id of the note it represents and all tags in that note.
	 * @param collectionId the id of the collection whose notes are used
	 * @return a list of NoteTags
	 */
	public List<NoteTags> getAllNoteTags(UUID collectionId) {
		return ClientBuilder.newClient(new ClientConfig())
				.target(server).path("api/tags/")
				.request(APPLICATION_JSON)
				.post(Entity.entity(collectionId, APPLICATION_JSON), new GenericType<>(){});
	}

	/**
	 * Used to load the tags of notes that are rendered into the sidebar, when they come from multiple collections.
	 * @param noteIds the id's of all notes whose tags are requested
	 * @return a list of NoteTags for each id of a note in the list.
	 */
	public List<NoteTags> getNoteTags(List<Long> noteIds) {
		return ClientBuilder.newClient(new ClientConfig())
				.target(server).path("api/tags/list")
				.request(APPLICATION_JSON)
				.post(Entity.entity(noteIds, APPLICATION_JSON), new GenericType<>(){});
	}
}
