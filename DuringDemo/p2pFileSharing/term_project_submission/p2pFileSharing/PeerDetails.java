package p2pFileSharing;

import java.io.Serializable;

public  class PeerDetails implements Serializable {
		/**
	 * 
	 */
	private static final long serialVersionUID = -2732734194182091118L;
		/**
	 * 
	 */
	
		public String peerProcessId;
		public String id;
		public String peerIpAddress;
		public String peerPortNo;
		public boolean hasFile;
		public String getPeerProcessId() {
			return peerProcessId;
		}

		/**
		 * Creates a PeerInfo object storing only the TCP port number.
		 *
		 */
		
		
		public void setPeerProcessId(String peerProcessId) {
			this.peerProcessId = peerProcessId;
		}

		public String getPeerIpAddress() {
			return peerIpAddress;
		}

		public void setPeerIpAddress(String peerIpAddress) {
			this.peerIpAddress = peerIpAddress;
		}

		public String getPeerPortNo() {
			return peerPortNo;
		}

		public void setPeerPortNo(String peerPortNo) {
			this.peerPortNo = peerPortNo;
		}

		public boolean isHasFile() {
			return hasFile;
		}

		public void setHasFile(boolean hasFile) {
			this.hasFile = hasFile;
		}

		

		public PeerDetails(String peerId, String peerAddress, String peerPort, boolean hasFile) {
			this.peerProcessId = peerId;
			this.peerIpAddress = peerAddress;
			this.peerPortNo = peerPort;
			this.hasFile = hasFile;
		}

		public PeerDetails(String port) {
			this.peerPortNo = port;
		}
		public PeerDetails() {
			
		}

		public String getId() {
			return id;
		}

		public void setId(String i) {
			this.id = i;
		}

		
		

	}

