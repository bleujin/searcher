package net.bleujin.searcher.rest;

import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.StreamingOutput;

import net.bleujin.searcher.SearchController;
import net.bleujin.searcher.common.ReadDocument;
import net.bleujin.searcher.rest.formater.SearchDocumentFormater;
import net.bleujin.searcher.search.SearchResponse;
import net.ion.radon.core.ContextParam;

@Path("/")
public class ListLet {

	@Path("/list.{format}")
	@GET
	public StreamingOutput listInfo(@ContextParam("SDC") SearchController sdc, @DefaultValue("0") @FormParam("skip") int skip, @DefaultValue("100") @FormParam("offset") int offset, 
				@DefaultValue("html") @PathParam("format") String format) throws Exception {

		SearchResponse response = sdc.newSearcher().createRequest("").skip(skip).offset(offset).find();
		
		List<ReadDocument> docs = response.getDocument() ;
		Class clz = Class.forName("net.bleujin.searcher.rest.formater.Search" + format.toUpperCase() + "Formater");
		SearchDocumentFormater af = (SearchDocumentFormater) clz.newInstance();
		
		return af.outputStreaming(docs) ;
	}

}
