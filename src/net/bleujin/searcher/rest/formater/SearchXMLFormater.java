package net.bleujin.searcher.rest.formater;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

import org.apache.ecs.xml.XML;
import org.apache.lucene.index.IndexableField;

import net.bleujin.searcher.common.ReadDocument;
import net.bleujin.searcher.search.SearchRequest;
import net.bleujin.searcher.search.SearchResponse;
import net.bleujin.searcher.util.MyWriter;

public class SearchXMLFormater implements SearchResponseFormater {
	public final static String XML_HEADER = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";

	public StreamingOutput outputStreaming(final SearchResponse iresponse) throws IOException {
		return new StreamingOutput(){

			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {
				MyWriter writer = new MyWriter(output, "UTF-8") ;

				SearchRequest irequest = iresponse.request();
				XML result = new XML("result");

				result.addElement(irequest.toXML());
				result.addElement(iresponse.toXML());
				XML nodes = new XML("nodes");
				appendChild(nodes, iresponse.getDocument());
				result.addElement(nodes);

				writer.write(XML_HEADER);
				result.output(writer);
			}
		} ;
	}

	private void appendChild(XML nodes, List<ReadDocument> docs) throws IOException {

		for (ReadDocument doc : docs) {
			XML node = new XML("node");
			List<IndexableField> fields = doc.fields();
			for (IndexableField field : fields) {
				XML property = new XML("property");
				property.addAttribute("name", field.name());
				property.addAttribute("stored", field.fieldType().stored());
				property.addAttribute("tokenized", field.fieldType().tokenized());
				property.addAttribute("indexed", field.fieldType().indexOptions());
				property.addElement("<![CDATA[" + field.stringValue() + "]]>");
				node.addElement(property);
			}
			nodes.addElement(node);
		}

	}

}
