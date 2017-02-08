package com.hyena.framework.samples.animator.parser;

import android.text.TextUtils;

import com.hyena.framework.samples.animator.parser.action.MapAction;
import com.hyena.framework.samples.animator.parser.action.MapActionAlpha;
import com.hyena.framework.samples.animator.parser.action.MapActionRotate;
import com.hyena.framework.samples.animator.parser.action.MapFrame;
import com.hyena.framework.samples.animator.parser.action.MapActionFrame;
import com.hyena.framework.samples.animator.parser.action.MapActionScale;
import com.hyena.framework.samples.animator.parser.action.MapActionSequence;
import com.hyena.framework.samples.animator.parser.action.MapActionTranslate;
import com.hyena.framework.samples.animator.parser.node.MapNodeBlock;
import com.hyena.framework.samples.animator.parser.node.MapNodeButton;
import com.hyena.framework.samples.animator.parser.node.MapNodeLine;
import com.hyena.framework.samples.animator.parser.node.MapNodeLayer;
import com.hyena.framework.samples.animator.parser.node.MapNode;
import com.hyena.framework.samples.animator.parser.node.MapNodeSprite;
import com.hyena.framework.samples.animator.parser.node.MapNodeText;
import com.hyena.framework.samples.animator.parser.node.MapNodeTitle;
import com.hyena.framework.samples.animator.parser.style.MapStyle;
import com.hyena.framework.samples.animator.parser.utils.XMLUtils;
import com.hyena.framework.utils.MathUtils;
import com.hyena.framework.utils.UIUtils;

import org.apache.http.protocol.HTTP;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
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
public class DefaultMapParser implements MapParser {

