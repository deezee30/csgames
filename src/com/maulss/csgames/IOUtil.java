/*
 * csgames
 * 
 * Created on 03 September 2016 at 11:33 PM.
 */

package com.maulss.csgames;

import com.maulss.csgames.match.Matches;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

public class IOUtil {

	// Disable initialization
	private IOUtil() {}

	public static final String MAIN_FOLDER_PATH = System.getProperty("user.home") + "/Documents/csgames";
	public static final String IMAGES_PATH = MAIN_FOLDER_PATH + "/team_images";
	private static final String DATA_PATH = MAIN_FOLDER_PATH + "/data.json";
	private static final String HLTV_SEARCH = "http://www.hltv.org/?pageid=152&query=";
	private static final String CSGL_MATCH_LINK = "https://csgolounge.com/match?m=";
	private static final String IMAGE_LINK = "https://csgolounge.com/img/teams/{TEAM}.jpg";

	public static void generateMainFolder() throws IOException {
		File main = new File(MAIN_FOLDER_PATH);
		if (!main.exists())
			main.mkdirs();

		File imgs = new File(IMAGES_PATH);
		if (!imgs.exists())
			imgs.mkdir();

		if (new File(DATA_PATH).createNewFile())
			Files.write(
					Paths.get(DATA_PATH),
					Collections.singletonList("[]"),
					Charset.forName("UTF-8")
			);
	}

	public static URLConnection sslConnection(String address) throws IOException {
		if (address == null)
			throw new NullPointerException("URL is null");

		URLConnection connection = new URL(address).openConnection();

		// Using custom headers due to forbidden access for non-browsers sometimes
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
		connection.setConnectTimeout(10000);
		connection.connect();

		return connection;
	}

	public static String readPageContents(String url) throws IOException {
		BufferedReader r = new BufferedReader(new InputStreamReader(sslConnection(url).getInputStream(), Charset.forName("UTF-8")));

		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = r.readLine()) != null) {
			sb.append(line);
		}

		return sb.toString();
	}

	public static void writeToDataFile(String content) throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(DATA_PATH);
		writer.print(content);
		writer.close();
	}

	public static String readFromDataFile() throws IOException {
		return new String(Files.readAllBytes(Paths.get(DATA_PATH)));
	}

	public static void resizeAndDownloadImage(String team) throws IOException {
		InputStream stream;
		try {
			stream = sslConnection(IMAGE_LINK.replace("{TEAM}", team)).getInputStream();
		} catch (FileNotFoundException ignored) {
			// Image doesn't exist, use default
			Matches.getInstance().setLogo(team, new ImageIcon(IMAGES_PATH + "/unknown.gif"));
			return;
		}

		final BufferedImage image = ImageIO.read(stream);
		final BufferedImage resized = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D g = resized.createGraphics();
		g.drawImage(image, 0, 0, 16, 16, null);
		g.dispose();
		ImageIO.write(resized, "gif", new File(IMAGES_PATH + "/" + team + ".gif"));
		Matches.getInstance().setLogo(team);
	}

	public static void openMatchId(int id) {
		openUrl(CSGL_MATCH_LINK + id);
	}

	public static void openSearchQuery(String query) {
		if (!query.equals("TBD"))
			openUrl(HLTV_SEARCH + query.replace(" ", "+"));
	}

	public static void openUrl(String url) {
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				desktop.browse(new URL(url).toURI());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void openDirectory(String path) {
		try {
			Desktop.getDesktop().open(new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}