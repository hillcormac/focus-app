# fyp
Final Year Project - Android app to improve focus and productivity

## complex features explained
- `tasks list filter`
    - handle everything in core activity
    - check user preferences onCreate
        - set default to date if not already there
        - else fetch current preference
    - store in variable
    - store arrays as they are now
    - pass selected array to listener as default and when populated
    - same process in onTaskCreated as above

## noted bugs
- adding 2 types at the same time and picking the 1st new one as the type causes crash

## features to do



- add secondary info TV  to task _e.g. date, type, priority_