*Project description*
A application to track karaoke events, and the songs that will be sung during the event.
It uses a firebase firestore database to store all of the data, features 2 roles, a regular user and a admin user. 
The admin user can add new events and new songs to the database and delete existing ones, and also link known songs to the events in the coresponding schedule, it even supports adding the same song multiple times.
While the regular users can bookmark the events they with to visit.
After a events is bookmarked the users can see the event for a longer period of time, and they will recieve a notification approximatly one hour before the event starts.

It uses one Activity and a navigation manager to move between different fragments for the view. It also uses firebase authentication to manage the users. 
It utilizes different views depending on the screen rotation and also saves the instance state before each device rotation.
Has the option to open the google maps application with the selected location so the user can navagate to the location easier

To set up the project locally one would need to install android studio, use a device with a API level higher that 33, link the project to firebase so that they can enable their own database,
and add their google maps API key. And also give apropriate permisions when first starting the app on the device

Photos of the project can be seen in the projectPhotos folder
