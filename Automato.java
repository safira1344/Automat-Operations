import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

public class Automato {
    public List<Estado> estados;
    public List<Transicao> transicoes;

    public Automato() {
        this.estados = new ArrayList<Estado>();
        this.transicoes = new ArrayList<Transicao>();
    }

    public void aplicarComplemento() {
        for (Estado estado : this.estados) {
            estado.isFinal = !estado.isFinal;
        }
    }

    public void aplicarEstrela() {
        Estado estadoInicial = new Estado();

        for (Estado estado : this.estados) {
            if (estado.isInitial) {
                estadoInicial = estado;
                break;
            }
        }

        estadoInicial.isInitial = false;

        Estado novoEstado = new Estado();
        novoEstado.isInitial = true;
        novoEstado.isFinal = true;
        novoEstado.id = Integer.toString(this.estados.size());
        novoEstado.name = "q" + novoEstado.id;
        novoEstado.x = -100;
        novoEstado.y = 100;

        this.estados.add(novoEstado);

        for (Estado estado : this.estados) {
            if (estado.isFinal) {
                Transicao novaTransicao = new Transicao();
                novaTransicao.from = Integer.parseInt(estado.id);
                novaTransicao.to = Integer.parseInt(estadoInicial.id);
                novaTransicao.read = "";

                this.transicoes.add(novaTransicao);
            }
        }
    }

    public static Automato getAutomatoUniao(Automato automato1, Automato automato2) {
        Automato novoAutomato = new Automato();

        Estado novoEstado = new Estado("0", "q0", -100f, 100f, true, false);

        novoAutomato.estados.add(novoEstado);

        for (Estado estado : automato1.estados) {
            estado.id = Integer.toString(Integer.parseInt(estado.id) + 1);
            estado.name = "q" + estado.id;
            novoAutomato.estados.add(estado);

            if (estado.isInitial) {
                estado.isInitial = false;
                Transicao transicao = new Transicao(Integer.parseInt(novoEstado.id), Integer.parseInt(estado.id), "");
                novoAutomato.transicoes.add(transicao);
            }
        }

        for (Estado estado : automato2.estados) {
            estado.id = Integer.toString((Integer.parseInt(estado.id) + 1) + automato1.estados.size());
            estado.name = "q" + estado.id;
            estado.y = estado.y + 200;
            novoAutomato.estados.add(estado);

            if (estado.isInitial) {
                estado.isInitial = false;
                Transicao transicao = new Transicao(Integer.parseInt(novoEstado.id), Integer.parseInt(estado.id), "");
                novoAutomato.transicoes.add(transicao);
            }
        }

        for (Transicao transicao : automato1.transicoes) {
            transicao.from = transicao.from + 1;
            transicao.to = transicao.to + 1;
            novoAutomato.transicoes.add(transicao);
        }

        for (Transicao transicao : automato2.transicoes) {
            transicao.from = transicao.from + automato1.estados.size() + 1;
            transicao.to = transicao.to + automato1.estados.size() + 1;
            novoAutomato.transicoes.add(transicao);
        }

        return novoAutomato;
    }

    public static Automato getAutomatoConcatenacao(Automato automato1, Automato automato2) {
        Automato novoAutomato = new Automato();

        List<Integer> estadosFinais = new ArrayList<Integer>();

        // percorre a lista de estados do automato1 modificando os IDS e adiciona os
        // estados ao novo automato
        for (Estado estado : automato1.estados) {
            novoAutomato.estados.add(estado);
        }

        // percorre a lista de estados do automato 2 modificando os IDS e adiciona os
        // estados ao novo automato
        for (Estado estado : automato2.estados) {
            estado.id = Integer.toString((Integer.parseInt(estado.id)) + automato1.estados.size());
            estado.name = "q" + estado.id;
            estado.y = estado.y + 200;
            novoAutomato.estados.add(estado);
        }

        // percorre a lista de transições do automato 1 aplicando as alterações de ids
        for (Transicao transicao : automato1.transicoes) {
            novoAutomato.transicoes.add(transicao);
        }

        // percorre a lista de transições do automato 2 aplicando as alterações de ids
        for (Transicao transicao : automato2.transicoes) {
            transicao.from = transicao.from + automato1.estados.size();
            transicao.to = transicao.to + automato1.estados.size();
            novoAutomato.transicoes.add(transicao);
        }

        for (Estado estado : automato1.estados) {
            // retira a tag de estado final dos estados do automato 1 e salva na lista
            // finalState
            if (estado.isFinal) {
                estadosFinais.add(Integer.parseInt(estado.id));
                estado.isFinal = false;
            }
        }

        for (Estado estado : automato2.estados) {
            if (estado.isInitial) {
                // para cada estado final do automato 1, cria uma transição para o estado
                // inicial do automato 2
                for (Integer estadoFinal : estadosFinais) {
                    Transicao transicao = new Transicao();

                    transicao.from = estadoFinal;
                    transicao.to = Integer.parseInt(estado.id);
                    transicao.read = "";

                    novoAutomato.transicoes.add(transicao);
                }
                estado.isInitial = false;
            }
        }

        return novoAutomato;
    }

