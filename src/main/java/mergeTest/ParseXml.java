package mergeTest;

import model.Component;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ParseXml {

	public static void main(String[] args) throws JDOMException, IOException {
		ParseXml parseXml = new ParseXml();
		List<Component> l = parseXml.run("PageXml/ZT7DP759AESC99KV/jiguang.chat.activity.MainActivity.xml");
		for(int i = 0;i < l.size();i++)
			System.out.println(l.get(i).getLocator());
		System.out.println(parseXml.idList.size());
		System.out.println(parseXml.textXpathList.size());
		System.out.println(parseXml.contentDescXpathList.size());
		System.out.println(parseXml.indexXpathList.size());


//		ParseXml parseXml = new ParseXml();
//		String path1 = "PageSource\\MainActivity1.xml";
//		String path2 = "PageSource\\MainActivity2.xml";
//		String path3 = "PageSource\\MainActivity3.xml";
//
////		System.out.println("MainActivity1");
//		List<Component> l1 = parseXml.run(path1);	//解析pagesource的XML文件,只获取属性clickable值为true的组件
////		System.out.println(l1.size());
////		for(int i = 0;i<l1.size();i++)
////		System.out.println(l1.get(i).getxPath());
//
////		System.out.println("MainActivity2");
//		List<Component> l2 = parseXml.run(path2);	//解析pagesource的XML文件,只获取属性clickable值为true的组件
////		System.out.println(l2.size());
////		for(int i = 0;i<l2.size();i++)
////		System.out.println(l2.get(i).getxPath());
//
////		System.out.println("MainActivity3");
//		List<Component> l3 = parseXml.run(path3);	//解析pagesource的XML文件,只获取属性clickable值为true的组件
////		System.out.println(l3.size());
////		for(int i = 0;i<l3.size();i++)
////		System.out.println(l3.get(i).getxPath());
//
//		int length=0;
//		int count=0;
//		if(l1.size()<l2.size())
//		{
//			length=l1.size();
//		}
//		else
//		{
//			length=l2.size();
//		}
//
//		for(int m=0;m<length;m++){	//统计原控件列表和现控件列表中,控件的xPath值相同的元素个数
//			for(int n=0;n<length;n++){
//				if((l2.get(n).getLocator()).equals(l1.get(m).getLocator())){
//					count++;
//				}
//			}
//		}
//
//		System.out.println("count = "+count);
//		if((2*count)>length)
//		{	//不相同的控件未过半，认为页面只发生了部分改变
//			System.out.println("same components more than half");
//		}
//		else
//		{
//			System.out.println("same components no more than half");
//		}
	}
	List<String> idList = new ArrayList<String>();
	List<String> textXpathList = new ArrayList<String>();
	List<String> contentDescXpathList = new ArrayList<String>();
	List<String> indexXpathList = new ArrayList<String>();

	List<Component> run(String path){
		List<Component> list = null;
		try {
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(new File(path));
			Element foo = doc.getRootElement();
			list = new ArrayList();
			list = DFS(foo,list);
			list = setComponentLocator(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	List<Component> DFS(Element e,List<Component> list){
		List allChildren = e.getChildren();
		if(allChildren.size() <= 0)
		{
			recordAllLocators(e);
			//对于叶子节点，直接判断本节点的clickable，true则将本节点加入list
			if(e.getAttributeValue("clickable") != null && e.getAttributeValue("clickable").equals("true"))
			{
				list.add(element2Component(e));
			}
			return list;
		}else
		{
			for(int i = 0;i < allChildren.size();i++)
			{//对于父亲节点，先去找自己孩子中clickable = true 的加入list（深度优先）
				DFS((Element)allChildren.get(i),list);
			}
			recordAllLocators(e);
			//孩子已遍历完，再看本节点clickable是否为true，是则将本节点加入list
			if(e.getAttributeValue("clickable") != null && e.getAttributeValue("clickable").equals("true"))
			{
				list.add(element2Component(e));
			}
			return list;
		}
	}
	Component element2Component(Element e){
		Component component = new Component();
		component.setResource_id(e.getAttributeValue("resource-id"));
		component.setIndex(e.getAttributeValue("index"));
		component.setText(e.getAttributeValue("text"));
		component.setPackagename(e.getAttributeValue("package"));
		component.setContent_desc(e.getAttributeValue("content-desc"));
		component.setCheckable(e.getAttributeValue("checkable"));
		component.setChecked(e.getAttributeValue("checked"));
		component.setClickable(e.getAttributeValue("clickable"));
		component.setEnabled(e.getAttributeValue("enabled"));
		component.setFocusable(e.getAttributeValue("focusable"));
		component.setFocused(e.getAttributeValue("focused"));
		component.setScrollable(e.getAttributeValue("scrollable"));
		component.setLong_clickable(e.getAttributeValue("long-clickable"));
		component.setPassword(e.getAttributeValue("password"));
		component.setSelected(e.getAttributeValue("selected"));
		
		//Set boundsXpath as default component locator
		String classname = e.getAttributeValue("class");
		String bounds = e.getAttributeValue("bounds");
		String boundsXpath = "//" + classname + "[contains(@bounds,'" + bounds + "')]";
		component.setBounds(bounds);
		component.setClassname(classname);
		component.setLocator(boundsXpath);
		return component;
	}
	void recordAllLocators(Element e){
		String classname = e.getAttributeValue("class");
		if(e.getAttributeValue("resource-id") != null && !e.getAttributeValue("resource-id").isEmpty()){
			idList.add(e.getAttributeValue("resource-id"));
		}
		if(e.getAttributeValue("text") != null && !e.getAttributeValue("text").isEmpty()){
			textXpathList.add("//" + classname + "[contains(@text,'" + e.getAttributeValue("text") + "')]");
		}
		if(e.getAttributeValue("content-desc") != null && !e.getAttributeValue("content-desc").isEmpty()){
			contentDescXpathList.add("//" + classname + "[contains(@content-desc,'" + e.getAttributeValue("content-desc") + "')]");
		}
		if(e.getAttributeValue("index") != null && !e.getAttributeValue("index").isEmpty()){
			indexXpathList.add("//" + classname + "[contains(@index,'" + e.getAttributeValue("index") + "')]");
		}
	}
	List<Component> setComponentLocator(List<Component> list){
		List<Component> newList = new ArrayList<Component>();
		for(Component component : list){
			String id = component.getResource_id();
			String textXpath = "//" + component.getClassname() + "[contains(@text,'" + component.getText() + "')]";
			String contentDescXpath = "//" + component.getClassname() + "[contains(@content-desc,'" + component.getContent_desc() + "')]";
			String indexXpath = "//" + component.getClassname() + "[contains(@index,'" + component.getIndex() + "')]";
			if(component.getResource_id() != null && !component.getResource_id().isEmpty() && idList.indexOf(id) == idList.lastIndexOf(id)){
				component.setLocator(id);
			}else if(component.getText() != null && !component.getText().isEmpty() && textXpathList.indexOf(textXpath) == textXpathList.lastIndexOf(textXpath)){
				component.setLocator(textXpath);
			}else if(component.getContent_desc() != null && !component.getContent_desc().isEmpty() && contentDescXpathList.indexOf(contentDescXpath) == contentDescXpathList.lastIndexOf(contentDescXpath)){
				component.setLocator(contentDescXpath);
			}else if(component.getIndex() != null && !component.getIndex().isEmpty() && indexXpathList.indexOf(indexXpath) == indexXpathList.lastIndexOf(indexXpath)){
				component.setLocator(indexXpath);
			}
			newList.add(component);
		}
		return newList;
	}
	String form(String str){
		String result = "";
		String[] array = str.split(" ");
		for(int i = 0;i < array.length;i++){
			if(!array[i].equals("")){
				result = result + array[i] + " ";
			}
		}
		if(result.length() <= 0){

		}else{
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}
	boolean isInList(List l,String str){
		for(int i = 0;i < l.size();i++){
			String buff = (String)l.get(i);
			if(buff.equals(str)){
				return true;
			}
		}
		return false;
	}

}