    @Override
    public CMap parse(String xml, int screenWidth, int screenHeight) {
        try {
            CMap map = new CMap();
            DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(xml.getBytes(HTTP.UTF_8)));
            Element rootElement = document.getDocumentElement();

            //初始化背景
            NodeList backGroundList = rootElement.getElementsByTagName("background");
            String backGround = "";
            for (int i = 0; i < backGroundList.getLength(); i++) {
                Node backGroundNode = backGroundList.item(i);
                backGround = XMLUtils.getAttributeValue(backGroundNode, "src");
            }
            map.mBackGround = backGround;

            //解析样式
            NodeList styleList = rootElement.getElementsByTagName("style");
            for (int i = 0; i < styleList.getLength(); i++) {
                Node style = styleList.item(i);
                NamedNodeMap attrMap = style.getAttributes();
                MapStyle mapStyle = new MapStyle(XMLUtils.getAttributeValue(style, "id"));
                for (int j = 0; j < attrMap.getLength(); j++) {
                    Node attr = attrMap.item(j);
                    mapStyle.setStyle(attr.getNodeName(), attr.getNodeValue());
                }
                map.addStyle(mapStyle);
            }

            NodeList layerList = rootElement.getElementsByTagName("layer");
            for (int i = 0; i < layerList.getLength(); i++) {
                Node layer = layerList.item(i);
                MapNodeLayer mapLayer = parseLayer(layer, screenWidth, screenHeight);
                map.addLayer(mapLayer);
            }
            return map;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private MapNodeLayer parseLayer(Node layer, int screenWidth, int screenHeight) {
        MapNodeLayer mapLayer = new MapNodeLayer();
        mapLayer.setId(XMLUtils.getAttributeValue(layer, "id"));
        mapLayer.setZIndex(MathUtils.valueOfInt(XMLUtils.getAttributeValue(layer, "zindex")));
        String depth = XMLUtils.getAttributeValue(layer, "depth");
        if (!TextUtils.isEmpty(depth)) {
            mapLayer.setDepth(MathUtils.valueOfFloat(depth));
        } else {
            mapLayer.setDepth(1);
        }
        String width = XMLUtils.getAttributeValue(layer, "width");
        String height = XMLUtils.getAttributeValue(layer, "height");
        if (width == null) {
            width = "WIDTH";
        }
        if (height == null) {
            height = "HEIGHT";
        }
        mapLayer.setSize(getNumber(width, screenWidth, screenHeight),
                getNumber(height, screenWidth, screenHeight));

        NodeList elementNode = layer.getChildNodes();
        for (int i = 0; i < elementNode.getLength(); i++) {
            Node element = elementNode.item(i);
            String nodeName = element.getNodeName();
            MapNode mapNode = null;
            if ("node".equals(nodeName)) {
                mapNode = parseSprite(element, screenWidth, screenHeight);
            } else if ("text".equals(nodeName)) {
                mapNode = parseText(element, screenWidth, screenHeight);
            } else if ("line".equals(nodeName)) {
                mapNode = parseLine(element);
            } else if ("title".equals(nodeName)){
                mapNode = parseTitle(element, screenWidth, screenHeight);
            } else if("button".equals(nodeName)){
                mapNode = parseButton(element, screenWidth, screenHeight);
            }
            if (mapNode != null) {
                //update x, y, tag
                updateMapNode(mapNode, element, screenWidth, screenHeight);
                //update actions
                updateActions(mapNode, element, screenWidth, screenHeight);

                mapLayer.addNode(mapNode);
            }
        }
        return mapLayer;
    }

    private void updateMapNode(MapNode mapNode, Node node, int screenWidth, int screenHeight) {
        mapNode.setX(getNumber(XMLUtils.getAttributeValue(node, "x"), screenWidth, screenHeight));
        mapNode.setY(getNumber(XMLUtils.getAttributeValue(node, "y"), screenWidth, screenHeight));
        mapNode.setZIndex(MathUtils.valueOfInt(XMLUtils.getAttributeValue(node, "zindex")));
        mapNode.setTag(XMLUtils.getAttributeValue(node, "tag"));

        String anchorX = XMLUtils.getAttributeValue(node, "anchorX");
        if (!TextUtils.isEmpty(anchorX)) {
            mapNode.setAnchorX(MathUtils.valueOfFloat(anchorX));
        }

        String anchorY = XMLUtils.getAttributeValue(node, "anchorY");
        if (!TextUtils.isEmpty(anchorY)) {
            mapNode.setAnchorY(MathUtils.valueOfFloat(anchorY));
        }
    }

    private void updateActions(MapNode mapNode, Node node, int screenWidth, int screenHeight) {
        NodeList nodeList = node.getChildNodes();
        if (nodeList != null && nodeList.getLength() > 0) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node child = nodeList.item(i);
                MapAction action = parseActions(child, screenWidth, screenHeight);
                if (action != null) {
                    mapNode.addAction(action);
                }
            }
        }
    }

    private MapAction parseActions(Node node, int screenWidth, int screenHeight){
        MapAction action = null;
        String type = XMLUtils.getAttributeValue(node, "type");
        if ("scale".equals(type)) {
            action = parseScaleAction(node);
        } else if ("translate".equals(type)) {
            action = parseTranslateAction(node, screenWidth, screenHeight);
        } else if ("alpha".equals(type)) {
            action = parseAlphaAction(node);
        } else if ("frame".equals(type)) {
            action = parseFrameAction(node);
        } else if ("sequence".equals(type)) {
            action = parseSequenceAction(node, screenWidth, screenHeight);
        } else if ("rotate".equals(type)) {
            action = parseRotateAction(node);
        }
        return action;
    }

    private MapActionRotate parseRotateAction(Node node){
        int duration = MathUtils.valueOfInt(XMLUtils.getAttributeValue(node, "duration"));
//        int repeat = MathUtils.valueOfInt(XMLUtils.getAttributeValue(node, "repeat"));
        String repeatStr = XMLUtils.getAttributeValue(node, "repeat");
        int repeat = 1;
        if (!TextUtils.isEmpty(repeatStr)) {
            repeat = MathUtils.valueOfInt(repeatStr);
        }
        MapActionRotate action = new MapActionRotate(duration, repeat);
        action.mFrom = MathUtils.valueOfInt(XMLUtils.getAttributeValue(node, "from"));
        action.mDegree = MathUtils.valueOfInt(XMLUtils.getAttributeValue(node, "degree"));
        return action;
    }

    private MapActionSequence parseSequenceAction(Node node, int screenWidth, int screenHeight) {
        int duration = MathUtils.valueOfInt(XMLUtils.getAttributeValue(node, "duration"));
//        int repeat = MathUtils.valueOfInt(XMLUtils.getAttributeValue(node, "repeat"));
        String repeatStr = XMLUtils.getAttributeValue(node, "repeat");
        int repeat = 1;
        if (!TextUtils.isEmpty(repeatStr)) {
            repeat = MathUtils.valueOfInt(repeatStr);
        }
        MapActionSequence action = new MapActionSequence(duration, repeat);

        NodeList actionList = node.getChildNodes();
        if (actionList != null && actionList.getLength() > 0) {
            for (int i = 0; i < actionList.getLength(); i++) {
                Node actionNode = actionList.item(i);
                MapAction subAction = parseActions(actionNode, screenWidth, screenHeight);
                if (subAction != null) {
                    action.addAction(subAction);
                }
            }
        }

        return action;
    }

    private MapActionFrame parseFrameAction(Node node) {
        int duration = MathUtils.valueOfInt(XMLUtils.getAttributeValue(node, "duration"));
//        int repeat = MathUtils.valueOfInt(XMLUtils.getAttributeValue(node, "repeat"));
        String repeatStr = XMLUtils.getAttributeValue(node, "repeat");
        int repeat = 1;
        if (!TextUtils.isEmpty(repeatStr)) {
            repeat = MathUtils.valueOfInt(repeatStr);
        }
        MapActionFrame action = new MapActionFrame(duration, repeat);

        NodeList frameList = node.getChildNodes();
        if (frameList != null && frameList.getLength() > 0) {
            for (int i = 0; i < frameList.getLength(); i++) {
                Node frameNode = frameList.item(i);

                MapFrame frame = new MapFrame(MathUtils.valueOfInt(
                        XMLUtils.getAttributeValue(frameNode, "duration")),
                        MathUtils.valueOfInt(XMLUtils.getAttributeValue(frameNode, "repeat")));
                frame.mSrc = XMLUtils.getAttributeValue(frameNode, "src");
                action.addFrame(frame);
            }
        }
        return action;
    }

    private MapActionAlpha parseAlphaAction(Node node) {
        int duration = MathUtils.valueOfInt(XMLUtils.getAttributeValue(node, "duration"));
//        int repeat = MathUtils.valueOfInt(XMLUtils.getAttributeValue(node, "repeat"));
        String repeatStr = XMLUtils.getAttributeValue(node, "repeat");
        int repeat = 1;
        if (!TextUtils.isEmpty(repeatStr)) {
            repeat = MathUtils.valueOfInt(repeatStr);
        }
        MapActionAlpha action = new MapActionAlpha(duration, repeat);
        action.mFrom = MathUtils.valueOfInt(XMLUtils.getAttributeValue(node, "from"));
        action.mTo = MathUtils.valueOfInt(XMLUtils.getAttributeValue(node, "to"));
        return action;
    }

    private MapActionTranslate parseTranslateAction(Node node, int screenWidth, int screenHeight) {
        int duration = MathUtils.valueOfInt(XMLUtils.getAttributeValue(node, "duration"));
        String repeatStr = XMLUtils.getAttributeValue(node, "repeat");
        int repeat = 1;
        if (!TextUtils.isEmpty(repeatStr)) {
            repeat = MathUtils.valueOfInt(repeatStr);
        }
        MapActionTranslate action = new MapActionTranslate(duration, repeat);
        action.mToX = getNumber(XMLUtils.getAttributeValue(node, "toX"), screenWidth, screenHeight);
        action.mToY = getNumber(XMLUtils.getAttributeValue(node, "toY"), screenWidth, screenHeight);
        return action;
    }

    private MapActionScale parseScaleAction(Node node) {
        int duration = MathUtils.valueOfInt(XMLUtils.getAttributeValue(node, "duration"));
        String repeatStr = XMLUtils.getAttributeValue(node, "repeat");
        int repeat = 1;
        if (!TextUtils.isEmpty(repeatStr)) {
            repeat = MathUtils.valueOfInt(repeatStr);
        }
        MapActionScale action = new MapActionScale(duration, repeat);
        action.mFrom = MathUtils.valueOfFloat(XMLUtils.getAttributeValue(node, "from"));
        action.mTo = MathUtils.valueOfFloat(XMLUtils.getAttributeValue(node, "to"));
        return action;
    }

    private MapNodeSprite parseSprite(Node node, int screenWidth, int screenHeight) {
        String id = XMLUtils.getAttributeValue(node, "id");
        String width = XMLUtils.getAttributeValue(node, "width");
        String height = XMLUtils.getAttributeValue(node, "height");
        MapNodeSprite sprite = new MapNodeSprite(id,
                getNumber(width, screenWidth, screenHeight),
                getNumber(height, screenWidth, screenHeight));
        sprite.mSrc = XMLUtils.getAttributeValue(node, "src");
        sprite.mUnableSrc = XMLUtils.getAttributeValue(node, "unable");
        sprite.mOpenSrc = XMLUtils.getAttributeValue(node, "open");
        sprite.mNextBagId = XMLUtils.getAttributeValue(node, "bagId");

        //解析描述快
        NodeList blockList = node.getChildNodes();
        if (blockList != null) {
            for (int i = 0; i < blockList.getLength(); i++) {
                Node blockNode = blockList.item(i);
                //描述块
                if ("block".equals(blockNode.getNodeName())) {
                    id = XMLUtils.getAttributeValue(blockNode, "id");
                    width = XMLUtils.getAttributeValue(blockNode, "width");
                    height = XMLUtils.getAttributeValue(blockNode, "height");
                    MapNodeBlock block = new MapNodeBlock(id,
                            getNumber(width, screenWidth, screenHeight),
                            getNumber(height, screenWidth, screenHeight));
                    block.mTitle = XMLUtils.getAttributeValue(blockNode, "title");
                    block.mSubTitle = XMLUtils.getAttributeValue(blockNode, "subTitle");
                    block.mStyle = XMLUtils.getAttributeValue(blockNode, "style");
                    sprite.addMapBlock(block);
                } else if ("text".equals(blockNode.getNodeName())) {
                    id = XMLUtils.getAttributeValue(blockNode, "id");
                    width = XMLUtils.getAttributeValue(blockNode, "width");
                    height = XMLUtils.getAttributeValue(blockNode, "height");
                    MapNodeText mapText = new MapNodeText(id,
                            getNumber(width, screenWidth, screenHeight),
                            getNumber(height, screenWidth, screenHeight));
                    mapText.mText = XMLUtils.getAttributeValue(blockNode, "text");
                    mapText.mStyle = XMLUtils.getAttributeValue(blockNode, "style");
                    sprite.addMapText(mapText);
                    updateMapNode(mapText, blockNode, screenWidth, screenHeight);
                } else if ("node".equals(blockNode.getNodeName())) {
                    MapNodeSprite cover = parseSprite(blockNode, screenWidth, screenHeight);
                    sprite.addSprite(cover);
                    updateMapNode(cover, blockNode, screenWidth, screenHeight);
                }
            }
        }
        return sprite;
    }

    private MapNodeText parseText(Node node, int screenWidth, int screenHeight) {
        String id = XMLUtils.getAttributeValue(node, "id");
        String width = XMLUtils.getAttributeValue(node, "width");
        String height = XMLUtils.getAttributeValue(node, "height");
        MapNodeText text = new MapNodeText(id,
                getNumber(width, screenWidth, screenHeight),
                getNumber(height, screenWidth, screenHeight));
        text.mColor = XMLUtils.getAttributeValue(node, "color");
        text.mPressColor = XMLUtils.getAttributeValue(node, "pressed");
        text.mFontSize = UIUtils.dip2px(MathUtils.valueOfInt(
                XMLUtils.getAttributeValue(node, "fontSize")));
        text.mText = XMLUtils.getAttributeValue(node, "text");
        text.mAlign = XMLUtils.getAttributeValue(node, "textAlign");
        return text;
    }

    private MapNodeLine parseLine(Node node) {
        String id = XMLUtils.getAttributeValue(node, "id");
        MapNodeLine line = new MapNodeLine(id, 0, 0);
        line.mFromId = XMLUtils.getAttributeValue(node, "from");
        line.mToId = XMLUtils.getAttributeValue(node, "to");
        line.mStyle = XMLUtils.getAttributeValue(node, "style");
        line.mColor = XMLUtils.getAttributeValue(node, "color");

//        line.mHasBag = "true".equals(XMLUtils.getAttributeValue(node, "hasBox"));
//        line.mBagStyle = XMLUtils.getAttributeValue(node, "bagStyle");
        return line;
    }

