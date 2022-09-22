package net.bleujin.searcher.rest;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;

import net.bleujin.searcher.SearchController;
import net.bleujin.searcher.reader.InfoHandler;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.MapUtil;
import net.ion.radon.core.ContextParam;

@Path("/")
public class InfoLet {

	@Path("/info.{format}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JsonObject info(@ContextParam("SDC") SearchController sdc, @DefaultValue("html") @PathParam("format") String format) throws Exception {
		Map<String, Object> infoMap = sdc.info(new InfoHandler<Map<String, Object>>() {
			@Override
			public Map<String, Object> view(IndexReader ireader, Directory dir) throws IOException {
				Map<String, Object> result = MapUtil.newMap() ;
				
				result.put("directory", dir) ;
				result.put("indexExists", DirectoryReader.indexExists(dir)) ;
				result.put("commits", DirectoryReader.listCommits(dir)) ;
				result.put("maxDoc", ireader.maxDoc()) ;
				result.put("numDoc", ireader.numDocs()) ;
				
				return result;
			}
		}) ;
		
		return JsonObject.fromObject(infoMap) ;
	}
	
	
}
