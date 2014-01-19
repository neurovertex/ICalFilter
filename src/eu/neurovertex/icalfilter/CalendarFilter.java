package eu.neurovertex.icalfilter;

import biweekly.component.VEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Neurovertex
 *         Date: 19/01/14, 04:33
 */
public class CalendarFilter {
	private static final Type filtersType = new TypeToken<ArrayList<Filter>>() {
	}.getType();
	private List<Filter> filters = new ArrayList<>();
	private boolean exclusive;

	public CalendarFilter(boolean inclusive) {
		this.exclusive = !inclusive;
	}

	public void importJSON(File f) throws IOException {
		Gson gson = new Gson();
		JsonReader reader = new JsonReader(new FileReader(f));
		filters = gson.fromJson(reader, filtersType);
		if (Main.verbose) {
			System.out.println("Importing filters (" + filters.size() + ") : ");
			for (Filter filter : filters)
				System.out.println(filter.filter);
		}
	}

	public void importPlainText(File f) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF8"));
		String line;
		if (Main.verbose)
			System.out.println("Importing filters : ");
		while ((line = in.readLine()) != null) {
			if (line.length() > 0) {
				line = removeNonASCII(line);
				filters.add(new Filter(line, false, true, false));
				if (Main.verbose)
					System.out.println(line);
			}
		}
		in.close();
	}

	public void exportJSON(File file) throws IOException {
		PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8"));
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		out.print(gson.toJson(filters, filtersType));

		out.close();
	}

	public boolean filter(VEvent ev) {
		boolean contains = false;
		for (Filter f : filters) {
			String val = removeNonASCII((f.matchDescription ? ev.getDescription() : ev.getSummary()).getValue().toLowerCase());
			if (Main.veryVerbose)
				System.out.print(String.format("%s : %s", val, f.filter));
			if (f.getPattern().matcher(val).find()) {
				contains = true;
				break;
			}
		}
		return contains ^ exclusive;
	}

	public static String removeNonASCII(String s) {
		return s.replaceAll("\\P{InBasic_Latin}", "");
	}

	private class Filter {
		private String filter = null;
		private boolean regex = false;
		private boolean caseInsensitive = true;
		private boolean matchDescription = false; // Matches the pattern against the description instead of the summary

		private transient Pattern pattern;

		@SuppressWarnings("UnusedDeclaration")
		private Filter() {
		}

		public Filter(String filter, boolean regex, boolean caseInsensitive, boolean desc) {
			this.filter = filter;
			this.regex = regex;
			this.caseInsensitive = caseInsensitive;
			this.matchDescription = desc;
		}

		public Pattern getPattern() {
			if (pattern == null) {
				int flags = Pattern.UNICODE_CASE + Pattern.UNICODE_CHARACTER_CLASS + (caseInsensitive ? Pattern.CASE_INSENSITIVE : 0);
				if (regex) {
					pattern = Pattern.compile(filter, flags);
				} else {
					pattern = Pattern.compile(Pattern.quote(filter), flags);
				}
			}
			return pattern;
		}

	}
}
