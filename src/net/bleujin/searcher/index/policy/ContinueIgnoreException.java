package net.bleujin.searcher.index.policy;

import java.io.IOException;

import net.bleujin.searcher.index.IndexSession;
import net.ion.framework.util.Debug;

public class ContinueIgnoreException implements ExceptionPolicy {

	private boolean end = false;

	public boolean isEnd() {
		return end;
	}

	public void whenExceptionOccured(IndexSession session, IOException e) {
		Debug.debug(e);
	}

}
