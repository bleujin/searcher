package net.bleujin.searcher.index.channel;


public interface RelayChannel<T> {

	void addMessage(T message)  ;

	T pollMessage() ;

	boolean hasMessage();
	
	void doEnd(String cause) ;

	boolean isEndMessageOccured() ;
	
	String getCause() ;
}
