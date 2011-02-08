package com.jayway.jsonpath;

import java.util.LinkedList;
import java.util.List;

/**
 * User: kalle stenflo
 * Date: 2/2/11
 * Time: 2:08 PM
 */
public class PathUtil {

    /**
     * Checks if a path points to a single item or if it potentially returns multiple items
     *
     * a path is considered <strong>not</strong> definite if it contains a scan fragment ".."
     * or an array position fragment that is not based on a single index
     *
     *
     * absolute path examples:
     *
     * $store.book
     * $store.book[1].value
     *
     * not absolute path examples
     *
     * $..book
     * $.store.book[1,2]
     * $.store.book[?(@.category = 'fiction')]
     *
     * @param jsonPath the path to check
     * @return true if path is definite (points to single item)
     */
    public static boolean isPathDefinite(String jsonPath) {
        //return !jsonPath.replaceAll("\"[^\"\\\\\\n\r]*\"", "").matches(".*(\\.\\.|\\*|\\[[\\\\/]|\\?|,|:|>|\\(|<|=|\\+).*");
        return !jsonPath.replaceAll("\"[^\"\\\\\\n\r]*\"", "").matches(".*(\\.\\.|\\*|\\[[\\\\/]|\\?|,|:\\s?\\]|\\[\\s?:|>|\\(|<|=|\\+).*");
    }

    /**
     * Splits a path into fragments
     *
     * the path <code>$.store.book[1].category</code> returns ["$", "store", "book", "[1]", "value"]
     *
     * @param jsonPath path to split
     * @return fragments
     */
    public static List<String> splitPath(String jsonPath) {

        LinkedList<String> fragments = new LinkedList<String>();

        if (!jsonPath.startsWith("$.")) {
            jsonPath = "$." + jsonPath;
        }

        jsonPath = jsonPath.replace("..", ".~.")
                .replace("[", ".[")
                .replace("@.", "@")
                .replace("['", "")
                .replace("']", "");

        String[] split = jsonPath.split("\\.");

        for (int i = 0; i < split.length; i++) {
            if (split[i].trim().isEmpty()) {
                continue;
            }
            fragments.add(split[i].replace("@", "@.").replace("~", ".."));
        }
        return fragments;
    }
}
