package com.hyena.framework.samples.animator.parser.utils;

import com.hyena.framework.samples.animator.parser.node.MapNodeLine;
import com.hyena.framework.samples.animator.parser.node.MapNodeLayer;
import com.hyena.framework.samples.animator.parser.node.MapNode;
import com.hyena.framework.samples.animator.parser.node.MapNodeSprite;
import com.hyena.framework.samples.animator.parser.node.MapNodeText;
import com.hyena.framework.utils.MathUtils;

import org.apache.http.protocol.HTTP;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by yangzc on 16/4/21.
 */
public class XMLUtils {

    public static void parse(String xml) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(xml.getBytes(HTTP.UTF_8)));
            Element rootElement = document.getDocumentElement();
            rootElement.getAttribute("");

            NodeList layerList = rootElement.getElementsByTagName("layer");
            for (int i = 0; i < layerList.getLength(); i++) {
                Node layer = layerList.item(i);
                parseLayer(layer);
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static MapNodeLayer parseLayer(Node layer) {
        MapNodeLayer mapLayer = new MapNodeLayer();
        mapLayer.setZIndex(MathUtils.valueOfInt(getAttributeValue(layer, "zindex")));
        NodeList elementNode = layer.getChildNodes();
        for (int i = 0; i < elementNode.getLength(); i++) {
            Node element = elementNode.item(i);
            String nodeName = element.getNodeName();
            MapNode mapNode = null;
            if ("node".equals(nodeName)) {
                mapNode = parseSprite(element);
            } else if ("text".equals(nodeName)) {
                mapNode = parseText(element);
            } else if ("line".equals(nodeName)) {
                mapNode = parseLine(element);
            }
            if (mapNode != null) {
                mapLayer.addNode(mapNode);
            }
        }
        return mapLayer;
    }

    private static MapNodeSprite parseSprite(Node node) {
        return null;
    }

    private static MapNodeText parseText(Node node) {
        return null;
    }

    private static MapNodeLine parseLine(Node node) {
        return null;
    }

    /**
     * 获得属性值
     * @param element  节点
     * @param attrName 属性名
     * @return
     */
    public static String getAttributeValue(Element element, String attrName) {
        if (element == null)
            return null;
        return element.getAttribute(attrName);
    }

    /**
     * 获得属性值
     * @param node     节点
     * @param attrName 属性名
     * @return
     */
    public static String getAttributeValue(Node node, String attrName) {
        if (node == null)
            return null;
        if (node.getAttributes() == null || node.getAttributes().getLength() == 0)
            return null;
        Node attrNode = node.getAttributes().getNamedItem(attrName);
        if (attrNode == null)
            return null;
        return attrNode.getNodeValue();
    }
}
