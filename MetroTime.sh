#!/bin/sh
# A shell script to run MetroTimer, assuming you have java, javafx, and MetroTime installed

PATH_TO_FX="/Library/Java/Extensions/javafx-sdk-11.0.2/lib/"
PATH_TO_METROTIME_CLASSES='/Users/redekopp/Documents/Documents - Eric’s MacBook Pro/Projects/MetroTimer/out/production/MetroTime'

cd "$PATH_TO_METROTIME_CLASSES"
java --module-path "$PATH_TO_FX" --add-modules=javafx.controls,javafx.fxml MainView &
