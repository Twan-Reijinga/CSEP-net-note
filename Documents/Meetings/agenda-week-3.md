| Key          | Value                         |
| ------------ |-------------------------------|
| Date :       | 04/12/2024                    |
| Time :       | 13:45                         |
| Location :   | Drebbelweg-Instruction Room 1 |
| Chair        | Oleh                          |
| Minute Taker | Pepijn                        |
| Attendees :  | Everyone                      |

# Agenda Items

## Opening by Chair (1min)

- Welcome everyone!

## Check-in: How is everyone doing? (2min)

## Agenda Announcement (3min)

- Did everyone read the detailed agenda for today?
- Announce the topics of this agenda
- Does anyone want to add anything to the agenda?

## Approval of Minutes from Previous Meeting (3min)

- Did everyone read the minutes from previous agenda?
- Is there anything that needs to be said about it?

## Elaboration: Feedback on Previous Agenda (3min)

- Ensure that minute taker will have enough time to write down necessary information
- Ensure that the conclusion is clear for every (actionable) agenda item
- Ensure that everyone has a say in discussion by explicitly asking if everyone agrees on a given topic

## Showcase Progress to TA (5min)

- Present application prototype to the TA

## Quick Recap: Meeting on Monday

- Sprint Review (what has been done?) (4min)
- Sprint Retrospective (what was commited and can be improved?) (4min)
- Sprint Planning (what will be done?): Make sure that everyone opened their issues (10min)

## Discussion Topics

### 1. How and when specifically we update our issues on Friday? (7min)

- Liviu raised a proposal that everyone updates the rest of the team about their progress on a given issue
- We agreed that these updates should be delivered through the GitLab Issues
- We only vaguely specified what we want to mention in these updates and more importantly when (by what time) they should be delivered

### 2. Leaving out the implementation details when assigning tasks (8min)

- Goal: Reduce time for planning the tasks and providing more room for creative solutions
- Reason: Implementation details are bounded to individual skills and time availability of a person implementing the feature
- Note: Dependencies between tasks MUST be discussed to avoid conflicts and code duplication

## Quick Remarks (3min)

- Establish how we assign reviewers and who merges the MRs: the person who wrote the code of the one who reviewed
- Discuss epics and milestones (pages on GitLab) and decide if we want to use them

## Discussing feedback on Code of Conduct (5min)

## Questions & Extra Topics (if any) (5min)

## Announcements by the TA (if any) (5min)

## Meeting Closure (2min)

- Thank everyone for participation!

# Minutes

### Roles:

 - Chair: Oleh
 - Minute taker: Pepijn

### Present:

 - Liviu
 - Pepijn
 - Petar
 - Oleh
 - Tushit
 - Twan

## Tasks:

- Next Chair: Pepijn prepares the next meetings (Monday, Wednesday) and reserves a space for Monday meeting.

- Everyone: should make issues for their implementation on gitlab (if they haven't done so).

- Twan and Tushit have to discuss what to do with the sidebar to embed it into the main UI or keep it separate.

- Liviu and Pepijn should discuss if pop-ups should be integrated with deleting notes this sprint.

- Liviu should implement a pop-up feature for key actions by the user (e.g. deleting notes).

- Pepijn: should implement the client side of adding and deleting notes, through buttons.

- Petar: should implement the functionality of the searchbar, being able to search on words in a collection.

- Oleh: should implement the synchronization of the current note with the content in the database.

- Twan: should implement displaying the note in both edit- and markdown panel, being able to change the title and highlight the current selected note in the sidebar.

- Tushit: should combine the UI elements (e.g. The markdown, sidebar, main UI).

- Everyone: should make sure their feature does what the issue states.

- Everyone: should announce if they have any changes for the CoC(Code of Conduct) by Friday.

- Current Minute taker (Pepijn): should upload the repair of the CoC(Code of Conduct) to Brightspace (before Friday).

- The next meeting is on Monday 16 december 2024 (keeping the midterm week for exams).

## Meeting

### Opening

- The chair(Oleh) started by announcing that he will leave time for the minute taker to write notes, and after everyone agrees providing a conclusion of the topic. (Feedback from previous meeting)

- We presented our work of the previous sprint to the TA.

- We updated the TA on our previous Monday meeting on 2nd december.

- We discussed our tasks for this sprint.

- We agreed that updating the issues when we're done with a task is a good idea.

- We agreed that the issues should be updated before Friday, so everyone is on track.

- We discussed the purpose of our Monday meetings.

- We discussed the way we want to review merge requests in the future.

## Agreements:

We agreed that on Monday meetings we will start a new sprint. Furthermore, we plan to keep Monday meetings for distributing tasks and discuss the potential dependencies.
In the Monday meetings we won't be focussing as much on the implementation of the tasks.

For next sprint we want to plan the reviewers for an issue in the sprint planning, so during the Monday meeting.

### Implementation details:

We agreed that the highlighting of a note should only happen to the selected note and not if your cursor hovers over a note.

We have agreed that the synchronization should wait for an update (aka: key press) before synchronizing the database, to minimize database calls.

We haven't come to an agreement for the searching algorithm, this should be further discussed, we did find it a good idea to just make it work for now and optimize it later.

### Organizational

We have to adjust the code of conduct to the provided feedback and submit it on Brightspace.

## TA announcements:

- Merge requests should be merged before sunday 23.59, since the knock-out criteria end at that time.
- Tushit will be able to attend the meeting on 18 december online.
- Week 4 and 5 are combined for the knock-out criteria, should still be one weeks work. However still needs to be checked.
- The knock-out criteria run from Monday (00.00) till Sunday (23.59).
- For the meetings we should prepare a demo of the work you did the **previous** sprint.
- Meetings should be less technical and more about the overall project and teamwork.

### Next TA meeting:

For next meeting everyone should be able to explain and (if applicable) show a demo of their feature.
This demo should be prepared beforehand, so the meeting will flow better.

For next meeting everyone should have looked at the agenda and previous minutes.