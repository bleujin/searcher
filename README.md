# searcher
apply lucene 9.3

{

	private SearchController sdc;

	@Override
	public void setUp() throws Exception {
		this.sdc = SearchControllerConfig.newRam().build(OpenMode.CREATE_OR_APPEND) ;
	}
	
	public void tearDown() throws Exception  {
		this.sdc.close() ;
	}
	
	public void testInterface() throws Exception {
		
		sdc.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws IOException {
				IndexConfig iconfig = isession.indexConfig() ;
				
				WriteDocument wdoc = isession.newDocument("bleujin").keyword("name", "bleujin").number("age", 20).text("content", "Hello Bleujin") ;
				isession.insertDocument(wdoc) ;
				return null;
			}
		}) ;

		sdc.search(new SearchJob<Void>() {
			@Override
			public Void handle(SearchSession ss) throws IOException {
				SearchResponse sres = ss.createRequest("bleujin").find();
				Debug.debug(sres, sres.totalCount()) ;
				return null;
			}
		}) ;
		
		sdc.close();
	}
}
