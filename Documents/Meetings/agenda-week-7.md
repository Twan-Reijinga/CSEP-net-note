| Key          | Value                         |
| ------------ |-------------------------------|
| Date :       | 08/01/2024                    |
| Time :       | 13:45                         |
| Location :   | Drebbelweg Instr. R1 / Online |
| Chair        | Twan                          |
| Minute Taker | Petar                         |
| Attendees :  |                               |

# Agenda Items

## Opening by Chair (1min)
- Welcome everyone!

## Check-in: How is everyone doing? (2min)

## Agenda Announcement (3 min)

- Did everyone read the detailed agenda for today?
- Announce the topics of this agenda
- Does anyone want to add anything to the agenda?
- A lot on the agenda, so not to in depth

## Approval of Minutes from Previous Meeting (2 min)

- Did everyone read the minutes from previous agenda?
- Is there anything that needs to be said about it?

## Showcase Progress to TA (10 min)

- Prototype of features that are shippable
- Briefly explain work, not to technical

## Quick Recap: Monday Meeting (2 min)

#### Distribution of tasks
1. Oleh: filtering by collection
2. Petar: tags + note links
3. Twan: Take over language switch


- Additional discussion topics, later in meeting

## Discussion Topics

### 1. How to proceed without Tushit? (5 min)

- Twan takes over tasks.
- Where to compromise in planning?

### 2.1 Reallocation of tasks Liviu & Pepijn (7 min)

- Less progress than predicted.
- Is planning still realistic?
- Discuss best strategy to proceed with higher success rate.

### 2.2 Distribution of tasks Liviu & Pepijn (8 min)

- Working on this sprint
- Work out later

### 3. Milestones on GitLab (4 min)
- Now expired?
- One milestone each sprint
- Make issue board person dependent

## Quick Remarks (3 min)
- Keep time tracking (and estimate) up to date
- Make issues/commits/MR with sensible titles
- connect MR to issues
- close old issues

## Questions & Extra Topics (if any) (5 min)

## Announcements by the TA (if any) (5 min)

## Meeting Closure (2 min)

- Thank everyone for participation!

# Minutes

### Roles:

- Chair: Twan
- Minute taker: Petar

### Present:
 - Twan
 - Petar
 - Oleh

### Joined online:
 - Liviu
 - Pepijn

### Important
It was confirmed that Tushit will no longer be working on the project as he has failed to meet the knockout criteria two weeks in a row. We decided that Twan will take over Tushit's task, and he will therefore stop his work on shortcuts for now.

## Tasks:

- Next chair (Petar): Prepares agenda for the next meetings (Mon 13 Jan, Wed 15 Jan). And reserves a room in the library.

- Everyone: Epics should not be milestones, delete the crurrent milestones and make them labels instead. Assign the labels to corresponding issues.

- Everyone: Add milestone for week 7 and assign it to issues that will be completed this week.

- Oleh: continues his work on multi-collection.

- Petar: to start working on note links (interconnected content).

- Twan: starts work on the live language switch extension.

- Liviu and Pepijn: to discuss how to split the embedded files extension 

#### Reminder: Add timetracking to issues.


## Meeting

### Start 
- Tushit wasn't present since he no longer works with the team.
- Pepijn was online, it was not clear whether he had notified the TA about that.
- Liviu was online because he was feeling under the weather. The TA had not been notified and while there was a reason for the absence, failing to notify the TA beforehand should be avoided.

- Showcase of work for previous week:
    1. Oleh: collection settings popup and dropdown to select a collection.
    2. Twan: Refresh sidebar(Ctrl + r), navigate through notes(arrows), and undo last action (Delete/Add)
    3. Petar: demo build failed.

### Agreements
- Checkstyle needs 10 rules that break the pipeline, additional rules may be warnings.

- Expired milestiones should be taken care of(deleted).

- Milestone for week 7 should be created and everyone will assign it to their tasks for the week.

### Workflow Improvements
#### 1. Reviewing MRs
- MRs should not be stalled for too long and should be reviewed as soon as possible.

#### 2. Closing issues
- Old issues should be closed once they are resolved.

### Implementation Details

#### 1. Behaviour of searching and filtering
- It's left to the team to decide what the behaviour of the app will be in the case when searching is performed and tags are already selected as filters and vice-verca. 

#### 2. Collections from multiple servers
- A client should be connected to one server at a time and therefore can load only collections from the same server. (May change based on TA's response)

### TA announcements

- Deadline for Implemented features and HCI is Friday, 10 Jan 23:59 
    - Progress should be pushed and merged into main by then, in order to receive feedback on it.


#### Reminder: The code freeze is on 24 January, 23:59.

### Next Meeting

The next meeting will take place on Monday, January 13th 2025.