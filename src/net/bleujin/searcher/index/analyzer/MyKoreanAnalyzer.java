package net.bleujin.searcher.index.analyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.ko.KoreanAnalyzer;
import org.apache.lucene.analysis.ko.KoreanPartOfSpeechStopFilter;
import org.apache.lucene.analysis.ko.KoreanReadingFormFilter;
import org.apache.lucene.analysis.ko.KoreanTokenizer;
import org.apache.lucene.analysis.ko.KoreanTokenizer.DecompoundMode;
import org.apache.lucene.analysis.ko.POS.Tag;
import org.apache.lucene.analysis.ko.dict.UserDictionary;

public class MyKoreanAnalyzer extends KoreanAnalyzer {

	private final UserDictionary userDict;
	private final DecompoundMode mode;
	private final Set<Tag> stopTags;
	private final boolean outputUnknownUnigrams;
	private final CharArraySet stopwords ;

	private static UserDictionary DftDict = null ;
	
	public MyKoreanAnalyzer() {
		this(createDefault(), KoreanTokenizer.DEFAULT_DECOMPOUND, KoreanPartOfSpeechStopFilter.DEFAULT_STOP_TAGS, CharArraySet.EMPTY_SET, false);
	}

	public MyKoreanAnalyzer(CharArraySet stopwords) {
		this(createDefault(), KoreanTokenizer.DEFAULT_DECOMPOUND, KoreanPartOfSpeechStopFilter.DEFAULT_STOP_TAGS, stopwords, false);
	}

	public MyKoreanAnalyzer(UserDictionary userDict, DecompoundMode mode, Set<Tag> stopTags, CharArraySet stopwords, boolean outputUnknownUnigrams) {
		this.userDict = userDict;
		this.mode = mode;
		this.stopTags = stopTags;
		this.stopwords = stopwords ;
		this.outputUnknownUnigrams = outputUnknownUnigrams;
	}

	protected TokenStreamComponents createComponents(String fieldName) {
		Tokenizer tokenizer = new KoreanTokenizer(TokenStream.DEFAULT_TOKEN_ATTRIBUTE_FACTORY, this.userDict, this.mode, this.outputUnknownUnigrams);
		TokenStream stream = new KoreanPartOfSpeechStopFilter(tokenizer, this.stopTags);
		stream = new KoreanReadingFormFilter(stream);
		stream = new LowerCaseFilter(stream);
		if (this.stopwords != null) stream = new StopFilter(stream, this.stopwords) ;
		return new TokenStreamComponents(tokenizer, stream);
	}

	protected TokenStream normalize(String fieldName, TokenStream in) {
		return new LowerCaseFilter(in);
	}


	private static UserDictionary createDefault() {
		if (DftDict != null) {
			return DftDict ;
		}
		
		try (InputStream in = MyKoreanAnalyzer.class.getResourceAsStream("dictionary.dic"); BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
			UserDictionary result = UserDictionary.open(reader);
			DftDict = result ;
			return result;
		} catch (IOException ex) {
			return null; //
		}
	}

}
