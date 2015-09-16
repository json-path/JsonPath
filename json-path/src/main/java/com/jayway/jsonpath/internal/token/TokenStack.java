
package com.jayway.jsonpath.internal.token;

import java.util.*;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.EvaluationCallback;
import com.jayway.jsonpath.internal.Path;
import com.fasterxml.jackson.core.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Hunter Payne
 **/
public class TokenStack
{
    private static final Logger log = LoggerFactory.getLogger(TokenStack.class);

    protected Configuration conf;
    protected Stack<TokenStackElement> elements;
    protected Stack<Object> objStack;
    protected List<Path> paths;
    protected Map<TokenStackElement, Path> matchedPaths;

    private TokenStackElement curr;
    private Path rootMatch;

    public TokenStack(Configuration conf)
    {
        this.conf = conf;
        paths = new ArrayList<Path>();
        matchedPaths = new HashMap<TokenStackElement, Path>();
        elements = new Stack<TokenStackElement>();
        objStack = new Stack<Object>();
        rootMatch = null;
    }

    public Stack<TokenStackElement> getStack()
    {
        return elements;
    }

    /**
     * registers a path for which to fire results
     */
    public void registerPath(Path path)
    {
        paths.add(path);
    }

    public void read(JsonParser parser, EvaluationCallback callback)
        throws Exception
    {
        read(parser,callback,true);
    }
    
    /**
     * reads from stream and notifies the callback of matched registered paths
     */
    public void read(JsonParser parser, EvaluationCallback callback, boolean getParents)
        throws Exception
    {
        assert(callback != null);

        Object obj = null;
        boolean needsPathCheck = false;
        /*
        if (null == curr && elements.empty()) {
            // check for $ patterns
            for (Path path : paths) {
                if (path.checkForMatch(this)) {
                    matchedPaths.put(curr, path);
                    callback.resultFound(path);
                    rootMatch = path;
                }
            }
        }
        */
        while (parser.nextToken() != null) {
            //log.debug("type/name/val: " + parser.getCurrentToken() + " " + parser.getCurrentName() + " " + parser.getText());
            boolean saveMatch = false;
            switch (parser.getCurrentToken()) {
            case START_ARRAY:
            {
                if (curr != null) {
                    TokenStackElement newElem = new ArrayToken();
                    curr.setValue(newElem);
                    curr = newElem;
                } else {
                    curr = new ArrayToken();
                }
                saveMatch = true;
                needsPathCheck = true;
                elements.push(curr);
               
                obj = stackPush(parser.getCurrentName(),new JSONArray());
                break;
            }
            case END_ARRAY:
            {
                Path match = matchedPaths.remove(curr);
                if (match != null) {
                    callback.resultFoundExit(parser.getCurrentToken(), obj, match);
                }
                elements.pop();
                if (elements.empty()) curr = null;
                else curr = elements.peek();
                
                obj = stackPop(callback, curr, JSONArray.class, match);
                break;
            }
            case VALUE_EMBEDDED_OBJECT:
            case START_OBJECT:
            {
                obj = stackPush(parser.getCurrentName(), new JSONObject());
                
                if (isArray(curr)) {
                    if (((ArrayToken)curr).getValue() != null &&
                        matchedPaths.containsKey(curr))
                    {
                        Path match = matchedPaths.remove(curr);
                        if (getParents) {
                           callback.resultFoundExit(parser.getCurrentToken(), obj, match);
                        }

                        if (match.checkForMatch(this)) {

                            matchedPaths.put(curr, match);
                            //callback.resultFound(match);
                            curr.setMatched();
                        }
                    }
                } else if (null == curr && elements.empty()) {
                    // check for $ patterns
                    for (Path path : paths) {
                        if (path.checkForMatch(this)) {
                            matchedPaths.put(curr, path);
                            //callback.resultFound(path);
                            rootMatch = path;
                        }
                    }
                }

                if (curr != null) {
                    TokenStackElement newElem = new ObjectToken();
                    curr.setValue(newElem);
                    curr = newElem;
                } else {
                    curr = new ObjectToken();
                }
                saveMatch = true;
                needsPathCheck = true;
                elements.push(curr);
                break;
            }
            case END_OBJECT:
            {
                if (getParents) {
                if (!"$".equals(curr)) {
                    Path match = matchedPaths.remove(curr);
                    if (match != null) {
                        callback.resultFoundExit(parser.getCurrentToken(), obj, match);
                    }
                } else {
                    Path match = matchedPaths.get("$");
                    if (match != null) {
                        callback.resultFoundExit(parser.getCurrentToken(), obj, match);
                    }
                }
                }
                elements.pop();
                if (elements.empty()) curr = null;
                else curr = elements.peek();
                
                obj = stackPop(callback, curr, JSONObject.class, null);
                break;
            }
            case FIELD_NAME:
            {
                assert(curr instanceof ObjectToken);
                ((ObjectToken)curr).key = parser.getText();
                break;
            }
            case VALUE_FALSE:
            {
                StringToken newToken = new StringToken("FALSE");
                curr.setValue(newToken);
                needsPathCheck = true;
                objPutVal(obj, curr, newToken.value);
                break;
            }
            case VALUE_TRUE:
            {
                StringToken newToken = new StringToken("TRUE");
                curr.setValue(newToken);
                needsPathCheck = true;
                objPutVal(obj, curr, newToken.value);
                break;
            }
            case VALUE_NUMBER_FLOAT:
            {
                FloatToken newToken =
                    new FloatToken((float)parser.getValueAsDouble());
                curr.setValue(newToken);
                needsPathCheck = true;
                objPutVal(obj, curr, newToken.value);
                break;
            }
            case VALUE_NUMBER_INT:
            {
                IntToken newToken = new IntToken(parser.getValueAsInt());
                curr.setValue(newToken);
                needsPathCheck = true;
                objPutVal(obj, curr, newToken.value);
                break;
            }
            case VALUE_STRING:
            {
                StringToken newToken = new StringToken(parser.getText());
                curr.setValue(newToken);
                needsPathCheck = true;
                objPutVal(obj, curr, parser.getText());
                break;
            }
            case VALUE_NULL:
            {
                curr.setValue(null);
                needsPathCheck = true;
                objPutVal(obj, curr, null);
                break;
            }
            default:
                assert false;
            }
            // now check the paths for matches
            if (needsPathCheck) {
                for (Path path : paths) {
                    if (path.checkForMatch(this)) {
                        if (saveMatch) matchedPaths.put(curr, path);
                        curr.setMatched();
                        if (getParents) {
                           callback.resultFound(parser.getCurrentToken(), obj, path);
                        }
                    }
                }
                needsPathCheck = false;
            }

            if (rootMatch != null && elements.empty() && getParents) {
               if (isArray(curr)) {
                    obj = new JSONArray();
                }
                callback.resultFoundExit(parser.getCurrentToken(), obj, rootMatch);
                rootMatch = null;
            }
        }
    }
    
