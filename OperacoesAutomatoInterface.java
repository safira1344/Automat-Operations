public interface OperacoesAutomatoInterface {

    // 1. Devera tratar de uma ferramenta que importe dois AFD/N no formato .jff e realize a operacao regular desejada, sendo ela: uniao;
    public void aplicarUniao(Automato automato1, Automato automato2);

    // 2. Devera tratar de uma ferramenta que importe dois AFD/N no formato .jff e realize a operacao regular desejada, sendo ela: interseccao;
    public void aplicarInterseccao(Automato automato1, Automato automato2);

    // 3. Devera tratar de uma ferramenta que importe dois AFD/N no formato .jff e realize a operacao regular desejada, sendo ela: diferenca;
    public void aplicarConcatenacao(Automato automato1, Automato automato2);

    // 4.  Importara um AFD/N no formato .jff e realizara a operacao desejada, sendo ela: complemento ou estrela;
    public void aplicarEstrela();
    public void aplicarComplemento();

    // 5. Importara um AFN no formato .jff e gerara o AFD equivalente, exportando-o para um novo arquivo .jff;
    public void converterAfnParaAfd();

    // 6. Importara um AFD no formato .jff e gerara o AFN equivalente, exportando-o para um novo arquivo .jff;
    public void aplicarMinimizacao();
}