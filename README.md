# Dragons Domain IV MUD Tools

A collection of tools for parsing and analysing data files used by the Dragons Domain IV MUD ("DD4 MUD").

The MUD server code can be found at [github.com/fromage-fraser/dd4](https://github.com/fromage-fraser/dd4).

## Tools

Tools are a collection of Kotlin programs. They run on the JVM and target Java 11.
You will need Java (at least version 11) to build the tools.
The project uses the `gradle` build tool. `make` and `npm` are recommended.
See below for building information.

### Area Parser

Parses DD4 area files writes them out to a YAML format. These files are used by other tools in this suite.

### Area Checker

Runs some basic linting checks over parsed area data. Can be used to find basic formatting errors, etc.

### Map Maker

Creates static HTML maps from parsed area files.

**NOTE:** These maps are far from perfect and have various layout issues.
However, they are generally "good enough". Lots to improve here!

### Area Query

Generates a specialised area entity database for use in an external query tool.

## Sample data

The _sample-data/_ directory is used by the Makefile to look for MUD area files.
It is empty in this repository: the _area/_ directory from the _dd4_ repository can be copied there.
Sample data are not required to run the tools: you can manually specify the location of source area data.
But you may wish to use `make` as it is more convenient.

## Build

`make` can be used to build the individual tools.

Clean the _output/_ and build directories:

    make clean

Build the tools:

    make build

Tool JARs will be copied to the _output/lib/_ directory.

## Usage

`make`  can be used to run the built tools against area data in the _sample-data/_ directory.
The built tools can be run manually to specify other input and output data.

### Parse area files

Parse sample-data area files and write them to the `output/data/areas.yml` file:

    make parse

General usage:

    java -jar output/lib/area-parser.jar --input-dir path/to/mud/area/dir --output-file path/to/parsed/areas.yml

    java -jar output/lib/area-parser.jar --help

### Check area files

Check area data. Provides some basic linting. Uses parsed area data as input.

Check sample-data area files:

    make check

General usage:

    java -jar output/lib/area-checker.jar --input-file path/to/parsed/areas.yml

    java -jar output/lib/area-checker.jar --help

### Create maps

Create individual HTML maps for areas, with an index page. Uses parsed area data as input.

To create maps from sample-data area files and write them to the `output/maps/minified/` directory:

    make maps

For full usage:

    java -jar output/lib/map-maker.jar --input-file path/to/parsed/areas.yml --output-dir path/to/maps/dir

    java -jar output/lib/map-maker.jar --help

This process requires the `html-minifier` Javascript library to be installed:

    npm install html-minifier -g

To generate maps without this minification process, run

    make maps-unminified

This will write maps to the `output/maps/unminified/` directory.
Note that maps are quite large if not minified.

The following is convenient for regenerating and checking maps quickly:

    make clean-output maps-unminified

### Query DB

Create a JSON document of area data that forms a static database for query tools.
Supports an external tool but may be generally useful.

    make query-db