    private Object stackPush(String key, Object jsObj) throws Exception {
        if (jsObj == null) {
            return null;
        }
        
        if (this.objStack.size() > 0) {
            Object obj = this.objStack.peek();
            //log.trace("PP : Push[" + this.objStack.size() + "] " + jsObj.getClass().getSimpleName() + " -> " + obj.getClass());
            if (obj.getClass() == JSONArray.class) {
                ((JSONArray)obj).add(jsObj);
            } else if (obj.getClass() == JSONObject.class) {
                ((JSONObject)obj).put(key, jsObj);
            } else {
                throw new Exception("Unhandled type: " + obj.getClass());
            }

        } else {
            //log.trace("PP : Push " + jsObj.getClass().getSimpleName() + " -> ROOT");
        }
        this.objStack.add(jsObj);
        
        return jsObj;
    }

    private <T> Object stackPop(EvaluationCallback callback, TokenStackElement tse, T jsObj, Path path) throws Exception {
        if (jsObj == null) {
            return null;
        }
        
        Object obj = null;
        Object popObj = null;
        if (this.objStack.size() > 0) {
            popObj = this.objStack.peek();
            
            //log.trace("PP : Pop " +( popObj != null ? popObj.getClass() : " null"));
            if (popObj.getClass() != jsObj) {
                throw new Exception("Unexpected type : " + popObj.getClass());
            } else {
                popObj = this.objStack.pop();
                if (this.objStack.size() > 0) {
                    obj = objStack.peek();
                } else {
                    //log.debug("PP : Now ROOT");
                    obj = null;
                }
            }
        }

        if (tse != null && tse.getMatched()) {
            callback.resultFound("Stack", popObj, path);
        } 
        
        //log.info("PP : Parent now[" + this.objStack.size() + "]: " + objShow(obj));
        return obj;
    }
    
    private boolean isArray(TokenStackElement current) { 
        return current != null && current.getType() == TokenType.ARRAY_TOKEN;
    }
    
    public String objShow(Object obj) {
        if (obj != null) {
            Class cls = obj.getClass();
            if (cls == JSONObject.class) {
                return "JSONObject: " + ((JSONObject)obj).toString();
            } else if (cls == JSONArray.class) {
                return "JSONArray: " + Arrays.toString(((JSONArray)obj).toArray());                
            }
        }
        return "NA";
    }
    
    private void objPutVal(Object objIn, TokenStackElement el, Object value) throws Exception {
        if (objIn == null) {
            return;
        }
        
        Class objInCls = objIn.getClass();
        if (objInCls == JSONObject.class) {
            JSONObject obj = (JSONObject)objIn;
            obj.put(((ObjectToken)el).key, value);
        } else if (objInCls == JSONArray.class) {
            JSONArray obj = (JSONArray)objIn;
            obj.add(value);
        } else {
            throw new Exception("Unhandled type: " + objInCls);
        }
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Token Stack depth=");
        sb.append(elements.size());
        for (TokenStackElement elem : elements) {
            sb.append(" ");
            sb.append(elem.toString());
        }
        sb.append(" ");
        if (!elements.empty()) sb.append(elements.peek().getValue());
        return sb.toString();
    }

    public Configuration configuration()
    {
        return conf;
    }
}

// End TokenStack.java

