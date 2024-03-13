import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        Automato automato1 = Automato.importarAutomato("automato1.jff");

        Automato automato2 = Automato.importarAutomato("automato2.jff");  

        Automato newAutomato = new Automato();

        newAutomato.aplicarConcatenacao(automato1, automato2);

        Automato.exportarAutomato(newAutomato, "automato-concatenacao.jff");
    }
}