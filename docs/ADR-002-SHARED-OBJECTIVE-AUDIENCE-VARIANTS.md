# ADR-002: Shared objectives with audience-specific variants

Status: accepted for Section 2.

## Decision

A lesson references stable, measurable learning objectives once and owns separately selectable presentation variants for ages 5–7, 8–11, 12–15, 16–17, and 18+. A variant changes framing, examples, density, tone and expected load; it does not silently change CEFR alignment or mastery semantics.

Selection is deterministic from the active learner profile. The exact band is preferred. A missing child variant may fall back only to the other child band, and a missing teen variant only to the other teen band. Adults never fall back to minor-oriented content, and minors never fall back to adult content. With no safe match, discovery omits the lesson.

## Why

Duplicating complete curricula by age would create divergent objectives, prerequisites and progress identifiers. Shared objectives preserve one progression graph and stable progress meaning while still preventing childish adult presentation and inappropriate child scenarios.

## Consequences

Editors must review every supplied audience variant. Published state applies to the lesson version as a whole, and learner queries expose only published, active lessons with a safe variant. Future languages reuse the structure without inheriting French-specific fields.
