package net.bleujin.searcher.index.policy;

import java.io.IOException;

import net.bleujin.searcher.index.IndexSession;


public abstract class AbstractWritePolicy implements IWritePolicy{
	public void begin(IndexSession session) throws IOException {}
	public void end(IndexSession session) {}

}
