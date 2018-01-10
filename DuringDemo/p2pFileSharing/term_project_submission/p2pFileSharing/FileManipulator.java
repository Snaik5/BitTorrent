package p2pFileSharing;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

class FileManipulator {

	String fileToBeShared;
	long fileSize;
	long pieceSize;
	int pieceCount;
	Vector<PeerDetails> peerVector = ConfigProperties.getPeerInfo();
	HashMap<String, String> configMap = ConfigProperties.getCommonConfig();
	public static final String FILE_SIZE = "FileSize";
	public static final String PIECE_SIZE = "PieceSize";
	public static final String FILE_NAME = "FileName";
	public static final String FILE_PATH = File.separator+"p2pFileSharing"+File.separator+"peer_";
	public static final String UNCHOKING_INTERVAL = "UnchokingInterval";
	public static final String OPTIMISTICUNCHOKINGINTERVAL = "OptimisticUnchokingInterval";
	public static final String NUMBEROFPREFNEIGHBORS = "NumberOfPreferredNeighbors";

	public boolean fileCheck(int peerID) throws IOException {
		int vectorSize = peerVector.size();
		for (int i = 0; i < vectorSize; i++) {
			if (peerVector.get(i).peerProcessId.trim().equals(String.valueOf(peerID))) {
				return peerVector.get(i).hasFile;
			}
		}
		return false;
	}

	public String getFileName() throws IOException {

		return configMap.get("FileName");
	}

	public int[] getTime() throws IOException {
		int[] time = new int[2];
		time[0] = Integer.parseInt(configMap.get(UNCHOKING_INTERVAL));
		time[1] = Integer.parseInt(configMap.get(OPTIMISTICUNCHOKINGINTERVAL));

		return time;
	}

	public void configReading() throws IOException {
		this.fileSize = Long.parseLong(configMap.get(FILE_SIZE));
		this.pieceSize = Integer.parseInt(configMap.get(PIECE_SIZE));
		this.fileToBeShared = configMap.get(FILE_NAME);
		double pieceCount = (double) this.fileSize / this.pieceSize;
		double checkPieceCount = Math.floor(pieceCount);
		if (pieceCount > checkPieceCount)
			this.pieceCount = (int) pieceCount + 1;
		else
			this.pieceCount = (int) pieceCount;
	}

	public void fileSplit(String FileName, long FileSize, long PieceSize, int PeerID) throws IOException {
		String FilePath = System.getProperty("user.dir") + FILE_PATH + PeerID + File.separator + FileName;
		BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(FilePath));
		int bytesRead = 0;
		int countOfbytesRead = 0;
		int count = 0;
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
				new FileOutputStream(FilePath + "." + count));
		while ((bytesRead = bufferedInputStream.read()) != -1) {

			if (countOfbytesRead < PieceSize) {
				bufferedOutputStream.write(bytesRead);
				countOfbytesRead++;
			} else {
				count++;
				bufferedOutputStream.close();
				bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(FilePath + "." + count));
				bufferedOutputStream.write(bytesRead);
				countOfbytesRead = 1;
			}
		}

		bufferedOutputStream.close();
		bufferedInputStream.close();
	}

	public void formFile(String FileName, int pieceCount, int PeerID) throws IOException {

		int bytesRead = 0;
		BufferedInputStream bufferedInputStream = null;
		FileName = System.getProperty("user.dir") + FILE_PATH + PeerID + File.separator + FileName;
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(FileName));
		for (int i = 0; i < pieceCount; i++) {
			bufferedInputStream = new BufferedInputStream(new FileInputStream(FileName + "." + i));
			while ((bytesRead = bufferedInputStream.read()) != -1) {
				bufferedOutputStream.write(bytesRead);
			}
			bufferedInputStream.close();
		}
		bufferedOutputStream.close();
	}

	public void writeToOutPutFile(String FileName, String data) throws IOException {

		File file = new File(FileName);
		if (!file.exists()) {
			file.createNewFile();
		}
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
		bufferedWriter.write(data);
		bufferedWriter.close();
	}

	public ArrayList<Integer> readPiece(int pieceIndex, int PeerID) throws IOException {

		int bytesRead = 0;
		String FileName = System.getProperty("user.dir") + FILE_PATH + PeerID + File.separator + configMap.get(FILE_NAME);
		BufferedInputStream bufferedInputStream = new BufferedInputStream(
				new FileInputStream(FileName + "." + pieceIndex));
		ArrayList<Integer> fileRead = new ArrayList<Integer>();
		try {
			while ((bytesRead = bufferedInputStream.read()) != -1) {
				fileRead.add(bytesRead);
			}
			bufferedInputStream.close();
		} catch (EOFException ex) {
		}
		return fileRead;

	}

	public void formPiece(ArrayList<Integer> FilePiece, int pieceIndex, int PeerID) throws IOException {

		String FileName = System.getProperty("user.dir") + FILE_PATH + PeerID + File.separator + configMap.get(FILE_NAME);
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
				new FileOutputStream(FileName + "." + pieceIndex));
		for (int i = 0; i < FilePiece.size(); i++) {
			bufferedOutputStream.write(FilePiece.get(i));
		}
		bufferedOutputStream.close();
	}

	public static int countPieceFolder(int PeerID) throws IOException {
        int count =0;
		String DirectoryPath = System.getProperty("user.dir") + FILE_PATH + PeerID;
		File directory = new File(DirectoryPath);
		if(directory.exists() && directory.listFiles()!=null)
		{
		count = directory.listFiles().length;
		}
		System.out.println("No of file pieces in folder" + count);
		return count;
	}

	public static void directoryCreate(int PeerID) {
		// String Directory = System.getProperty("user.dir") +
		// "/p2pFileSharing/peer_" + PeerID;
		File directory = new File(System.getProperty("user.dir") + FILE_PATH + PeerID);
		if (!directory.exists())
			directory.mkdir();

	}

}
