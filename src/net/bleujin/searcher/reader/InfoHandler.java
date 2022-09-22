package net.bleujin.searcher.reader;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.store.Directory;

import net.ion.framework.util.MapUtil;

public interface InfoHandler<T> {
	public InfoHandler<Map<String, String>> COMMIT_DATA = new InfoHandler<Map<String, String>>() {
		@Override
		public Map<String, String> view(IndexReader ireader, Directory dir) throws IOException {
			List<IndexCommit> list = DirectoryReader.listCommits(dir);
			if (list.size() <= 0) return MapUtil.EMPTY ;
			return list.get(0).getUserData() ;
		}
	};

	public InfoHandler<Set<String>> FIELDNAMES = new InfoHandler<Set<String>>() {
		@Override
		public Set<String> view(IndexReader ireader, Directory dir) throws IOException {
			HashSet<String> fieldnames = new HashSet<String>();
			
			
			
			for (LeafReaderContext subReader : ireader.leaves()) {
			    FieldInfos fields = subReader.reader().getFieldInfos();
			    for (FieldInfo fieldname : fields) {
			        fieldnames.add(fieldname.name);
			    }
			}
			
			return fieldnames ;
		}
	};

	
	public T view(IndexReader ireader, Directory dir) throws IOException ;
	
}
