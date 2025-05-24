# Changelog

All notable changes to this project will be documented in this file.

## [v1.0.0] - 2025-05-24
### Added
- Context-aware widgets activated by query or result metadata.
- Caching layer using Proxy pattern to reduce repeated queries.
- Spelling corrector.
- Metadata summaries in results: file type, year, language.
- Git pre-commit hook for code checks.

### Changed
- Integrated widget selection into core search service.
- Query parsing supports `content:` and `path:` prefixes.
- Search result ranking improved via metadata scoring.
- Simplified Thymeleaf handling of optional widgets.

### Fixed
- Thymeleaf expression errors when rendering widgets.
- Corrected logic in content/path search when both fields are null to fallback to global search.
- Corrected scoring method to avoid redundant rank computations.
- Included the standard search service in the new proxy implementation.