//    <title id="title" x="func(WIDTH/2-61)" y="10" width="123" height="43" background="res:map1/title_bg.png"
//    title="海底世界" titleFontSize="16" titleColor="#ffffff"
//    subTitleLeft="10/" subTitleFontSize="12" subTitleLeftColor="#ffffff"
//    subTitleRight="50" subTitleRightColor="#ffffff"/>
    private MapNodeTitle parseTitle(Node node, int screenWidth, int screenHeight){
        String id = XMLUtils.getAttributeValue(node, "id");
        String width = XMLUtils.getAttributeValue(node, "width");
        String height = XMLUtils.getAttributeValue(node, "height");
        MapNodeTitle title = new MapNodeTitle(id,
                getNumber(width, screenWidth, screenHeight),
                getNumber(height, screenWidth, screenHeight));


        title.mBackGround = XMLUtils.getAttributeValue(node, "background");
        title.mTitle = XMLUtils.getAttributeValue(node, "title");
        title.mTitleFontSize = UIUtils.dip2px(MathUtils.valueOfInt(XMLUtils
                .getAttributeValue(node, "titleFontSize")));
        title.mTitleColor = XMLUtils.getAttributeValue(node, "titleColor");

        title.mSubTitleFontSize = UIUtils.dip2px(MathUtils.valueOfInt(XMLUtils
                .getAttributeValue(node, "subTitleFontSize")));

        title.mSubTitleLeft = XMLUtils.getAttributeValue(node, "subTitleLeft");
        title.mSubTitleLeftColor = XMLUtils.getAttributeValue(node, "subTitleLeftColor");
        title.mSubTitleRight = XMLUtils.getAttributeValue(node, "subTitleRight");
        title.mSubTitleRightColor = XMLUtils.getAttributeValue(node, "subTitleRightColor");

        return title;
    }

    private MapNodeButton parseButton(Node node, int screenWidth, int screenHeight){
        String id = XMLUtils.getAttributeValue(node, "id");
        String width = XMLUtils.getAttributeValue(node, "width");
        String height = XMLUtils.getAttributeValue(node, "height");
        MapNodeButton button = new MapNodeButton(id,
                getNumber(width, screenWidth, screenHeight),
                getNumber(height, screenWidth, screenHeight));
        button.mTitle = XMLUtils.getAttributeValue(node, "title");
        return button;
    }

