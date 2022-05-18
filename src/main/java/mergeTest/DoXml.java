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

public class DoXml {
	public static void main(String[] args) throws JDOMException, IOException {
		DoXml doXml = new DoXml();
		String path = "PageSource\\2.xml";
		List<Component> l = doXml.run(path);
		System.out.println(l.size());
		System.out.println(l.hashCode());
		for(int i = 0;i < l.size();i++)
		System.out.println(l.get(i).getLocator());
	}
	 List<Component> run(String path){
		List<Component> list = null;
		try { 
			SAXBuilder builder = new SAXBuilder(); 
			Document doc = builder.build(new File(path)); 
			Element foo = doc.getRootElement();
			list=new ArrayList();
			list=DFS(foo,list);
		} catch (Exception e) { 
			e.printStackTrace(); 
			} 
		return list;
	}
	List<Component> DFS(Element e,List<Component> list){
		List allChildren = e.getChildren(); 
		if(allChildren.size() <= 0){
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
			
			String classname=e.getAttributeValue("class");			
			String bounds=e.getAttributeValue("bounds");
			String xPath = "//" + classname + "[contains(@bounds,'"+bounds+"')]";
			component.setBounds(bounds);
			component.setClassname(classname);
			component.setLocator(xPath);
			
			list.add(component);
			return list;
		}else{
			for(int i = 0;i < allChildren.size();i++) {
				DFS((Element)allChildren.get(i),list);
			}
			return list;
		} 
		

	}
}
