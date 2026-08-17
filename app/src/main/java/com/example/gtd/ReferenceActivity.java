package com.example.gtd;

public class ReferenceActivity extends GtdListActivity {
    @Override protected String listKey() { return GtdRepository.REFERENCE; }
    @Override protected int screenTitle() { return R.string.reference; }
    @Override protected int screenDescription() { return R.string.reference_description; }
    @Override protected int inputHint() { return R.string.reference_hint; }
    @Override protected int emptyMessage() { return R.string.reference_empty; }
    @Override protected boolean itemsAreCheckable() { return false; }
}
