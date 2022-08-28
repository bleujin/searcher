package net.bleujin.searcher.index;

import java.io.Reader;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;

public final class VTextField extends Field {
	public static final FieldType TYPE_NOT_STORED = new FieldType();
	public static final FieldType TYPE_STORED = new FieldType();

	public VTextField(String name, Reader reader) {
		super(name, reader, TYPE_NOT_STORED);
	}

	public VTextField(String name, String value, Store store) {
		super(name, value, store == Store.YES ? TYPE_STORED : TYPE_NOT_STORED);
	}

	public VTextField(String name, TokenStream stream) {
		super(name, stream, TYPE_NOT_STORED);
	}

	static {
		TYPE_NOT_STORED.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
		TYPE_NOT_STORED.setTokenized(true);
		TYPE_NOT_STORED.setStoreTermVectors(true);
		TYPE_NOT_STORED.freeze();
		TYPE_STORED.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
		TYPE_STORED.setTokenized(true);
		TYPE_STORED.setStored(true);
		TYPE_STORED.setStoreTermVectors(true);
		TYPE_STORED.freeze();
	}
}