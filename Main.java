import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        Automato automato1 = Automato.extrairAutomato("automato.jff");

        Automato automato2 = Automato.extrairAutomato("automato2.jff");  

        // Automato newAutomato = Automato.getAutomatoUniao(automato1, automato2);
        // Automato.salvarAutomato(newAutomato, "automato-uniao.jff");

        Automato newAutomato = Automato.getAutomatoConcatenacao(automato1, automato2);
        Automato.salvarAutomato(newAutomato, "automato-concatenacao.jff");

        // automato.aplicarComplemento();
        // Automato.salvarAutomato(automato, "automato-complemento.jff");

        // automato.aplicarEstrela();
        // Automato.salvarAutomato(automato, "automato-estrela.jff");
    }
}