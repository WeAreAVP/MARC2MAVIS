/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mavis3;

import java.io.File;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author rimsha@geeks
 */
public class XMLParser {

    private File xmlFile = null;
    private Document doc = null;

    public XMLParser(File xmlFile) {
        this.xmlFile = xmlFile;
    }

    public Map parseXML() {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            dBuilder.setErrorHandler(new SimpleErrorHandler());
//            File fXmlFile = new File(this.xmlFile);
            Document doc = dBuilder.parse(this.xmlFile);

            doc.getDocumentElement().normalize();

            NodeList resultNode = doc.getChildNodes();

            HashMap result = new HashMap();
            MyNodeList tempNodeList = new MyNodeList();

            String emptyNodeName = null, emptyNodeValue = null;

            for (int index = 0; index < resultNode.getLength(); index++) {
                Node tempNode = resultNode.item(index);

                if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                    tempNodeList.addNode(tempNode);
                }
                emptyNodeName = tempNode.getNodeName();
                emptyNodeValue = tempNode.getNodeValue();
            }

            if (tempNodeList.getLength() == 0 && emptyNodeName != null
                    && emptyNodeValue != null) {
                result.put(emptyNodeName, emptyNodeValue);
                return result;
            }

            this.parseXMLNode(tempNodeList, result);
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private void parseXMLNode(NodeList nList, HashMap result) {
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);

