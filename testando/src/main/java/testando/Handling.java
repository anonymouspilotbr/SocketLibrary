package testando;

import java.io.*;

public class Handling{
	private static Library library;
	private static BufferedWriter bufferedWriter;
	private static boolean executed = false;

	public Handling(Library lib, BufferedWriter bW) {
		library = lib;
		bufferedWriter = bW;
	}

	// Invoca todos os comandos
	public static void handleCommands(String cmd) throws IOException {

		if (cmd == null) {
			return;
		}

		// Separação entre comando e argumentos
		String command = cmd.split(" ")[0].toUpperCase(); // Primeira palavra da frase "/comando"
		String[] args = new String[cmd.split(" ").length - 1]; // Todo o resto da frase

		int c = 0;
		for (String arg : cmd.split(" ")) {
			if (c == 0) {
				c++;
				continue; // Inograr primeira palavra (pois é o comando)
			}

			args[c - 1] = arg;

			c++;

		}

		// Todos os comandos existentes tentarão executar

		try {

			commandHelp(command, args);    // HELP
			commandExit(command, args);    // EXIT
			commandListar(command, args);  // LISTAR <DISPONIVEIS | ALUGADOS>
			commandAlugar(command, args);  // ALUGAR <NOME_DO_LIVRO>
			commandDevolver(command, args);// DEVOLVER <NOME_DO_LIVRO>
			commandDoar(command, args);    // DOAR <NOME>, <AUTOR>, <GENERO>, <EXEMPLARES>
			commandAdmin(command, args);   // ADMIN (<DELETE> <NOME_DO_LIVRO>) | <BACKUP>

			if (!executed) {
				sendToClient("Nenhum comando encontrado" +
						"<br>----------------------------------------<br>");
			}

		} catch (Exception e) {

			sendToClient("Algo deu errado! Confira a digitação e tente novamente" +
					"<br>----------------------------------------<br>");
			e.printStackTrace();

		}

		executed = false;

	}

	// Lista os comandos
	private static void commandHelp(String command, String[] args) throws IOException {

		// Nome do comando
		String commandName = "HELP";

		// Não executar caso não seja esse o comando chamado
		if (!command.equalsIgnoreCase("/" + commandName)) {
			return;
		}
		executed = true;

		sendToClient("'/listar <disponiveis> | <alugados>' - Lista todos os livros disponívieis/alugados. <br>" +
				"'/exit' - Encerra a sessão. <br>" +
				"'/alugar <nome_do_livro>' - Aluga um livro. <br>" +
				"'/devolver <nome_do_livro>' - Devolve um livro alugado. <br>" +
				"'/doar <nome>, <autor>, <genero>, <exemplares>' - Adiciona um livro à biblioteca. <br>" +
				"'/admin (<delete> <nome_do_livro>) | <backup>' - Deleta um livro ou reseta toda a biblioteca." +
				"<br>----------------------------------------<br>");

	}

	// Encerra a sessão
	private static void commandExit(String command, String[] args) throws IOException {

		// Nome do comando
		String commandName = "EXIT";

		// Não executar caso não seja esse o comando chamado
		if (!command.equalsIgnoreCase("/" + commandName)) {
			return;
		}
		executed = true;

		System.out.println("Saindo da sessão...");
		sendToClient("Encerrando sessão");

		Server.exit = true;

	}

	// Lista livros disponiveis ou alugados
		public static void commandListar(String command, String[] args) throws Exception {
			
			// Nome do comando
			String commandName = "LISTAR";

			// Não executar caso não seja esse o comando chamado
			if (!command.equalsIgnoreCase("/" + commandName)) { return; }
			executed = true;

			String response = "";

			if (args.length <= 0) {
				sendToClient("Você precisa informar se quer listar disponiveis ou alugados." + 
				"<br>----------------------------------------<br>");
				return;
			}
			
			String selected = args[0].toUpperCase();
			if (selected.equals("DISPONIVEIS")){

				// Se não houver livros disponíveis:
				if (library.getAvaliableBooks().length == 0) {
					sendToClient("Nenhum livro disponível!" + 
					"<br>----------------------------------------<br>");
					return;
				}


				response = "Livros disponiveis: <br>----------------------------------------<br>";

				for (Book book : library.getAvaliableBooks()) {
					response += "Livro: " + book.nome + "<br>"
						+ "Autor: " + book.autor + "<br>"
						+ "Gênero: " + book.genero + "<br>"
						+ "Exemplares: " + book.exemplares + "<br>"
						+ "----------------------------------------<br>";

				}
			}	

			else if (selected.equals("ALUGADOS")) {

				// Se não houver livros alugados:
				if (library.getRentedBooks().length == 0) {
					sendToClient("Nenhum livro alugado! <br>----------------------------------------<br>");
					return;
				}

				response = "Livros alugados: <br>----------------------------------------<br>";

				for (Book book : library.getRentedBooks()) {
					response += "Livro: " + book.nome + "<br>"
						+ "Autor: " + book.autor + "<br>"
						+ "Gênero: " + book.genero + "<br>"
						+ "Exemplares: " + book.exemplares + "<br>"
						+ "----------------------------------------<br>";

				}
			}
			
			else { throw new Exception(); }

			sendToClient(response);
			
		}

