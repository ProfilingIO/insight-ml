<!-- [MANAGED_BY_AGENT_PLATFORM] -->
# AGENTS.md

## Project identity

- repo: insight-ml
- product area: insight
- primary language: java
- owners: ["platform"]

## Mandatory Operating Rules

1. **Check repo-local rules:** Before beginning any implementation, you **MUST** read and follow all rules in `.junie/rules/`.
2. **Read active operating surfaces:** Before beginning non-trivial implementation, check fresh repo-local `.smartseer/plans/`, `.smartseer/follow-ups/`, and `.smartseer/unblockers/` artifacts and treat them as the operational source of truth when present.
3. **Use Atlas and record evidence:** After workspace registry or `agent-bootstrap` orientation, use read-only Atlas MCP tools when configured; otherwise use Atlas CLI context packs for broad or multi-seam work, Atlas path resolution for file-specific work, and `docs/agents/atlas.md` as the fallback before broad search. After non-trivial work, write an Atlas usage note when the CLI is available so rollout quality can be measured.
4. **Knowledge Capture:** If the task is non-trivial (per `10-knowledge-capture.md`), you **MUST** create or update a repo-local note in `docs/agent-notes/` before finishing. The task is not complete until your final response names that note path explicitly, or states `No note created because: <reason>` when the task was truly trivial.
5. **Internal docs when relevant:** For documentation-driven, product-behavior, architecture, or process questions, search Confluence or other internal docs before relying only on repository code search.
6. **API docs repo when relevant:** For client-facing API contracts, OpenAPI behavior, integration examples, or tracking/recommender API guidance, check `apidocs-server` explicitly.
7. **Keep README as an entrypoint:** Do not append detailed feature, runbook, or reference docs to `README.md`; update the repo docs entrypoint or authoritative docs surface and run docs lint when available.
8. **Out-of-scope work:** Do not perform cleanup or improvements outside the current task's scope. Instead, record them in `.smartseer/follow-ups/`.
9. **Build and Test:** Run the narrowest relevant verification command before finishing.
10. **Oversized tasks:** If the task is too large to complete safely in one session, persist a phased plan in `.smartseer/plans/` and finish the best next slice instead of forcing a one-shot implementation.
11. **Operational vs durable planning surfaces:** Use `.smartseer/plans/`, `.smartseer/follow-ups/`, and `.smartseer/unblockers/` for agent continuation and routing only. Customer/stakeholder QA plans, specs, and execution plans belong in Confluence or Google Docs, with Jira tracking ownership and status.
12. **Operational vs context-only evidence:** Treat `docs/agent-notes/`, `memory/findings/`, and Jira drafts as context-only unless the work has been promoted into `.smartseer/plans/`, `.smartseer/follow-ups/`, or `.smartseer/unblockers/`.
13. **Jira completion:** When completing work for a Jira ticket, commit and push the changes on the current shared branch with the Jira ticket key in the commit message, then add a Jira comment summarizing the change, verification, and follow-up work. Tailor the comment to the ticket reporter: use developer-level detail for engineering reporters and product/support-oriented language for non-engineering reporters. Do not include secrets, local-only tokens, cookies, or raw private customer data in commits or Jira comments.
14. **Branch discipline:** Stay on the repository default branch unless a human explicitly asks you to create, switch to, or work from another branch. Do not create Codex or LLM-named branches as a default workflow step.

## Core rules

- Prefer the smallest safe change.
- Keep behavior stable unless the task explicitly changes it.
- If a request can reasonably mean more than one materially different implementation, pause and ask a targeted clarification before editing. Do not rely on assumptions when the choice could change semantics, access control, data shape, UX behavior, or scope.
- Ensure corresponding Playwright or unit tests exist for all changes.
- Always run a brief self-improvement sweep before finishing: check how this session could leave agents smarter, more capable, more efficient, or more reliable, then either implement the small improvement or record it as durable follow-up work.
- When Agent Atlas misses a significant owner, file, test scope, workflow, or command, record it in an Atlas usage note. If the miss is repeated, blocks safe work, or implies a framework bug or missing Agent Atlas capability, create a Linear ticket for Agent Atlas when Linear access is available; otherwise create a ticket-ready follow-up with the exact evidence and routing need.
- Ground prioritization and improvement ideas in the available workspace objectives, ownership context, and existing repo artifacts before asking humans for meta-guidance.
- Prefer leaving behind a concrete staged plan over vague “future work” when the main task itself is too large for one high-quality session.
- Prefer cross-platform helper implementations for reusable tooling; use platform-specific wrappers only around a shared implementation.
- Do not broaden scope to unrelated cleanup unless it materially reduces risk.
- If you discover worthwhile but out-of-scope work, draft a follow-up ticket instead of expanding the task silently.
- When writing human-facing documents, use plain English, explicit status labels, short structure, and active consolidation when document sprawl or duplication is making the topic hard to read.
- Keep `README.md` short and route detailed documentation to the configured repo docs entrypoint, the central docs repo, or Confluence.

## Smartseer-specific expectations

- Protect business logic and ranking semantics from accidental drift.
- When changing APIs or contracts, update docs and compatibility notes.
- For model or ranking changes, preserve or add regression fixtures where practical.
- Do not add secrets or production credentials.
- Prefer local repo conventions over generic assumptions.

## Commands

- build: `./mvnw verify`
- test: `./mvnw test`
- lint: `./mvnw -q -DskipTests package`

<!-- [LOCAL_START] -->

<!-- [LOCAL_END] -->

## Related control plane

This file is generated from:
`../smartseer-agent-platform/templates/repo/AGENTS.md.tpl`

Project metadata lives in:
`../smartseer-agent-platform/.smartseer/` and `../smartseer-agent-platform/workspace/`
