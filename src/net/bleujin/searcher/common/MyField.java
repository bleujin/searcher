package net.bleujin.searcher.common;

import java.text.ParseException;
import java.util.Date;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.IndexableFieldType;

import net.bleujin.searcher.index.VTextField;
import net.ion.framework.util.DateUtil;
import net.ion.framework.util.NumberUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.framework.util.StringUtil;

public class MyField {

	public enum MyFieldType {
		Keyword, Number, Date, Text
	}
	
	private IndexableField ifield;
	private MyFieldType mtype;
	private boolean ignoreBody; 

	public MyField(Field ifield, MyFieldType mtype){
		this.ifield = ifield ;
		this.mtype = mtype ;
	}

	public static MyField forWriteDoc(IndexableField ifield) {
		if (ifield.fieldType().omitNorms()) { // keyword or dateformat
			if (isDateFormat(ifield.stringValue())) {
				return keyword(ifield.name(), ifield.stringValue()) ;
			} else {
				return date(ifield.name(), toDateString(ifield.stringValue())) ;
			}
		} else { // number or text
			if (ifield.numericValue() == null) { // text 
				return  text(ifield.name(), ifield.stringValue()) ;
			} else { // number
				return number(ifield.name(), ifield.numericValue().longValue()) ; 
			}
		}
	}
	
	
	private static Date toDateString(String val) {
		 try {
			return DateUtil.stringToDate(val) ;
		} catch (ParseException e) {
			return new Date() ;
		}
	}
	
	
	
	public String name() {
		return ifield.name() ;
	}
	public String stringValue() {
		return ifield.stringValue() ;
	}
	
	
	public MyField changeType(MyFieldType mtype) {
		if (mtype == MyFieldType.Number) {
			this.ifield = new NumericDocValuesField(ifield.name(), Long.parseLong(ifield.stringValue())) ; 
		} else if (mtype == MyFieldType.Keyword) {
			this.ifield = new StringField(ifield.name(), ifield.stringValue(), Store.YES) ;
		} else if (mtype == MyFieldType.Text) {
			this.ifield = new VTextField(ifield.name(), ifield.stringValue(), Store.YES) ;
		} else if (mtype == MyFieldType.Date) {
			this.ifield = new StringField(ifield.name(), ifield.stringValue(), Store.YES) ;
		}
			
		this.mtype = mtype ;
		return this ;
	}
	
	public IndexableFieldType fieldType(){
		return ifield.fieldType() ;
	}
	
	public MyFieldType myFieldtype(){
		return mtype ;
	}

	
	
	public static MyField keyword(String name, String value) {
		return new MyField(new StringField(name, value, Store.YES), MyFieldType.Keyword) ;
	}

	public static MyField number(String name, long value) {
		return new MyField(new NumericDocValuesField(name, value), MyFieldType.Number) ;
	}
	
	public static MyField number(String name, int value) {
		return new MyField(new NumericDocValuesField(name, value), MyFieldType.Number);
	}

	public static MyField text(String name, String value) {
		return new MyField(new VTextField(name, value, Store.YES), MyFieldType.Text);
	}

	public static MyField notext(String name, String value) {
		return new MyField(new VTextField(name, value, Store.NO), MyFieldType.Text);
	}
	
	public static MyField date(String name, Date date){ 
		if (date == null) throw new IllegalArgumentException(date + " is not dateformat") ;
		return new MyField(new StringField(name, DateUtil.dateToString(date, "yyyyMMdd HHmmss"), Store.YES), MyFieldType.Date) ;
	}

	public static MyField date(String name, int yyyymmdd, int hh24miss){
		String ymd = String.valueOf(yyyymmdd) ;
		String hms = String.valueOf(hh24miss) ;
		Date date = new Date(NumberUtil.toInt(StringUtil.substring(ymd, 0, 4)) - 1900, 
				NumberUtil.toInt(StringUtil.substring(ymd, 4, 6))-1, 
				NumberUtil.toInt(StringUtil.substring(ymd, 6, 8)), 
				NumberUtil.toInt(StringUtil.substring(hms, 0, 2)), 
				NumberUtil.toInt(StringUtil.substring(hms, 2, 4)), 
				NumberUtil.toInt(StringUtil.substring(hms, 4, 6))) ;
		return date(name, date) ;
	}


	public static MyField unknown(String name, Long value) {
		return number(name, value) ;
	}
	public static MyField unknown(String name, Integer value) {
		return number(name, value) ;
	}
	public static MyField unknown(String name, Date value) {
		return date(name, value) ;
	}
	public static MyField unknown(String name, String value){
		if (StringUtil.isNotBlank(value) && StringUtil.isNumeric(value)) {
			return number(name, Long.parseLong(value)) ;
		} else if (isKeywordType(value)){
			return keyword(name, value) ;
		} else
			return text(name, value);
	}

	public static MyField manual(String name, String value, Store store, boolean analyze, MyFieldType fieldType){
		if (StringUtil.isBlank(value)) return new MyField(new StringField(name, "", store), fieldType) ;
		
		if (isKeywordType(value)){
			return keyword(name, value) ;
		} else
			return new MyField( analyze ? (new VTextField(name, value, store)) : (new StringField(name, value, store)), fieldType);
	}

	public static MyField unknown(String name, Object value) {
		if (value == null){
			return keyword(name, "") ;
		}
		if (value.getClass().equals(Long.class)){
			return number(name, (Long)value) ;
		} else if(value.getClass().equals(Integer.class)){
			return number(name, (Integer)value * 1L) ;
		} else if(value.getClass().equals(Date.class)){
			return date(name, (Date)value) ;
		} else if (CharSequence.class.isInstance(value)) {
			return unknown(name, value.toString()) ;
		} else {
			return text(name, ObjectUtil.toString(value)) ;
		}
	}

	public static MyField noIndex(String name, String value) {
		return new MyField(new StoredField(name, value), MyFieldType.Text);
	}

	public static MyField noIndex(String name, long value) {
		return new MyField(new StoredField(name, value), MyFieldType.Number);
	}

	public IndexableField indexField(FieldIndexingStrategy strategy, Document doc) {
		strategy.save(doc, this, ifield) ;
		return ifield ;
	}

	
	
	
	
	private static boolean isKeywordType(String str) {
		if (str == null) {
			return false;
		} else {
			char[] chars = str.toCharArray();
			int i = 0;
			for (int last = chars.length; i < last; ++i) {
				char ch = chars[i];
				if ((ch < 'A' || ch > 'Z') && (ch < 'a' || ch > 'z') && (ch < '0' || ch > '9') && ch != '_' && ch != '-') {

					return false;
				}
			}
			return true;
		}
	}


	private static boolean isDateFormat(String val) {
		try {
			DateUtil.stringToDate(val) ;
			return true ;
		} catch(IllegalArgumentException | ParseException e) {
			return false ;
		}
	}

	public boolean ignoreBody() {
		return ignoreBody;
	}

	public MyField ignoreBody(boolean ignoreBody) {
		this.ignoreBody = ignoreBody ;
		return this;
	}
}

