package net.bleujin.searcher.rest;

import java.util.List;
import java.util.Map.Entry;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;

import org.jboss.resteasy.spi.HttpRequest;

import net.bleujin.searcher.SearchController;
import net.bleujin.searcher.common.MyField;
import net.bleujin.searcher.common.WriteDocument;
import net.bleujin.searcher.index.IndexJob;
import net.bleujin.searcher.index.IndexSession;
import net.ion.radon.core.ContextParam;

@Path("/")
public class IndexLet {

	@Path("/index.{format}")
	@POST
	public String indexParam(@ContextParam("SDC") SearchController sdc,  @DefaultValue("html") @PathParam("format") String format, @Context HttpRequest request) throws Exception {
		final MultivaluedMap<String, String> map = request.getFormParameters();
		sdc.index(new IndexJob<Void>() {
			public Void handle(IndexSession isession) throws Exception {
				WriteDocument doc = isession.newDocument();
				for (Entry<String, List<String>> entry : map.entrySet()) {
					doc.add(MyField.unknown(entry.getKey(), entry.getValue())) ;
				}
				isession.insertDocument(doc) ;
				return null;
			}
		}) ;
		
		return "indexed" ;
	}
}
