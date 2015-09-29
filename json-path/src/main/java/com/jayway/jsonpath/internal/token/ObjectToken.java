
package com.jayway.jsonpath.internal.token;

/**
 *
 * @author Hunter Payne
 **/
public class ObjectToken implements TokenStackElement
{
    String key;
    TokenStackElement value; // can be an array, object, or property

    public ObjectToken()
    {
        key = null;
        value = null;
    }

    public TokenType getType()
    {
        return TokenType.OBJECT_TOKEN;
    }

    public String getKey()
    {
        return key;
    }

    public TokenStackElement getValue()
    {
        return value;
    }

    public void setValue(TokenStackElement elem)
    {
        value = elem;
    }

    public String toString()
    {
        return "Object[key=" + key + "]";
    }
}

// End ObjectToken.java
