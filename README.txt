########## AUTHOR ###########

Fabian Braun
21st of December 2014

####### INSTALLATION ########

To run this program a Java Runtime Environment in version 7 is required.
Please extract the .zip-archive.
It contains the following folder structure:
- randomTheLucky (parent folder)
	\- run.bat (shell script for execution on Windows OS)
	\- randomTheLucky.jar (runnable .jar-file, no program arguments required)
	\- save (directory for saved games, empty initially)
	\- resources (directory for configuration file and graphics)
		\- config.properties (configuration for the program)
		\- frog-icon_blue.png (image representing the player playing upwards)
		\- frog-icon_green.png (image representing the player playing downwards)

## CONFIGURATION and START ##

To start the program run the .bat file.
The configuration can be done over the file config.properties.
The following values can be modified:
	- The starting player (upwards or downwards)
	- A file containing a saved game to be continued
	- Whether a game should be saved after each move
	- Whether the GUI should be shown also when to automatic bots play against each other
	- The client/bot to be used (per player)
	- The evaluation function to be used (per player)
	- The total time that a player may spend during the game (per player)
For more instructions see the configuration file.

