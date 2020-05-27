package server;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ServerConfigUtil {
    private final String filename;
    
    public ServerConfigUtil(String file) {
        this.filename = file;
    }

    /**
     * 通过名字返回xml配置中int类型的值
     *
     * @param name
     * @return
     * @throws Exception
     */
    public int getIntValue(String name) throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Element element = builder.parse(new File(filename)).getDocumentElement();
        NodeList children = element.getChildNodes();

        for (int a = 0; a < children.getLength(); ++a) {
            Node child = children.item(a);
            if (child instanceof Element) {
                if (name.equalsIgnoreCase(child.getNodeName())) {
                    return getIntAttribute(child);
                }
            }
        }
        throw new Exception("can't find node named : " + name);
    }

    /**
     * 在节点中返回int类型的value
     *
     * @param node
     * @return
     */
    private int getIntAttribute(Node node) {
        String intValue = null;
        NamedNodeMap attributes = node.getAttributes();
        for (int j = 0; j < attributes.getLength(); ++j) {
            Node attribute = attributes.item(j);
            String attName = attribute.getNodeName();
            String attValue = attribute.getNodeValue();
            if (attName.equals("value")) {
                intValue = attValue;
                break;
            }
        }
        return Integer.parseInt(intValue);
    }

    /**
     * 通过名字返回xml配置中String类型的值
     *
     * @param name
     * @return
     * @throws Exception
     */
    public String getStringValue(String name) throws Exception {
        String string = getStringValueEmpty(name);
        if (string == null || string.isEmpty()) {
            throw new Exception("can't find node named : " + name);
        }
        return string;
    }

    /**
     * 通过名字返回xml配置中String类型的值
     *
     * @param name
     * @return
     * @throws Exception
     */
    public String getStringValueEmpty(String name) throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Element element = builder.parse(new File(filename)).getDocumentElement();
        NodeList children = element.getChildNodes();

        for (int a = 0; a < children.getLength(); ++a) {
            Node child = children.item(a);
            if (child instanceof Element) {
                if (name.equalsIgnoreCase(child.getNodeName())) {
                    return getStringAttribute(child);
                }
            }
        }
        return "";
    }

    /**
     * 在节点中返回String类型的value
     *
     * @param node
     * @return
     */
    private String getStringAttribute(Node node) {
        NamedNodeMap attributes = node.getAttributes();
        for (int j = 0; j < attributes.getLength(); ++j) {
            Node attribute = attributes.item(j);
            String attName = attribute.getNodeName();
            String attValue = attribute.getNodeValue();
            if (attName.equals("value")) {
                return attValue;
            }
        }
        return null;
    }
}