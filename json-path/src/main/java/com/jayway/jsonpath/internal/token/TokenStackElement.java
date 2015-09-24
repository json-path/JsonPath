
package com.jayway.jsonpath.internal.token;

/**
 *
 * @author Hunter Payne
 **/
public interface TokenStackElement
{
    public TokenType getType(); // otherwise its an object

    public TokenStackElement getValue();

    public void setValue(TokenStackElement elem);
}

// End TokenStackElement.java
