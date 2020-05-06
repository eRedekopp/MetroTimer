# MetroTimer

### What is MetroTimer?

MetroTimer is a metronome and a timer. It's simple. It's lightweight. It works on Windows, Mac, Linux, and anything
else that can run JavaFX. It doesn't have annoying ads or a million extra features that you were never going to use 
anyway.

### How do I download it?

Unfortunately, bundling a standalone JavaFX executable was harder than I expected. If you are on Linux or Mac, I've
included a shell script that I use to launch the application in my day-to-day use (see below). If you are on Windows and really
want to use this app, message me and I'll see about adding a similar solution for windows. Or you can make one yourself
and put in a pull request and I'll add it.

### How do I use the shell script?

These instructions only work for Mac and Linux.

Clone this repo or just download all the files. Move MetroTime.sh to wherever you want to launch the application from. Open the
file with a text editor and fill in the blanks (between the quotation marks) with the locations where you stored your files 
(instructions in the file). Note that you will need to change these if you ever decide to move the files.

To open it, double click the shell script from Finder, and make sure that "terminal" is your default application for .sh files.

You must have Java 11 installed on your command path, and JavaFX 11 installed somewhere where you can find it. I've added the 
compiled .class binaries to the repository to make this easier for non-programmers, so you don't have to compile yourself.
