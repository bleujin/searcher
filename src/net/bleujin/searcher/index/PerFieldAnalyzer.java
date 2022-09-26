package net.bleujin.searcher.index;

import java.util.Collections;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.DelegatingAnalyzerWrapper;

import net.ion.framework.util.MapUtil;

public class PerFieldAnalyzer extends DelegatingAnalyzerWrapper {
	
	private final Analyzer defaultAnalyzer;
	private final Map<String, Analyzer> fieldAnalyzers;

	public PerFieldAnalyzer(Analyzer defaultAnalyzer, Map<String, Analyzer> fieldAnalyzers) {
		super(PER_FIELD_REUSE_STRATEGY);
		this.defaultAnalyzer = defaultAnalyzer;
		this.fieldAnalyzers = fieldAnalyzers != null ? fieldAnalyzers : Collections.emptyMap();
	}

	protected Analyzer getWrappedAnalyzer(String fieldName) {
		Analyzer analyzer = (Analyzer) this.fieldAnalyzers.get(fieldName);
		return analyzer != null ? analyzer : this.defaultAnalyzer;
	}

	public PerFieldAnalyzer defineAnalyzer(String name, Analyzer analyzer) {
		fieldAnalyzers.put(name, analyzer) ;
		return this ;
	}

	public PerFieldAnalyzer removeField(String fieldName) {
		fieldAnalyzers.remove(fieldName) ;
		return this ;
	}

	
	public PerFieldAnalyzer putField(String fieldName, Analyzer analyzer) {
		fieldAnalyzers.put(fieldName, analyzer) ;
		return this ;
	}
	
	public PerFieldAnalyzer copyAnalyzer(Analyzer defaultAnalyzer) {
		Map<String, Analyzer> copyMap = MapUtil.newCaseInsensitiveMap() ;
		fieldAnalyzers.entrySet().forEach(entry ->{
			copyMap.put(entry.getKey(), entry.getValue()) ;
		});
		
		PerFieldAnalyzer result = new PerFieldAnalyzer(defaultAnalyzer, copyMap) ;
		return result ;
	}


}
