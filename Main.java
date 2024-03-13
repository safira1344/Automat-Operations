import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        Automato automato1 = Automato.importarAutomato("automato.jff");

        Automato automato2 = Automato.importarAutomato("automato2.jff");  

        // Automato newAutomato = Automato.getAutomatoUniao(automato1, automato2);
        // Automato.salvarAutomato(newAutomato, "automato-uniao.jff");

        Automato newAutomato = Automato.getAutomatoConcatenacao(automato1, automato2);
        Automato.exportarAutomato(newAutomato, "automato-concatenacao.jff");

        // automato.aplicarComplemento();
        // Automato.salvarAutomato(automato, "automato-complemento.jff");

        // automato.aplicarEstrela();
        // Automato.salvarAutomato(automato, "automato-estrela.jff");
    }
}