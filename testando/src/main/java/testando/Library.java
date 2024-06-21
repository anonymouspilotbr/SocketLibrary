package testando;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Library {

    // Arquivo Library.json representado por um JsonNode
    public static JsonNode fullJsonNode;   

    public Library() throws IOException {

        // Transformando arquivo Library.json em uma String
        BufferedReader jsonBufferedReader = new BufferedReader(new FileReader("Library.json"));
        String jsonFileContent = ""; // Todo o conteúdo do arquivo
        String jsonFileLine = ""; // Linha específica do arquivo

        // Adicionando cada linha do arquivo ao conteúdo final completo
        while ((jsonFileLine = jsonBufferedReader.readLine()) != null) { jsonFileContent += jsonFileLine; }
        jsonBufferedReader.close();

        // Tranformando String final em um JsonNode
        fullJsonNode = Parser.parser(jsonFileContent);
        
    }

    // Listagem de livros disponíveis para alugar
    public Book[] getAvaliableBooks() throws JsonProcessingException, IllegalArgumentException{

        JsonNode disponiveis = fullJsonNode.get("disponiveis");
        Book[] avaliableBooks = new Book[disponiveis.size()];

        int c = 0;
        for (JsonNode book : disponiveis) {
            
            avaliableBooks[c] = Parser.fromJSON(book, Book.class);
            c++;

        }

        return avaliableBooks;

    }

    // Listagens de livros alugados pelo cliente
    public Book[] getRentedBooks() throws JsonProcessingException, IllegalArgumentException {

        JsonNode alugados = fullJsonNode.get("alugados");
        Book[] rentedBooks = new Book[alugados.size()];

        int c = 0;
        for (JsonNode book : alugados) {
            
            rentedBooks[c] = Parser.fromJSON(book, Book.class);
            c++;

        }

        return rentedBooks;
    }

    // Tira um livro de "disponiveis" e coloca em "alugados"
    public void rentBook(String name) throws Exception {

        Book rented = null; // Começa sendo null
        for (Book book : getAvaliableBooks()) { // Procura algum livro com esse nome
            
            if (book.nome.equalsIgnoreCase(name)) { rented = book; }

        }
        
        // Se nenhum livro com esse nome foi encontrado, um erro será disparado
        if (rented == null) { throw new Exception(); }

        ArrayNode alugados = (ArrayNode) fullJsonNode.path("alugados");

        Book copy = null; // Representa o mesmo livro já alugado, caso esteja sendo alugado novamente
        for (Book book : getRentedBooks()) { // Procura se ele já tem esse livro alugado

            if (book.nome.equalsIgnoreCase(name)) { copy = book; }

        }

        if (copy == null) { // Se não houver nenhum livro igual lá, vamos adicioná-lo pela primeira vez

            alugados.add( // Colocando livro em "alugados"
                ((ObjectNode) Parser.toJson(rented))
                .put("exemplares", 1) // Setando exemplares para: 1
            ); 

        } else { // Se já houver um livro desse alugado, a gente apenas aumenta o número de exemplares

            Iterator<JsonNode> nodes = alugados.elements();

            while (nodes.hasNext()) {
                
                JsonNode node = nodes.next();
                if (node.get("nome").asText().equalsIgnoreCase(name)) {

                    ((ObjectNode)node)
                    .put("exemplares", 
                    node.get("exemplares").asInt() + 1); // Aumentando o nível de exemplares
                    
                }
            }
        }

        JsonNode disponiveis = fullJsonNode.path("disponiveis");
        Iterator<JsonNode> nodes = disponiveis.elements();
        
        // Procurando o livro em "disponiveis" para removê-lo ou diminuir seu número de exemplares
        while (nodes.hasNext()) {
            
            JsonNode node = nodes.next();
            if(node.get("nome").asText().equalsIgnoreCase(name)) { 
                
                // Se só tinha 1 exemplar sobrando, a gente tira o livro do JSON
                if (node.get("exemplares").asInt() <= 1) {

                    nodes.remove();

                } else { // Se tiver mais de 1, a gente só diminui em 1 o número de exemplares

                    ((ObjectNode)node)
                    .put("exemplares",
                    node.get("exemplares").asInt() - 1);

                }
            }
        }

        // Atualizando o JSON que representa o arquivo no geral
        ((ObjectNode) fullJsonNode).set("alugados", alugados);
        ((ObjectNode) fullJsonNode).set("disponiveis", disponiveis);

        // Deletando a versão antiga do arquivo
        Files.deleteIfExists(Path.of("Library.json"));

        // Criando a versão modificada e atualizada do arquivo após mudanças
        BufferedWriter writer = new BufferedWriter(new FileWriter("Library.json"));
        writer.write(Parser.stringfy(fullJsonNode));
        writer.close();
    
    }

    // Devolvendo livro que antes estava alugado (mesmo processo que rentBook(), mas ao contrário)
    public void returnBook(String name) throws Exception {

        Book returned = null;
        for (Book book : getRentedBooks()) {
            
            if (book.nome.equals(name)) { returned = book; }

        }
        
        if (returned == null) { throw new Exception(); }
        
        ArrayNode disponiveis = (ArrayNode) fullJsonNode.path("disponiveis");

        Book copy = null; // Representa o mesmo livro já disponível, caso ele ainda tenha 1 ou mais exemplares lá sobrando
        for (Book book : getAvaliableBooks()) { // Procura se ele livro tem um exemplar disponível

            if (book.nome.equalsIgnoreCase(name)) { copy = book; }

        }

        if (copy == null) { // Se não houver nenhum livro igual lá, vamos adicioná-lo de volta

            disponiveis.add( // Colocando livro em "disponiveis"
                ((ObjectNode) Parser.toJson(returned))
                .put("exemplares", 1) // Setando exemplares para: 1
            ); 

        } else { // Se já houver um livro desse disponível, a gente apenas aumenta o número de exemplares

            Iterator<JsonNode> nodes = disponiveis.elements();

            while (nodes.hasNext()) {
                
                JsonNode node = nodes.next();
                if (node.get("nome").asText().equalsIgnoreCase(name)) {

                    ((ObjectNode)node)
                    .put("exemplares", 
                    node.get("exemplares").asInt() + 1); // Aumentando o nível de exemplares
                    
                }
            }
        }

        JsonNode alugados = fullJsonNode.path("alugados");
        Iterator<JsonNode> nodes = alugados.elements();
        
        // Procurando o livro em "alugados" para removê-lo ou diminuir seu número de exemplares
        while (nodes.hasNext()) {
            
            JsonNode node = nodes.next();
            if(node.get("nome").asText().equalsIgnoreCase(name)) { 
                
                // Se só tinha 1 exemplar sobrando, a gente tira o livro do JSON
                if (node.get("exemplares").asInt() <= 1) {

                    nodes.remove();

                } else { // Se tiver mais de 1, a gente só diminui em 1 o número de exemplares

                    ((ObjectNode)node)
                    .put("exemplares",
                    node.get("exemplares").asInt() - 1);

                }
            }
            
        }

        ((ObjectNode) fullJsonNode).set("disponiveis", disponiveis);
        ((ObjectNode) fullJsonNode).set("alugados", alugados);

        Files.deleteIfExists(Path.of("Library.json"));

        BufferedWriter writer = new BufferedWriter(new FileWriter("Library.json"));
        writer.write(Parser.stringfy(fullJsonNode));
        writer.close();
    
    }
    
    // Adicionando um novo livro à biblioteca
    public void registerBook(Book book) throws IOException {
        
        ArrayNode disponiveis = (ArrayNode) fullJsonNode.path("disponiveis");

        disponiveis.add(Parser.toJson(book));

        ((ObjectNode) fullJsonNode).set("disponiveis", disponiveis);

        Files.deleteIfExists(Path.of("Library.json"));

        BufferedWriter writer = new BufferedWriter(new FileWriter("Library.json"));
        writer.write(Parser.stringfy(fullJsonNode));
        writer.close();
    
    }

    // Removendo livro da biblioteca
    public void deleteBook(String name) throws Exception {

        Book deleted = null;
        for (Book book : getAvaliableBooks()) {
            
            if (book.nome.equals(name)) { deleted = book; }

        }
        
        if (deleted == null) { throw new Exception(); }
        
        JsonNode disponiveis = fullJsonNode.path("disponiveis");
        Iterator<JsonNode> elements = disponiveis.elements();
        
        while (elements.hasNext()) {
            
            JsonNode next = elements.next();
            if(next.get("nome").asText().equals(name)) { 
                elements.remove();
            }
            
        }
        ((ObjectNode) fullJsonNode).set("disponiveis", disponiveis);

        Files.deleteIfExists(Path.of("Library.json"));

        BufferedWriter writer = new BufferedWriter(new FileWriter("Library.json"));
        writer.write(Parser.stringfy(fullJsonNode));
        writer.close();
 
    }

    // Reseta Library.json para o modelo do Library(Backup).json
    public void resetLibrary() throws IOException{

        BufferedReader jsonBufferedReader = new BufferedReader(new FileReader("Library(Backup).json"));
        String jsonFileContent = "";
        String jsonFileLine = "";
        while ((jsonFileLine = jsonBufferedReader.readLine()) != null) { jsonFileContent += jsonFileLine; }
        jsonBufferedReader.close();

        JsonNode newLibrary = Parser.parser(jsonFileContent);

        Files.deleteIfExists(Path.of("Library.json"));

        BufferedWriter writer = new BufferedWriter(new FileWriter("Library.json"));
        writer.write(Parser.stringfy(newLibrary));
        writer.close();

        fullJsonNode = newLibrary;
 
    }
}
