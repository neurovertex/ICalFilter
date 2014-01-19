package eu.neurovertex.icalfilter;

import biweekly.component.VEvent;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Neurovertex
 *         Date: 19/01/14, 04:33
 */
public class CalendarFilter {
	private List<String> filters = new ArrayList<>();
	private boolean exclusive;

	public CalendarFilter(boolean inclusive) {
		this.exclusive = !inclusive;
		for (int i = 0; i < filters.size(); i++)
			filters.set(i, filters.get(i).toLowerCase());
	}

	public void setFilters(List<String> filters) {
		this.filters = new ArrayList<>(filters);
	}

	public void importFromFile(File f) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF8"));
		String line;
		if (Main.verbose)
			System.out.println("Importing filters : ");
		while ((line = in.readLine()) != null) {
			if (line.length() > 0) {
				line = removeNonASCII(line);
				filters.add(line);
				if (Main.verbose)
					System.out.println(line);
			}
		}
		in.close();
	}

	public void exportToFile(File f) throws IOException {
		PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF8"));
		for (String s : filters)
			out.println(s);
		out.close();
	}

	public boolean filter(VEvent ev) {
		boolean contains = false;
		for (String s : filters) {
			String sum = removeNonASCII(ev.getSummary().getValue().toLowerCase());
			if (Main.veryVerbose)
				System.out.println(sum +" : "+ s.toLowerCase() +" : "+ sum.contains(s.toLowerCase()));
			if (sum.contains(s.toLowerCase())) {
				contains = true;
				break;
			}
		}
		return contains ^ exclusive;
	}

	public static String removeNonASCII(String s) {
		return s.replaceAll("\\P{InBasic_Latin}", "");
	}
}
