
package com.jayway.jsonpath.internal.token;

/**
 *
 * @author Hunter Payne
 **/
public class ArrayToken extends TokenStackElement
{
    int currentIndex;
    TokenStackElement value; // can be an object, array, or property

    public ArrayToken()
    {
        currentIndex = 0;
        value = null;
    }

    public TokenType getType()
    {
        return TokenType.ARRAY_TOKEN;
    }

    public int getIndex()
    {
        return currentIndex;
    }

    public TokenStackElement getValue()
    {
        return value;
    }

    public void setValue(TokenStackElement elem)
    {
        if (value != null) {
            ++currentIndex;
        }
        value = elem;
    }

    public String toString()
    {
        return "Array[idx=" + currentIndex + "]";
    }
}

// End ArrayToken.java
