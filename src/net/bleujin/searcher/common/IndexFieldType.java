package net.bleujin.searcher.common;

import java.util.Set;

import net.bleujin.searcher.common.MyField.MyFieldType;
import net.ion.framework.util.SetUtil;

public interface IndexFieldType {

	public final IndexFieldType DEFAULT = new IndexFieldType() {

		private Set<String> numericField = SetUtil.newSyncSet();

		public void decideField(MyField field) {
			if (field.myFieldtype() == MyFieldType.Number) {
				numericField.add(field.name());
			}
		}

		public boolean isNumericField(String field) {
			return numericField.contains(field);
		}
	};

	public void decideField(MyField field);

	public boolean isNumericField(String field);
	
}
