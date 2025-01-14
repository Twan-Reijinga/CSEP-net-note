| Key          | Value                         |
| ------------ |-------------------------------|
| Date :       | 15/01/2024                    |
| Time :       | 13:45                         |
| Location :   | Drebbelweg Instr. R1          |
| Chair        | Petar                         |
| Minute Taker | Liviu                         |
| Attendees :  |                               |

# Agenda Items

## Opening by Chair (1 min)
- Welcome everyone!

## Check-in: How is everyone doing? (3 min)

## Agenda Announcement (3 min)

- Did everyone read the agenda for today?
- Announce the topics of this agenda
- Does anyone want to add anything to the agenda?

## Approval of Minutes from Previous Meeting (2 min)

- Did everyone read the minutes from the previous week?
- Is there anything that needs to be said about it?

## Showcase Progress to TA 

- Demonstrate progress on the main branch (5 min)
- Briefly explain work (5 min)

## Quick Recap: Monday Meeting (2 min)

#### Distribution of tasks
1. Oleh: Finish multi-collection
2. Pepijn: Finish embeded files
3. Petar: Finish note links
4. Liviu: Finish client sync and start making MR's
5. Twan: Start implementing tests for uncovered code


- Additional discussion topics, later in the meeting

---

## Discussion Topics

### 1. Feedback for Implemented Features (3 min)
- Don't forget to implement the last basic requirement.
- There is a bug when creating new notes - this should be fixed.

### 2. Feedback for Usability/Accessibility (8 min)
- Discuss items we should focus on:

- Accessibility:
    - Not all required shortcuts are implemented.
    - 3+ elements need multi-modal visualization (e.g. search options, add/remove note buttons, collections)
- Navigation:
    - Keyboard Navigation should be implemented 
    - Undo action should go back more than one step for the currently selected note.
- User feedback:
    - Error messages (Probably not enough time for this)
    - Informative feedback 
    - Confirmation for key actions


### 3. Refactoring the backend (3 min)

- Moving logic into services
- Controllers only handle the HTTP requests.

### 4. Change synchronization and WebSockets (2 min)

- Should all data transfer rely solely on WebSockets 
- Inform the TA of the agreements made during the Monday meeting

### 5. Selecting the server for multi-collection (2 min)
- Now that the TA answered on Mattermost, which option will be implemented:
    - a single server, selected on startup
    - many servers, can be changed during runtime

### 6. The use of AI and other external tools (4 min)
- Ask the TA how the use of AI should be documented
- Ask whether extra steps should be taken when using external libraries (FREJ)

### 7. Minute taker for next week? (1 min)
- Who should take on that role, now that Tushit is gone

## Quick Remarks (3 min)
- frontend needs refactoring
- connect MR to issues
- close old issues
- don't forget to add time tracking to issues
- be respectful when interacting with the team
- try to show up in person for the meetings that are left

## Questions & Extra Topics (if any) (5 min)

## Announcements by the TA (if any) (5 min)

## Meeting Closure (2 min)

- Thank everyone for participating!



## Minutes
--- 