//    private MapNodeBlock parseBlock(Node node, int screenWidth, int screenHeight){
//        String id = XMLUtils.getAttributeValue(node, "id");
//        String width = XMLUtils.getAttributeValue(node, "width");
//        String height = XMLUtils.getAttributeValue(node, "height");
//        MapNodeBlock block = new MapNodeBlock(id,
//                getNumber(width, screenWidth, screenHeight),
//                getNumber(height, screenWidth, screenHeight));
//        block.mTitle = XMLUtils.getAttributeValue(node, "title");
//        block.mSubTitle = XMLUtils.getAttributeValue(node, "subTitle");
//        block.mSubTitleSrc = XMLUtils.getAttributeValue(node, "subTitleSrc");
//        block.mAttachNodeId = XMLUtils.getAttributeValue(node, "attach");
//        block.mAttachDirection = XMLUtils.getAttributeValue(node, "attachDirection");
//
//        block.mTitleColor = XMLUtils.getAttributeValue(node, "titleColor");
//        block.mTitleFontSize = MathUtils.valueOfInt(XMLUtils.getAttributeValue(node, "titleFontSize"));
//        block.mSubTitleFontSize = MathUtils.valueOfInt(XMLUtils.getAttributeValue(node, "subTitleFontSize"));
//
//        block.mMarginLeft = MathUtils.valueOfInt(XMLUtils.getAttributeValue(node, "marginLeft"));
//        block.mMarginRight = MathUtils.valueOfInt(XMLUtils.getAttributeValue(node, "marginRight"));
//        return block;
//    }

    private int getNumber(String value, int screenWidth, int screenHeight) {
        if (TextUtils.isEmpty(value))
            return 0;
        try {
            if (value.startsWith("func")) {
                //取得公式部分
                String eval = value.substring(5);
                eval = eval.substring(0, eval.indexOf(")"));
                eval = eval.replaceAll("HEIGHT", screenHeight + "");
                eval = eval.replaceAll("WIDTH", screenWidth + "");
                int result;
                try {
                    result = Integer.valueOf(eval);
                } catch (Exception e) {
                    result = MathUtils.eval(eval);
                }
                return UIUtils.dip2px(result);
            }
            return UIUtils.dip2px(Integer.valueOf(value));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
