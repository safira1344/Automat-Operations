import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        Automato automato = Automato.extrairAutomato("automato.jff");

        automato.aplicarComplemento();
        Automato.salvarAutomato(automato, "automato-complemento.jff");

        automato.aplicarEstrela();
        Automato.salvarAutomato(automato, "automato-estrela.jff");
    }
}