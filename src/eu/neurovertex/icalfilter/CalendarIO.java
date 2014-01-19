package eu.neurovertex.icalfilter;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Neurovertex
 *         Date: 18/01/14, 17:48
 */
public class CalendarIO {
	private File infile, outfile;
	private List<ICalendar> cals;

	public CalendarIO(File inFile, File outFile) throws IOException {
		this.infile = inFile;
		if (! inFile.exists()) {
			throw new FileNotFoundException();
		} else if (! inFile.isFile() || ! inFile.canRead()) {
			throw new IOException();
		}
		this.outfile = outFile;
	}

	public CalendarIO(String inname, String outname) throws IOException {
		this(new File(inname), new File(outname));
	}

	public List<ICalendar> read() throws IOException {
		cals = Biweekly.parse(infile).all();
		if (Main.verbose)
			System.out.println("Successfully read "+ cals.size() +" calendars");
		return cals;
	}

	public void write() throws IOException {
		Biweekly.write(cals).go(outfile);
	}

	public Set<String> getEventSummaries() {
		Set<String> summaries = new HashSet<>();
		for (ICalendar cal : cals)
			for (VEvent ev : cal.getEvents())
				summaries.add(ev.getSummary().getValue());
		return summaries;
	}

	public int getEventCount() {
		int count = 0;
		for (ICalendar cal : cals)
			count += cal.getEvents().size();
		return count;
	}

	public int filter(CalendarFilter filter) {
		int kept = 0;
		for (int i = 0; i < cals.size(); i ++) {
			ICalendar cal = new ICalendar();
			for (VEvent ev : cals.get(i).getEvents())
				if (filter.filter(ev)) {
					kept ++;
					cal.addEvent(ev);
				}
			cals.set(i, cal);
		}
		return kept;
	}
}