		// Aluga um livro
		public static void commandAlugar(String command, String[] args) throws Exception {

			// Nome do comando
			String commandName = "ALUGAR";

			// Não executar caso não seja esse o comando chamado
			if (!command.equalsIgnoreCase("/" + commandName)) { return; }
			executed = true;

			// Livro informado
			String selected = String.join(" ", args);

			// Procurando o livro informado
			for (Book book : library.getAvaliableBooks()) {
				
				// Após achar o livro, alugar e encerrar função
				if (book.nome.equalsIgnoreCase(selected)) {

					library.rentBook(book.nome);
					sendToClient("Livro alugado com sucesso!" + 
					"<br>----------------------------------------<br>");
					return;
						
				}
			}

			// Se chegamos aqui, não encontramos nenhum livro
			sendToClient("Não foi possível encontrar nenhum livro disponível com este nome :(" +
				"<br>----------------------------------------<br>");

		}

		// Devolve um livro
		public static void commandDevolver(String command, String[] args) throws Exception {

			// Nome do comando
			String commandName = "DEVOLVER";

			// Não executar caso não seja esse o comando chamado
			if (!command.equalsIgnoreCase("/" + commandName)) { return; }
			executed = true;

			// Livro informado
			String selected = String.join(" ", args);

			// Procurando o livro informado
			for (Book book : library.getRentedBooks()) {
				
				// Após achar o livro, devolver e encerrar função
				if (book.nome.equalsIgnoreCase(selected)) {

					library.returnBook(book.nome);
					sendToClient("Livro devolvido com sucesso!" + 
					"<br>----------------------------------------<br>");
					return;
						
				}
			}

			// Se chegamos aqui, não encontramos nenhum livro
			sendToClient("Não foi possível encontrar nenhum livro alugado com este nome :(" +
				"<br>----------------------------------------<br>");

		}

		// Doa um livro
		public static void commandDoar(String command, String[] args) throws IOException {

			// Nome do comando
			String commandName = "DOAR";

			// Não executar caso não seja esse o comando chamado
			if (!command.equalsIgnoreCase("/" + commandName)) { return; }
			executed = true;

			String request = String.join(" ", args);
			args = request.split(",");
			
			// Cirando um livro com base nos valores informados
			Book donated = new Book();
			donated.nome = args[0].trim();
			donated.autor = args[1].trim();
			donated.genero = args[2].trim();

			try {

				// Garantindo que "exemplares" seja um valor numérico inteiro
				donated.exemplares = Integer.parseInt(args[3].trim());

			} catch (Exception e) {

				sendToClient("Desculpe, mas o campo <EXEMPLARES> precisa ser um valor numérico inteiro!" +
				"<br>----------------------------------------<br>");
				return;

			}

			library.registerBook(donated);
			
			sendToClient("Muito obrigado por doar o livro '" + donated.nome + "'!" + 
				"<br>----------------------------------------<br>");

		}

		// Comandos da Administração
		public static void commandAdmin(String command, String[] args) throws Exception {

			// Nome do comando
			String commandName = "ADMIN";

			// Não executar caso não seja esse o comando chamado
			if (!command.equalsIgnoreCase("/" + commandName)) { return; }
			executed = true;

			if (args[0].trim().equalsIgnoreCase("BACKUP")) {
				
				library.resetLibrary();

				sendToClient("Biblioteca restaurada." + 
				"<br>----------------------------------------<br>");
				
				return;
			}
		
			if (args[0].trim().equalsIgnoreCase("DELETE")) {
				
				String bookName = "";
				
				int i = 0;
				for (String arg : args) {

					if (i == 0) { i++; continue; }

					bookName += arg.trim() + " ";

				}

				bookName = bookName.trim(); 

				try {

					library.deleteBook(bookName.trim());

				} catch (Exception e) {

					sendToClient("Não foi possível deletar o livro '" + bookName + "'.<br>" + 
					"Certifique-se de que o nome esteja escrito EXATAMENTE como o nome do livro disponível" +
					"<br>----------------------------------------<br>");
					return;

				}

				sendToClient("Livro deletado com sucesso." + 
				"<br>----------------------------------------<br>");
				return;

			}
		}

		// Mandar output para Client
		public static void sendToClient(String msg) throws IOException {
			
			bufferedWriter.write(msg);
			bufferedWriter.newLine();
			bufferedWriter.flush();

		}
	}
