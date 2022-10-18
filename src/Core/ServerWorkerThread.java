package Core;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class ServerWorkerThread implements Runnable{
	Socket connectionSocket;
	
	public ServerWorkerThread(Socket connectionSocket) throws IOException {
		if(connectionSocket!=null) {
			this.connectionSocket = connectionSocket;
		}else {
			throw new IOException("IOExcepion: Assigned connection socket "
														+ "doesn't exist.");
		}
	}
	
	@Override
	public void run() {
		try {
			InputStreamReader InputFromClient = new InputStreamReader(
											connectionSocket.getInputStream());
			BufferedReader connectionInStream = new BufferedReader(InputFromClient);
			String handShakeReq = connectionInStream.readLine();
			HandShakeMessage hsm = new HandShakeMessage(handShakeReq);
			if(!hsm.permitToConnect()) {
				connectionSocket.close();
				throw new IOException("Not Authorized: Server peer closed the connection.");
			}
			
			OutputStream output = connectionSocket.getOutputStream();
			DataOutputStream writeToClientStream = new DataOutputStream(output);
			writeToClientStream.writeBytes("");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				connectionSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
