| Key            | Value                         |
| -------------- | ----------------------------- |
| Date :         | 27/11/2024                    |
| Start Time :   | 13:45                         |
| End Time :     | 14:45                         |
| Location :     | Drebbelweg-Instruction Room 1 |
| Chair :        | Tushit Jain                   |
| Minute Taker : | Oleh Dzoba                    |
| Attendees :    | 6                             |

# Agenda Items

- Opening by chair (2 min )
- Check - in : How is everyone doing? (2 min )
- Quick summary on the structure of this meeting (1 min)
- Approval of the agenda - Does anyone have any additions or would like to add anything at the end? (0-2 min )
- Announcements by the TA (5 min, more if required )

- Talking Points : ( Inform / brainstorm / decision making / discuss )
- < Agenda - item 1 > Let the TA know about the meeting this Monday (2 min )
- < Agenda - item 2 > Talk about the assignment, how the tasks were devided and what everyone is working on (5 min )
- < Agenda - item 3 > Talk to TA about git issues, and whether the task distribution is too broad (2 min)
- < Agenda - item 4 > If team members want to speak on what they have done so far/ their progress(3 mins per person if required)
- < Agenda - item 5 > Speak on the client side confusion about adding/changing notes (5 mins, more if required)
- < Agenda - item 6 > Get feedback on Code of Conduct (5 mins if required)
- < Agenda - item 7 > Talk about Liviu opting out of the meeting on Monday (5 mins if required)
- < Agenda - item 8> Talk about any additional items other team members might raise (2 mins)

- Summarize action points : Who, what, when? TODO's discussed in the meeting (5 min)
- Feedback round : What went well and what can be improved next time (5 mins )
- Planned meeting duration (60 min )
- Question round(5 min )
- Closure (1 min )

## Minutes

Roles:

- Chair: Tushit
- Minute taker: Oleh

Present:

- Tushit
- Oleh
- Petar
- Twan
- Liviu
- Pepijn

### Actions

> The outline of the meeting can lies in next sections.

- Next chair (Oleh): Prepares agenda for the next meetings (Mn, Wd) and reserves a room for Monday.

- Current chair (Tushit): Creates and assigns issues on GitLab for the current sprint.

- Everyone should test their code if it actually resolves the issued assigned to them before the next meeting.

- Petar and Twan should discuss the implementaion and their collaboration on the sidebar.

- Liviu should implement the Markdown view with two panes and no tabs (by the end of the sprint).

- Twan should remove tabs (for collections) from the sidebar UI (by the end of the sprint).

- Everyone should verify if they have anything to change in the Code of Conduct by Friday.

- Minute taker (Oleh) should upload the updated CoC (if changed) by Friday 23:59.

- Everyone will show up on all future Monday meeting.

- The team has next meeting on Monday, December 2nd at 14:00.

- The team must decide on the ID attribute for `Note` by the end of the sprint.

### Introduction & Announcements

We started of by achknowledging our TA of our meeting on Monday.

We established that chair plans a meeting for Monday and Wednesday. That includes writing agenda and reserving a place for the meeting.

We agreed that meetings on Monday are helpful to get some work done before mandatory meeting with the TA.

TA clarified that issues (on GitLab) can be prepared well in advance and don't have to be resolved right away in the active sprint.

We discussed that splitting of the tasks on front-end and back-end tasks is a good approach as long as features are actually delivered (that implies well-tested code). These tasks (front-/back-end) have to be interchanged for each individual team member, so that everyone knows the codebase.

It was clarified that Jackson is built-in into Sprint Boot, therefore by using Sprint Boot non-functional requirement to use Jackson (for data de-/serialization) is fullfilled.

### Individual Tasks & Concerns

Petar and Twan should discuss the impementation of sidebar because they might run into conflicts or redoing the same functionality.

Collaborative note editing - the implementation of this feature is complex. Therefore we leave the feature out (as was mentioned in the backlog).

Contents of the note will be stored in the database table until one wants to implement collaborative note editing. Then they may decide if it should be changed.

For the sidebar, we stick to the design provided to us: there will be filtering functionality as a drop-down in right-top corner, hence tabs (to change between collections) are unnecessary.

For the Markdown view, there will be one pane for the actively edited text and one for the rendered view. Tabs will introduce extra (unnecessary) complexity, therefore are not priority.

### Organizational

We may change the Code of Conduct (CoC) until Friday 23:59. The updated version must be submitted to the Brightspace. After the deadline we'll recieve formative feedback upon which we can improve our CoC. The final version (for summative assessment) must be submitted to the Brightspace and the deadline for it is in later weeks.

### No-show

Liviu did not show up to the meeting the team planned to have on Monday because he was feeling unwell. He understands that such behaviour is decremental to the team's progress and will adhere to the future agreements regarding meeting with the team.

Everyone agreed to the importance of meeting: both mandatory and extra ones. Everyone understands that meeting are crucial to the team's performance.

### Next Meeting

We agreed that the next meeting will take place on Monday, December 2nd at 14:00.

Rooms must be booked well in advance for the meetings.

### Code Contribution & Technical Details

#### Updating documentation

Updated documentation should be merged to main without creating extra MRs. Creating an MR for every document would introduce additional overhead which needs to be avoided.

#### ID attribute for `Note`

We did not agree on the ID field for the `Note` entity. Two options were discussed, namely: using `Name` as ID, and created a dedicated autoincrement ID attribute.

Different arguments were raised to support one option or the other, yet no final decision was made. The topic requires further discussion.

### Closing (by TA)

It was explained about the importance of weekly contributions: every team member has to submit at least one MR and contribute 100+ lines of code to pass the knock-out criteria. If the contribution is not sufficient a team member will recieve a warning by email. Insufficient contribution two weeks in a row will lead to a team member getting expelled from the team.

It was pointed out that agreements in the CoC must be SMART (specific, measurable, acceptable, realistic, time-bound). The CoC must contain agreements on how and when to contact the TA if conflicts arise.

By the next TA meeting the team is expected to deliver a product that can be presented to the TA and every team member should be able to explain what they contributed to the project. This way the progress of the team can be monitored.
