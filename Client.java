import java.io.*;
import java.net.*;
import java.util.*;

public class Client {

	public static void main(String[] args) {

		Socket socket = null;
		InputStreamReader inputStreamReader = null;
		OutputStreamWriter outputStreamWriter = null;
		BufferedReader bufferedReader = null;
		BufferedWriter bufferedWriter = null;

		try {

			socket = new Socket("localhost", 1234);

			System.out.println("Seja bem vindo(a) à Biblioteca Online!");
			inputStreamReader = new InputStreamReader(socket.getInputStream());
			outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());

			bufferedReader = new BufferedReader(inputStreamReader);
			bufferedWriter = new BufferedWriter(outputStreamWriter);

			Scanner scanner = new Scanner(System.in);

			while (true) {
				
				String msgToSend = scanner.nextLine();
				bufferedWriter.write(msgToSend);
				bufferedWriter.newLine();
				bufferedWriter.flush();

				System.out.println("\nServer:---------------------------------\n" + formatResponse(bufferedReader.readLine()));

				if (msgToSend.toUpperCase().startsWith("/EXIT")) { break; }

			}

			scanner.close();

		} catch (Exception e) {

			e.printStackTrace();

		} finally {
			
			try {

				if (socket != null) { socket.close(); }			
				if (inputStreamReader != null) { inputStreamReader.close(); }
				if (outputStreamWriter != null) { outputStreamWriter.close(); }
				if (bufferedReader != null) { bufferedReader.close(); }
				if (bufferedWriter != null) { bufferedWriter.close(); }

			} catch (Exception e) {
				
				e.printStackTrace();		

			}
		}

	}
	
	public static String formatResponse(String response) {
		try {

			return response.replaceAll("<br>", System.lineSeparator());

		} catch (Exception e) {

			return response;

		}
	}
}
