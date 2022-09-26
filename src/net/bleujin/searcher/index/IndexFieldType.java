package net.bleujin.searcher.index;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.bleujin.searcher.common.MyField;
import net.bleujin.searcher.common.MyField.MyFieldType;
import net.ion.framework.util.MapUtil;

public class IndexFieldType {

	private Map<String, MyField.MyFieldType> typeMap = MapUtil.newCaseInsensitiveMap() ;
	public void decideFieldType(String name, MyFieldType mtype) {
		typeMap.put(name, mtype);
	}

	public boolean isNumericField(String field) {
		return typeMap.get(field) == MyFieldType.Number;
	}
	
	public MyFieldType fieldType(String name) {
		return typeMap.get(name) ;
	}
	
	public Set<Entry<String, MyFieldType>> entrySet() {
		return typeMap.entrySet() ;
	}
	
}
