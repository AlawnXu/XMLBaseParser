package com.anyapps.xmlparser.core;

import com.anyapps.xmlparser.utils.ObjectUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Enumeration;

public class XMLBaseParser<TYPE> {
    //root module
    public static String ROOT = "root";
    public static String ITEM = "item";
    ArrayList<TYPE> xmlDataArray = new ArrayList<>();
    private Class<TYPE> clazz;

    public XMLBaseParser() {
        //当前对象的直接超类的 Type
        Type genericSuperclass = getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            //参数化类型
            ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
            //返回表示此类型实际类型参数的 Type 对象的数组
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            this.clazz = (Class<TYPE>) actualTypeArguments[0];
        } else {
            this.clazz = (Class<TYPE>) genericSuperclass;
        }
    }

    public Class<TYPE> getClazz() {
        return clazz;
    }

    public ArrayList<TYPE> parseXMLResponseList(String xmlData) throws Exception {
        try {
            parseCommonResponse(getXmlFromUrl(xmlData));
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return xmlDataArray;
    }

    private ArrayList<TYPE> parseCommonResponse(InputStream stream) throws Exception {
        try {
            final XmlDomNode rootElement = XmlDomParser.parseTree(stream);
            if (rootElement.getName().equalsIgnoreCase(ROOT)) {
                Enumeration rootChildren = rootElement.getChildren();
                while (rootChildren.hasMoreElements()) {
                    XmlDomNode rootChildNode = (XmlDomNode) rootChildren.nextElement();
                    String rootNodeName = rootChildNode.getName();
                    if (rootNodeName.equalsIgnoreCase(ITEM)) {
                        Enumeration mainChild = rootChildNode.getChildren();
                        TYPE mType = ObjectUtils.newTclass(getClazz());
                        configAttributes(rootElement, rootChildNode, mType);
                        boolean isHasMoreItem = false;//存在多个item节点
                        while (mainChild.hasMoreElements()) {
                            XmlDomNode childrenElement = (XmlDomNode) mainChild.nextElement();
                            String childrenName = childrenElement.getName();
                            if (childrenName.equalsIgnoreCase(ITEM)) {
                                TYPE mmType = ObjectUtils.newTclass(getClazz());
                                configAttributes(rootElement, rootChildNode, mmType);
                                Enumeration grandChildren = childrenElement.getChildren();
                                while (grandChildren.hasMoreElements()) {
                                    XmlDomNode grandchildrenElement = (XmlDomNode) grandChildren.nextElement();
                                    String grandchildrenName = grandchildrenElement.getName();
                                    for (Object filedName : ObjectUtils.getFiledName(mmType)) {
                                        if (grandchildrenName.equalsIgnoreCase(filedName.toString())) {
                                            ObjectUtils.setFieldValueByName(grandchildrenName, grandchildrenElement.getText(), mmType);
                                        }
                                    }
                                }
                                isHasMoreItem = true;
                                xmlDataArray.add(mmType);
                            } else {
                                for (Object filedName : ObjectUtils.getFiledName(mType)) {
                                    if (childrenName.equalsIgnoreCase(filedName.toString())) {
                                        ObjectUtils.setFieldValueByName(childrenName, childrenElement.getText(), mType);
                                    }
                                }
                            }
                        }
                        if (!isHasMoreItem)
                            xmlDataArray.add(mType);
                    }
                }
            }
        } catch (Exception e) {
            throw new Exception("Sorry!Root Model are not available");
        }
        return xmlDataArray;
    }

    private void configAttributes(XmlDomNode rootElement, XmlDomNode rootChildNode, TYPE mType) {
        for (Object filedName : ObjectUtils.getFiledName(mType)) {
            if (rootElement.getAttributes() != null && !rootElement.getAttributes().isEmpty()) {
                if (rootElement.getAttributes().containsKey(filedName.toString())) {
                    ObjectUtils.setFieldValueByName((String) filedName, rootElement.getAttribute((String) filedName), mType);
                }
            }
            if (rootChildNode.getAttributes() != null && !rootChildNode.getAttributes().isEmpty()) {
                if (rootChildNode.getAttributes().containsKey(filedName.toString())) {
                    ObjectUtils.setFieldValueByName((String) filedName, rootChildNode.getAttribute((String) filedName), mType);
                }
            }
        }
    }

    public InputStream getXmlFromUrl(String xmlData) {
        InputStream xml1 = null;
        try {
            xml1 = new ByteArrayInputStream(xmlData.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return xml1;
    }
}
