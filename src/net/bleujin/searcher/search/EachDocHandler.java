package net.bleujin.searcher.search;

import java.util.List;

import net.bleujin.searcher.common.ReadDocument;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;

public interface EachDocHandler<T> {

	public final static EachDocHandler<Void> DEBUG = new EachDocHandler<Void>() {

		@Override
		public <T> T handle(EachDocIterator iter) {
			while (iter.hasNext()) {
				Debug.line(iter.next());
			}
			return null;
		}
	};

	public final static EachDocHandler<List<ReadDocument>> TOLIST = new EachDocHandler<List<ReadDocument>>() {
		@Override
		public List<ReadDocument> handle(EachDocIterator iter) {
			List<ReadDocument> result = ListUtil.newList();
			while (iter.hasNext()) {
				result.add(iter.next());
			}
			return result;
		}
	};

	public <T> T handle(EachDocIterator iter);
}