    public static void exportarAutomato(Automato automato, String diretorio) throws IOException {
        File arquivo = new File(diretorio);
        FileWriter writer = new FileWriter(arquivo);

        writer.write(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><!--Created with JFLAP 6.4. By Safi--><structure>&#13;\n");
        writer.write("\t<type>fa</type>&#13;\n");
        writer.write("\t<automaton>&#13;\n");
        writer.write("\t\t<!--The list of states.-->&#13;\n");

        // Escreve os estados
        for (Estado estado : automato.estados) {
            writer.write("\t\t<state id=\"" + estado.id + "\" name=\"" + estado.name + "\">&#13;\n");
            writer.write("\t\t\t<x>" + estado.x + "</x>&#13;\n");
            writer.write("\t\t\t<y>" + estado.y + "</y>&#13;\n");
            if (estado.isInitial) {
                writer.write("\t\t\t<initial/>&#13;\n");
            }
            if (estado.isFinal) {
                writer.write("\t\t\t<final/>&#13;\n");
            }
            writer.write("\t\t</state>&#13;\n");
        }

        writer.write("\t\t<!--The list of transitions.-->&#13;\n");

        // Escreve as transições
        for (Transicao transicao : automato.transicoes) {
            writer.write("\t\t<transition>&#13;\n");
            writer.write("\t\t\t<from>" + transicao.from + "</from>&#13;\n");
            writer.write("\t\t\t<to>" + transicao.to + "</to>&#13;\n");
            // Caso a transição seja epsilon
            if (transicao.read.isBlank()) {
                writer.write("\t\t\t<read/>\n");
            } else {
                writer.write("\t\t\t<read>" + transicao.read + "</read>&#13;\n");
            }
            writer.write("\t\t</transition>&#13;\n");
        }

        writer.write("\t</automaton>&#13;\n");
        writer.write("</structure>");
        writer.close();
    }

    public static Automato importarAutomato(String diretorio) throws IOException {
        File arquivo = new File(diretorio);

        Automato automato = new Automato();
        automato.estados = new ArrayList<>();
        automato.transicoes = new ArrayList<>();

        Scanner scanner = new Scanner(arquivo);
        while (scanner.hasNextLine()) {
            String linha = scanner.nextLine();

            // Quando encontra Inicio de Estado
            if (linha.contains("\t\t<state ")) {
                Estado estado = new Estado();
                int inicioId = linha.indexOf("=\"") + 2;
                int fimId = linha.indexOf("\" ");
                String id = linha.substring(inicioId, fimId);

                estado.id = id;

                linha = linha.substring(fimId + 2);

                int inicioName = linha.indexOf("=\"") + 2;
                int fimName = linha.indexOf("\">");
                String name = linha.substring(inicioName, fimName);

                estado.name = name;

                linha = scanner.nextLine();

                int inicioX = linha.indexOf(">") + 1;
                int fimX = linha.indexOf("</");
                String x = linha.substring(inicioX, fimX);

                estado.x = Float.parseFloat(x);

                linha = scanner.nextLine();

                int inicioY = linha.indexOf(">") + 1;
                int fimY = linha.indexOf("</");
                String y = linha.substring(inicioY, fimY);

                estado.y = Float.parseFloat(y);

                // Se chegou no final do estado
                while (!linha.contains("\t\t</state>")) {
                    // Se é estado inicial
                    if (linha.contains("<initial/>")) {
                        estado.isInitial = true;
                    }
                    // Se é estado final
                    if (linha.contains("<final/>")) {
                        estado.isFinal = true;
                    }
                    linha = scanner.nextLine();
                }

                automato.estados.add(estado);

            } else if (linha.contains("\t\t<transition")) {
                Transicao transicao = new Transicao();

                linha = scanner.nextLine();

                // From
                int inicioFrom = linha.indexOf(">") + 1;
                int fimFrom = linha.indexOf("</");
                String from = linha.substring(inicioFrom, fimFrom);

                transicao.from = Integer.parseInt(from);

                linha = scanner.nextLine();

                // To
                int inicioTo = linha.indexOf(">") + 1;
                int fimTo = linha.indexOf("</");
                String to = linha.substring(inicioTo, fimTo);

                transicao.to = Integer.parseInt(to);

                linha = scanner.nextLine();

                // Read
                if (linha.contains("<read/>")) {
                    transicao.read = "";
                } else {
                    int inicioRead = linha.indexOf(">") + 1;
                    int fimRead = linha.indexOf("</");
                    String read = linha.substring(inicioRead, fimRead);

                    transicao.read = read;
                }

                automato.transicoes.add(transicao);

                linha = scanner.nextLine();
            }

        }
        scanner.close();
        return automato;
    }

    public static Automato importarAutomatoXML(String caminho) throws Exception {
        File arquivo = new File(caminho);
        Automato automato = new Automato();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(arquivo);
        doc.getDocumentElement().normalize();

        NodeList nList = doc.getElementsByTagName("state");

        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                String id = eElement.getAttribute("id");
                String name = eElement.getAttribute("name");
                float x = Float.parseFloat(eElement.getElementsByTagName("x").item(0).getTextContent());
                float y = Float.parseFloat(eElement.getElementsByTagName("y").item(0).getTextContent());
                boolean isInitial = eElement.getElementsByTagName("initial").getLength() > 0;
                boolean isFinal = eElement.getElementsByTagName("final").getLength() > 0;

                Estado estado = new Estado();
                estado.id = id;
                estado.name = name;
                estado.x = x;
                estado.y = y;
                estado.isInitial = isInitial;
                estado.isFinal = isFinal;

                automato.estados.add(estado);
            }
        }

