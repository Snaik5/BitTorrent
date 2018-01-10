package p2pFileSharing;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

abstract public class MessageHandlerParent {
	

public Object listenForMessages (InputStream inpStrm, SetupClientConnection clientConObj) throws IOException {
		
		try {
			ObjectInputStream objInpStrm = new ObjectInputStream(inpStrm);
			Object obj = objInpStrm.readObject();
			clientConObj.actMsg = (ActualMessages)obj;
			return obj;
		}
		catch (ClassNotFoundException e) {
			System.out.println(e);
		}
		
		return null;
		
	}
public Object listenForMessages (Socket socket, SetupServerConnection serverConObj) throws IOException {
	
	try {
		InputStream inpStrm = socket.getInputStream();  
		ObjectInputStream objInpStrm = new ObjectInputStream(inpStrm);
		
		Object obj = objInpStrm.readObject();
		serverConObj.actualMesg = (ActualMessages)obj;
		
		return obj;
		
		
	}
	catch (ClassNotFoundException e) {
		System.out.println(e);
	}
	
	return null;
	
}

}
