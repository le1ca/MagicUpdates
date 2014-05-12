MagicUpdates
============

Application for automatically applying updates to certain software on Windows.


Caveats
-----------

* It's written in Java, so unfortunately you'll have to already have Java installed to run this.
* Requires the HTTP library Resty, version 0.3.2 (name it resty-0.3.2.jar and place it in the same directory as this Readme)
* Some versions of the JVM don't play nice with it (seems to be ok on 7U45 and up, definitely works on 7U55)
* Only works on Windows (tested on 7) and requires being run as Administrator
* It requires an HTTP server on your LAN to download the updates from
* It uses the statically configured directory c:\magicupdates to save its state and anything it downloads
* I churned this thing out over the course of about 2 days at work so it might have some bugs
* Don't blame me if it breaks something
* Not very configurable, I only wrote code for the rules I needed to use


Usage
-----------

* Compile it (run `make`)
* Edit the config file as required and put it on your server, along with all the required update files
* Run `java -jar magic.jar http://your-update-host/path/to/updates.cfg` as Administrator
* It generates some stuff (batch file, state file, data files, and logs) in c:\magicupdates and schedules a task to run on reboot to install the updates
* When you reboot, the updates will install


Configuration rules
-----------

* Comments must start at the beginning of a line and begin with //
* Empty lines are ignored
* Each configuration item is named with an 'entry' line
* State the version of the item with the 'version' line
* State the files required for the item with 'file' lines
* State the commands required for the installation with 'command' lines (See Replacements section below for details)
* Specify any specific conditions for installation with 'cond' lines (See Conditions section below)
* End the item block with an 'end' line
* Refer to the included example config file for details


Conditions
-----------

Conditions take the form type;param{;moreparams}

Three types of conditions are supported:

* platform;xx
  * platform;32 to only run the item on a 32 bit system
  * platform;64 to only run the item on a 64 bit system
* cmdnotcontain;string;command
  * Runs the specified command, and installs the item if it doesn't contain the specified string
  * For an example, look at the Java rules in the example config
* cmdcontain;string;command
  * Same as the cmdnotcontain rule, but the opposite behavior


Replacements
-----------

Command strings can have variables in them. Three types of replacements are supported:

* %%n
  * Replaced by the full pathname of file number n of this entry
* !!n
  * Like %%n, but located in the log directory and with '.log' appended (for a log file related to a specific downloaded file)
* @@
  * Replaced by the working directory c:\magicupdates\data

Again, refer to the example config file to see how all of these work.
