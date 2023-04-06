import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.lang.reflect.Field;

import static org.junit.Assert.fail;

/**
 *
 *
 */
public class Issue_515 {
    @Test
    public void Test_DocumentContextSerializable1(){
        String json = "{\n" +
                "    \"store\": [\n" +
                "        {\n" +
                "            \"books\": [\n" +
                "                {\n" +
                "                    \"category\": \"reference\",\n" +
                "                    \"author\": \"Nigel Rees\",\n" +
                "                    \"title\": \"Sayings of the Century\",\n" +
                "                    \"price\": 8.95\n" +
                "                }\n" +
                "            ],\n" +
                "            \"address\": [\n" +
                "                {\n" +
                "                    \"city\": \"New York\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"city\": \"Paris\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"books\": [\n" +
                "                {\n" +
                "                    \"category\": \"fiction\",\n" +
                "                    \"author\": \"Evelyn Waugh\",\n" +
                "                    \"title\": \"Sword of Honour\",\n" +
                "                    \"price\": 12.99\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        DocumentContext originContext= JsonPath.parse(json);
        boolean equal=true;
        try {
            FileOutputStream fos=new FileOutputStream("obj.txt");
            ObjectOutputStream outputStream=new ObjectOutputStream(fos);
            outputStream.writeObject(originContext);
            outputStream.close();
            fos.close();
            FileInputStream ios=new FileInputStream("obj.txt");
            ObjectInputStream inputStream=new ObjectInputStream(ios);
            DocumentContext inputObj=(DocumentContext) inputStream.readObject();
            Field[] inputFields=inputObj.getClass().getDeclaredFields(),originFields=originContext.getClass().getDeclaredFields();
            ios.close();
            inputStream.close();
            if(inputFields.length==originFields.length){
                for(int i=0;i<inputFields.length;i++){
                    inputFields[i].setAccessible(true);
                    originFields[i].setAccessible(true);
                    if(!inputFields[i].get(inputObj).equals(originFields[i].get(originContext))){
                        equal=false;
                        break;
                    }
                }
            }else{
                equal=false;
            }
        } catch (IOException | ClassNotFoundException | IllegalAccessException e) {
            fail();
        }finally {
            File file=new File("obj.txt");
            boolean del=file.delete();
            if(!del){
                equal=false;
            }
        }
        Assert.assertTrue(equal);
    }
    @Test
    public void Test_DocumentContextSerializable2(){
        String json = "{}";
        DocumentContext originContext=JsonPath.parse(json);
        boolean equal=true;
        try {
            FileOutputStream fos=new FileOutputStream("obj.txt");
            ObjectOutputStream outputStream=new ObjectOutputStream(fos);
            outputStream.writeObject(originContext);
            outputStream.close();
            fos.close();
            FileInputStream ios=new FileInputStream("obj.txt");
            ObjectInputStream inputStream=new ObjectInputStream(ios);
            DocumentContext inputObj=(DocumentContext) inputStream.readObject();
            Field[] inputFields=inputObj.getClass().getDeclaredFields(),originFields=originContext.getClass().getDeclaredFields();
            ios.close();
            inputStream.close();
            if(inputFields.length==originFields.length){
                for(int i=0;i<inputFields.length;i++){
                    inputFields[i].setAccessible(true);
                    originFields[i].setAccessible(true);
                    if(!inputFields[i].get(inputObj).equals(originFields[i].get(originContext))){
                        equal=false;
                        break;
                    }
                }
            }else{
                equal=false;
            }
        } catch (IOException | ClassNotFoundException | IllegalAccessException e) {
            fail();
        }finally {
            File file=new File("obj.txt");
            boolean del=file.delete();
            if(!del){
                equal=false;
            }
        }
        Assert.assertTrue(equal);
    }
}
