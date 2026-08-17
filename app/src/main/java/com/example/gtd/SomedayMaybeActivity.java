package com.example.gtd;

public class SomedayMaybeActivity extends GtdListActivity {
    @Override protected String listKey() { return GtdRepository.SOMEDAY_MAYBE; }
    @Override protected int screenTitle() { return R.string.someday_maybe; }
    @Override protected int screenDescription() { return R.string.someday_maybe_description; }
    @Override protected int inputHint() { return R.string.someday_maybe_hint; }
    @Override protected int emptyMessage() { return R.string.someday_maybe_empty; }
    @Override protected boolean itemsAreCheckable() { return false; }
}
