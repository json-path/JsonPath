package com.jayway.jsonassert.impl;

import java.util.LinkedList;
import java.util.Queue;

/**
 * User: kalle stenflo
 * Date: 1/23/11
 * Time: 1:52 PM
 */
class Path {

    private final Queue<PathFragment> path;

    Path(String path) {
        this.path = compilePathFragments(path);
    }

    Path(Queue<PathFragment> path) {
        this.path = new LinkedList<PathFragment>();
        this.path.addAll(path);
    }

    int size() {
        return path.size();
    }

    boolean hasMoreFragments() {
        return !path.isEmpty();
    }

    PathFragment poll() {
        return path.poll();
    }

    PathFragment peek() {
        return path.peek();
    }

    @Override
    protected Path clone() {
        return new Path(path);
    }

    @Override
    public String toString() {
        String result = "";
        for (PathFragment fragment : path) {
            result = fragment.appendToPath(result);
        }
        return result;
    }

    private Queue<PathFragment> compilePathFragments(String path) {
        //TODO: this needs some attention but will work for now
        Queue<PathFragment> processed = new LinkedList<PathFragment>();

        //fix initial deep scan
        if(path.startsWith("..")){
           path = path.replaceFirst("\\.\\.", "\\*\\.");
        }


        //fix path scans
        path = path.replace("..", ".*.");


        String[] split = path.split("[\\.|\\[]");

        for (int i = 0; i < split.length; i++) {
            if (split[i].trim().length() > 0) {

                String compiledFragment = split[i].endsWith("]") ? "[" + split[i] : split[i];

                boolean isLeaf = (i == split.length - 1);

                processed.add(new PathFragment(compiledFragment, isLeaf));
            }
        }
        return processed;
    }
}
