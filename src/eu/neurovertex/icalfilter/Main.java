package eu.neurovertex.icalfilter;

import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.util.TreeSet;

/**
 * @author Neurovertex
 *         Date: 18/01/14, 15:39
 */
public class Main {
	protected static boolean verbose, veryVerbose;

	@SuppressWarnings("AccessStaticViaInstance")
	public static void main(String arguments[]) {
		Options opts = new Options();
		opts.addOption("h", "help", false, "Displays this help");
		opts.addOption("i", "inclusive", false, "Only accepts events that match the filters. Default is exclusive");
		opts.addOption("q", "quiet", false, "Quiet mode. Won't output anything except errors.");
		opts.addOption("v", "verbose", false, "Verbose mode. More detailed output, will print the list of event (once per different summary) in the output file.");
		opts.addOption("n", "normalize", true, "Normalise the filters file (removes ASCII characters) and write it to ARG");
		opts.addOption("vv", "very-verbose", false, "Very verbose mode. Will display for each event if it matches each filter.");
		HelpFormatter formatter = new HelpFormatter();

		try {
			CommandLine cli = new BasicParser().parse(opts, arguments);

			veryVerbose = cli.hasOption("vv");
			verbose = cli.hasOption('v') || veryVerbose;
			boolean quiet = cli.hasOption('q');

			if (quiet && verbose) {
				System.err.println("You can't enable both quiet and verbose mode. Make up your mind.");
				System.exit(-1);
			}

			String[] args = cli.getArgs();
			if (cli.hasOption("h")) {
				throw new ParseException("help");
			} else if (args.length != 3) {
				if (cli.hasOption("n") && args.length == 1) {
					CalendarFilter filter = new CalendarFilter(cli.hasOption("inclusive"));
					filter.importFromFile(new File(args[0]));
					filter.exportToFile(new File(cli.getOptionValue('n')));
					System.out.println("Wrote normalized rules to "+ cli.getOptionValue('n'));
				} else
					throw new ParseException("Needs more arguments");
			} else {
				CalendarIO io = new CalendarIO(args[0], args[1]);
				io.read();
				CalendarFilter filter = new CalendarFilter(cli.hasOption("inclusive"));
				filter.importFromFile(new File(args[2]));
				int total = io.getEventCount();
				int kept = io.filter(filter);
				if (!quiet)
					System.out.println(String.format("Filtered out %d event, kept %d/%d", total - kept, kept, total));
				if (verbose)
					for (String s : new TreeSet<>(io.getEventSummaries()))
						System.out.println(s);
				io.write();
				System.out.println("Wrote filtered calendar to " + arguments[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (ParseException e) {
			formatter.printHelp("java -jar ICalFilter.jar [OPTIONS]... <INPUT> <OUTPUT> <FILTERS>\njava -jar ICalFilter.jar -n <OUTPUT> [OPTIONS]... <FILTERS>", "\nFilters elements from INPUT and write it to OUTPUT. " +
					"Each line in the FILTERS file is considered a filter, an event matches if its summary contains at least one of the filters. " +
					"Matching events are removed, unless --inclusive is specified then non-matching events are removed. " +
					"Non-ASCII (InBasic_Latin) characters are ignored during the matching process (event names won't be modified in the output file).", opts, "", false);
			System.exit(e.getMessage().equals("help") ? 0 : -1);
		}
	}
}
