#!/bin/sh
# A shell script to run MetroTimer, assuming you have java, javafx, and MetroTime installed

# The path to javafx11 module files, ie .../javafx11.../lib/
PATH_TO_FX=""

# The path to the compiled classes. Unless you compiled it yourself, this is .../MetroTimer/out/production/MetroTimer/
PATH_TO_METROTIME_CLASSES=""

# This is the part that actually opens the window. 
# You don't need to change anything here
cd "$PATH_TO_METROTIME_CLASSES"
java --module-path "$PATH_TO_FX" --add-modules=javafx.controls,javafx.fxml MainView &
