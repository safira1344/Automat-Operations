import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        File file = new File("automato.jff");

        Automato automato = Automato.extrairAutomato(file);

        automato.aplicarComplemento();

        Automato.salvarAutomato(automato);
    }
}