# CSEP Template Project

This repository contains the template for the CSE project. Please extend this README.md with sufficient instructions that will illustrate for your TA and the course staff how they can run your project.

To run the template project from the command line, you either need to have [Maven](https://maven.apache.org/install.html) installed on your local system (`mvn`) or you need to use the Maven wrapper (`mvnw`). You can then execute

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
