<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Opencell Community Tools - IntelliJ Plugin Changelog

## [Unreleased]

### Added
- Action for compare a script with an environment (context menu or Alt+C)
- Auto-detection of script interfaces (on first project opening or directly in settings)
- Input validation on script interface and environment dialogs

### Changed
- Improved Opencell script detection by performing deep search of ScriptInterface on supers

### Security
- Use `PasswordSafe` for Environment credentials storing instead of plugin settings file (you need to re-enter it)

## [1.2.5] - 2023-02-03
- Support for IntelliJ 2022.3

## [1.2.4]

### Added
- Support for IntelliJ 2022.2

## [1.2.3]

### Added
- Support for IntelliJ 2022.1

## [1.2.2]

### Added
- Support for IntelliJ 2021.3

## [1.2.1]

### Added
- Support for IntelliJ 2021.2

## [1.2.0]

### Added
- Support for IntelliJ 2021.1

### Changed
- Improved error handling on deploy task

### Fixed
- [#12](https://github.com/Halvra/opencell-intellij-plugin/issues/12) Exception on <kbd>Alt+D</kbd> during indexing
- [#15](https://github.com/Halvra/opencell-intellij-plugin/issues/15) Exception when deploy task fail

## [1.1.0]

### Added
- Shortcut for postman generation on a script (<kbd>Alt</kbd>+<kbd>P</kbd>)
- Action for directly deploy a script to an environment (context menu or <kbd>Alt</kbd>+<kbd>D</kbd>)

### Changed
- Use `opencell-api-dto` dependency to simplify new feature implementation instead of creating DTO directly in this plugin

### Fixed
- Abstract classes extending `org.meveo.service.script.Script` wrongly detected as valid Script

## [1.0.0]

### Changed
- Plugin name now reflect the real project intention
- Overall code cleanup after [Sonar](https://sonarcloud.io/dashboard?id=opencell-intellij-plugin) setup

### Fixed
- Resource leak on deploy task

## [0.0.1]

### Added
- Initial scaffold created from [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)
- Plugin settings (project scoped) : add your environments and define script interfaces
- Action: generate Script JSON body and copy it to clipboard (useful for your postman collections)
- Task: automatically upload scripts to your preferred instance

[Unreleased]: https://github.com/Halvra/opencell-intellij-plugin/compare/v1.2.5...HEAD
[1.2.5]: https://github.com/Halvra/opencell-intellij-plugin/compare/v1.2.4...v1.2.5
[1.2.4]: https://github.com/Halvra/opencell-intellij-plugin/compare/v1.2.3...v1.2.4
[1.2.3]: https://github.com/Halvra/opencell-intellij-plugin/compare/v1.2.2...v1.2.3
[1.2.2]: https://github.com/Halvra/opencell-intellij-plugin/compare/v1.2.1...v1.2.2
[1.2.1]: https://github.com/Halvra/opencell-intellij-plugin/compare/v1.2.0...v1.2.1
[1.2.0]: https://github.com/Halvra/opencell-intellij-plugin/compare/v1.1.0...v1.2.0
[1.1.0]: https://github.com/Halvra/opencell-intellij-plugin/compare/v1.0.0...v1.1.0
[1.0.0]: https://github.com/Halvra/opencell-intellij-plugin/compare/v0.0.1...v1.0.0
[0.0.1]: https://github.com/Halvra/opencell-intellij-plugin/commits/v0.0.1
