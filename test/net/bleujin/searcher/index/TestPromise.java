package net.bleujin.searcher.index;

import java.io.File;
import java.util.concurrent.Callable;

import net.bleujin.searcher.AbTestCase;
import net.ion.framework.promise.AlwaysCallback;
import net.ion.framework.promise.DoneCallback;
import net.ion.framework.promise.FailCallback;
import net.ion.framework.promise.ProgressCallback;
import net.ion.framework.promise.Promise;
import net.ion.framework.promise.Promise.State;
import net.ion.framework.promise.impl.DefaultDeferredManager;
import net.ion.framework.util.Debug;

public class TestPromise extends AbTestCase{
	
	
	public void testDefault() throws Exception {
		
		
		Promise<File, Throwable, Void> promise = new DefaultDeferredManager().when(new Callable<File>(){
			public File call() throws Exception {
				Debug.line("when");
				for (int i=0 ; i < 5 ; i++) {
					Thread.sleep(100) ;
				}
				
				return new File(".");
//				throw new FileNotFoundException() ;
			}
		}).fail(new FailCallback<Throwable>(){
			public void onFail(Throwable ex) {
				Debug.line("fail");
				ex.printStackTrace(); 
			}
		}).progress(new ProgressCallback<Void>() {
			public void onProgress(Void progress) {
				Debug.line("P", progress);
			}
		}).always(new AlwaysCallback<File, Throwable>() {
			public void onAlways(State state, File resolved, Throwable ex) {
				Debug.line(state, resolved, ex);
			}
		}).done(new DoneCallback<File>() {
			public void onDone(File result) {
				Debug.line("DONE", result);
			}
		}) ;
		

		Thread.sleep(1000);
	}
}
