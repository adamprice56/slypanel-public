SlyPanel
========

SlyPanel is an app for Android (5.0+) that takes information from Linux computers/servers and serves as a "Status monitor".

At the moment, external libraries are as follows:

 - JSch (For the SSH backend)
 - AndroidPlot (For the graphs)
 
Server requirements:

 - Debian based OS (Commands were tested on Ubuntu 14.10 but feel free to test)
 - SSH server enabled and port forwarded (If required)
 - Root access to the server (Only for lm-sensors installation - Currently not supported)
 
The application is in early stages, At the moment it can send SSH commands but not get the response. A testing page is being implemented mainly for debugging and checking commands are being sent properly etc. 

Current status: (Updated 20/02/2015)
  - App retrieves information about CPU and RAM usage with minimal delay (Some minor UI glitches)
  - No UI blocking from updates, They are all run in AsyncTasks now in parallel
  - New Material design with Material Light theme (Personally... It's awesome ;D)
  - Seems stable... so far... :P
