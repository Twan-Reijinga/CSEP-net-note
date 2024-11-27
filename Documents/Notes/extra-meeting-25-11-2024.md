# Notes from 25/11/2024 (by Oleh)

## Present
- Oleh
- Tushit
- Twan
- Petar
- Pepijn

## Takeaway

We got the consensus on how the application is supposed to work:
there is no authentication system;  there can be multiple servers,
each hosting multiple collections, every collection can contain multiple notes.

We did not agree on how the client application must handle creating/editing notes 
in different collections on different servers. 
The topic is left open for future discussion. 

We agreed that we'll work through the midterm week (week 5) 
as it is required in knock-out criteria to make contributions 
to the code every week starting from week 2 (that is incl. week5). 

We estimate that it will take us 3 weeks to complete all basic 
requirements for the application that is for a passing grade. 

After the midterms we'll start working on extra features. 
Since this is only an estimation timeline can be changed 
according to actual needs. 

We start working on the codebase from week 2 focusing primarily on
one-server one-collection design. 

We ignore any mentions of the remote server in the app (front-end or back-end) *for now*.
We focus on a local server only. 

We'll be splitting the work on front-end tasks and back-end tasks. 

Mocking (of back-end) will be used to make front-end more or less 
usable and testable without the actual back-end.

We generally discussed how the database will be structured.

Database design consisting of conceptual and logical ERD must be
designed, documented and updated throughout the project to ensure that
everyone knows the design of the database.

Database design must be translated into JPA models as soon as possible.

Markdown feature is complicated therefore must be implemented gradually 
throughout a few weeks. 

## Front-end Layout

Front-end layout consists of: 
- Active note panel
- Sidebar (with filtered notes)
- Search & other controls (at the top)
- Pop-ups


## Features/Tasks for Week 2

1. Oleh. Back-end. JPA: 
   - ERD: conceptual and logical database
   - Create all necessary models in JPA
2. Twan. Front-end. Sidebar:
   - create a mock endpoint for retrieving notes
   - used that mock endpoint to fetch notes to be displayed
   - generate a list of available notes in the sidebar
   - impl. "refresh" button
3. Petar. Back-end. Back-end endpoints: 
   - endpoint to get a note from the server by ID (incl. active note)
   - endpoint to get a set of notes (ID + Title) for the sidebar
4. Tushit. Front-end. Display an active note:
   - create a mock default (initial) note
   - load this note on app start-up
   - display the mocked note (its title and text - *NOT* markdown) in a panel
5. Pepijn. Back-end. Add/Delete note endpoints:
   - create an endpoint to add a note
   - create an endpoint to delete a note
   - possibly? an endpoint to edit a note 
6. Liviu. Front-end. Markdown: 
   - create a hardcoded note in *markdown*
   - use provided library to convert it into HTML
   - use WebView to display it in JavaFX
   - **NOTE**: should be done separately to the app UI in an new window 
   to avoid conflicts with the other UI.

