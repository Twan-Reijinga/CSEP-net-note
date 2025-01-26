# CSE Project Team 04 - NetNote

This repository contains our note taking application for the CSE project 2024-2025.

To run the project from the command line, you either need to have [Maven](https://maven.apache.org/install.html) installed on your local system (`mvn`) or you need to use the Maven wrapper (`mvnw`). You can then execute

	mvn clean install

to package and install the artifacts for the three subprojects. Afterwards, you can run ...

	cd server
	mvn spring-boot:run

to start the server or ...

	cd client
	mvn javafx:run

to run the client. Please note that the server needs to be running, before you can start the client.

Once this is working, you can try importing the project into your favorite IDE.

# Keyboard Shortcuts

| Shortcut | Action |
|----------|--------|
| `ESC` | Focus search bar |
| `Ctrl + Z` | Undo note addition/deletion |
| `Ctrl + ↓` | Select next note in dropdown |
| `Ctrl + ↑` | Select previous note in dropdown |
| `Ctrl + Tab` | Cycle forward through collections |
| `Ctrl + Shift + Tab` | Cycle backward through collections |
| `Ctrl + N` | Create new note |
| `Ctrl + Del` | Delete selected note |
| `Ctrl + R` | Manually refresh sidebar contents from server |
| `Ctrl + L` | Focus on the active note title |

# Implememented extensions
- [x] Multi-collections (100%)
- [x] Embedded files (100%)
- [x] Interconnected content (100%)
- [x] Automated change synchronization (100%)
- [x] Live Language Switch (100%)

# Additional implementations
- As an extra that was not listed in the rubriks, we have stack based undo actions for note deletion and addition.
- Implemented a fuzzy search algoritm for the note search functionality.

# Participated team members
- Liviu
- Oleh
- Pepijn
- Petar
- Twan
- (Tushit)