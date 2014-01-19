
ICalFilter
==========

Introduction
------------

This is a small utility that filters events in an iCalendar file based on a set of filters. For now there's only basic matching, it'll just check if the summary contains one of the filter, no regular expression support, but it'll come eventually.

Usage
-----

    usage: java -jar ICalFilter.jar [OPTIONS]... <INPUT> <OUTPUT> <FILTERS>
                java -jar ICalFilter.jar -n <OUTPUT> [OPTIONS]... <FILTERS>

    Filters elements from INPUT and write it to OUTPUT. Each line in the
    FILTERS file is considered a filter, an event matches if its summary
    contains at least one of the filters. Matching events are removed, unless
    --inclusive is specified then non-matching events are removed. Non-ASCII
    (InBasic_Latin) characters are ignored during the matching process (event
    names won't be modified in the output file).
     -h,--help              Displays this help
     -i,--inclusive         Only accepts events that match the filters.
                            Default is exclusive
     -n,--normalize <arg>   Normalise the filters file (removes ASCII
                            characters) and write it to ARG
     -q,--quiet             Quiet mode. Won't output anything except errors.
     -v,--verbose           Verbose mode. More detailed output, will print the
                            list of event (once per different summary) in the
                            output file.
     -vv,--very-verbose     Very verbose mode. Will display for each event if
                            it matches each filter.

Note that the filter file needs to be encoded in UTF-8 without BOM

Dependencies
------------

This project use [Biweekly](http://sourceforge.net/projects/biweekly/) and [Apache Commons CLI](http://commons.apache.org/proper/commons-cli/)

TODO
----

- Actual pattern (regex) matching
- Use JSON format for the filters file to allow finer filtering, like matching the description or location of events.