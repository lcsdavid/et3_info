package lucasdavid.xml;

import lucasdavid.xml.element.AbstractElement;
import lucasdavid.xml.element.Element;
import lucasdavid.xml.element.EmptyElement;
import lucasdavid.xml.element.SimpleElement;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.util.*;
import java.util.function.Predicate;
import javax.xml.stream.*;
import javax.xml.stream.events.XMLEvent;

public class XMLParser {
    private InputStream toParseFile;

    private List<AbstractElement> parseBuffer;

    /**
     * Default constructor.
     * Allocates fields with empty values.
     */
    public XMLParser() {
        toParseFile = null;

        parseBuffer = new ArrayList<>();
    }

    /**
     * Constructor.
     *
     * @param toParseFile source file
     */
    public XMLParser(@NotNull InputStream toParseFile) {
        this.toParseFile = toParseFile;

        parseBuffer = new ArrayList<>();
    }

    /**
     * Returns {@link XMLParser#toParseFile}
     *
     * @return {@link XMLParser#toParseFile}
     */
    public InputStream getToParseFile() {
        return toParseFile;
    }

    /**
     * Sets {@link XMLParser#toParseFile}.
     *
     * @param toParseFile XML source file
     */
    public void setToParseFile(@NotNull InputStream toParseFile) {
        this.toParseFile = toParseFile;
    }

    /**
     * Parses {@link XMLParser#toParseFile}
     * XML stream parse putting all data in a {@link List}.
     * Which means this is a semi-stream-DOM model.
     *
     * @throws FileNotFoundException if parsing file path is invalid
     * @throws XMLStreamException    ...
     */
    public void parse() throws FileNotFoundException, XMLStreamException {
        XMLStreamReader streamReader = XMLInputFactory.newInstance().createXMLStreamReader(toParseFile);
        parseBuffer.clear();

        Stack<Element> elementStack = new Stack<>();
        int eventType;
        while (streamReader.hasNext()) {
            eventType = streamReader.next();
            /* Premier cas triggered dans la boucle */
            if (eventType == XMLEvent.START_ELEMENT) {
                /* Récolte des premières infos */
                String name = streamReader.getName().toString();
                Map<String, String> attributes = new HashMap<>();
                for (int i = 0; i < streamReader.getAttributeCount(); i++)
                    attributes.put(streamReader.getAttributeName(i).toString(), streamReader.getAttributeValue(i));

                /* Instanciation de  <? extends AbstractElement>} */
                AbstractElement abstractElement;
                eventType = streamReader.next();
                if (streamReader.hasText() && !streamReader.getText().trim().isEmpty()) {
                    abstractElement = new SimpleElement(name, attributes, streamReader.getText().trim());
                    while (eventType != XMLEvent.END_ELEMENT)
                        eventType = streamReader.next();
                } else if (eventType == XMLEvent.END_ELEMENT)
                    abstractElement = new EmptyElement(name, attributes);
                else
                    abstractElement = new Element(name, attributes);

                /* Coeur de la boucle (gère le modèle arboricole des Element(s) avec une Pile et le buffer final */
                if (parseBuffer.isEmpty() || parseBuffer.get(parseBuffer.size() - 1).isLock()) {
                    parseBuffer.add(abstractElement);
                    if (abstractElement instanceof Element)
                        elementStack.push((Element) abstractElement);
                } else {
                    if (!elementStack.empty())
                        elementStack.peek().addSubElement(abstractElement);
                    if (abstractElement instanceof Element)
                        elementStack.push((Element) abstractElement);
                }
            } else if (eventType == XMLEvent.END_ELEMENT) /* Second cas */
                    elementStack.pop().lock(); /* Autre partie du coeur arboricole (pour que ça boucle correctement) */
        }
    }

    /**
     * Applies a filter to {@link #parseBuffer}.
     *
     * @param filter the filter which determines the query
     * @return {@link #parseBuffer} copy filtered
     */
    public List<AbstractElement> queryElements(@NotNull Predicate<AbstractElement> filter) {
        List<AbstractElement> result = new ArrayList<>();
        for (AbstractElement abstractElement : parseBuffer)
            result.addAll(abstractElement.query(filter));
        return result;
    }

    /* Exemples d'utilisations de queryElements(Predicate) */

    /**
     * Non-generic version of {@link #queryElements(Predicate)}.
     * Here {@link AbstractElement} are filtered with {@link AbstractElement#name}.
     *
     * @param name the name to filter
     * @return {@link #parseBuffer} copy filtered
     */
    public List<AbstractElement> queryElementsByName(@NotNull String name) {
        return queryElements(element -> element.getName().equals(name));
    }

    /**
     * Non-generic version of {@link #queryElements(Predicate)}.
     * Here {@link AbstractElement} are filtered with {@link AbstractElement#attributes}.
     *
     * @param attribute the name to filter
     * @return {@link #parseBuffer} copy filtered
     */
    public List<AbstractElement> queryElementsByAttribute(@NotNull Map.Entry<String, String> attribute) {
        return queryElements(element -> {
            if (element.getAttribute(attribute.getKey()) == null) return false;
            else return element.getAttribute(attribute.getKey()).equals(attribute.getValue());
        });
    }
}
