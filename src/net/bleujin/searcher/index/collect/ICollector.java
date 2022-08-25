package net.bleujin.searcher.index.collect;

import java.util.Date;

import org.apache.http.impl.cookie.DateUtils;

import net.bleujin.searcher.index.handler.DocumentHandler;


public interface ICollector  {
	
	final static String DEFAULT_NAME  = DateUtils.formatDate(new Date(), "yyyyMMdd") + "/DEFAULT";

	public void collect() ;
	public void shutdown(String cause) ;
	public String getCollectName() ;
	public DocumentHandler getDocumentHandler() ;
	public void setDocumentHandler(DocumentHandler handler) ;
}
