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

import net.ion.framework.util.MapUtil;

public class InfoReader {

	public interface InfoHandler<T> {
		public T view(IndexReader ireader, DirectoryReader dreader) throws IOException ;
	}
	
	private IndexReader ireader ;
	private DirectoryReader dreader ;
	private InfoReader(IndexReader ireader, DirectoryReader dreader)  {
		this.ireader = ireader ;
		this.dreader = dreader ;
	}

	public final static InfoReader create(IndexReader ireader, DirectoryReader dreader)  {
		return new InfoReader(ireader, dreader);
	}

	
//	public Collection<IndexCommit> listCommits() throws IOException{
//		return (Collection<IndexCommit>)getIndexReader().listCommits(searcher.dir()) ;
//	}
	
	public <T> T info(InfoHandler<T> ihandler) throws IOException{
//		Lock lock = searcher.central().readLock() ;
		try {
//			lock.lock(); 
			T result = ihandler.view(ireader, dreader);
			return result ;
		} finally {
//			lock.unlock(); 
		}
	}
	
	public int maxDoc() throws IOException{
		return ireader.maxDoc() ;
	}
	
	public int numDoc() throws IOException{
		return ireader.numDocs() ;
	}

	public Set<String> getFieldNames() throws IOException{
		HashSet<String> fieldnames = new HashSet<String>();
		for (LeafReaderContext subReader : dreader.leaves()) {
		    FieldInfos fields = subReader.reader().getFieldInfos();
		    for (FieldInfo fieldname : fields) {
		        fieldnames.add(fieldname.name);
		    }
		}
		
		return fieldnames ;
	}

	public Map<String, String> commitUserData() throws IOException {
		List<IndexCommit> list = DirectoryReader.listCommits(dreader.directory());
		if (list.size() <= 0) return MapUtil.EMPTY ;
		return list.get(0).getUserData() ;
	}
	
}
