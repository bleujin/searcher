package net.bleujin.searcher.index.policy;

import java.io.IOException;

import net.bleujin.searcher.common.AbDocument.Action;
import net.bleujin.searcher.common.WriteDocument;
import net.bleujin.searcher.index.IndexSession;



public interface IWritePolicy {
	public void begin(IndexSession session) throws IOException ;
	public Action apply(IndexSession session, WriteDocument doc) throws IOException ;
	public void end(IndexSession session) ;
}
