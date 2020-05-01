package com.nowcoder.community.utils;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SensitiveFilter.class);

    private static final String REPLACEMENT = "***";

    //根节点
    private TrieNode rootNode = new TrieNode();

    //初始化字典树
    @PostConstruct
    public void init(){
        try(InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");//从类目录下加载文件资源
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));){

            String keyword;
            while((keyword=reader.readLine())!=null){
                this.addKeyWord(keyword);
            }

        }catch (IOException e){

        }
    }

    //添加敏感词,字典树插入操作
    private void addKeyWord(String keyword){
        TrieNode node = rootNode;
        for(int i = 0; i < keyword.length(); i++){
            char c = keyword.charAt(i);
            TrieNode subNode = node.getSubNode(c);
            if(subNode == null){
                subNode = new TrieNode();
                node.addSubNode(c,subNode);
            }
            node = subNode;
            if(i == keyword.length()-1){
                node.setKeywordEnd(true);  //设置结束位置为敏感词
            }
        }
    }

    /**
     * 过滤敏感词
     * @param text 待过滤文本
     * @return
     */
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }

        //指针1
        TrieNode tmpNode = rootNode;
        //指针2
        int begin = 0;
        //指针3
        int position = 0;
        //结果
        StringBuilder ans = new StringBuilder("");
        int len = text.length();
        while(position < len){
            char c = text.charAt(position);
            //跳过符号
            if(isSymbol(c)){
                if(tmpNode == rootNode){
                    ans.append(c);
                    begin++;
                }
                position++;
            }else{
                tmpNode = tmpNode.getSubNode(c);
                if(tmpNode == null){  //不是敏感词
                    ans.append(text.charAt(begin));
                    position = ++begin;
                    tmpNode = rootNode;
                }else if(tmpNode.isKeywordEnd()){ //是敏感词
                    ans.append(REPLACEMENT);
                    begin = ++position;
                }else{
                    position++;
                }
            }
        }

        //将最后一批结果插入
        ans.append(text.substring(begin));
        return ans.toString();
    }

    //判断是否为符号
    private boolean isSymbol(char c){
        //0x9fff~0x2e80 东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }
    /**
     * 字典树
     */
    private class TrieNode{

        //关键字结束标识
        private boolean isKeywordEnd = false;

        //子节点
        private Map<Character,TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        //添加子节点
        public void addSubNode(Character c, TrieNode node){
            subNodes.put(c,node);
        }

        //获取子节点
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }
}
