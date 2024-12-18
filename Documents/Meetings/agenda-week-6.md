| Key          | Value                         |
| ------------ |-------------------------------|
| Date :       | 18/12/2024                    |
| Time :       | 13:45                         |
| Location :   | Drebbelweg Instr. R1 / Online |
| Chair        | Pepijn                        |
| Minute Taker | Twan                          |
| Attendees :  | 5                             |

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
- Is there anything unclear or needs further discussion?

## Elaboration: Feedback on Previous Meeting (3min)

- Ensure that the discussion won't go too much in details of the implementations.
- The chair will demo main with everyone's changes.

## Showcase Progress to TA (5min)

- Present application prototype to the TA

## Quick Recap: Meeting on Monday

- Sprint Review (what has been done?) (4min)
- Sprint Retrospective (what was commited and can be improved?) (4min)
- Sprint Planning (what will be done?): Make sure that everyone opened their issues with appropriate milestones (8min)

## Discussion Topics

### 1. Clarify updating our issues on Friday? (5min)

- On monday we didn't reach an agreement on what to do with updating the issues on friday.
    -  Either update the issues through time tracking in gitlab
    -  Or update the issues in the description of them

### 2. Distributing the extra features in the future (10min)

- During the monday meeting we hadn't agreed over a sufficient plan for our task distribution.
- For this sprint we established a distribution, however we felt that during the Christmas break this won't hold. Since Tushit will be done soon with the language switch.
- We discussed the possibility of multiple people working on a single feature...

## Quick Remarks (3min)

- Confirm we assign one reviewer, last sprint was a bit messy with the reviews.
- And if the MR owner merges or the reviewer.

## Questions & Extra Topics (if any) (5min)

## Announcements by the TA (if any) (5min)

## Meeting Closure (2min)

- Thanks to everyone for participating!

### Planned time (50min)

# Minutes

### Roles:

- Chair: Pepijn
- Minute taker: Twan

### Present:
 - Liviu
 - Pepijn
 - Petar
 - Oleh
 - Twan

## Tasks:

- Next chair (Twan): Prepares agenda for the next meetings (Mon 6 Jan, Wed 8 Jan). And reserves room for Mon 6 Jan at 11:00.

- Everyone: Make sure all issues are working towards the milestones. 

- Everyone: Add issues for bugs if encountered. 

- Everyone: Add time tracking to all issues, and update actual time.

- Everyone: Look at shortcut issues, notify Twan for requests or changes.

- Everyone: Keep issue board up to date for in progress tasks.

- Liviu: implements part of the automated change sync extension.

- Pepijn: implements part of the integrated files extension.

- Oleh: implements part of the collections extension.

- Petar: implements part of the interconnected notes extention.

- Tushit: implements part of the languages extension (almost done).

- Twan: implements keyboard shortcuts for accessibility.

- If done: Discuss in group to support others with extensions (Oleh, Pepijn, Petar, Liviu). Or solve bugs and unassigned issues.

## Meeting

### Opening 
- Tushit couldn't stay connect (reason: bad airport Wi-Fi), this will need to be noted as not present, but that is allowed once.

- Showcase of work for previous week:
    1. Liviu: pop-ups.
    2. Pepijn: add/delete notes.
    3. Petar: searchbar with filters.
    4. Oleh: sync notes to server.
    5. Tushit: language selection menu.
    6. Twan: Open notes and updating titles.

- Updated TA on our Monday meeting.

### Agreements
- Friday progress updates are not required anymore. Only update time tracking.

- Could add progress report if one specific element of it in solved.

- No meetings throughout the Christmas break (Communication via WhatsApp with team).

- Communicate in group if done with task to help Oleh, Pepijn, Petar or Liviu with extension.

- Update issues on time, keep last day(s) free for approvals and merges.

### Workflow Improvements
#### 1. Assigning reviewers
- Always only assign one reviewer for each merge request. For this, it is required that the merge requests are not too big.

#### 2. Merging by reviewers
- Use auto-merge to speed up merge process and clearify in merge request if there are still changes coming, so reviewers don't merge it for you (past problem).

#### 3. Demo branch (suggestion)
- Discussion about an extra demo branch. 
    - Decision: no, not needed. 

### Implementation Details

#### 1. Title as input field
- Twan title of note now an input field. Discussion for change to edit button and pop up. Agreed to leave it and later layout with CSS.

#### 2. UUID for collection
- Oleh discussed with the team for possibility for changing the Collection ID to a UUID for multiserver compatibility. Team agreed + evaluated other solutions. 

### TA announcements

- There won't be any knock-out criteria in the Christmas break. We can still decide to work on it.

- We need to make our own check style to pass.

- The pipeline needs to follow check style.

- The TA left earlier (reason: time mix up), meeting went on for an extra 10 min to discuss some technical details.

### Next Meeting

We agreed that the next meeting will take place on Monday, January 6th 2025 at 11:00.

Twan books room 'Albert Einstein' in the Library (not yet possible, will communicate to team later).
