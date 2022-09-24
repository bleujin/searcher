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
		
	}
	
	public void testIndexTran() throws Exception {
		
		Object result = sdc.indexTran( isession ->{
			isession.newDocument("bleujin").keyword("name", "bleujin").number("age", 20).updateVoid() ;
			isession.newDocument("jin").keyword("name", "jin").number("age", 30).updateVoid() ;
			isession.newDocument("hero").keyword("name", "hero").number("age", 40).updateVoid() ;
			
			return "indexed" ;
		}).exceptionally(ex ->{
			ex.printStackTrace() ;
			return "exception" ;
		}).thenApply( rtn ->{
			try {
				 return "confirmed " + sdc.newSearcher().createRequest("").find().totalCount() ;
			} catch (IOException | ParseException e) {
				return "fail" ;
			}
		}).get();
		
		assertEquals("confirmed 3", result);
	}

	
	public void testStreamFirst() throws Exception {
		sdc.index(SAMPLE) ;
		
		// search && filtering
		sdc.newSearcher().createRequest("").find().readStream().gte("age", 20L).eq("name", "bleujin").forEach(System.out::println);

	
		
		// search && filtering && update 
		sdc.index(isession ->{
			SearchSession session = isession.searchSession();
			session.searchConfig() ;
			
			session.createRequest("").find().writeStream(isession).gte("age", 30L).forEach(wdoc ->{
				try {
					wdoc.keyword("name", "new " + wdoc.asString("name")).updateVoid() ;
				} catch (IOException ignore) {
				}
			});
			
			return null ;
		}) ;

		sdc.newSearcher().createRequest("").find().debugPrint("name", "age");
		
	}
}
