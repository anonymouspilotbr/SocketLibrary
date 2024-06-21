package testando;

import java.io.*;
import java.net.*;

public class Server {

	public static Socket socket;
	public static ServerSocket serverSocket;

	// Readers e Writers de streams de dados
	public static InputStreamReader inputStreamReader;
	public static OutputStreamWriter outputStreamWriter;
	public static BufferedReader bufferedReader;
	public static BufferedWriter bufferedWriter;
	
	// Encerrar sessão?
	public static boolean exit = false;

	// Algum comando foi executado?
	public static boolean executed = false;

	// Classe de acesso ao arquivo "Library.json"
	public static Library library;
	
	// Chama a classe de handling
	public static Handling handling;
		
	public static void main(String[] args) {

		try {

			// Inicia uma biblioteca com base no em "Library,json"
			library = new Library(); 

			// Iniciar um Server Socket na porta 1234
			serverSocket = new ServerSocket(1234); 

			while (true) { // Mesmo que a conexão encerre, esperaremos por outra
			
				System.out.println("Esperando por uma conexão...");
				socket = serverSocket.accept(); // Aguarda uma conexão e cria um socket
				System.out.println("Conexão estabelecida.");

				// "Leitores" e "Escritores" da stream do socket
				inputStreamReader = new InputStreamReader(socket.getInputStream());
				outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());

				// "Leitores" e "Escritores" em buffer
				bufferedReader = new BufferedReader(inputStreamReader);
				bufferedWriter = new BufferedWriter(outputStreamWriter);
				
				// Instanciando o manipulador de comandos
				handling = new Handling(library, bufferedWriter);

				while (true) {
					
					// Esperando para ler a mensagem do cliente
					String msgFromClient = bufferedReader.readLine();

					if (msgFromClient == null) { break; }

					// Imprimindo a mensagem recebida
					System.out.println("Client: " + msgFromClient);

					// Acionando comandos com base no pedido do cliente
					Handling.handleCommands(msgFromClient);

					// Encerrando a conexão se exit == true
					if (exit) { break; }
				}
				
				// Encerramento de tudo caso sessão seja finalizada
				socket.close();
				inputStreamReader.close();
				outputStreamWriter.close();
				bufferedReader.close();
				bufferedWriter.close();

			}
		} catch (Exception e) {

			e.printStackTrace();

		} finally {
			
			try {

				// Independente de qualquer coisa, encerraremos tudo ao final do programa
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
}