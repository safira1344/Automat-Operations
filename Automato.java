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
    public List<State> states;
    public List<Transition> transitions;

    public void aplicarComplemento() {
        for (State state : this.states) {
            state.isFinal = !state.isFinal;
        }
    }

    public void aplicarEstrela() {
        
    }

    public static void salvarAutomato(Automato automato) throws IOException {
        File file = new File("automatoNovo.jff");
        FileWriter writer = new FileWriter(file);

        writer.write(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><!--Created with JFLAP 6.4. By Safi--><structure>&#13;\n");
        writer.write("\t<type>fa</type>&#13;\n");
        writer.write("\t<automaton>&#13;\n");
        writer.write("\t\t<!--The list of states.-->&#13;\n");

        // Escreve os estados
        for (State state : automato.states) {
            writer.write("\t\t<state id=\"" + state.id + "\" name=\"" + state.name + "\">&#13;\n");
            writer.write("\t\t\t<x>" + state.x + "</x>&#13;\n");
            writer.write("\t\t\t<y>" + state.y + "</y>&#13;\n");
            if (state.isInitial) {
                writer.write("\t\t\t<initial/>&#13;\n");
            }
            if (state.isFinal) {
                writer.write("\t\t\t<final/>&#13;\n");
            }
            writer.write("\t\t</state>&#13;\n");
        }

        writer.write("\t\t<!--The list of transitions.-->&#13;\n");

        // Escreve as transições
        for (Transition transition : automato.transitions) {
            writer.write("\t\t<transition>&#13;\n");
            writer.write("\t\t\t<from>" + transition.from + "</from>&#13;\n");
            writer.write("\t\t\t<to>" + transition.to + "</to>&#13;\n");
            // Caso a transição seja epsilon
            if (transition.read.isBlank()) {
                writer.write("\t\t\t<read/>\n");
            } else {
                writer.write("\t\t\t<read>" + transition.read + "</read>&#13;\n");
            }
            writer.write("\t\t</transition>&#13;\n");
        }

        writer.write("\t</automaton>&#13;\n");
        writer.write("</structure>");
        writer.close();
    }

    public static Automato extrairAutomato(File arquivo) throws IOException {
        Automato automaton = new Automato();
        automaton.states = new ArrayList<>();
        automaton.transitions = new ArrayList<>();

        Scanner scanner = new Scanner(arquivo);
        while (scanner.hasNextLine()) {
            String linha = scanner.nextLine();

            // Quando encontra Inicio de Estado
            if (linha.contains("\t\t<state ")) {
                State state = new State();
                int inicioId = linha.indexOf("=\"") + 2;
                int fimId = linha.indexOf("\" ");
                String id = linha.substring(inicioId, fimId);

                state.id = id;

                linha = linha.substring(fimId + 2);

                int inicioName = linha.indexOf("=\"") + 2;
                int fimName = linha.indexOf("\">");
                String name = linha.substring(inicioName, fimName);

                state.name = name;

                linha = scanner.nextLine();

                int inicioX = linha.indexOf(">") + 1;
                int fimX = linha.indexOf("</");
                String x = linha.substring(inicioX, fimX);

                state.x = Float.parseFloat(x);

                linha = scanner.nextLine();

                int inicioY = linha.indexOf(">") + 1;
                int fimY = linha.indexOf("</");
                String y = linha.substring(inicioY, fimY);

                state.y = Float.parseFloat(y);

                // Se chegou no final do estado
                while (!linha.contains("\t\t</state>")) {
                    // Se é estado inicial
                    if (linha.contains("<initial/>")) {
                        state.isInitial = true;
                    }
                    // Se é estado final
                    if (linha.contains("<final/>")) {
                        state.isFinal = true;
                    }
                    linha = scanner.nextLine();
                }

                automaton.states.add(state);

            } else if (linha.contains("\t\t<transition")) {
                Transition transicao = new Transition();

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

                automaton.transitions.add(transicao);

                linha = scanner.nextLine();
            }

        }
        scanner.close();
        return automaton;
    }

    public static Automato extrairAutomatoXML(File inputFile) throws Exception {
        Automato automaton = new Automato();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(inputFile);
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

                State state = new State();
                state.id = id;
                state.name = name;
                state.x = x;
                state.y = y;
                state.isInitial = isInitial;
                state.isFinal = isFinal;

                automaton.states.add(state);
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

                Transition transition = new Transition();
                transition.from = from;
                transition.to = to;
                transition.read = read;

                automaton.transitions.add(transition);
            }
        }

        return automaton;
    }

    public static void salvarAutomatoXML(Automato automato) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();

        Element rootElement = doc.createElement("structure");
        doc.appendChild(rootElement);

        Element type = doc.createElement("type");
        type.appendChild(doc.createTextNode("fa"));
        rootElement.appendChild(type);

        Element automaton = doc.createElement("automaton");
        rootElement.appendChild(automaton);

        for (State state : automato.states) {
            Element stateElement = doc.createElement("state");
            stateElement.setAttribute("id", state.id);
            stateElement.setAttribute("name", state.name);

            Element x = doc.createElement("x");
            x.appendChild(doc.createTextNode(Float.toString(state.x)));
            stateElement.appendChild(x);

            Element y = doc.createElement("y");
            y.appendChild(doc.createTextNode(Float.toString(state.y)));
            stateElement.appendChild(y);

            if (state.isInitial) {
                Element initial = doc.createElement("initial");
                stateElement.appendChild(initial);
            }

            if (state.isFinal) {
                Element finalState = doc.createElement("final");
                stateElement.appendChild(finalState);
            }

            automaton.appendChild(stateElement);
        }

        for (Transition transition : automato.transitions) {
            Element transitionElement = doc.createElement("transition");

            Element from = doc.createElement("from");
            from.appendChild(doc.createTextNode(Integer.toString(transition.from)));
            transitionElement.appendChild(from);

            Element to = doc.createElement("to");
            to.appendChild(doc.createTextNode(Integer.toString(transition.to)));
            transitionElement.appendChild(to);

            Element read = doc.createElement("read");
            read.appendChild(doc.createTextNode(transition.read));
            transitionElement.appendChild(read);

            automaton.appendChild(transitionElement);
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File("automatoNovo.jff"));
        transformer.transform(source, result);
    }
}