package p2pFileSharing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;


public class ConfigProperties {
	static String peerInfoPath = System.getProperty("user.dir") + File.separator+"p2pFileSharing"+File.separator+"PeerInfo.cfg";
	static String configInfoPath = System.getProperty("user.dir") + File.separator+"p2pFileSharing"+File.separator+"Common.cfg";
	static HashMap<String, String> commonconfig = new HashMap<>();
	static Vector<PeerDetails> peerInfo = new Vector<>();
	static ConfigProperties config = null;

	public ConfigProperties() {
	     readConfigInfo(configInfoPath);
	     readPeerInfo(peerInfoPath);

	}

	public void readConfigInfo(String configInfoPath) {
		try (BufferedReader br = new BufferedReader(new FileReader(configInfoPath))) {
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				String[] parts = sCurrentLine.trim().split("\\s+");
				commonconfig.put(parts[0], parts[1]);
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}

	public static void readPeerInfo(String peerInfoPath) {
		try (BufferedReader br = new BufferedReader(new FileReader(peerInfoPath))) {
			String sCurrentLine;
			PeerDetails peerDetails = null;
			while ((sCurrentLine = br.readLine()) != null) {
				String[] parts = sCurrentLine.trim().split("\\s+");
				if (parts[3].equals("0")) {
					peerDetails = new PeerDetails(parts[0], parts[1], parts[2], false);
				} else {
					peerDetails = new PeerDetails(parts[0], parts[1], parts[2], true);
				}
				peerInfo.add(peerDetails);
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	

	public static HashMap<String, String> getCommonConfig() {
		if (config==null) {
			config = new ConfigProperties();
		}
		return commonconfig;

	}

	public static Vector<PeerDetails> getPeerInfo() {
		if (config==null) {
			config = new ConfigProperties();
		}
		return peerInfo;
	}
 

}
