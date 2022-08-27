package net.bleujin.searcher.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;

import net.ion.framework.util.StringUtil;

public class SortExpression {

	private static String[] KEYWORD_FIELD = new String[]{"_doc", "_score"};
	private static String[] ORDER = new String[]{"asc", "desc"};

	private SearchConfig sconfig;
	public SortExpression(SearchConfig sconfig){
		this.sconfig = sconfig ;
	}
	
	public SortField[] parse(String expression) {
		return parseExpression(StringUtil.split(expression, ",")) ;
	}
	
	private SortField[] parseExpression(String... fields) {
		if (fields == null || fields.length == 0 || (fields.length == 1 && StringUtil.isBlank(fields[0]))) return new SortField[]{SortField.FIELD_SCORE} ;
		
		
		List<SortField> result = new ArrayList<SortField>() ; 
		for(String field : fields){
			if (StringUtil.isBlank(field)) continue ;
			
			String[] sps = StringUtil.split(field, " =") ;

			String fieldName = StringUtil.lowerCase(StringUtil.trim(sps[0])) ; // mandatory
			Type sortFieldType = SortField.Type.STRING ;
			boolean isRerverse = false ;
			
			if (ArrayUtils.contains(KEYWORD_FIELD, fieldName) && sps.length == 1) {
				result.add( ("_doc".equals(fieldName)) ? SortField.FIELD_DOC : SortField.FIELD_SCORE ) ;
			} else {
				if (sps.length == 2 && ArrayUtils.contains(ORDER, sps[1])) {
					isRerverse = "desc".equalsIgnoreCase(StringUtil.trim(sps[1])) ;
				}
				
				sortFieldType = sconfig.isNumField(fieldName) ? SortField.Type.LONG : SortField.Type.STRING ;
				result.add(new SortField(fieldName, sortFieldType, isRerverse)) ;
			}
		}
		
		return (SortField[])result.toArray(new SortField[0]) ;
	}

	
}
