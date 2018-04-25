# Recorder

Utility to record TCT data stream to CSV file.

## Windows Installation

1.  Run `gradle build`.
2.  Copy ZIP archive in `build/distributions/recorder.zip` to Driver Station.
3.  Extract all to `C:\` to create `C:\recorder` installation.

## Windows Usage

1.  Enable grapher on robot, for example by installing safety interlock.
2.  Open command prompt.
3.  Change directory to `c:\recorder`.
4.  Run `bin\recorder` to start streaming data to CSV file.
5.  Run the robot.
6.  Type `CTRL-C` to stop recording data.
7.  The resulting CSV file will be found in `C:\recorder`.
