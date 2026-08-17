package com.example.gtd;

public class ProjectsActivity extends GtdListActivity {
    @Override protected String listKey() { return GtdRepository.PROJECTS; }
    @Override protected int screenTitle() { return R.string.projects; }
    @Override protected int screenDescription() { return R.string.projects_description; }
    @Override protected int inputHint() { return R.string.project_hint; }
    @Override protected int emptyMessage() { return R.string.projects_empty; }
}
