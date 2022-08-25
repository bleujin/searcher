package net.bleujin.searcher.common;

import java.io.Reader;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleDocValuesField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.IndexableFieldType;
import org.apache.lucene.util.BytesRef;

import net.bleujin.searcher.common.MyField.MyFieldType;
import net.bleujin.searcher.index.IndexConfig;
import net.ion.framework.util.DateUtil;
import net.ion.framework.util.NumberUtil;
import net.ion.framework.util.StringUtil;

public abstract class FieldIndexingStrategy {

	public static final FieldIndexingStrategy DEFAULT = new FieldIndexingStrategy() {
		@Override
		public void save(Document doc, MyField myField, final IndexableField ifield) {

			final String fieldName = IKeywordField.Field.reservedId(ifield.name()) ? ifield.name() :  StringUtil.lowerCase(ifield.name());
			
			if (myField.myFieldtype() == MyFieldType.Keyword && !IKeywordField.Field.reservedId(ifield.name())) { 
				doc.add(new SortedDocValuesField(fieldName, new BytesRef(ifield.stringValue())));
			} else if (myField.myFieldtype() == MyFieldType.Number){
				doc.add(new StringField(fieldName, ifield.stringValue(), Store.NO));
				doc.add(new StoredField(fieldName, ifield.numericValue().longValue()));
			} else if (myField.myFieldtype() == MyFieldType.Text){

			} else if (myField.myFieldtype() == MyFieldType.Date){
				// new Date().getTime();
				Date date = DateUtil.stringToDate(ifield.stringValue(), "yyyyMMdd HHmmss") ;
				doc.add(new StringField(fieldName, StringUtil.substringBefore(ifield.stringValue(), " "), Store.NO)) ;
				doc.add(new SortedDocValuesField(fieldName, new BytesRef(ifield.stringValue()))) ;
			}
			doc.add(ifield);  // 
			
		}
	};
	
	public abstract void save(Document doc, MyField myField, IndexableField ifield)  ;

	public static String makeSortFieldName(String fieldName) {
		return fieldName;
		// return (fieldName + MyField.SORT_POSTFIX);
	}

	
	public static final FieldIndexingStrategy create(final IndexConfig iconfig) {
		return new FieldIndexingStrategy() {

			@Override
			public void save(Document doc, final MyField myField, IndexableField ifield) {
				final String fieldName = IKeywordField.Field.reservedId(ifield.name()) ? ifield.name() :  StringUtil.lowerCase(ifield.name());
				

				
				
				if (myField.myFieldtype() == MyFieldType.Keyword && !IKeywordField.Field.reservedId(ifield.name())) { 
					doc.add(new SortedDocValuesField(fieldName, new BytesRef(ifield.stringValue())));
				} else if (myField.myFieldtype() == MyFieldType.Number){
					doc.add(new StringField(fieldName, ifield.stringValue(), Store.NO));
					doc.add(new StoredField(fieldName, ifield.numericValue().longValue()));
				} else if (myField.myFieldtype() == MyFieldType.Text){

				} else if (myField.myFieldtype() == MyFieldType.Date){
					// new Date().getTime();
					Date date = DateUtil.stringToDate(ifield.stringValue(), "yyyyMMdd HHmmss") ;
					doc.add(new StringField(fieldName, StringUtil.substringBefore(ifield.stringValue(), " "), Store.NO)) ;
					doc.add(new SortedDocValuesField(fieldName, new BytesRef(ifield.stringValue()))) ;
				}
				doc.add(ifield);  // 
			}
		} ;
	}
}


//doc.add(new StringField("name", name, Store.YES)); // keyword
//doc.add(new SortedDocValuesField("name", new BytesRef(name)));
//
//doc.add(new TextField("content", content, Store.YES)); // text
//
//doc.add(new NumericDocValuesField("age", age)); // number
//doc.add(new StringField("age", "" + age, Store.NO)); 
//doc.add(new StoredField("age", age));
//
//doc.add(new StringField("birth", DateUtil.dateToString(birth, "yyyyMMdd-HHmmss"), Store.YES)); //date
////doc.add(new StringField("birth", DateUtil.dateToString(birth, "yyyyMMdd"), Store.NO)); 
//doc.add(new SortedDocValuesField("birth", new BytesRef(DateUtil.dateToString(birth, "yyyyMMdd-HHmmss"))));
