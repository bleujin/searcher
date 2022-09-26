package net.bleujin.searcher.index;

import net.bleujin.searcher.common.WriteDocument;

public interface IndexJob<T> {
	
	public final static IndexJob<Void> SAMPLE_INSERT = new IndexJob<Void>() {
		@Override
		public Void handle(IndexSession isession) throws Exception {
			WriteDocument wdoc = isession.newDocument("bleujin").keyword("name", "bleujin").number("age", 20).text("intro", "Hello Bleujin") ;
			isession.insertDocument(wdoc) ;
			return null;
		}
	};
	
	public T handle(IndexSession isession) throws Exception ;


}