            if (nNode.getNodeType() == Node.ELEMENT_NODE
                    && nNode.hasChildNodes()
                    && nNode.getFirstChild() != null
                    && (nNode.getFirstChild().getNextSibling() != null
                    || nNode.getFirstChild().hasChildNodes())) {
                NodeList childNodes = nNode.getChildNodes();
                MyNodeList tempNodeList = new MyNodeList();
                for (int index = 0; index < childNodes.getLength(); index++) {
                    Node tempNode = childNodes.item(index);
                    if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                        tempNodeList.addNode(tempNode);
                    }
                }
                HashMap dataHashMap = new HashMap();
                if (result.containsKey(nNode.getNodeName()) && result.get(nNode.getNodeName()) instanceof List) {
                    List mapExisting = (List) result.get(nNode.getNodeName());
                    mapExisting.add(dataHashMap);
                } else if (result.containsKey(nNode.getNodeName())) {
                    List counterList = new ArrayList();
                    counterList.add(result.get(nNode.getNodeName()));
                    counterList.add(dataHashMap);
                    result.put(nNode.getNodeName(), counterList);
                } else {
                    result.put(nNode.getNodeName(), dataHashMap);
                }
                if (nNode.getAttributes().getLength() > 0) {
                    Map attributeMap = new HashMap();
                    for (int attributeCounter = 0;
                            attributeCounter < nNode.getAttributes().getLength();
                            attributeCounter++) {
                        attributeMap.put(
                                nNode.getAttributes().item(attributeCounter).getNodeName(),
                                nNode.getAttributes().item(attributeCounter).getNodeValue()
                        );
                    }
                    dataHashMap.put("attributes", attributeMap);
                }
                this.parseXMLNode(tempNodeList, dataHashMap);
            } else if (nNode.getNodeType() == Node.ELEMENT_NODE
                    && nNode.hasChildNodes() && nNode.getFirstChild() != null
                    && nNode.getFirstChild().getNextSibling() == null) {
                this.putValue(result, nNode);
            } else if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                this.putValue(result, nNode);
            }
        }
    }

    private void putValue(HashMap result, Node nNode) {

        HashMap attributeMap = new HashMap();
        Object nodeValue = null;
        if (nNode.getFirstChild() != null) {
            nodeValue = nNode.getFirstChild().getNodeValue();
            if (nodeValue != null) {
                nodeValue = nodeValue.toString().trim();
            }
        }
        HashMap nodeMap = new HashMap();
        nodeMap.put("value", nodeValue);
        Object putNode = nodeValue;
        if (nNode.getAttributes().getLength() > 0) {
            for (int attributeCounter = 0;
                    attributeCounter < nNode.getAttributes().getLength();
                    attributeCounter++) {
                attributeMap.put(
                        nNode.getAttributes().item(attributeCounter).getNodeName(),
                        nNode.getAttributes().item(attributeCounter).getNodeValue()
                );
            }
            nodeMap.put("attributes", attributeMap);
            putNode = nodeMap;
        }
        if (result.containsKey(nNode.getNodeName()) && result.get(nNode.getNodeName()) instanceof List) {
            List mapExisting = (List) result.get(nNode.getNodeName());
            mapExisting.add(putNode);
        } else if (result.containsKey(nNode.getNodeName())) {
            List counterList = new ArrayList();
            counterList.add(result.get(nNode.getNodeName()));
            counterList.add(putNode);
            result.put(nNode.getNodeName(), counterList);
        } else {
            result.put(nNode.getNodeName(), putNode);
        }
    }

    public String processTag010(Object subField) {
        if (subField instanceof List) {
            List list = (List) subField;
            for (Integer in = 0; in < list.size(); in++) {
                Map df = (Map) list.get(in);
                Map mAttibutes = (Map) df.get("attributes");
                Object svalue = df.get("value");
                Iterator it = mAttibutes.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pairs = (Map.Entry) it.next();
                    String key = pairs.getKey().toString();
                    Object dfvalue = pairs.getValue();
                    if (key.equals("code") && dfvalue.equals("a")) {
                        return svalue.toString();
                    }
                }
            }
        } else if (subField instanceof Map) {
            Map df = (Map) subField;
            Map mAttibutes = (Map) df.get("attributes");
            Object svalue = df.get("value");
            Iterator it = mAttibutes.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry) it.next();
                String key = pairs.getKey().toString();
                Object dfvalue = pairs.getValue();
                if (key.equals("code") && dfvalue.equals("a")) {
                    return svalue.toString();
                }
            }
        }
        return "";
    }

    public String processTag033(Object subField, String ind) {
        List<String> sVals = new ArrayList<String>();
        if (subField instanceof List) {
            List list = (List) subField;
            for (Integer in = 0; in < list.size(); in++) {
                Map df = (Map) list.get(in);
                Map mAttibutes = (Map) df.get("attributes");
                Object svalue = df.get("value");
                Iterator it = mAttibutes.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pairs = (Map.Entry) it.next();
                    String key = pairs.getKey().toString();
                    Object dfvalue = pairs.getValue();
                    if (key.equals("code") && dfvalue.equals("a")) {
                        sVals.add(svalue.toString().replaceAll("-", ""));
                    }
                }
            }
        } else if (subField instanceof Map) {
            Map df = (Map) subField;
            Map mAttibutes = (Map) df.get("attributes");
            Object svalue = df.get("value");
            Iterator it = mAttibutes.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry) it.next();
                String key = pairs.getKey().toString();
                Object dfvalue = pairs.getValue();
                if (key.equals("code") && dfvalue.equals("a")) {
                    sVals.add(svalue.toString().replaceAll("-", ""));
                }
            }
        }
        String out = "";
        String separator = "";
        if (ind.equals("2")) {
            separator = "-";
        } else if (ind.equals("0")) {
            separator = ",";
        }
        if (!separator.equals("")) {
            for (int x = 0; x < sVals.size(); x++) {
                if (x == (sVals.size() - 1)) {
                    out += sVals.get(x);
                } else {
                    out += sVals.get(x) + separator;
                }
            }
        }
        return out;
    }

    public String processTag090(Object subField) {
        if (subField instanceof List) {
            List list = (List) subField;
            for (Integer in = 0; in < list.size(); in++) {
                Map df = (Map) list.get(in);
                Map mAttibutes = (Map) df.get("attributes");
                Object svalue = df.get("value");
                Iterator it = mAttibutes.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pairs = (Map.Entry) it.next();
                    String key = pairs.getKey().toString();
                    Object dfvalue = pairs.getValue();
                    if (key.equals("code") && dfvalue.equals("a") && svalue.toString().startsWith("AFC")) {
                        return svalue.toString();
                    }
                }
            }
        } else if (subField instanceof Map) {
            Map df = (Map) subField;
            Map mAttibutes = (Map) df.get("attributes");
            Object svalue = df.get("value");
            Iterator it = mAttibutes.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry) it.next();
                String key = pairs.getKey().toString();
                Object dfvalue = pairs.getValue();
                if (key.equals("code") && dfvalue.equals("a") && svalue.toString().startsWith("AFC")) {
                    return svalue.toString();
                }
            }
        }
        return "";
    }

    public String getDataFields(Map xmlMap, String path) {
        try {
            String name = FilenameUtils.getBaseName(this.xmlFile.getPath()) + "_" + System.currentTimeMillis() + ".xml";
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            this.doc = docBuilder.newDocument();
            // root element
            Element temElement = this.doc.createElement("mavis");
            this.doc.appendChild(temElement);

            Attr mattr = this.doc.createAttribute("xmlns");
            mattr.setValue(Normalizer.normalize("http://www.wizardis.com.au/2005/12/MAVIS", Normalizer.Form.NFD).toString());
            Attr mattr1 = this.doc.createAttribute("xmlns:xl");
            mattr1.setValue(Normalizer.normalize("http://www.w3.org/1999/xlink", Normalizer.Form.NFD).toString());
            Attr mattr2 = this.doc.createAttribute("database");
            mattr2.setValue(Normalizer.normalize("LOC:mbrp", Normalizer.Form.NFD).toString());
            Attr mattr3 = this.doc.createAttribute("organisation");
            mattr3.setValue(Normalizer.normalize("Library of Congress", Normalizer.Form.NFD).toString());
            Attr mattr4 = this.doc.createAttribute("version");
            mattr4.setValue(Normalizer.normalize("03.07.06", Normalizer.Form.NFD).toString());
            temElement.setAttributeNode(mattr2);
            temElement.setAttributeNode(mattr3);
            temElement.setAttributeNode(mattr);
            temElement.setAttributeNode(mattr1);
            temElement.setAttributeNode(mattr4);

            Object value = xmlMap.get("record");
            if (value == null) {
                Object col = xmlMap.get("collection");
                Map records = (Map) col;
                value = records.get("record");
            }

            if (value instanceof Map) {
                this.createRecordNode((Map) value, temElement);
            } else {
                List list = (List) value;
                for (Integer in = 0; in < list.size(); in++) {
                    Map record = (Map) list.get(in);
                    this.createRecordNode(record, temElement);
                }
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);

            StreamResult result = new StreamResult(new File(path + File.separator + name));
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);
            return "done";

        } catch (ParserConfigurationException ex) {
            return ex.getMessage();
        } catch (TransformerConfigurationException ex) {
            return ex.getMessage();
        } catch (TransformerException ex) {
            return ex.getMessage();
        }
    }

    private void createRecordNode(Map value, Element ele) {
        String processTag090 = "";
        String processTag010 = "";
        String processTag245 = "";
        String processTag520 = "";
        String processTag033 = "";
        Map datafield = value;
        Object datafields = datafield.get("datafield");
        if (datafields instanceof List) {
            List list = (List) datafields;
            for (Integer in = 0; in < list.size(); in++) {
                Map df = (Map) list.get(in);
                Map mAttibutes = (Map) df.get("attributes");
                Object subfields = df.get("subfield");
                Iterator it = mAttibutes.entrySet().iterator();
                String tag = "";
                String ind1 = "";
                while (it.hasNext()) {
                    Map.Entry pairs = (Map.Entry) it.next();
                    String key = pairs.getKey().toString();
                    Object dfvalue = pairs.getValue();
                    if (key.equals("tag")) {
                        tag = dfvalue.toString();
                    } else if (key.equals("ind1")) {
                        ind1 = dfvalue.toString();
                    }
                }
                if (tag.equals("090") && processTag090.equals("")) {
                    String out = this.processTag090(subfields).trim();
                    if (!out.equals("")) {
                        processTag090 = out;
                    }
                } else if (tag.equals("010") && processTag010.equals("")) {
                    processTag010 = this.processTag010(subfields).trim();
                } else if (tag.equals("245") && processTag245.equals("")) {
                    processTag245 = "AFC - " + this.processTag010(subfields).trim().toUpperCase();
                } else if (tag.equals("520") && processTag520.equals("")) {
                    processTag520 = this.processTag010(subfields).trim();
                } else if (tag.equals("033") && processTag033.equals("")) {
                    processTag033 = this.processTag033(subfields, ind1).trim();
                }
            }
        }
        Element Collection = this.doc.createElement("Collection");
        ele.appendChild(Collection);
        Element objectIdentifiers = doc.createElement("objectIdentifiers");
        Collection.appendChild(objectIdentifiers);
        // ObjectIdentifier1
        Element ObjectIdentifier1 = doc.createElement("ObjectIdentifier");
        objectIdentifiers.appendChild(ObjectIdentifier1);
        Element identifier = doc.createElement("identifier");
        identifier.appendChild(doc.createTextNode(processTag090));
        ObjectIdentifier1.appendChild(identifier);
        Element identifierType = doc.createElement("identifierType");
        identifierType.appendChild(doc.createTextNode("AFC"));
        ObjectIdentifier1.appendChild(identifierType);
        Attr attr = doc.createAttribute("xl:href");
        attr.setValue(Normalizer.normalize("/Code/key/IDENTIFIER_TYPE/COLLECTION/AFC", Normalizer.Form.NFD).toString());
        Attr attr1 = doc.createAttribute("xl:title");
        attr1.setValue(Normalizer.normalize("AFC Collection No.", Normalizer.Form.NFD).toString());
        identifierType.setAttributeNode(attr);
        identifierType.setAttributeNode(attr1);

        //ObjectIdentifier2
        Element ObjectIdentifier2 = doc.createElement("ObjectIdentifier");
        objectIdentifiers.appendChild(ObjectIdentifier2);
        Element identifier2 = doc.createElement("identifier");
        identifier2.appendChild(doc.createTextNode(processTag010));
        ObjectIdentifier2.appendChild(identifier2);
        Element identifierType2 = doc.createElement("identifierType");
        identifierType2.appendChild(doc.createTextNode("LCCN"));
        ObjectIdentifier2.appendChild(identifierType2);
        Attr attr2 = doc.createAttribute("xl:href");
        attr2.setValue(Normalizer.normalize("/Code/key/IDENTIFIER_TYPE/COLLECTION/LCCN", Normalizer.Form.NFD).toString());
        Attr attr3 = doc.createAttribute("xl:title");
        attr3.setValue(Normalizer.normalize("Library of Congress Control Number", Normalizer.Form.NFD).toString());
        identifierType2.setAttributeNode(attr2);
        identifierType2.setAttributeNode(attr3);

        Element collectionItemCount = doc.createElement("collectionItemCount");
        collectionItemCount.appendChild(doc.createTextNode(String.valueOf(0)));
        Collection.appendChild(collectionItemCount);

        Element collectionName = doc.createElement("collectionName");
        collectionName.appendChild(doc.createTextNode(processTag245));
        Collection.appendChild(collectionName);
//
        Element historicalPeriod = doc.createElement("historicalPeriod");
        historicalPeriod.appendChild(doc.createTextNode(processTag033));
        Collection.appendChild(historicalPeriod);
//
        Element summary = doc.createElement("summary");
        summary.appendChild(doc.createTextNode(processTag520));
        Collection.appendChild(summary);
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }

}
