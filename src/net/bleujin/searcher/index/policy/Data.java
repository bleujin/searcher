package net.bleujin.searcher.index.policy ;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface Data {
    public abstract Map<String, HashBean> getHashData() throws ExecutionException;
}
