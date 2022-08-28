package net.bleujin.searcher.rest;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.StreamingOutput;

import net.bleujin.searcher.SearchController;
import net.bleujin.searcher.rest.formater.SearchResponseFormater;
import net.bleujin.searcher.search.SearchResponse;
import net.ion.radon.core.ContextParam;


@Path("/")
public class SearchLet {


	@Path("/search.{format}")
	@GET
	public StreamingOutput search(@ContextParam("SDC") SearchController sdc, @DefaultValue("html") @PathParam("format") String format, @FormParam("query") String query, @FormParam("sort") String sort, 
				@DefaultValue("0") @FormParam("skip") int skip, @DefaultValue("100") @FormParam("offset") int offset) throws Exception {
		
		SearchResponse response = sdc.newSearcher().createRequest(query).ascending(sort).skip(skip).offset(offset).find() ;
		
		Class clz = Class.forName("net.bleujin.searcher.rest.formater.Search" + format.toUpperCase() + "Formater");
		SearchResponseFormater af = (SearchResponseFormater) clz.newInstance();
		return af.outputStreaming(response);
	}


}
