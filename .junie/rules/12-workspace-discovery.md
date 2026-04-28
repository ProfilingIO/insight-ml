<!-- [MANAGED_BY_AGENT_PLATFORM] -->
# Workspace Discovery Rule

To efficiently navigate and understand the multi-repository SMARTSEER workspace, use these tools and strategies.

## Efficient File Discovery

Avoid using recursive terminal commands (like `ls -R` or `Get-ChildItem -Recurse`) in repository roots. They often produce too much output because of `node_modules`, `target`, and other build artifacts.

Instead, use:
- **Tool**: `mcp_workspace-registry_workspace_get_file_tree`
  - Purpose: Get a structured, filtered view of the repository structure.
  - Automatically excludes: `node_modules`, `.git`, `target`, `dist`.
- **Tool**: `mcp_workspace-registry_workspace_find_file`
  - Purpose: Quickly locate a file by name pattern across the workspace.
- **Tool**: `search_project`
  - Purpose: Search for specific text or symbols within the current repository context.

## Global Search

Instead of searching individual repositories sequentially, use the `workspace_search` MCP tool.

- **Tool**: `mcp_workspace-registry_workspace_search`
- **When to use**:
  - Finding where a component or API is used across the entire workspace.
  - Impact analysis when changing shared contracts or data models.
  - Searching for domain terms or business logic implementation across repos.

## Agent Atlas

Use Agent Atlas as the repo-local navigation and rollout-evidence layer. All SMARTSEER repos receive Atlas guidance; detailed `.agent-atlas` cards remain minimal and evidence-driven.

1. Use the workspace registry or `pnpm agent-bootstrap --repo-path <path>` from `smartseer-agent-platform` for repo selection and cross-repo orientation.
2. When the agent runtime has `agent-atlas-company` MCP configured, prefer its read-only `context_pack` and `resolve_path` tools before broad repository search.
3. When MCP is unavailable, run `pnpm atlas:context-pack "<task>" <repo-path> --budget 4000` from `smartseer-agent-platform` for broad or multi-seam work.
4. For file-specific work without MCP, run `pnpm atlas resolve-path <repo-relative-path> <repo-path>` from `smartseer-agent-platform`.
5. If only generated docs are available, read `docs/agents/atlas.md` before broad repository search.
6. If Atlas setup looks stale, run `pnpm atlas:doctor` from `smartseer-agent-platform` before guessing at CLI behavior.
7. After non-trivial work, record Atlas evidence when the CLI is available:
   - `pnpm atlas usage-note "<task>" --path <repo-path> --command context-pack --entity <entity-id> --file <path> --test "<command>" --out .runtime/agent-atlas/usage/<task-slug>.yaml`
   - add `--broad-search-fallback` when Atlas did not avoid broad search
   - add `--missing-card "<gap>"` or `--misleading-card "<gap>"` when the graph was incomplete or wrong
   - until OPS-2245 is fixed, keep usage receipts outside `.agent-atlas` so Atlas validation does not parse receipts as entity cards
8. For significant Agent Atlas bugs, repeated missing-card patterns, misleading cards, adoption friction, or feature requests, create a Linear ticket for Agent Atlas when Linear access is available. If Linear is unavailable, create a ticket-ready SMARTSEER follow-up that names the evidence, affected repo, task, and requested Atlas change.

Do not create or expand `.agent-atlas` metadata speculatively. Add minimal cards only when concrete agent work exposes repeated navigation waste, ambiguous ownership, or recurring test-selection cost.

## Capturing Knowledge

Every repository should have a `docs/agent-notes/` directory for captured knowledge. Always check this directory when starting work in a new area.

- **Note creation**: For non-trivial tasks, you MUST create a repo-local note in `docs/agent-notes/`.
- **Note tool**: `pnpm new-agent-note --repo-path <path> --title "<title>"` (from `smartseer-agent-platform`)

## Internal Collaboration Context

For internal Slack or coworker-facing work, also consult the central collaboration manifests:

- `../smartseer-agent-platform/workspace/company.yaml`
- `../smartseer-agent-platform/workspace/slack.yaml`

## Business Context

Before asking humans what to optimize, what matters most, or whether an improvement is worthwhile, consult the central
workspace context first:

- `workspace://metrics` or `../smartseer-agent-platform/workspace/metrics.yaml`
- `workspace://ownership` or `../smartseer-agent-platform/workspace/ownership.yaml`
- the repo profile in `workspace://repo/<repo>` or `../smartseer-agent-platform/workspace/repos.yaml`
- any existing `.smartseer/plans/`, `.smartseer/follow-ups/`, and `docs/agent-notes/` artifacts in the active repo

If the needed objective, performance signal, or ownership context is missing, create durable follow-up work to expose
that context instead of treating the absence as normal.

## Skill Guidance

For detailed guidance on workspace discovery and advanced search techniques, refer to the central skill:
- `../smartseer-agent-platform/skills/knowledge-capture-and-discovery/SKILL.md`

<!-- [LOCAL_START] -->

<!-- [LOCAL_END] -->
