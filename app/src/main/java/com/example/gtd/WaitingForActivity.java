package com.example.gtd;

public class WaitingForActivity extends GtdListActivity {
    @Override protected String listKey() { return GtdRepository.WAITING_FOR; }
    @Override protected int screenTitle() { return R.string.waiting_for; }
    @Override protected int screenDescription() { return R.string.waiting_for_description; }
    @Override protected int inputHint() { return R.string.waiting_for_hint; }
    @Override protected int emptyMessage() { return R.string.waiting_for_empty; }
}