        NodeList tList = doc.getElementsByTagName("transition");

        for (int temp = 0; temp < tList.getLength(); temp++) {
            Node tNode = tList.item(temp);

            if (tNode.getNodeType() == Node.ELEMENT_NODE) {
                Element tElement = (Element) tNode;
                int from = Integer.parseInt(tElement.getElementsByTagName("from").item(0).getTextContent());
                int to = Integer.parseInt(tElement.getElementsByTagName("to").item(0).getTextContent());
                String read = tElement.getElementsByTagName("read").item(0).getTextContent();

                Transicao transicao = new Transicao();
                transicao.from = from;
                transicao.to = to;
                transicao.read = read;

                automato.transicoes.add(transicao);
            }
        }

        return automato;
    }

    public static void exportarAutomatoXML(Automato automato) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();

        Element rootElement = doc.createElement("structure");
        doc.appendChild(rootElement);

        Element type = doc.createElement("type");
        type.appendChild(doc.createTextNode("fa"));
        rootElement.appendChild(type);

        Element automatoNovo = doc.createElement("automaton");
        rootElement.appendChild(automatoNovo);

        for (Estado estado : automato.estados) {
            Element estadoElement = doc.createElement("state");
            estadoElement.setAttribute("id", estado.id);
            estadoElement.setAttribute("name", estado.name);

            Element x = doc.createElement("x");
            x.appendChild(doc.createTextNode(Float.toString(estado.x)));
            estadoElement.appendChild(x);

            Element y = doc.createElement("y");
            y.appendChild(doc.createTextNode(Float.toString(estado.y)));
            estadoElement.appendChild(y);

            if (estado.isInitial) {
                Element initial = doc.createElement("initial");
                estadoElement.appendChild(initial);
            }

            if (estado.isFinal) {
                Element finalState = doc.createElement("final");
                estadoElement.appendChild(finalState);
            }

            automatoNovo.appendChild(estadoElement);
        }

        for (Transicao transicao : automato.transicoes) {
            Element transicaoElement = doc.createElement("transition");

            Element from = doc.createElement("from");
            from.appendChild(doc.createTextNode(Integer.toString(transicao.from)));
            transicaoElement.appendChild(from);

            Element to = doc.createElement("to");
            to.appendChild(doc.createTextNode(Integer.toString(transicao.to)));
            transicaoElement.appendChild(to);

            Element read = doc.createElement("read");
            read.appendChild(doc.createTextNode(transicao.read));
            transicaoElement.appendChild(read);

            automatoNovo.appendChild(transicaoElement);
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File("automatoNovo.jff"));
        transformer.transform(source, result);
    }
}