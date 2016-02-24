# Moonshots
Our FRC repo
#Step 1
Download Java JDK at http://www.oracle.com/technetwork/java/javase/downloads/index.html Select "JDK Download", then scroll down the page to "Java SE Development Kit". We use Java 8.  The version (either x86 or x64) should match the version of Eclipse that you have installed or plan to install on your computer.
#Step 2
Download Eclipse IDE for Java Developers http://www.eclipse.org/downloads/packages/eclipse-ide-java-developers/keplersr1 (32/64 MUST match Java version).
#Step 3
Extract files from .zip to Program Files. Within the eclipse folder you'll see the file "eclipse.exe". Pin it to the start menu and run it.
#Step 4
Start eclipse with the default workspace. Select Windows from the menu bar, then Preferences. Choose Java preferences in the list on the left of the Preferences window, then Installed JREs. Be sure that the installed JDK is selected as shown  (make sure the "Location" field includes jdk 8 or 1.8, the name field may be the same in either location). This will enable eclipse to build Java programs for the RoboRIO. Without this setting you will see error messages about the JRE path not being set correctly.
#Step 5
To set automatic saves, go to Window -> Preferences -> General -> Workspace -> Check Save automatically before build -> OK.
#Step 6
To get WPILib development tools: 
Select "Help"
Click "Install new software".
Push the "Add..." button then fill in the "Add Repository" dialog with:
Name: FRC Plugins
Location: http://first.wpi.edu/FRC/roborio/release/eclipse/
Click "OK".
#Step 7
Select the WPILib Robot Development plugin Java. Click next, next, agree, and finish, and Eclipse will restart.
#Step 8
Follow these instructions carefully for LabVIEW, FRC Driver Station, and FRC Utilities: https://wpilib.screenstepslive.com/s/4485/m/13809/l/144150-installing-the-frc-2016-update-suite-all-languages
#Step 9 
Use this serial number: M81X06605 
#Step 10
After all those components are installed, it's time to download Strongback. This has this specific instructions to download Strongback and add it to Eclipse: https://strongback.gitbooks.io/using-strongback/content/getting_started.html if needed. But I'll condense it in the next few steps.
#Step 11
Download latest version of Strongback zip file: https://github.com/strongback/strongback-java/releases
12. 
