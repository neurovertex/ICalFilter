
ICalFilter
==========

Introduction
------------

This is a small utility that filters events in an iCalendar file based on a set of filters. For now there's only basic matching, it'll just check if the summary contains one of the filter, no regular expression support, but it'll come eventually.

Usage
-----

    usage: java -jar ICalFilter.jar [OPTIONS]... <INPUT> <OUTPUT> <FILTERS>
                java -jar ICalFilter.jar -n <OUTPUT> [OPTIONS]... <FILTERS>

    Filters elements from INPUT and write it to OUTPUT. The filters must be
    formatted in JSON, see https://github.com/neurovertex/ICalFilter for
    details. Matching events are removed, unless --inclusive is specified then
    non-matching events are removed. Non-ASCII (InBasic_Latin) characters are
    ignored during the matching process (event names won't be modified in the
    output file).
     -h,--help              Displays this help
     -i,--inclusive         Only accepts events that match the filters.
                            Default is exclusive
     -n,--normalize <arg>   Normalise the filters file (removes ASCII
                            characters, convert to JSON) and write it to ARG
     -p,--plain-text        Reads the filters file as plain text instead of
                            the default JSON
     -q,--quiet             Quiet mode. Won't output anything except errors.
     -v,--verbose           Verbose mode. More detailed output, will print the
                            list of event (once per different summary) in the
                            output file.
     -vv,--very-verbose     Very verbose mode. Will display for each event if
                            it matches each filter.


Filters format
--------------

The JSON format for filters is an array of object with the following properties :

    {
    "filter": "Event 1",
    "regex": false,
    "caseInsensitive": true,
    "matchDescription": false
    }

filter is the pattern text, regex indicates if it is plain text or a regex, caseInsensitive is pretty much self-explainatory, and matchDescription states if the pattern should be matched against the description of the events instead of, by defaut, the summary.
If normalized from plain text, the value of the boolean fields are as the exemple shown above.

Dependencies
------------

This project use [Biweekly](http://sourceforge.net/projects/biweekly/) and [Apache Commons CLI](http://commons.apache.org/proper/commons-cli/)

TODO
----

- Actual pattern (regex) matching
- Use JSON format for the filters file to allow finer filtering, like matching the description or location of events.