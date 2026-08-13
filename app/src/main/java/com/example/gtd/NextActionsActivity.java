package com.example.gtd;

public class NextActionsActivity extends GtdListActivity {
    @Override protected String listKey() { return GtdRepository.NEXT_ACTIONS; }
    @Override protected int screenTitle() { return R.string.next_actions; }
    @Override protected int screenDescription() { return R.string.next_actions_description; }
    @Override protected int inputHint() { return R.string.next_action_hint; }
    @Override protected boolean canAddToToday() { return true; }
}
