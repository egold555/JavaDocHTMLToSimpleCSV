import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

	public static void main(String[] args) throws IOException {
		List<File> everyHtmlFile = new ArrayList<File>();
		getEveryFile("javadoc", everyHtmlFile);

		List<File> everyExceptionFile = sortOutExceptionFiles(everyHtmlFile);

		PrintWriter writer = new PrintWriter("exported2.csv");
		
		for(File f : everyExceptionFile) {
			
			//String name = getName(f);
			String imp = getImport(f);
			String html = getHtml(f);
			String description = getDescriptionFromHtml(html);
			
			writer.println(imp + ",\"" + description + "\"");
			//System.out.println(imp + "," + name + "," + description);
			
		}
		
		writer.close();
		

	}

	private static String getHtml(File f) throws IOException {
		return new String(Files.readAllBytes(f.toPath()));
	}
	
	private static String getImport(File f) throws IOException {
		String result = f.getAbsolutePath();
		result = result.replace("C:\\Users\\eric\\eclipse-workspace\\JavaDocToSpreadSheet\\javadoc\\", "");
		result = result.replace("\\", ".");
		result = result.replace(".html", "");
		return result;
	}

	private static String getName(File f) {
		String name = f.getName();
		name = name.replace(".html", "");
		return name;
	}

	
	static final Pattern pattern = Pattern.compile("</a></pre><div class=\"block\">(.+?)</div>", Pattern.DOTALL);
	static final Pattern pattern2 = Pattern.compile("</span></pre><div class=\"block\">(.+?)</div>", Pattern.DOTALL);
	static String getDescriptionFromHtml(String html) {
		
		html = html.replace("\n", "").replace("\r", "");
		html = html.replace("&lt;", "").replace("&gt;", "").replace("&le;", "").replace("&ge;", "");
		
		Matcher matcher = pattern.matcher(html);
		boolean didFind = matcher.find();
		String found = didFind ? matcher.group(1) : null;
		
		if(found == null) {
			matcher = pattern2.matcher(html);
			didFind = matcher.find();
			found = didFind ? matcher.group(1) : "**NOT FOUND**";
		}
		
		found = found.replaceAll("\\<.*?\\>", ""); //remove all html formatting, like clickable links
		
		return found;
	}

	static List<File> sortOutExceptionFiles(List<File> in){
		List<File> toReturn = new ArrayList<File>();
		for(File f : in) {
			if(f.getName().toLowerCase().contains("exception") || f.getName().toLowerCase().contains("error")) {
				if(!f.getPath().contains("class-use")) {
					String pathTest = f.getPath();
					pathTest = pathTest.replace("C:\\Users\\eric\\eclipse-workspace\\JavaDocToSpreadSheet\\javadoc\\", "");
					if(pathTest.startsWith("java")) {
						toReturn.add(f);
					}
					
				}
				
			}
		}
		return toReturn;
	}

	static void getEveryFile(String directoryName, List<File> files) {
		File directory = new File(directoryName);

		// Get all files from a directory.
		File[] fList = directory.listFiles();
		if(fList != null) {
			for (File file : fList) {      
				if (file.isFile()) {
					files.add(file);
				} 
				else if (file.isDirectory()) {
					getEveryFile(file.getAbsolutePath(), files);
				}
			}
		}
	}
	
	static String getBetweenStrings(String text, String textFrom, String textTo) {
		String result = "";
		result = text.substring(text.indexOf(textFrom) + textFrom.length(), text.length());
		result = result.substring(0,result.indexOf(textTo));
		return result;
	}

}
