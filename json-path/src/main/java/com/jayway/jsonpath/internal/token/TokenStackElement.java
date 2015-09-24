
package com.jayway.jsonpath.internal.token;

import java.util.logging.Logger;

/**
 *
 * @author Hunter Payne
 **/
public abstract class TokenStackElement
{
    private static Logger log = Logger.getLogger(TokenStackElement.class.getName());
    
    private boolean matched = false;
    
    private TokenStackElement parent;
    
    public abstract TokenType getType(); // otherwise its an object

    public abstract TokenStackElement getValue();

    public abstract void setValue(TokenStackElement elem);
    
    public TokenStackElement getParent() {
        if (parent == null) {
            return this;
        }
        //log.trace("parent: " + parent);
        return parent;
    }

    public void setParent(TokenStackElement parent) {
        this.parent = parent;
    }

    public void setMatched() {
        this.matched = true;
    }
    
    public boolean getMatched() {
        return matched;
    } 
}

// End TokenStackElement.java
