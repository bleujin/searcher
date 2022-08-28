package net.bleujin.searcher.rest;

import java.util.concurrent.ExecutionException;

import net.bleujin.searcher.SearchController;
import net.bleujin.searcher.SearchControllerConfig;
import net.ion.framework.util.IOUtil;
import net.ion.nradon.Radon;
import net.ion.nradon.config.RadonConfiguration;
import net.ion.radon.core.let.PathHandler;

public class MainServer {
	public static void main(String[] args) throws Exception{
		try {

			final SearchController sdc = SearchControllerConfig.newRam().newBuild() ;

			final Radon radon = RadonConfiguration.newBuilder(8182)
					.add("/isearcher", new PathHandler(InfoLet.class, SearchLet.class, IndexLet.class, ListLet.class).prefixURI("isearcher"))
					.createRadon() ;
			radon.getConfig().getServiceContext().putAttribute("SDC", sdc) ;
			radon.start().get() ;

			Runtime.getRuntime().addShutdownHook(new Thread(){
				public void run(){
					try {
						IOUtil.closeQuietly(sdc); 
						radon.stop().get() ;
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
				}
			});
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
