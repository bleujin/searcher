package net.bleujin.searcher.extend;

public class HighlightTerm {

	public final static HighlightTerm NONE = new HighlightTerm("", "" ) ;
	private final String field;
	private final String matchedValue;
	
	public HighlightTerm(String field, String matchedValue) {
		this.field = field ;
		this.matchedValue = matchedValue ;
	}
	
	public String field() {
		return field ;
	}
	
	public String matcString() {
		return matchedValue ;
	}
	
}
