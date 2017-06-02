package com.dd.news.test;

import java.util.List;

/**
 * Created by J.Tommy on 17/3/23.
 */

public class Math {

    public static void main(String[] args) {
        Tree rootTree = new Tree();
        int height=getTreeHeight(rootTree);
        System.out.println("height=" + height);

    }

    private static class Tree {
        List<Tree> mTrees;
    }


    public static int getTreeHeight(Tree tree) {
        int height = 0;
        int lastTempHeight = 0;
        if (tree.mTrees != null && tree.mTrees.size() > 0) {
            height+=1;
            for (Tree tempTree : tree.mTrees) {
                int tempHeight = getTreeHeight(tempTree);
                if (tempHeight > lastTempHeight) {
                    lastTempHeight = tempHeight;
                }
            }
            height += lastTempHeight;
        }
        return height;
    }
}
